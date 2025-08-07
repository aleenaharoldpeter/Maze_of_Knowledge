package src.main.quiz;


import src.main.auth.SessionManager;
import src.main.ui.CodeChallengeScene;
import src.main.utils.PreferencesManager;
import src.main.utils.SceneManager;
import src.main.utils.AudioManager;
import src.main.quiz.WrongAnswerRecord;
import src.main.api.JDoodleClient;
import src.main.leaderboard.LeaderboardService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.util.Duration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

/**
 * The QuizScene class encapsulates the logic and UI for the quiz portion of the application.
 * It handles the timer, question display, answer selection, score tracking, health bars for the player
 * and the computer mascot, and transitions to code challenge scenes as well as end-of-quiz behavior.
 */
public class QuizScene {
    // Global quiz state variables.
    private static Timeline quizTimer;
    private static Label timerLabelRef;
    private static int score = 0;
    private static int timeRemaining;
    private static List<Question> questions;
    private static List<WrongAnswerRecord> wrongAnswers;
    
    public static final int MAX_QUESTIONS = 10;
    private static boolean quizEnded = false;
    private static Popup currentHintPopup = null;
    private static boolean difficultyChanged = false;
    
    // Player mascot and health bar (left side).
    private static ImageView quizMascot;
    private static ProgressBar healthBar;
    private static double health = 1.0;
    
    // Computer mascot and health bar (right side).
    private static ImageView computerMascot;
    private static ProgressBar computerHealthBar;
    private static double computerHealth = 1.0;
    
    // Animation constants for sprite sheets (player attack/hurt).
    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;
    private static final int ATTACK_FRAMES = 15;
    private static final int ATTACK_FRAME_DURATION_MS = 100;
    private static final int COMPUTER_ATTACK_FRAME_DURATION_MS = 200;
    private static final int HURT_FRAMES = 15;
    private static final int HURT_FRAME_DURATION_MS = 150;
    private static boolean isResettingForDifficulty = false;
    
    // Walking animation for player.
    private static Timeline walkingTimeline;
    private static final int WALK_FRAMES = 9;
    private static final int WALK_FRAME_DURATION_MS = 120;
    private static final int WALK_FRAME_WIDTH = 64;
    private static final int WALK_FRAME_HEIGHT = 64;
    
    // Computer idle animation constants.
    private static final int COMPUTER_DISPLAY_WIDTH = 576;
    private static final int COMPUTER_DISPLAY_HEIGHT = 332;
    private static final int COMPUTER_FRAME_DURATION_MS = 150;
    private static final int COMPUTER_DEFAULT_FRAME_COUNT = 6;
    private static Timeline computerIdleTimeline = null;
    private static int computerCurrentFrame = 0;
    
    // Field for computer attack animation frame index.
    private static int attackcomputerCurrentFrame = 0;
    
    // Fields for coding challenge (if any).
    private static String testInputRun;
    private static String testInputSubmit;
    private static String challengeExplanation;
    
    // Code editor and selected language.
    private static TextArea codeEditor;
    private static String currentLanguage = "java";

    // Current Directory Path
    private static final String BASE_DIR = System.getProperty("user.dir");
    static {
        System.out.println("Current Directory Path: " + BASE_DIR);
    }
    
    /**
     * Creates a pane displaying wrong answer records.
     * Used in the end-of-quiz screen.
     *
     * @return a ScrollPane containing wrong answer records.
     */
    private static Parent createWrongAnswersPane() {
        VBox wrongAnswersBox = new VBox(10);
        wrongAnswersBox.setPadding(new Insets(10));
        wrongAnswersBox.setAlignment(Pos.TOP_LEFT);
        wrongAnswersBox.setStyle("-fx-background-color: transparent;");
        
        if (wrongAnswers.isEmpty()) {
            Label noWrong = new Label("No wrong answers recorded.");
            wrongAnswersBox.getChildren().add(noWrong);
        } else {
            for (WrongAnswerRecord record : wrongAnswers) {
                VBox recordBox = new VBox(5);
                recordBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 5;");
                Label questionLabel = new Label("Q: " + record.getQuestion());
                questionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                Label answerLabel = new Label("Your Answer: " + record.getSelectedAnswer());
                answerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
                Label correctAnswer = new Label("Correct Answer: " + record.getCorrectAnswer());
                correctAnswer.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
                Label hintLabel = new Label("Hint: " + record.getHint());
                hintLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: yellow;");
                Label keywordsLabel = new Label("Keywords: " + record.getKeywords());
                keywordsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: orange;");
                Label explanationTitle = new Label("Explanation:");
                explanationTitle.setStyle("-fx-font-weight: bold;");
                VBox explanationLinesBox = new VBox(5);
                explanationLinesBox.setAlignment(Pos.CENTER_LEFT);
                String explanationText = record.getExplanation().replace("\\n", "\n");
                String[] lines = explanationText.split("\n");
                for (String line : lines) {
                    Label lineLabel = new Label(line);
                    explanationLinesBox.getChildren().add(lineLabel);
                }
                explanationLinesBox.setStyle("-fx-font-weight: bold; -fx-text-fill: orange;");
                recordBox.getChildren().addAll(questionLabel, answerLabel, correctAnswer, hintLabel, keywordsLabel, explanationTitle, explanationLinesBox);
                wrongAnswersBox.getChildren().add(recordBox);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(wrongAnswersBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scrollPane;
    }
    
    // -------------------- Player Mascot Methods --------------------
    
    /**
     * Returns the walking sprite sheet image for the player's mascot.
     * If the walking image does not exist, falls back to the idle image.
     *
     * @return an Image representing the walking animation.
     */
    private static Image getWalkingImage() {
        String selectedPlayer = PreferencesManager.getSelectedPlayer();
        String walkPath = "file:" + BASE_DIR + "/assets/Sprite/Player/" 
                        + selectedPlayer + "/Walking/right_walk.png";
        // Remove the "file:" prefix to check if the file exists.
        File walkFile = new File(walkPath.substring(5));
        if (walkFile.exists()) {
            return new Image(walkPath);
        } else {
            return getIdleImage();
        }
    }

    /**
     * Returns the idle image for the player's mascot.
     *
     * @return an Image representing the idle state.
     */
    private static Image getIdleImage() {
        String selectedPlayer = PreferencesManager.getSelectedPlayer();
        String idlePath = "file:" + BASE_DIR + "/assets/Sprite/Player/" 
                          + selectedPlayer + "/Idle/idle.png";
        return new Image(idlePath);
    }
    
    /**
     * Returns a random attack image for the player's mascot.
     *
     * @return an Image representing an attack frame.
     */
    private static Image getRandomAttackImage() {
        String selectedPlayer = PreferencesManager.getSelectedPlayer();
        String attackFolderPath = BASE_DIR  + "/assets/Sprite/Player/" 
                                  + selectedPlayer + "/Attack";
        File folder = new File(attackFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (files != null && files.length > 0) {
                int idx = new Random().nextInt(files.length);
                return new Image("file:" + files[idx].getAbsolutePath());
            }
        }
        return getIdleImage();
    }
    
    /**
     * Returns the hurt image for the player's mascot.
     *
     * @return an Image representing the hurt state.
     */
    private static Image getHurtImage() {
        String selectedPlayer = PreferencesManager.getSelectedPlayer();
        String hurtPath = "file:" + BASE_DIR + "/assets/Sprite/Player/" 
                          + selectedPlayer + "/Hurt/hurt.png";
        return new Image(hurtPath);
    }
    
    // -------------------- Player Mascot Animation Methods --------------------
    
    /**
     * Plays the attack animation for the player's mascot.
     * Stops walking animation, cycles through attack frames, then restarts walking animation.
     *
     * @param onFinish a Runnable to execute after the animation completes.
     */
    private static void showAttackAnimation(Runnable onFinish) {
        stopWalkingAnimation();
        quizMascot.setPreserveRatio(true);
        Image attackImg = getRandomAttackImage();
        quizMascot.setImage(attackImg);
        quizMascot.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_HEIGHT));
    
        final int[] frameIndex = {0};
        Timeline attackTimeline = new Timeline(new KeyFrame(Duration.millis(ATTACK_FRAME_DURATION_MS), e -> {
            frameIndex[0] = (frameIndex[0] + 1) % ATTACK_FRAMES;
            quizMascot.setViewport(new Rectangle2D(frameIndex[0] * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        }));
        attackTimeline.setCycleCount(ATTACK_FRAMES);
        attackTimeline.setOnFinished(e -> {
            startWalkingAnimation();
            if (onFinish != null) {
                onFinish.run();
            }
        });
        attackTimeline.play();
    }
    
    /**
     * Plays the hurt animation for the player's mascot.
     * Stops walking animation, cycles through hurt frames, then restarts walking animation.
     *
     * @param onFinish a Runnable to execute after the animation completes.
     */
    private static void showHurtAnimation(Runnable onFinish) {
        stopWalkingAnimation();
        quizMascot.setPreserveRatio(true);
        Image hurtImg = getHurtImage();
        quizMascot.setImage(hurtImg);
        quizMascot.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_HEIGHT));
    
        final int[] frameIndex = {0};
        Timeline hurtTimeline = new Timeline(new KeyFrame(Duration.millis(HURT_FRAME_DURATION_MS), e -> {
            frameIndex[0] = (frameIndex[0] + 1) % HURT_FRAMES;
            quizMascot.setViewport(new Rectangle2D(frameIndex[0] * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        }));
        hurtTimeline.setCycleCount(HURT_FRAMES);
        hurtTimeline.setOnFinished(e -> {
            startWalkingAnimation();
            if (onFinish != null) {
                onFinish.run();
            }
        });
        hurtTimeline.play();
    }
    
    /**
     * Starts the walking animation for the player's mascot using a sprite sheet.
     */
    private static void startWalkingAnimation() {
        stopWalkingAnimation();
        Image walkSheet = getWalkingImage();
        quizMascot.setImage(walkSheet);
        quizMascot.setViewport(new Rectangle2D(0, 0, WALK_FRAME_WIDTH, WALK_FRAME_HEIGHT));
        final int[] frameIndex = {0};
        walkingTimeline = new Timeline(new KeyFrame(Duration.millis(WALK_FRAME_DURATION_MS), e -> {
            frameIndex[0] = (frameIndex[0] + 1) % WALK_FRAMES;
            quizMascot.setViewport(new Rectangle2D(frameIndex[0] * WALK_FRAME_WIDTH, 0, WALK_FRAME_WIDTH, WALK_FRAME_HEIGHT));
        }));
        walkingTimeline.setCycleCount(Timeline.INDEFINITE);
        walkingTimeline.play();
    }
    
    /**
     * Stops the walking animation if it is running.
     */
    private static void stopWalkingAnimation() {
        if (walkingTimeline != null) {
            walkingTimeline.stop();
            walkingTimeline = null;
        }
    }
    
    // -------------------- Computer Mascot Methods --------------------
    
    /**
     * Returns the computer's idle image.
     *
     * @return an Image representing the computer's idle state.
     */
    private static Image getComputerIdleImage() {
        String selectedComputer = PreferencesManager.getSelectedComputer();
        String idlePath = "file:" + BASE_DIR + "/assets/Sprite/Computer/" 
                          + selectedComputer + "/Idle/idle.png";
        return new Image(idlePath);
    }
    
    /**
     * Returns a random attack image for the computer's mascot.
     *
     * @return an Image representing a computer attack frame.
     */
    private static Image getRandomComputerAttackImage() {
        String selectedComputer = PreferencesManager.getSelectedComputer();
        String attackFolderPath = "file:" + BASE_DIR + "/assets/Sprite/Computer/" 
                                  + selectedComputer + "/Attack";
        File folder = new File(attackFolderPath.substring(5));
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (files != null && files.length > 0) {
                int idx = new Random().nextInt(files.length);
                return new Image("file:" + files[idx].getAbsolutePath());
            }
        }
        return getComputerIdleImage();
    }
    
    /**
     * Returns the computer's hurt image.
     *
     * @return an Image representing the computer's hurt state.
     */
    private static Image getComputerHurtImage() {
        String selectedComputer = PreferencesManager.getSelectedComputer();
        String hurtPath = "file:" + BASE_DIR + "/assets/Sprite/Computer/" 
                          + selectedComputer + "/Hurt/hurt.png";
        return new Image(hurtPath);
    }
    
    // -------------------- Computer Mascot Animation Methods --------------------
    
    /**
     * Stops the computer idle animation if it is running.
     */
    private static void stopComputerIdleAnimation() {
        if (computerIdleTimeline != null) {
            computerIdleTimeline.stop();
            computerIdleTimeline = null;
        }
    }
    
    /**
     * Starts the computer's idle animation.
     * Uses either a sprite sheet or multiple images approach based on available files.
     */
    private static void startComputerIdleAnimation() {
        stopComputerIdleAnimation();
        
        String selectedComputer = PreferencesManager.getSelectedComputer();
        String idleFolderPath = BASE_DIR + "/assets/Sprite/Computer/" 
                                + selectedComputer + "/Idle";
        File idleFolder = new File(idleFolderPath);
        File[] idleFiles = idleFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
    
        if (idleFiles == null || idleFiles.length == 0) {
            computerMascot.setImage(getComputerIdleImage());
            computerMascot.setViewport(new Rectangle2D(0, 0, COMPUTER_DISPLAY_WIDTH, COMPUTER_DISPLAY_HEIGHT));
            // if (PreferencesManager.getSelectedComputer().equals("Frost Guardian")) {
            //     // Move Frost Guardian a bit lower (increase the Y value to shift down)
            //     computerMascot.setTranslateY(-60);
            //     StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));
            // } else {
            //     computerMascot.setTranslateY(0);
            //     StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));
            // }            
            return;
        }
        
        if (idleFiles.length == 1) {
            // Single sprite sheet approach.
            Image spriteSheet = new Image("file:" + idleFiles[0].getAbsolutePath());
            int sheetWidth = (int) spriteSheet.getWidth();
            int sheetHeight = (int) spriteSheet.getHeight();
            int frameCount = COMPUTER_DEFAULT_FRAME_COUNT;
            int frameWidth = sheetWidth / frameCount;
            int frameHeight = sheetHeight;
            
            computerMascot.setImage(spriteSheet);
            computerMascot.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
            computerMascot.setFitWidth(COMPUTER_DISPLAY_WIDTH);
            computerMascot.setFitHeight(COMPUTER_DISPLAY_HEIGHT);
            // if (PreferencesManager.getSelectedComputer().equals("Frost Guardian")) {
            //     // Move Frost Guardian a bit lower (increase the Y value to shift down)
            //     // computerMascot.setTranslateY(-200);
            //     StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));
            // } else {
            //     computerMascot.setTranslateY(0);
            //     StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));
            // }            
            computerMascot.setPreserveRatio(true);
            
            computerCurrentFrame = 0;
            computerIdleTimeline = new Timeline(new KeyFrame(Duration.millis(COMPUTER_FRAME_DURATION_MS), e -> {
                computerCurrentFrame = (computerCurrentFrame + 1) % frameCount;
                double frameX = computerCurrentFrame * frameWidth;
                computerMascot.setViewport(new Rectangle2D(frameX, 0, frameWidth, frameHeight));
            }));
            computerIdleTimeline.setCycleCount(Timeline.INDEFINITE);
            computerIdleTimeline.play();
        } else {
            // Multiple images approach.
            Arrays.sort(idleFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            Image[] idleFrames = new Image[idleFiles.length];
            for (int i = 0; i < idleFiles.length; i++) {
                idleFrames[i] = new Image("file:" + idleFiles[i].getAbsolutePath());
            }
            computerMascot.setImage(idleFrames[0]);
            computerMascot.setFitWidth(COMPUTER_DISPLAY_WIDTH);
            computerMascot.setFitHeight(COMPUTER_DISPLAY_HEIGHT);
            // if (PreferencesManager.getSelectedComputer().equals("Frost Guardian")) {
            //     // Move Frost Guardian a bit lower (increase the Y value to shift down)
            //     // computerMascot.setTranslateY(-200);
            //     StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));
            // } else {
            //     computerMascot.setTranslateY(0);
            //     StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));
            // }            
            computerMascot.setPreserveRatio(true);
            
            computerCurrentFrame = 0;
            computerIdleTimeline = new Timeline(new KeyFrame(Duration.millis(COMPUTER_FRAME_DURATION_MS), e -> {
                computerCurrentFrame = (computerCurrentFrame + 1) % idleFrames.length;
                computerMascot.setImage(idleFrames[computerCurrentFrame]);
            }));
            computerIdleTimeline.setCycleCount(Timeline.INDEFINITE);
            computerIdleTimeline.play();
        }
    }
    
    /**
     * Plays the computer's attack animation.
     * Attempts to use a subfolder approach first; falls back to sprite sheet approach if needed.
     *
     * @param onFinish a Runnable to execute when the animation finishes.
     */
    private static void showComputerAttackAnimation(Runnable onFinish) {
        stopComputerIdleAnimation();
        computerMascot.setPreserveRatio(true);
    
        String attackFolderPath = BASE_DIR + "/assets/Sprite/Computer/"
                + PreferencesManager.getSelectedComputer() + "/Attack";
        File attackFolder = new File(attackFolderPath);
    
        File[] subFolders = attackFolder.listFiles(File::isDirectory);
        if (subFolders != null && subFolders.length > 0) {
            File chosenSubfolder = subFolders[new Random().nextInt(subFolders.length)];
            File[] pngFiles = chosenSubfolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (pngFiles != null && pngFiles.length > 0) {
                Arrays.sort(pngFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                final int totalFrames = pngFiles.length;
                Image[] frames = new Image[totalFrames];
                for (int i = 0; i < totalFrames; i++) {
                    frames[i] = new Image("file:" + pngFiles[i].getAbsolutePath());
                }
                computerMascot.setImage(frames[0]);
                attackcomputerCurrentFrame = 0;
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(COMPUTER_ATTACK_FRAME_DURATION_MS), e -> {
                    attackcomputerCurrentFrame = (attackcomputerCurrentFrame + 1) % totalFrames;
                    computerMascot.setImage(frames[attackcomputerCurrentFrame]);
                }));
                timeline.setCycleCount(totalFrames);
                timeline.setOnFinished(e -> {
                    startComputerIdleAnimation();
                    if (onFinish != null) {
                        onFinish.run();
                    }
                });
                timeline.play();
                return;
            } else {
                System.out.println("No PNG files found in subfolder: " + chosenSubfolder.getAbsolutePath());
            }
        }
        
        // Fallback: use sprite sheet approach.
        System.out.println("Falling back to sprite sheet approach for attack animation.");
        Image attackImg = getRandomComputerAttackImage();
        computerMascot.setImage(attackImg);
        Timeline timeline = new Timeline();
        final int[] frameIndex = {0};
        KeyFrame kf = new KeyFrame(Duration.millis(COMPUTER_ATTACK_FRAME_DURATION_MS), e -> {
            frameIndex[0] = (frameIndex[0] + 1) % ATTACK_FRAMES;
            computerMascot.setViewport(new Rectangle2D(frameIndex[0] * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        });
        timeline.getKeyFrames().add(kf);
        timeline.setCycleCount(ATTACK_FRAMES);
        timeline.setOnFinished(e -> {
            startComputerIdleAnimation();
            if (onFinish != null) {
                onFinish.run();
            }
        });
        timeline.play();
    }
    
    /**
     * Plays the computer's hurt animation.
     * Animates multiple frames if available; otherwise, falls back to a default hurt image.
     *
     * @param onFinish a Runnable to execute when the animation finishes.
     */
    private static void showComputerHurtAnimation(Runnable onFinish) {
        stopComputerIdleAnimation();
        computerMascot.setPreserveRatio(true);
    
        String computerHurtFolderPath = BASE_DIR + "/assets/Sprite/Computer/"
                + PreferencesManager.getSelectedComputer() + "/Hurt";
        File hurtFolder = new File(computerHurtFolderPath);
        File[] hurtFiles = hurtFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
    
        if (hurtFiles == null || hurtFiles.length == 0) {
            Image hurtImg = getComputerHurtImage();
            computerMascot.setImage(hurtImg);
            computerMascot.setViewport(new Rectangle2D(0, 0, hurtImg.getWidth(), hurtImg.getHeight()));
            if (onFinish != null) {
                onFinish.run();
            }
            startComputerIdleAnimation();
        } else if (hurtFiles.length == 1) {
            Image hurtImg = new Image("file:" + hurtFiles[0].getAbsolutePath());
            computerMascot.setImage(hurtImg);
            computerMascot.setViewport(new Rectangle2D(0, 0, hurtImg.getWidth(), hurtImg.getHeight()));
            if (onFinish != null) {
                onFinish.run();
            }
            startComputerIdleAnimation();
        } else {
            Arrays.sort(hurtFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            final int totalFrames = hurtFiles.length;
            Image[] frames = new Image[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                frames[i] = new Image("file:" + hurtFiles[i].getAbsolutePath());
            }
            computerMascot.setImage(frames[0]);
            computerCurrentFrame = 0;
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(HURT_FRAME_DURATION_MS), e -> {
                computerCurrentFrame = (computerCurrentFrame + 1) % totalFrames;
                computerMascot.setImage(frames[computerCurrentFrame]);
            }));
            timeline.setCycleCount(totalFrames);
            timeline.setOnFinished(e -> {
                startComputerIdleAnimation();
                if (onFinish != null) {
                    onFinish.run();
                }
            });
            timeline.play();
        }
    }
    
    /**
     * Reduces the computer's health by a specified amount and updates its progress bar.
     *
     * @param amount the amount to reduce health by (0 to 1).
     */
    private static void reduceComputerHealth(double amount) {
        computerHealth -= amount;
        if (computerHealth < 0) computerHealth = 0;
        Platform.runLater(() -> {
            computerHealthBar.setProgress(computerHealth);
            updateComputerHealthBarStyle();
        });
    }
    
    /**
     * Updates the computer health bar style based on the current health.
     */
    private static void updateComputerHealthBarStyle() {
        if (computerHealth >= 0.5) {
            computerHealthBar.setStyle("-fx-accent: green;");
        } else if (computerHealth >= 0.3) {
            computerHealthBar.setStyle("-fx-accent: yellow;");
        } else {
            computerHealthBar.setStyle("-fx-accent: red;");
        }
    }
    
    // -------------------- Player Health Bar Methods --------------------
    
    /**
     * Reduces the player's health by a specified amount and updates the health bar.
     *
     * @param amount the amount to reduce health by (0 to 1).
     */
    private static void reduceHealth(double amount) {
        health -= amount;
        if (health < 0) health = 0;
        Platform.runLater(() -> {
            healthBar.setProgress(health);
            updateHealthBarStyle();
        });
    }
    
    /**
     * Updates the player's health bar style based on the current health.
     */
    private static void updateHealthBarStyle() {
        if (health >= 0.5) {
            healthBar.setStyle("-fx-accent: green;");
        } else if (health >= 0.3) {
            healthBar.setStyle("-fx-accent: yellow;");
        } else {
            healthBar.setStyle("-fx-accent: red;");
        }
    }
    
    // -------------------- Layout, Timer, and Reset Methods --------------------
    
    /**
     * Creates and returns the root node for the quiz scene.
     * Sets up the UI including the question, answer options, timer, score, health bars, mascots, and hint.
     *
     * @return the Parent node representing the quiz scene.
     */
    public static Parent createRoot() { 
        String selectedComputerCharacter = PreferencesManager.getSelectedComputer();
        // Reset game if difficulty has changed.
        if (difficultyChanged) {
            resetGame();
            difficultyChanged = false;
            isResettingForDifficulty = true;
            health = 1.0;
            computerHealth = 1.0;
        }
        
        // If no questions exist and not resetting, end the quiz.
        if (!isResettingForDifficulty && (questions == null || questions.isEmpty())) {
            if (questions != null && questions.isEmpty()) {
                endQuiz();
                return null;
            }
            resetGame();
            health = 1.0;
            computerHealth = 1.0;
        }
        
        if (isResettingForDifficulty) {
            isResettingForDifficulty = false;
        }    
        
        // Get a random question.
        Question currentQuestion = getRandomQuestion();
        System.out.printf("No of questions in createRoot: %s%n", currentQuestion);
        if (currentQuestion.getOptions().length == 0) {
            return new Pane(); // Delegate to CodeChallengeScene.
        }
        
        // Build quiz UI.
        VBox quizLayout = new VBox(20);
        quizLayout.setAlignment(Pos.CENTER);
        quizLayout.setPadding(new Insets(20));
        quizLayout.setStyle("-fx-background-color: transparent;");
        
        Label timerLabel = new Label("Time: " + timeRemaining + "s");
        timerLabelRef = timerLabel;
        Label scoreLabel = new Label("Score: " + score);
        Label feedbackLabel = new Label();
        Label questionLabel = new Label(currentQuestion.getQuestionText());
        
        VBox optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        String[] options = currentQuestion.getOptions();
        for (int i = 0; i < options.length; i++) {
            Button optionButton = createAnimatedButton(options[i]);
            final int index = i;
            optionButton.setOnAction(e -> {
                // Disable all options.
                for (Node node : optionsBox.getChildren()) {
                    if (node instanceof Button) {
                        node.setDisable(true);
                    }
                }
                feedbackLabel.setText("");
                if (index == currentQuestion.getCorrectIndex()) {
                    addScore(10);
                    feedbackLabel.setText("✅ Correct! Great job!");
                    AudioManager.playApplause();
                    // On correct: animate attack, reduce computer health, update score, and continue.
                    showAttackAnimation(() -> {
                        reduceComputerHealth(0.1);
                        scoreLabel.setText("Score: " + score);
                        updateTimerLabel();
                        SceneManager.showQuizScene();
                    });
                    showComputerHurtAnimation(null);
                } else {
                    feedbackLabel.setText("❌ Wrong! Better luck next time!");
                    AudioManager.playWrong();
                    reduceHealth(0.1);
                    // Use a PauseTransition with a longer delay for Frost Guardian.
                    PauseTransition delay;
                    if (PreferencesManager.getSelectedComputer().equals("Frost Guardian")) {
                        delay = new PauseTransition(Duration.millis(2400));
                    } else if (PreferencesManager.getSelectedComputer().equals("Bringer of Death")){
                        delay = new PauseTransition(Duration.millis(1300));
                    }
                    else {
                        delay = new PauseTransition(Duration.millis(1800));
                    }
                    delay.setOnFinished(ev  -> showHurtAnimation(() -> {
                        scoreLabel.setText("Score: " + score);
                        updateTimerLabel();
                        SceneManager.showQuizScene();
                    }));
                    delay.play();                    
                    showComputerAttackAnimation(null);
                    if (wrongAnswers == null) {
                        wrongAnswers = new ArrayList<>();
                    }
                    wrongAnswers.add(new WrongAnswerRecord(
                        currentQuestion.getQuestionText(),
                        options[index],
                        currentQuestion.getOptions()[currentQuestion.getCorrectIndex()],
                        currentQuestion.getHint(),
                        currentQuestion.getKeywords(),
                        currentQuestion.getExplanation()
                    ));
                }
            });
            optionsBox.getChildren().add(optionButton);
        }
        
        quizLayout.getChildren().addAll(timerLabel, scoreLabel, questionLabel, optionsBox, feedbackLabel);
        updateTimerLabel();
        if (quizTimer == null || !quizTimer.getStatus().equals(Timeline.Status.RUNNING)) {
            startTimer(timerLabel);
        }
        
        // Build container.
        StackPane root = new StackPane();
        root.getChildren().add(quizLayout);
        
        // Player health bar.
        healthBar = new ProgressBar(health);
        healthBar.setPrefWidth(300);
        updateHealthBarStyle();
        StackPane.setAlignment(healthBar, Pos.TOP_LEFT);
        StackPane.setMargin(healthBar, new Insets(10, 0, 0, 10));
        root.getChildren().add(healthBar);
        
        // Computer health bar.
        computerHealthBar = new ProgressBar(computerHealth);
        computerHealthBar.setPrefWidth(300);
        updateComputerHealthBarStyle();
        StackPane.setAlignment(computerHealthBar, Pos.TOP_RIGHT);
        StackPane.setMargin(computerHealthBar, new Insets(10, 10, 0, 0));
        root.getChildren().add(computerHealthBar);
        
        // Player mascot.
        quizMascot = new ImageView();
        quizMascot.setFitWidth(64);
        quizMascot.setFitHeight(64);
        quizMascot.setViewport(new Rectangle2D(0, 0, 64, 64));
        StackPane.setAlignment(quizMascot, Pos.BOTTOM_CENTER);
        quizMascot.setTranslateX(-40);
        StackPane.setMargin(quizMascot, new Insets(0, 0, 10, 0));
        root.getChildren().add(quizMascot);
        startWalkingAnimation();
        
        // Computer mascot.
        double computerframeIndex = 0;
        double scaleFactor = 2.0;
        double frameWidth = 288 * scaleFactor;
        double frameHeight = 166 * scaleFactor;
        computerMascot = new ImageView();
        computerMascot.setFitWidth(frameWidth);
        computerMascot.setFitHeight(frameHeight);
        computerMascot.setViewport(new Rectangle2D(computerframeIndex * 288, 0, 288, 166));
        StackPane.setAlignment(computerMascot, Pos.BOTTOM_CENTER);
        // Determine vertical offset based on the selected computer character.
        double verticalOffset = 0;
        System.out.println(selectedComputerCharacter);
        switch (selectedComputerCharacter) {
            case "Demon King":
                // verticalOffset = 0; // baseline position
                computerMascot.setTranslateX(150);
                // Apply the vertical offset (do not reset afterwards).
                computerMascot.setTranslateY(0);
                // (Removed extra setTranslateY(0) that was overriding the offset)
                StackPane.setMargin(computerMascot, new Insets(0, 0, 10, 0));                
                break;
            case "Frost Guardian":
                // verticalOffset = 20; // lower by 20 pixels
                computerMascot.setTranslateX(220);
                // Apply the vertical offset (do not reset afterwards).
                computerMascot.setTranslateY(20);
                // (Removed extra setTranslateY(0) that was overriding the offset)
                StackPane.setMargin(computerMascot, new Insets(0, 0, -100, 0));                   
                System.out.println("Frost Guardian offset");
                break;
            case "Bringer of Death":
                computerMascot.setTranslateX(150);
                // verticalOffset = 15; // adjust as needed
                computerMascot.setTranslateY(20);
                // (Removed extra setTranslateY(0) that was overriding the offset)
                StackPane.setMargin(computerMascot, new Insets(0, 0, -100, 0));       
                break;
            default:
                verticalOffset = 0;
                break;
        }

        root.getChildren().add(computerMascot);
        startComputerIdleAnimation();
        
        // Hint icon.
        // Image bulbImage = new Image(QuizScene.class.getResourceAsStream("/assets/images/bulb.png"));
        // Load image from baseDir
        // File imageFile = new File(BASE_DIR, "assets/images/bulb.png");
        // Image bulbImage = new Image(new FileInputStream(imageFile));
        String bulbPath = "file:" + BASE_DIR + "/assets/images/bulb.png";
        Image bulbImage = new Image(bulbPath);
        ImageView hintIcon = new ImageView(bulbImage);
        hintIcon.setFitWidth(40);
        hintIcon.setFitHeight(40);
        hintIcon.setOpacity(0.8);
        StackPane.setAlignment(hintIcon, Pos.TOP_RIGHT);
        hintIcon.setTranslateX(-20);
        hintIcon.setTranslateY(20);
        hintIcon.setStyle("-fx-background-color: transparent;");
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1), hintIcon);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        hintIcon.setOnMouseEntered(e -> {
            hintIcon.setOpacity(1.0);
            if (currentHintPopup == null || !currentHintPopup.isShowing()) {
                showHintPopup(currentQuestion.getHint(), hintIcon);
            }
        });
        hintIcon.setOnMouseExited(e -> {
            hintIcon.setOpacity(0.8);
            if (currentHintPopup != null && currentHintPopup.isShowing()) {
                currentHintPopup.hide();
            }
        });
        root.getChildren().add(hintIcon);
        
        // Mark root so that SceneManager does not add the persistent walking mascot.
        root.getStyleClass().add("no-mascot");
        
        return root;
    }
    
    /**
     * Creates an animated button with a scale transition effect.
     *
     * @param text the button text.
     * @return an animated Button.
     */
    private static Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-color: #3498db; -fx-text-fill: white; " +
            "-fx-background-radius: 10px;"
        );
    
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);
    
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
    
        button.setOnMouseEntered(e -> scaleUp.playFromStart());
        button.setOnMouseExited(e -> scaleDown.playFromStart());
    
        return button;
    }
    
    /**
     * Displays a popup with a hint near the specified anchor node.
     *
     * @param hintText   the hint text to display.
     * @param anchorNode the node to anchor the popup near.
     */
    private static void showHintPopup(String hintText, Node anchorNode) {
        if (hintText == null || hintText.isEmpty()) {
            hintText = "No hint available for this question.";
        }
        Popup popup = new Popup();
        currentHintPopup = popup;
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-border-color: gray;");
        box.setAlignment(Pos.CENTER);
        Label hintLabel = new Label(hintText);
        hintLabel.setWrapText(true);
        hintLabel.setStyle("-fx-font-size: 14; -fx-text-fill: black;");
        box.getChildren().add(hintLabel);
        popup.getContent().add(box);
        box.setOnMouseExited(e -> popup.hide());
        Point2D screenPos = anchorNode.localToScreen(0, anchorNode.getLayoutBounds().getHeight());
        popup.show(anchorNode.getScene().getWindow(), screenPos.getX(), screenPos.getY());
    }
    
    /**
     * Starts the quiz timer that counts down every second.
     *
     * @param timerLabel the Label to display the remaining time.
     */
    private static void startTimer(Label timerLabel) {
        if (timeRemaining < 0) {
            resetTimer();
        }
        quizEnded = false;
        if (quizTimer != null) {
            quizTimer.stop();
            quizTimer = null;
        }
        timerLabelRef = timerLabel;
        quizTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (quizEnded) return;
            timeRemaining--;
            Platform.runLater(() -> {
                if (timerLabelRef != null) {
                    timerLabelRef.setText("Time: " + timeRemaining + "s");
                }
            });
            if (timeRemaining < 0) {
                quizTimer.stop();
                Platform.runLater(() -> endQuiz());
            }
        }));
        quizTimer.setCycleCount(Timeline.INDEFINITE);
        quizTimer.play();
        Platform.runLater(() -> timerLabel.setText("Time: " + timeRemaining + "s"));
    }
    
    /**
     * Updates the timer label text on the UI thread.
     */
    private static void updateTimerLabel() {
        Platform.runLater(() -> {
            if (timerLabelRef != null) {
                timerLabelRef.setText("Time: " + timeRemaining + "s");
            }
        });
    }
    
    /**
     * Resets the timer to the initial time from preferences.
     */
    public static void resetTimer() {
        timeRemaining = PreferencesManager.getInitialTime();
    }
    
    /**
     * Ends the quiz by stopping the timer, recording the final score,
     * and displaying an end-of-quiz screen with a score summary and wrong answer records.
     */
    public static void endQuiz() {
        if (quizEnded) return;
        quizEnded = true;
        if (quizTimer != null) {
            quizTimer.stop();   
        }
        timeRemaining = 0;
        System.out.println("Quiz ended. Final Score: " + score);
        
        // Record unanswered questions as wrong answers.
        for (Question q : questions) {
            if (q.getOptions().length == 0) {
                wrongAnswers.add(new WrongAnswerRecord(
                    q.getQuestionText(),
                    "Not Attempted",
                    "Expected Code Solution",
                    q.getHint(),
                    q.getKeywords(),
                    q.getExplanation()
                ));
            } else {
                wrongAnswers.add(new WrongAnswerRecord(
                    q.getQuestionText(),
                    "Not Answered",
                    q.getOptions()[q.getCorrectIndex()],
                    q.getHint(),
                    q.getKeywords(),
                    q.getExplanation()
                ));
            }
        }
        
        VBox leftPane = new VBox(20);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(20));
        leftPane.setPrefWidth(300);
        
        Label scoreSummary = new Label("Time's up!\nYour Score: " + score + "\nDifficulty: " + PreferencesManager.getDifficultyString());
        Label enterNameLabel = new Label("Enter your name:");
        TextField nameField = new TextField();
        // auto‑fill username from session
        nameField.setText(SessionManager.getUsername());
        Button saveScoreBtn = createAnimatedButton("Save Score");
        Button playAgainBtn = createAnimatedButton("Play Again");
        Button mainMenuBtn = createAnimatedButton("Main Menu");
        Button quitBtn = createAnimatedButton("Quit");
        
        saveScoreBtn.setOnAction(e -> {
            String name = nameField.getText();
            if (name != null && !name.trim().isEmpty()) {
                LeaderboardService.pushLeaderboardDataAsync(name, score, PreferencesManager.getDifficultyString(), "Classic");
                saveScoreBtn.setDisable(true);
            }
        });
        
        playAgainBtn.setOnAction(e -> {
            if (quizTimer != null) quizTimer.stop();
            resetGame();
            SceneManager.showQuizScene();
        });
        
        mainMenuBtn.setOnAction(e -> {
            if (quizTimer != null) quizTimer.stop();
            questions = null;
            SceneManager.showMainMenu();
        });
        
        quitBtn.setOnAction(e -> System.exit(0));
        
        leftPane.getChildren().addAll(scoreSummary, enterNameLabel, nameField, saveScoreBtn, playAgainBtn, mainMenuBtn, quitBtn);
        Parent revisionPane = createWrongAnswersPane();
        
        BorderPane endLayout = new BorderPane();
        endLayout.setLeft(leftPane);
        endLayout.setCenter(revisionPane);
        BorderPane.setMargin(leftPane, new Insets(10));
        BorderPane.setMargin(revisionPane, new Insets(10));
        
        SceneManager.setRoot(new StackPane(endLayout));
    }
    
    /**
     * Stops the quiz timer and resets the remaining time.
     */
    public static void stopTimer() {
        if (quizTimer != null) {
            quizTimer.stop();
        }
        timeRemaining = 0;
    }
    
    /**
     * Resets the game by stopping the timer, resetting score and health bars, and loading questions.
     * Also shuffles and limits the number of questions based on user preferences and available credits.
     */
    public static void resetGame() {
        stopTimer();
        resetTimer();
        score = 0;
        // Reset health bars.
        health = 1.0; 
        computerHealth = 1.0;
        if (healthBar != null) {
            healthBar.setProgress(health);
        }
        if (computerHealthBar != null) {
            computerHealthBar.setProgress(computerHealth);
        }        
        List<Question> cached = QuestionService.loadQuestionsWithCache();
        List<Question> loadedQuestions;
        if (cached.isEmpty()) {
            System.out.println("No questions loaded. Falling back to hardcoded questions.");
            loadedQuestions = new ArrayList<>(QuestionService.initializeHardcodedQuestions());
        } else {
            loadedQuestions = new ArrayList<>(cached);
        }
        int remainingCredits = JDoodleClient.getRemainingCredits();
        System.out.println("Remaining JDoodle Credits: " + remainingCredits);
        if (remainingCredits <= 0) {
            System.out.println("You have used all credits for the day.");
            List<Question> mcqQuestions = new ArrayList<>();
            for (Question q : loadedQuestions) {
                if (q.getOptions().length != 0) {
                    mcqQuestions.add(q);
                }
            }
            loadedQuestions = new ArrayList<>(mcqQuestions);
        } else {
            if (!cached.isEmpty()) {
                int MAX_NUM_CACHED_QUESTIONS = PreferencesManager.getDifficultyQuestionBankNumbers();
                if (loadedQuestions.size() > MAX_NUM_CACHED_QUESTIONS) {
                    loadedQuestions = new ArrayList<>(loadedQuestions.subList(0, MAX_NUM_CACHED_QUESTIONS));
                }
                List<Question> codingChallenges = new ArrayList<>();
                codingChallenges.add(new Question("Write a function named 'sum' that takes two integers and returns their sum."));
                codingChallenges.add(new Question("Write a function named 'reverseString' that returns the reverse of a given string."));
                Collections.shuffle(codingChallenges, new Random());
                int codingCount = PreferencesManager.getDifficultyJavaPythonCodingNumbers();
                for (int i = 0; i < codingCount && i < codingChallenges.size(); i++) {
                    loadedQuestions.add(codingChallenges.get(i));
                }
            }
        }
        Collections.shuffle(loadedQuestions, new Random());
        System.out.println("Number of questions: " + loadedQuestions.size());
        if (loadedQuestions.size() > MAX_QUESTIONS) {
            loadedQuestions = new ArrayList<>(loadedQuestions.subList(0, MAX_QUESTIONS));
        }
        questions = loadedQuestions;
        System.out.println("Number of questions towards end of resetGame: " + questions.size());
        wrongAnswers = new ArrayList<>();
    }
    
    /**
     * Retrieves a random question from the list.
     * If the question is a coding challenge (no options), transitions to the CodeChallengeScene.
     *
     * @return the selected Question.
     */
    private static Question getRandomQuestion() {
        int QuestionBankNumbers = PreferencesManager.getDifficultyQuestionBankNumbers();
        if (questions == null || questions.isEmpty()) {
            questions = new ArrayList<>(QuestionService.loadQuestionsWithCache());
            if (questions.size() > QuestionBankNumbers) {
                questions = new ArrayList<>(questions.subList(0, QuestionBankNumbers));
            }
            System.out.println("Total questions loaded for session in getRandomQuesitons: " + questions.size());
            Collections.shuffle(questions, new Random());
        }
        Question q = questions.remove(0);
        if (q.getOptions().length == 0) {
            Platform.runLater(() -> SceneManager.setRoot(CodeChallengeScene.createRoot(q)));
        }
        return q;
    }
    
    /**
     * Returns the remaining time.
     *
     * @return the remaining time in seconds.
     */
    public static int getTimeRemaining() {
        return timeRemaining;
    }
    
    /**
     * Sets the remaining time.
     *
     * @param t the new remaining time in seconds.
     */
    public static void setTimeRemaining(int t) {
        timeRemaining = t;
    }
    
    /**
     * Returns the current score.
     *
     * @return the score.
     */
    public static int getScore() {
        return score;
    }
    
    /**
     * Adds points to the current score.
     *
     * @param points the number of points to add.
     */
    public static void addScore(int points) {
        score += points;
        if (score < 0) score = 0;
    }
    
    /**
     * Records a coding challenge attempt by adding a wrong answer record.
     *
     * @param question    the coding challenge question.
     * @param code        the submitted code.
     * @param explanation the explanation of the expected solution.
     * @param skipped     whether the challenge was skipped.
     * @param language    the programming language used.
     */
    public static void recordCodingChallenge(Question question, String code, String explanation, boolean skipped, String language) {
        String recordedCode = code.isEmpty() ? "No answer provided" : code;
        String correctAnswer = "";
        if (language.equalsIgnoreCase("java")) {
            if (question.getQuestionText().toLowerCase().contains("sum")) {
                correctAnswer = "Option 1: public static int sum(int a, int b) { return a + b; }\n" +
                        "Option 2: public static int sum(int a, int b) { return Math.addExact(a, b); }";
            } else if (question.getQuestionText().toLowerCase().contains("reverse")) {
                correctAnswer = "Option 1: public static String reverseString(String input) { return new StringBuilder(input).reverse().toString(); }\n" +
                        "Option 2: public static String reverseString(String input) {\n" +
                        "    char[] chars = input.toCharArray();\n" +
                        "    for (int i = 0, j = chars.length - 1; i < j; i++, j--) {\n" +
                        "        char temp = chars[i];\n" +
                        "        chars[i] = chars[j];\n" +
                        "        chars[j] = temp;\n" +
                        "    }\n" +
                        "    return new String(chars);\n" +
                        "}";
            }
        } else if (language.equalsIgnoreCase("python")) {
            if (question.getQuestionText().toLowerCase().contains("sum")) {
                correctAnswer = "Option 1: def sum(a, b):\n    return a + b\n" +
                        "Option 2: def sum(a, b):\n    return a.__add__(b)";
            } else if (question.getQuestionText().toLowerCase().contains("reverse")) {
                correctAnswer = "Option 1: def reverseString(input):\n    return input[::-1]\n" +
                        "Option 2: def reverseString(input):\n    return ''.join(reversed(input))";
            }
        }
        if (wrongAnswers == null) {
            wrongAnswers = new ArrayList<>();
        }
        wrongAnswers.add(new WrongAnswerRecord(
                "Coding Challenge (" + language + "): " + question.getQuestionText(),
                recordedCode,
                correctAnswer,
                "",
                "",
                explanation
        ));
    }
    
    /**
     * Applies a new difficulty setting by resetting the game state.
     */
    public static void applyNewDifficulty() {
        System.out.println("Applying new difficulty... Resetting game.");
        questions = null;  // Clear current questions.
        resetGame();
        difficultyChanged = true;
    }
}
