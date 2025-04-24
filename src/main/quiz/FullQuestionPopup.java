package src.main.quiz;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FullQuestionPopup {
    
    /**
     * Displays a modal popup showing the full question text.
     * The popup contains the question text and a button to close the window.
     *
     * @param fullQuestion The complete question text to be displayed.
     */
    public static void show(String fullQuestion) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        // Set modality so that the popup blocks events to other windows until closed
        popupStage.initModality(Modality.APPLICATION_MODAL);
        // Set the title of the popup window
        popupStage.setTitle("Full Question");

        // Create a label to display the full question text, with wrapping enabled
        Label questionLabel = new Label(fullQuestion);
        questionLabel.setWrapText(true);
        // Apply styling to the question text (e.g., font size)
        questionLabel.setStyle("-fx-font-size: 16px;");

        // Create a close button that will close the popup when clicked
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> popupStage.close());

        // Create a vertical box layout to arrange the label and button
        VBox layout = new VBox(20, questionLabel, closeBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Create the scene with the layout and set its size
        Scene scene = new Scene(layout, 400, 300);
        popupStage.setScene(scene);
        // Show the popup and wait until it is closed before returning control to the caller
        popupStage.showAndWait();
    }
}
