package src.main.auth;

import src.main.utils.SceneManager;
import src.main.sprites.CatFaceBuilder;
import src.main.ui.ThemeManager;



import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.application.Platform;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Button togglePasswordBtn;
    @FXML private Label toggleLabel;
    @FXML private Pane catPane;  // Used to host the code-generated cat face
    @FXML private CheckBox rememberMeCheckBox;  // New "Remember Me" checkbox

    private boolean isPasswordVisible = false;
    private final AuthService authService = new AuthService();

    public void initialize() {
        try {
            Stage stage = getCurrentStage();
            stage.setFullScreen(true);
            ThemeManager.applyTheme(stage.getScene());
            // Add the generated cat face (eyes open by default)
            catPane.getChildren().add(CatFaceBuilder.createCatFace(false));
            // Bind the visible password field text to the hidden password field text
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to retrieve the current stage
    private Stage getCurrentStage() {
        return (Stage) emailField.getScene().getWindow();
    }

    private void updateCatFace(boolean eyesClosed) {
        catPane.getChildren().clear();
        Node catFaceNode = CatFaceBuilder.createCatFace(eyesClosed);
        catPane.getChildren().add(catFaceNode);
    }

    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            passwordVisibleField.setVisible(false);
            passwordField.setVisible(true);
            toggleLabel.setText("Show");
            updateCatFace(false); // Eyes open
            isPasswordVisible = false;
        } else {
            passwordVisibleField.setVisible(true);
            passwordField.setVisible(false);
            toggleLabel.setText("Hide");
            updateCatFace(true);  // Eyes closed
            isPasswordVisible = true;
        }
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Please enter both email and password.");
            return;
        }

        // Call AuthService to log in.
        authService.login(email, password).thenAccept(response -> {
            String token = response.optString("access_token", null);
            if (token != null) {
                // Use the stored username if available; otherwise derive it from the email.
                String storedUsername = SessionManager.getUsername();
                String username = (storedUsername != null && !storedUsername.isEmpty()) 
                                    ? storedUsername 
                                    : email.split("@")[0];
                // Check if the "Remember Me" checkbox is selected.
                if (rememberMeCheckBox.isSelected()) {
                    // Persist the session (this saves it using Preferences)
                    SessionManager.saveSession(token, email, username);
                } else {
                    // In a fully-implemented version, you might store this transiently.
                    // For demonstration, we'll call saveSession() regardless.
                    SessionManager.saveSession(token, email, username);
                }
                Platform.runLater(() -> {
                    SceneManager.showMainMenu();
                });
            }
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showAlert("Login Error", ex.getMessage());
            });
            return null;
        });
    }

    @FXML
    private void goToSignup() {
        // Navigate to the Signup view using SceneManager.
        SceneManager.showSignup();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
