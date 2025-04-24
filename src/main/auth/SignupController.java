
package src.main.auth;

import src.main.utils.SceneManager;
import src.main.ui.ThemeManager;
import src.main.sprites.CatFaceBuilder;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.application.Platform;

public class SignupController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Button togglePasswordBtn;
    @FXML private Label toggleLabel;
    @FXML private ImageView catImageView;
    @FXML private TextField usernameField; // New field for username
    @FXML private Pane catPane;  // Used to host the generated cat face

    private boolean isPasswordVisible = false;
    private final AuthService authService = new AuthService();

    public void initialize() {
        try {        
            Stage stage = getCurrentStage();
            stage.setFullScreen(true);
            ThemeManager.applyTheme(stage.getScene());
            // Add the generated cat face (eyes open by default)
            catPane.getChildren().add(CatFaceBuilder.createCatFace(false));
            // Bind the visible password field with the hidden password field.
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }            
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
    private void handleSignup() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()){
            showAlert("Validation Error", "Please enter username, email, and password.");
            return;
        }

        // Call AuthService to sign up.
        authService.signup(username, email, password).thenAccept(response -> {
            Platform.runLater(() -> {
                // Save the chosen username in preferences so it is used on login.
                SessionManager.saveUsername(username);
                showAlert("Signup Success", "Account created successfully. Please verify your email and login.");
                // After signup, navigate to the Login view.
                SceneManager.showLogin();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showAlert("Signup Error", ex.getMessage());
            });
            return null;
        });
    }

    @FXML
    private void goToLogin() {
        // Navigate to the Login view using SceneManager.
        SceneManager.showLogin();
    }

    private Stage getCurrentStage() {
        return (Stage) usernameField.getScene().getWindow();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
