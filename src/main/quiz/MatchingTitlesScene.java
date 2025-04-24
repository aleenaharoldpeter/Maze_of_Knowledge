package src.main.quiz;

import src.main.utils.AudioManager;
import src.main.utils.SceneManager;
import src.main.utils.PreferencesManager;
import src.main.leaderboard.LeaderboardService;
import src.main.auth.SessionManager;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MatchingTitlesScene {
    // Define card types for the matching game.
    private enum CardType { QUESTION, ANSWER, DISTRACTOR }

    // Track the currently flipped card (if any) and a flag to block further input.
    private static Card selectedCard = null;
    private static boolean busy = false;

    // Timer components.
    private static Timeline matchingTimer;
    private static int timeRemaining;
    private static Label timerLabel;

    // Matching progress and score variables.
    private static int matchedCount;  // Number of correctly matched Q–A pairs.
    private static int totalPairs;    // Total number of Q–A pairs.
    private static int score;         // Calculated score (e.g., matchedCount * 10).

    // For revision: store each question's info keyed by a unique ID.
    private static Map<Integer, QnAInfo> questionMap;

    /**
     * Creates the root node for the matching titles scene.
     * This method sets up the game grid with cards, timer, and a back button.
     *
     * @return the Parent node containing the complete matching titles UI.
     */
    public static Parent createRoot() {
        // Initialize game variables.
        matchedCount = 0;
        score = 0;
        questionMap = new HashMap<>();

        // Load questions using your existing QuestionService.
        List<Question> questions = QuestionService.loadQuestionsWithCache();
        totalPairs = questions.size(); // One Q–A pair per question.

        // Build a list of cards (question, answer, and distractor cards).
        List<Card> cards = new ArrayList<>();
        List<Card> distractorCards = new ArrayList<>();
        Random rand = new Random();

        // For each question, create corresponding cards.
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int id = i + 1;  // Unique ID for the Q–A pair.

            // Store question info for revision.
            QnAInfo info = new QnAInfo(q.getQuestionText(), q.getOptions()[q.getCorrectIndex()]);
            questionMap.put(id, info);

            // Create QUESTION and ANSWER cards.
            // String qTitle = extractTitle(q.getQuestionText());
            String fullQuestion = q.getQuestionText();
            String correctAnswer = q.getOptions()[q.getCorrectIndex()];
            // cards.add(new Card(CardType.QUESTION, id, qTitle));
            cards.add(new Card(CardType.QUESTION, id, fullQuestion));
            cards.add(new Card(CardType.ANSWER, id, correctAnswer));

            // Create one distractor card using one wrong option (if available).
            List<String> wrongOptions = new ArrayList<>();
            for (int j = 0; j < q.getOptions().length; j++) {
                if (j != q.getCorrectIndex()) {
                    wrongOptions.add(q.getOptions()[j]);
                }
            }
            if (!wrongOptions.isEmpty()) {
                String wrong = wrongOptions.get(rand.nextInt(wrongOptions.size()));
                distractorCards.add(new Card(CardType.DISTRACTOR, 0, wrong));
            }
        }
        // Ensure even number of distractor cards.
        if (distractorCards.size() % 2 != 0 && !distractorCards.isEmpty()) {
            distractorCards.remove(rand.nextInt(distractorCards.size()));
        }
        // Add distractor cards to main card list and shuffle.
        cards.addAll(distractorCards);
        Collections.shuffle(cards);

        // Layout: Create a grid to display cards in a grid layout.
        GridPane grid = new GridPane();
        grid.setHgap(10);  // Horizontal spacing.
        grid.setVgap(10);  // Vertical spacing.
        grid.setPadding(new Insets(20));
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);

        // Define the number of columns for card display.
        int columns = 6;
        for (int i = 0; i < cards.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            grid.add(cards.get(i).getView(), col, row);
        }

        // Timer setup: Get initial time from preferences.
        timeRemaining = PreferencesManager.getInitialTime();
        timerLabel = new Label("Time: " + timeRemaining + "s");
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        startTimer();

        // Back button to return to main menu.
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(e -> {
            stopTimer();
            SceneManager.showMainMenu();
        });

        // Assemble the main layout.
        VBox root = new VBox(20, timerLabel, grid, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        return root;
    }

    /**
     * Starts the countdown timer for the matching game.
     * The timer updates every second and ends the game when time runs out.
     */
    private static void startTimer() {
        stopTimer();  // Ensure no previous timer is running.
        matchingTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            timerLabel.setText("Time: " + timeRemaining + "s");
            if (timeRemaining <= 0) {
                matchingTimer.stop();
                endGame(false);  // End game due to time running out.
            }
        }));
        matchingTimer.setCycleCount(Timeline.INDEFINITE);
        matchingTimer.play();
    }

    /**
     * Stops the matching game timer if it is running.
     */
    private static void stopTimer() {
        if (matchingTimer != null) {
            matchingTimer.stop();
        }
    }

    /**
     * Ends the game.
     * Calculates the score and shows the end screen with options to review, play again, or return to main menu.
     *
     * @param completedAll true if all pairs were matched; false if time ran out.
     */
    private static void endGame(boolean completedAll) {
        stopTimer();
        score = matchedCount * 10;

        // Create left pane for name entry and navigation buttons (similar to quiz end screen).
        VBox leftPane = new VBox(15);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(20));

        Label resultLabel;
        if (completedAll) {
            resultLabel = new Label("Congratulations! You matched all pairs!");
        } else {
            resultLabel = new Label("Time's up! You matched " + matchedCount + " out of " + totalPairs + " pairs.");
        }
        resultLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        Label namePrompt = new Label("Enter your name:");
        namePrompt.setStyle("-fx-text-fill: white;");
        TextField nameField = new TextField();
        // auto‑fill username from session
        nameField.setText(SessionManager.getUsername());
        Button saveScoreBtn = new Button("Save Score");
        saveScoreBtn.setOnAction(e -> {
            String name = nameField.getText();
            if (name != null && !name.trim().isEmpty()) {
                LeaderboardService.pushLeaderboardDataAsync(name, score, PreferencesManager.getDifficultyString(), "Matching");
                saveScoreBtn.setDisable(true);
            }
        });

        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setOnAction(e -> {
            stopTimer();
            SceneManager.showMatchingTitlesScene();
        });
        Button mainMenuBtn = new Button("Main Menu");
        mainMenuBtn.setOnAction(e -> {
            stopTimer();
            SceneManager.showMainMenu();
        });
        Button quitBtn = new Button("Quit");
        quitBtn.setOnAction(e -> System.exit(0));

        leftPane.getChildren().addAll(resultLabel, namePrompt, nameField, saveScoreBtn, playAgainBtn, mainMenuBtn, quitBtn);

        // Create the revision pane on the right side to review unmatched Q–A pairs.
        ScrollPane revisionPane = createRevisionPane(completedAll);

        // Assemble the end screen layout using a BorderPane.
        BorderPane endLayout = new BorderPane();
        endLayout.setLeft(leftPane);
        endLayout.setCenter(revisionPane);
        BorderPane.setMargin(leftPane, new Insets(10));
        BorderPane.setMargin(revisionPane, new Insets(10));

        // Set the new scene root to the end layout wrapped in a StackPane.
        SceneManager.setRoot(new StackPane(endLayout));
    }

    /**
     * Creates a revision pane to review unmatched questions.
     *
     * @param completedAll true if all pairs were matched; false otherwise.
     * @return a ScrollPane containing the revision information.
     */
    private static ScrollPane createRevisionPane(boolean completedAll) {
        VBox revisionBox = new VBox(12);
        revisionBox.setAlignment(Pos.TOP_LEFT);
        revisionBox.setPadding(new Insets(10));
        revisionBox.setStyle("-fx-background-color: transparent;");

        // Header for the revision pane.
        Label header = new Label("Review Unmatched Questions:");
        header.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        revisionBox.getChildren().add(header);

        if (completedAll) {
            Label noRevision = new Label("No unmatched pairs!");
            noRevision.setStyle("-fx-text-fill: white;");
            revisionBox.getChildren().add(noRevision);
        } else {
            List<Integer> unmatchedIDs = new ArrayList<>();
            // Identify unmatched questions based on QnAInfo.
            for (int id : questionMap.keySet()) {
                QnAInfo info = questionMap.get(id);
                if (!info.isMatched) {
                    unmatchedIDs.add(id);
                }
            }
            if (unmatchedIDs.isEmpty()) {
                Label none = new Label("No unmatched pairs!");
                none.setStyle("-fx-text-fill: white;");
                revisionBox.getChildren().add(none);
            } else {
                // For each unmatched question, create a record for revision.
                for (int id : unmatchedIDs) {
                    QnAInfo info = questionMap.get(id);
                    VBox recordBox = new VBox(5);
                    recordBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 5;");
                    Label questionLabel = new Label("Q: " + info.questionText);
                    questionLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    Label answerLabel = new Label("A: " + info.correctAnswer);
                    answerLabel.setStyle("-fx-text-fill: yellow;");
                    recordBox.getChildren().addAll(questionLabel, answerLabel);
                    revisionBox.getChildren().add(recordBox);
                }
            }
        }

        ScrollPane scrollPane = new ScrollPane(revisionBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        // Hide scrollbars.
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMaxHeight(300);
        return scrollPane;
    }

    /**
     * Checks for a match between the currently flipped card and the newly flipped card.
     * If a valid match is found, updates the matched count and marks the question as matched.
     * If not, flips the cards back after a short delay.
     *
     * @param card the newly flipped card.
     */
    private static void checkMatch(Card card) {
        if (selectedCard == null) {
            // No card was previously selected; store the current card.
            selectedCard = card;
        } else {
            busy = true;
            boolean match = false;
            // Valid match: one card is QUESTION and one is ANSWER, same id != 0.
            if (((selectedCard.type == CardType.QUESTION && card.type == CardType.ANSWER) ||
                 (selectedCard.type == CardType.ANSWER && card.type == CardType.QUESTION))
                && selectedCard.id == card.id && selectedCard.id != 0) {
                match = true;
            }
            // Distractor cards never match.
            if (selectedCard.type == CardType.DISTRACTOR || card.type == CardType.DISTRACTOR) {
                match = false;
            }

            if (match) {
                // Valid match found.
                matchedCount++;
                // Mark the corresponding question as matched.
                if (questionMap.containsKey(card.id)) {
                    questionMap.get(card.id).isMatched = true;
                }
                selectedCard = null;
                busy = false;
                // If all pairs are matched, end the game.
                if (matchedCount >= totalPairs) {
                    endGame(true);
                }
            } else {
                // Not a match: wait for a moment before flipping cards back.
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                final Card first = selectedCard;
                final Card second = card;
                pause.setOnFinished(e -> {
                    first.hide();
                    second.hide();
                    selectedCard = null;
                    busy = false;
                });
                pause.play();
            }
        }
    }

    /**
     * Utility method to extract a concise title from a long question text.
     * If the question text is longer than 30 characters, returns the first 30 characters followed by ellipsis.
     *
     * @param questionText the full question text.
     * @return a concise title.
     */
    private static String extractTitle(String questionText) {
        if (questionText.length() > 30) {
            return questionText.substring(0, 30) + "...";
        }
        return questionText;
    }

    // ===== Inner Classes =====

    /**
     * QnAInfo stores the full question text, correct answer, and matched state.
     */
    private static class QnAInfo {
        String questionText;
        String correctAnswer;
        boolean isMatched;
        QnAInfo(String questionText, String correctAnswer) {
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
            this.isMatched = false;
        }
    }

    /**
     * Card represents one tile in the matching game.
     */
    private static class Card {
        CardType type;
        int id;         // For QUESTION/ANSWER, id > 0; for distractors, id = 0.
        String text;    // The text displayed on the card.
        StackPane view; // The graphical view of the card.
        Label label;    // Label used to display the card's text.

        Card(CardType type, int id, String text) {
            this.type = type;
            this.id = id;
            this.text = text;
            createView();
        }

        /**
         * Creates the visual representation of the card.
         * Sets up the layout, styling, and mouse click event to flip the card.
         */
        // private void createView() {
        //     view = new StackPane();
        //     // Set preferred and maximum size for the card.
        //     view.setPrefSize(300, 180);
        //     view.setMaxSize(300, 180);
        //     // Styling for the card.
        //     view.setStyle("-fx-background-color: #3498db; -fx-border-color: white; -fx-border-width: 3;");

        //     label = new Label("");
        //     label.setFont(Font.font("Verdana", 14));
        //     label.setStyle("-fx-text-fill: white;");
        //     label.setWrapText(true);
        //     label.setMaxWidth(180);
        //     view.getChildren().add(label);

        //     // When the card is clicked, flip it unless already busy or already flipped.
        //     view.setOnMouseClicked(e -> {
        //         if (busy || !label.getText().isEmpty()) return;
        //         flip();
        //     });
        // }
        private void createView() {
            view = new StackPane();
            view.setPrefSize(300, 180);
            view.setMaxSize(300, 180);
            view.setStyle("-fx-background-color: #3498db; -fx-border-color: white; -fx-border-width: 3;");

            // Optional: Add padding if desired
            view.setPadding(new Insets(10)); // 10px padding on all sides

            label = new Label("");
            label.setFont(Font.font("Verdana", 14));
            label.setStyle("-fx-text-fill: white;");
            label.setWrapText(true);
            
            // allow label to use nearly the full card width:
            label.setMaxWidth(300 - 20);  // 20px horizontal padding
            label.setMaxHeight(180 - 20); // if you want vertical wrapping too

            view.getChildren().add(label);

            view.setOnMouseClicked(e -> {
                if (busy || !label.getText().isEmpty()) return;
                flip();
            });
        }
        /**
         * Returns the view (StackPane) of the card.
         *
         * @return the card's view.
         */
        public StackPane getView() {
            return view;
        }

        /**
         * Flips the card by playing a tile flip sound, setting its text,
         * and applying a fade transition. Then checks for a match.
         */
        public void flip() {
            AudioManager.playTileFlip();
            label.setText(text);
            FadeTransition ft = new FadeTransition(Duration.seconds(0.3), view);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            checkMatch(this);
        }

        /**
         * Hides the card's text (flips it back).
         */
        public void hide() {
            label.setText("");
        }
    }
}
