package src.main.ui;

import src.main.game.Classic.RetroRunning.PixelRetroRunner;
import src.main.game.Classic.Fighting.FightingGame;
import src.main.quiz.QuizScene;
import src.main.quiz.MatchingTitlesScene;
import src.main.utils.SceneManager;


import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class ClassicModeSelectionPopup {
    
    /**
     * Displays a modal popup that allows the user to select a classic mode option.
     * The modal layer covers the existing scene with a semi-transparent blocker
     * and an overlay pane that contains option buttons.
     */
    public static void show() {
        // Retrieve the current scene and its root node
        Scene scene = SceneManager.getScene();
        Parent currentRoot = scene.getRoot();
        StackPane stackRoot;
        
        // Ensure the root is a StackPane for layering; if not, wrap the existing root in a new StackPane.
        if (currentRoot instanceof StackPane) {
            stackRoot = (StackPane) currentRoot;
        } else {
            stackRoot = new StackPane();
            stackRoot.getChildren().add(currentRoot);
            scene.setRoot(stackRoot);
        }
        
        // Create a modal layer to display the popup over the existing scene
        StackPane modalLayer = new StackPane();
        
        // Create a full-screen blocking rectangle to cover the entire area and intercept mouse events
        Rectangle blocker = new Rectangle();
        blocker.widthProperty().bind(stackRoot.widthProperty());
        blocker.heightProperty().bind(stackRoot.heightProperty());
        blocker.setFill(Color.rgb(0, 0, 0, 0.3));  // Semi-transparent dark overlay
        modalLayer.getChildren().add(blocker);
        
        // Create an overlay pane styled as a popup window
        BorderPane overlayPane = new BorderPane();
        overlayPane.setPrefSize(400, 300);
        overlayPane.setMaxSize(400, 300);
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-background-radius: 10; -fx-padding: 20px;");
        
        // Create and style the title label at the top of the popup
        Label title = new Label("Select Classic Mode Option");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        overlayPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        
        // Create a VBox to hold the two option buttons in the center of the popup
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        
        // Create buttons for the two classic mode options
        Button classicQuizButton = new Button("Classic Quiz");
        Button matchingTitlesButton = new Button("Matching Titles Game");
        Button retroBtn = new Button("Retro Running");
        Button FightingBtn = new Button("Fighting");

                // ------------------ Bottom Section ------------------
        VBox bottomBox = new VBox();
        bottomBox.setAlignment(Pos.CENTER);
        Button closeOverlay = new Button("Close");
        // When closing the overlay, refresh the persistent mascot and return to the main menu.
        closeOverlay.setOnAction(e -> {
            SceneManager.refreshMascot();
            SceneManager.showMainMenu();
        });
        bottomBox.getChildren().add(closeOverlay);
        overlayPane.setBottom(bottomBox);
        BorderPane.setMargin(bottomBox, new Insets(10, 0, 0, 0));
        
        // Define a common style for both buttons
        String buttonStyle = "-fx-font-size: 18px; -fx-padding: 10px 20px; " +
                             "-fx-background-color:rgb(163, 170, 174); -fx-text-fill: white; " +
                             "-fx-background-radius: 10px;"; 
        classicQuizButton.setStyle(buttonStyle);
        matchingTitlesButton.setStyle(buttonStyle);
        retroBtn.setStyle(buttonStyle);
        FightingBtn.setStyle(buttonStyle);
        closeOverlay.setStyle(buttonStyle);
        
        // Create and set up a scale transition for the classic quiz button when hovered
        ScaleTransition classicScale = new ScaleTransition(Duration.millis(150), classicQuizButton);
        classicQuizButton.setOnMouseEntered(e -> {
            classicScale.setToX(1.1);
            classicScale.setToY(1.1);
            classicScale.playFromStart();
        });
        classicQuizButton.setOnMouseExited(e -> {
            classicScale.setToX(1.0);
            classicScale.setToY(1.0);
            classicScale.playFromStart();
        });
        
        // Create and set up a scale transition for the matching titles button when hovered
        ScaleTransition matchingScale = new ScaleTransition(Duration.millis(150), matchingTitlesButton);
        matchingTitlesButton.setOnMouseEntered(e -> {
            matchingScale.setToX(1.1);
            matchingScale.setToY(1.1);
            matchingScale.playFromStart();
        });
        matchingTitlesButton.setOnMouseExited(e -> {
            matchingScale.setToX(1.0);
            matchingScale.setToY(1.0);
            matchingScale.playFromStart();
        });
        
        // Create and set up a scale transition for the retro running button when hovered
        ScaleTransition retroScale = new ScaleTransition(Duration.millis(150), retroBtn);
        retroBtn.setOnMouseEntered(e -> {
            retroScale.setToX(1.1);
            retroScale.setToY(1.1);
            retroScale.playFromStart();
        });
        retroBtn.setOnMouseExited(e -> {
            retroScale.setToX(1.0);
            retroScale.setToY(1.0);
            retroScale.playFromStart();
        });

        // Create and set up a scale transition for the fighting button when hovered
        ScaleTransition FightingScale = new ScaleTransition(Duration.millis(150), FightingBtn);
        FightingBtn.setOnMouseEntered(e -> {
            FightingScale.setToX(1.1);
            FightingScale.setToY(1.1);
            FightingScale.playFromStart();
        });
        FightingBtn.setOnMouseExited(e -> {
            FightingScale.setToX(1.0);
            FightingScale.setToY(1.0);
            FightingScale.playFromStart();
        });

        ScaleTransition closeOverlayScale = new ScaleTransition(Duration.millis(150), closeOverlay);
        closeOverlay.setOnMouseEntered(e -> {
            closeOverlayScale.setToX(1.1);
            closeOverlayScale.setToY(1.1);
            closeOverlayScale.playFromStart();
        });
        closeOverlay.setOnMouseExited(e -> {
            closeOverlayScale.setToX(1.0);
            closeOverlayScale.setToY(1.0);
            closeOverlayScale.playFromStart();
        });


        // Set the action for the classic quiz button:
        // Switch to the quiz scene and remove the modal popup from the root.
        classicQuizButton.setOnAction(e -> {
            SceneManager.showQuizScene();
            stackRoot.getChildren().remove(modalLayer);
        });
        
        // Set the action for the matching titles button:
        // Switch to the matching titles scene and remove the modal popup from the root.
        matchingTitlesButton.setOnAction(e -> {
            SceneManager.showMatchingTitlesScene();
            stackRoot.getChildren().remove(modalLayer);
        });
        
        // Set the action for the retro running button:
        // Switch to the retro running scene and remove the modal popup from the root.
        retroBtn.setOnAction(e -> {
            SceneManager.showRetroRunningScene();
            stackRoot.getChildren().remove(modalLayer);
        });

        // Set the action for the fighting button:
        // Switch to the fighting game scene and remove the modal popup from the root.
        FightingBtn.setOnAction(e -> {
            SceneManager.showFightingGame();
            stackRoot.getChildren().remove(modalLayer);
        });        
         

        // Add the buttons to the center content layout
        centerContent.getChildren().addAll(classicQuizButton, matchingTitlesButton, retroBtn, FightingBtn);
        overlayPane.setCenter(centerContent);
        
        // Add the overlay pane (popup window) on top of the blocker within the modal layer
        modalLayer.getChildren().add(overlayPane);
        
        // Finally, add the modal layer to the root so that it displays on top of the current scene
        stackRoot.getChildren().add(modalLayer);
    }
}
