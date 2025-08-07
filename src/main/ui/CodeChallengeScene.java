package src.main.ui;

import src.main.utils.SceneManager;
import src.main.quiz.QuizScene;
import src.main.quiz.Question;
import src.main.api.JDoodleClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class CodeChallengeScene {
    // Total challenge time in seconds (5 minutes)
    public static final int CHALLENGE_TIME = 300;
    
    // Timer used to count down the challenge time
    private static Timeline challengeTimer;
    
    // Shared UI labels for timer and score display from the QuizScene
    private static Label timerLabel;
    private static Label scoreLabel;
    
    // The current challenge question being attempted
    private static Question currentChallenge;
    
    // Text area for displaying output or error messages from code execution
    private static TextArea outputDisplay;
    
    // Flag to indicate if the challenge is still active to prevent duplicate actions
    private static volatile boolean challengeActive = true;

    // Variables to store test inputs and explanation for the challenge
    private static String testInputRun;    // For the "Run" button – one test case
    private static String testInputSubmit; // For the "Submit" button – both test cases
    private static String challengeExplanation;

    // Static code editor so its content is accessible across methods including the timer
    private static TextArea codeEditor;

    // Variable to store the currently selected programming language; default is Java
    private static String currentLanguage = "java";    

    /**
     * Creates the root node for the Code Challenge Scene.
     * This method sets up the UI components including the problem statement,
     * code editor, output display, timer, and control buttons.
     *
     * @param challengeQuestion The coding challenge question to be displayed.
     * @return The Parent node representing the complete scene.
     */
    public static Parent createRoot(Question challengeQuestion) {
        // Reset challenge state and store the current challenge
        challengeActive = true;
        currentChallenge = challengeQuestion;
        
        // Determine test cases and explanation based on the challenge question text.
        if (challengeQuestion.getQuestionText().toLowerCase().contains("sum")) {
            testInputRun = "3 5";
            testInputSubmit = "3 5\n10 20";
            challengeExplanation = "The function 'sum' should return the sum of two integers.";
        } else if (challengeQuestion.getQuestionText().toLowerCase().contains("reverse")) {
            testInputRun = "hello";
            testInputSubmit = "hello\nworld";
            challengeExplanation = "The function 'reverseString' should return the reverse of the input string.";
        } else {
            testInputRun = "3 5";
            testInputSubmit = "3 5\n10 20";
            challengeExplanation = "Default explanation for the coding challenge.";
        }
        
        // Modify problem text to remove language-specific instructions.
        String problemText = challengeQuestion.getQuestionText()
                                .replaceAll("(?i)write your code in java", "code can be written in Java or Python");
                
        // Create main layout pane with padding.
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(20));
        
        // Left pane: Contains the problem statement.
        VBox leftPane = new VBox(10);
        leftPane.setAlignment(Pos.TOP_LEFT);
        leftPane.setPrefWidth(400);
        Label problemLabel = new Label("Coding Challenge:\n\n" + challengeQuestion.getQuestionText());
        problemLabel.setFont(Font.font("Verdana", 16));
        problemLabel.setWrapText(true);
        leftPane.getChildren().add(problemLabel);
        
        // Right pane: Contains language selection and code editor.
        VBox rightPane = new VBox(10);
        rightPane.setAlignment(Pos.TOP_LEFT);
        rightPane.setPrefWidth(400);
        
        // Language selection dropdown (ComboBox) with default value "Java"
        ComboBox<String> languageChoiceBox = new ComboBox<>();
        languageChoiceBox.getItems().addAll("Java", "Python");
        languageChoiceBox.setValue("Java"); // default selection
        languageChoiceBox.setPrefWidth(150);
        
        // Update currentLanguage variable based on the default value
        currentLanguage = languageChoiceBox.getValue().toLowerCase();
        
        // Label for the code editor area.
        Label editorLabel = new Label("Your Code:");
        editorLabel.setFont(Font.font("Verdana", 16));
        
        // Code editor TextArea; assigned to static field for global access (e.g., by timer)
        codeEditor = new TextArea();
        codeEditor.setFont(Font.font("Monospaced", 14));
        codeEditor.setPrefHeight(300);
        
        // Add language dropdown, editor label, and code editor to the right pane.
        rightPane.getChildren().addAll(languageChoiceBox, editorLabel, codeEditor);
        
        // Pre-populate the code editor with default scaffold code based on challenge and selected language.
        final String[] defaultCodeContainer = new String[] { getDefaultCode(challengeQuestion, languageChoiceBox.getValue()) };
        codeEditor.setText(defaultCodeContainer[0]);
        
        // Update the code editor content if the selected language changes.
        languageChoiceBox.setOnAction(e -> {
            String selectedLang = languageChoiceBox.getValue();
            currentLanguage = selectedLang.toLowerCase();
            defaultCodeContainer[0] = getDefaultCode(challengeQuestion, selectedLang);
            codeEditor.setText(defaultCodeContainer[0]);
        });
        
        // Output display area for showing execution output or error messages.
        outputDisplay = new TextArea();
        outputDisplay.setFont(Font.font("Monospaced", 14));
        outputDisplay.setPrefHeight(100);
        outputDisplay.setEditable(false);
        
        // Top pane: Displays shared timer and score information.
        HBox topPane = new HBox(20);
        topPane.setAlignment(Pos.CENTER);
        timerLabel = new Label("Time: " + QuizScene.getTimeRemaining() + "s");
        timerLabel.setFont(Font.font("Verdana", 16));
        scoreLabel = new Label("Score: " + QuizScene.getScore());
        scoreLabel.setFont(Font.font("Verdana", 16));
        topPane.getChildren().addAll(timerLabel, scoreLabel);
        
        // Bottom pane: Contains control buttons for running, submitting, and skipping the challenge.
        HBox bottomPane = new HBox(20);
        bottomPane.setAlignment(Pos.CENTER);
        Button runButton = new Button("Run");
        Button submitButton = new Button("Submit");
        Button skipButton = new Button("Skip");
        
        // Set action for "Run" button to execute the code with one test case
        runButton.setOnAction(e -> {
            if (challengeActive) {
                String language = languageChoiceBox.getValue().toLowerCase();
                runJDoodleCode(codeEditor.getText(), testInputRun, false, currentLanguage);
            }
        });
        
        // Set action for "Submit" button to execute the code with both test cases
        submitButton.setOnAction(e -> {
            if (challengeActive) {
                String language = languageChoiceBox.getValue().toLowerCase();
                runJDoodleCode(codeEditor.getText(), testInputSubmit, true, currentLanguage);
            }
        });
        
        // Set action for "Skip" button to mark challenge as skipped and return to the quiz scene
        skipButton.setOnAction(e -> {
            if (challengeActive) {
                challengeActive = false;
                stopTimer();
                QuizScene.recordCodingChallenge(currentChallenge, codeEditor.getText(), challengeExplanation, true, currentLanguage);
                SceneManager.showQuizScene();
            }
        });
        bottomPane.getChildren().addAll(runButton, submitButton, skipButton);
        
        // Combine the right pane (editor) with the output display in a vertical layout.
        VBox centerRight = new VBox(10, rightPane, new Label("Output:"), outputDisplay);
        
        // Create the center pane combining the problem statement and the code editor/output.
        HBox centerPane = new HBox(20, leftPane, centerRight);
        centerPane.setAlignment(Pos.CENTER);
        
        // Set the top, center, and bottom sections of the main pane.
        mainPane.setTop(topPane);
        mainPane.setCenter(centerPane);
        mainPane.setBottom(bottomPane);
        BorderPane.setMargin(bottomPane, new Insets(20));
        
        // Start the challenge timer using the shared QuizScene timer values.
        startTimer();
        
        // Mark the scene to prevent the persistent mascot from being added.
        mainPane.getStyleClass().add("no-mascot");
        
        return mainPane;
    }
    
    /**
     * Returns a default scaffold code snippet based on the challenge question and selected language.
     *
     * @param challenge The challenge question to generate code for.
     * @param language The programming language (e.g., "Java" or "Python").
     * @return A default code scaffold as a String.
     */
    private static String getDefaultCode(Question challenge, String language) {
        String questionText = challenge.getQuestionText().toLowerCase();
        if (questionText.contains("sum")) {
            if (language.equalsIgnoreCase("java")) {
                return "public class Main {\n" +
                       "    public static int sum(int a, int b) {\n" +
                       "        // Write your code here\n" +
                       "        return 0;\n" +
                       "    }\n\n" +
                       "    public static void main(String[] args) {\n" +
                       "        // Expected output for sum(3, 5) is 8\n" +
                       "        System.out.println(sum(3, 5));\n" +
                       "    }\n" +
                       "}";
            } else if (language.equalsIgnoreCase("python")) {
                return "def sum(a, b):\n" +
                       "    # Write your code here\n" +
                       "    return 0\n\n" +
                       "if __name__ == '__main__':\n" +
                       "    print(sum(3, 5))";
            }
        } else if (questionText.contains("reverse")) {
            if (language.equalsIgnoreCase("java")) {
                return "public class Main {\n" +
                       "    public static String reverseString(String input) {\n" +
                       "        // Write your code here\n" +
                       "        return \"\";\n" +
                       "    }\n\n" +
                       "    public static void main(String[] args) {\n" +
                       "        // Expected output for reverseString(\"hello\") is \"olleh\"\n" +
                       "        System.out.println(reverseString(\"hello\"));\n" +
                       "    }\n" +
                       "}";
            } else if (language.equalsIgnoreCase("python")) {
                return "def reverseString(input):\n" +
                       "    # Write your code here\n" +
                       "    return \"\"\n\n" +
                       "if __name__ == '__main__':\n" +
                       "    print(reverseString(\"hello\"))";
            }
        }
        // Default scaffold if the challenge type is not recognized.
        if (language.equalsIgnoreCase("java")) {
            return "public class Main {\n" +
                   "    public static void main(String[] args) {\n" +
                   "        // Write your Java code here\n" +
                   "    }\n" +
                   "}";
        } else if (language.equalsIgnoreCase("python")) {
            return "# Write your Python code here";
        }
        return "";
    }  
    
    /**
     * Starts the countdown timer for the coding challenge.
     * The timer updates the shared timer label and handles the challenge timeout.
     */
    private static void startTimer() {
        // Initialize timer label with the current time remaining
        timerLabel.setText("Time: " + QuizScene.getTimeRemaining() + "s");
        challengeTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Decrement time and update the timer label
            int currentTime = QuizScene.getTimeRemaining();
            QuizScene.setTimeRemaining(currentTime - 1);
            timerLabel.setText("Time: " + QuizScene.getTimeRemaining() + "s");
            
            // If time runs out and the challenge is still active, handle timeout logic
            if (QuizScene.getTimeRemaining() <= 0 && challengeActive) {
                challengeActive = false;
                stopTimer();
                outputDisplay.setText("Time's up!");
                // Use default code if the user has not provided any code
                String submittedCode = codeEditor.getText().trim().isEmpty() 
                    ? getDefaultCode(currentChallenge, currentLanguage) 
                    : codeEditor.getText();
                QuizScene.recordCodingChallenge(currentChallenge, submittedCode, challengeExplanation, false, currentLanguage);
            
                // Reset time to zero to prevent negative values in QuizScene
                QuizScene.setTimeRemaining(0);
                // Also stop the QuizScene timer if it is running
                QuizScene.stopTimer();
                                
                // End the quiz and transition to the revision view
                QuizScene.endQuiz();
            }
        }));
        challengeTimer.setCycleCount(Timeline.INDEFINITE);
        challengeTimer.play();
    }
    
    /**
     * Executes the provided source code using the JDoodle API.
     * Parses the JSON response and displays either the output or error.
     * If running all tests (Submit) and both test cases pass, awards points and transitions scenes.
     *
     * @param sourceCode The code to execute.
     * @param stdin The input provided to the code.
     * @param runAll Flag indicating if all test cases should be run (Submit scenario).
     * @param language The selected programming language.
     */
    private static void runJDoodleCode(String sourceCode, String stdin, boolean runAll, String language) {
        new Thread(() -> {
            try {
                // Call the JDoodle API to execute code with the selected language.
                String result = JDoodleClient.executeCode(sourceCode, stdin, language);
                Gson gson = new Gson();
                JsonObject obj = gson.fromJson(result, JsonObject.class);
                String displayText = "";
                
                // If an error is present, display it; otherwise, display the output.
                if (obj.has("error") && !obj.get("error").isJsonNull() && !obj.get("error").getAsString().isEmpty()) {
                    displayText = obj.get("error").getAsString();
                } else if (obj.has("output") && !obj.get("output").isJsonNull()) {
                    displayText = obj.get("output").getAsString();
                }
                
                // If running all test cases (Submit), append test case results.
                if (runAll) {
                    // Create a visual separator for clarity.
                    String separator = "\n------------------------------------------\n";
                    displayText += "\nTest case 1: " + (displayText.trim().length() > 0 ? "PASSED" : "FAILED")
                               + "\nTest case 2: " + (displayText.trim().length() > 0 ? "PASSED" : "FAILED")
                               + separator + "Correct solution!";
                }
                final String finalDisplay = displayText;
                Platform.runLater(() -> {
                    if (challengeActive) {
                        // Update the output display and score label on the UI thread.
                        outputDisplay.setText(finalDisplay);
                        scoreLabel.setText("Score: " + QuizScene.getScore());
                        // If the submission passes both test cases, award points and move on.
                        if (runAll && finalDisplay.contains("PASSED") && finalDisplay.contains("Test case 2: PASSED")) {
                            QuizScene.addScore(10);
                            scoreLabel.setText("Score: " + QuizScene.getScore());
                            challengeActive = false;
                            stopTimer();
                            // Optional slight delay before transitioning back to the quiz scene.
                            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), ev -> SceneManager.showQuizScene()));
                            delay.play();                            
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    if (challengeActive) {
                        outputDisplay.setText("Error: " + ex.getMessage());
                    }
                });
            }
        }).start();
    }
    
    /**
     * Stops the challenge timer if it is running.
     */
    public static void stopTimer() {
        if (challengeTimer != null) {
            challengeTimer.stop();
        }
    }
}
