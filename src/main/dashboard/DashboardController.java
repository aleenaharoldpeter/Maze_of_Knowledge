package src.main.dashboard;

import src.main.auth.AuthService;
import src.main.utils.SceneManager;
import src.main.auth.SessionManager;
import src.main.ui.ThemeManager;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class DashboardController {

    @FXML 
    private Label usernameLabel;
    @FXML 
    private Label emailLabel;
    @FXML 
    private Label classicGamesLabel;
    @FXML 
    private Label adventureGamesLabel;
    @FXML 
    private Label highestScoreQuizLabel;
    @FXML 
    private Label highestScoreMatchingLabel;
    @FXML 
    private Label highestScoreAdventureLabel;    

    private final AuthService authService = new AuthService();

    public void initialize() {
        // Delay initialization until the scene is attached.
        Platform.runLater(() -> {
            Stage stage = getCurrentStage();
            stage.setFullScreen(true);
            ThemeManager.applyTheme(stage.getScene());

            // Retrieve session details.
            String username = SessionManager.getUsername();
            String email = SessionManager.getEmail();
            int classicGamesPlayed = SessionManager.getClassicGamesPlayed();
            int adventureGamesPlayed = SessionManager.getAdventureGamesPlayed();
            int highestScoreQuiz = SessionManager.getHighestScoreQuiz();
            int highestScoreMatching = SessionManager.getHighestScoreMatching();
            int highestScoreAdventure = SessionManager.getHighestScoreAdventure();

            // Setting default values for labels
            usernameLabel.setText(username != null ? username : "N/A");
            emailLabel.setText(email != null ? email : "N/A");
            classicGamesLabel.setText(classicGamesPlayed >= 0 ? String.valueOf(classicGamesPlayed) : "0");
            adventureGamesLabel.setText(adventureGamesPlayed >= 0 ? String.valueOf(adventureGamesPlayed) : "0");
            highestScoreQuizLabel.setText(highestScoreQuiz >= 0 ? String.valueOf(highestScoreQuiz) : "0");
            highestScoreMatchingLabel.setText(highestScoreMatching >= 0 ? String.valueOf(highestScoreMatching) : "0");
            highestScoreAdventureLabel.setText(highestScoreAdventure >= 0 ? String.valueOf(highestScoreAdventure) : "0");
        });
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        // Navigate to the Login view using SceneManager.
        SceneManager.showLogin();
    }

    @FXML
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete your account?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Delete Account");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                String token = SessionManager.getToken();
                authService.deleteAccount(token).thenAccept(result -> {
                    SessionManager.clearSession();
                    Platform.runLater(() -> {
                        SceneManager.showLogin();
                    });
                }).exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Failed to delete account: " + ex.getMessage(), ButtonType.OK);
                        alert.showAndWait();
                    });
                    return null;
                });
            }
        });
    }
    
    @FXML
    private void handleBackToMainMenu(ActionEvent event) {
        // Navigate back to the Main Menu using the SceneManager.
        SceneManager.showMainMenu();
    }

    // Helper method to get the current Stage.
    private Stage getCurrentStage() {
        return (Stage) usernameLabel.getScene().getWindow();
    }
}