package src.main.app;

import src.main.ui.ClassicModeSelectionPopup;
import src.main.utils.SceneManager;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;


public class MainMenu {

    /**
     * Creates the root node for the main menu scene.
     */
    public static Parent createRoot() {
        // Create animated buttons for menu options.
        Button classicModeBtn = createAnimatedButton("🎮 Classic Mode");
        Button adventureModeBtn = createAnimatedButton("🕹 Adventure Mode");
        Button leaderboardBtn = createAnimatedButton("🏆 Leaderboard");
        Button settingsBtn = createAnimatedButton("⚙ Settings");
        Button quitBtn = createAnimatedButton("❌ Quit");

        // Use unified navigation via SceneManager.
        // classicModeBtn.setOnAction(e -> SceneManager.showQuizScene());
        classicModeBtn.setOnAction(e -> ClassicModeSelectionPopup.show());
        adventureModeBtn.setOnAction(e -> SceneManager.showAdventureMode());
        leaderboardBtn.setOnAction(e -> SceneManager.showLeaderboard());
        settingsBtn.setOnAction(e -> SceneManager.showSettings());
        quitBtn.setOnAction(e -> System.exit(0));

        VBox centerBox = new VBox(30, classicModeBtn, adventureModeBtn, leaderboardBtn, settingsBtn, quitBtn);
        centerBox.setAlignment(Pos.CENTER);

        Label title = new Label("VTU Gamified Quiz App");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        // Dashboard button now directly loads Dashboard.fxml
        // Button dashboardBtn = new Button("Dashboard");
        Button dashboardBtn = new Button("📊 Dashboard");
        dashboardBtn.setFont(Font.font("Arial", 16));
        dashboardBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff);" + // gradient background
            "-fx-text-fill: white;" +
            "-fx-background-radius: 30;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;" +
            "-fx-border-color: transparent;" +
            "-fx-font-weight: bold;"
        );

        // Optional drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(10);
        dashboardBtn.setEffect(shadow);

        // Hover effect
        dashboardBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            dashboardBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #0072ff, #00c6ff);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 30;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-font-weight: bold;"
            );
        });
        dashboardBtn.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            dashboardBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 30;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;" +
                "-fx-font-weight: bold;"
            );
        });        
        // dashboardBtn.setOnAction(e -> {
        //     try {
        //         FXMLLoader loader = new FXMLLoader(MainMenu.class.getResource("/Dashboard.fxml"));
        //         Parent root = loader.load();
        //         Scene scene = new Scene(root);
        //         Stage stage = (Stage) dashboardBtn.getScene().getWindow();
        //         stage.setScene(scene);
        //         stage.setTitle("Dashboard");
        //         stage.setFullScreen(true); // Optional: keep full screen
        //         stage.show();
        //     } catch (Exception ex) {
        //         ex.printStackTrace();
        //     }
        // });
        dashboardBtn.setOnAction(e -> SceneManager.showDashboard());
        // Create an HBox for the top: title on the left, spacer, and dashboard button on the right.
        HBox topBox = new HBox();
        topBox.setStyle("-fx-padding: 10px;");
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setSpacing(10);
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        topBox.getChildren().addAll(title, spacer, dashboardBtn);

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setStyle("-fx-padding: 20px;");

        return root;
    }

    private static Button createAnimatedButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-font-size: 24px; -fx-padding: 15px 30px; -fx-background-color: #3498db; -fx-text-fill: white; " +
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
}








