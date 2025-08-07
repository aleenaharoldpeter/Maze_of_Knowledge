package src.main.utils;

// your project‐specific imports:
import src.main.app.MainMenu;
import src.main.ui.ClassicModeSelectionPopup;
import src.main.ui.SettingsScene;
import src.main.ui.ThemeManager;
import src.main.ui.SpriteAnimationPane;
import src.main.game.Adventure.AdventureMode;
import src.main.game.Classic.Fighting.FightingGame;
import src.main.game.Classic.RetroRunning.PixelRetroRunner;
import src.main.quiz.QuizScene;
import src.main.quiz.MatchingTitlesScene;
import src.main.leaderboard.LeaderboardScene;



// JavaFX imports:
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

// import Classic.RetroRunning.PixelRetroRunner;
// import Classic.Fighting.FightingGame;

public class SceneManager {
    // The primary stage of the application.
    private static Stage primaryStage;
    // The main scene that is set on the primary stage.
    private static Scene mainScene;
    // Persistent mascot instance (walking mascot) used in modes that require it.
    private static SpriteAnimationPane persistentSprite;

    private static String baseDir = System.getProperty("user.dir");

    /**
     * Initializes the SceneManager with the primary stage.
     * Sets up the main scene and shows the stage in full screen.
     * Also creates the persistent mascot instance.
     *
     * @param stage the primary Stage of the application.
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
        // Create a new scene with an empty AnchorPane as the root.
        mainScene = new Scene(new AnchorPane(), 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();

        // Create the persistent mascot instance.
        persistentSprite = new SpriteAnimationPane();
        // Ensure the mascot does not intercept mouse events.
        persistentSprite.setMouseTransparent(true);
        persistentSprite.setPickOnBounds(false);  // <-- New: Do not pick on its bounds
    }

    /**
     * Sets the root of the main scene.
     * Wraps the given root in an AnchorPane and adds the persistent mascot
     * if the root does not contain the "no-mascot" style class.
     *
     * @param root the new root node to set.
     */
    public static void setRoot(Parent root) {
        // Create a container AnchorPane to hold the UI root.
        AnchorPane container = new AnchorPane();
        // Anchor the main UI root to all edges of the container.
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        container.getChildren().add(root);

        // If the root does NOT have the "no-mascot" style class, add the persistent mascot.
        if (!root.getStyleClass().contains("no-mascot")) {
            // Remove persistentSprite from its previous parent, if any.
            if (persistentSprite.getParent() != null) {
                ((AnchorPane) persistentSprite.getParent()).getChildren().remove(persistentSprite);
            }
            // Re-ensure that the persistent mascot does not intercept any events.
            persistentSprite.setMouseTransparent(true);
            persistentSprite.setPickOnBounds(false);
            // Anchor the mascot to the bottom with some padding.
            AnchorPane.setBottomAnchor(persistentSprite, 10.0);
            AnchorPane.setLeftAnchor(persistentSprite, 0.0);
            AnchorPane.setRightAnchor(persistentSprite, 0.0);
            // Set a preferred height for the mascot.
            persistentSprite.setPrefHeight(150);
            // Add the persistent mascot as the first child so it is rendered behind the UI.
            container.getChildren().add(0, persistentSprite);
        }
        // Set the new container as the root of the main scene.
        mainScene.setRoot(container);
        // Apply the current theme to the scene.
        ThemeManager.applyTheme(mainScene);
    }

    /**
     * Retrieves the main scene.
     *
     * @return the main Scene.
     */
    public static Scene getScene() {
        return mainScene;
    }

    /**
     * Displays the main menu by stopping any running quiz timer and setting the main menu root.
     */
    public static void showMainMenu() {
        QuizScene.stopTimer();
        setRoot(MainMenu.createRoot());
    }

    /**
     * Displays the quiz scene.
     */
    public static void showQuizScene() {
        setRoot(QuizScene.createRoot());
    }

    /**
     * Displays the adventure mode scene.
     */
    public static void showAdventureMode() {
        setRoot(AdventureMode.createRoot());
    }

    /**
     * Displays the leaderboard scene.
     */
    public static void showLeaderboard() {
        setRoot(LeaderboardScene.createRoot());
    }

    /**
     * Displays the settings scene.
     */
    public static void showSettings() {
        setRoot(SettingsScene.createRoot());
    }

    /**
     * Displays the matching titles scene.
     */
    public static void showMatchingTitlesScene() {
        setRoot(MatchingTitlesScene.createRoot());
    }


    // public static void showRetroRunningScene() {
    //     // if you use changeScene() for other modes:
    //     // changeScene(new RetroRunningScene());
    //     setRoot(PixelRetroRunner.createRoot());
    // }
    // public static void showRetroRunningScene() {
    // Parent retro = PixelRetroRunner.createRoot();
    // setRoot(retro);
    // Platform.runLater(() -> {
    //     retro.requestFocus();
    //     ((Stage)retro.getScene().getWindow()).setFullScreen(true);
    // });
    // }    
    public static void showRetroRunningScene() {
    // hide the main menu window
    primaryStage.hide();
    PixelRetroRunner.launchGame();
    }

    public static void showFightingGame() {
        // hide the main menu window
        primaryStage.hide();
        FightingGame.launchGame();
    }


    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Refreshes the persistent mascot by creating a new instance.
     * This may be used to update its appearance.
     */
    public static void refreshMascot() {
        persistentSprite = new SpriteAnimationPane();
        persistentSprite.setMouseTransparent(true);
        persistentSprite.setPickOnBounds(false);
    }    

    // --- Navigation for Login, Signup, and Dashboard ---

    public static void showLogin() {
        try {
            // Use absolute resource path.
            // FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/LoginView.fxml"));
            File loginFxmlFile = new File(baseDir, "resources/fxml/LoginView.fxml");
            URL loginFxmlUrl    = loginFxmlFile.toURI().toURL();
            FXMLLoader loader   = new FXMLLoader(loginFxmlUrl);
            Parent root = loader.load();
            // Prevent the mascot on login.
            // root.getStyleClass().add("no-mascot");
            setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    public static void showSignup() {
        try {
            // FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/SignupView.fxml"));
            File SignupFxmlFile = new File(baseDir, "resources/fxml/SignupView.fxml");
            URL SignupFxmlUrl    = SignupFxmlFile.toURI().toURL();
            FXMLLoader loader   = new FXMLLoader(SignupFxmlUrl);
            Parent root = loader.load();
            root.getStyleClass().add("no-mascot");
            setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDashboard() {
        try {
            // FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/Dashboard.fxml"));
            File dashboardFxmlFile = new File(baseDir, "resources/fxml/Dashboard.fxml");
            URL dashboardFxmlUrl    = dashboardFxmlFile.toURI().toURL();
            FXMLLoader loader   = new FXMLLoader(dashboardFxmlUrl);
            Parent root = loader.load();
            root.getStyleClass().add("no-mascot");
            setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the adventure leaderboard.
     * Currently not implemented.
     */
    public static void showAdventureLeaderboard() {
        System.out.println("Adventure leaderboard not implemented yet.");
    }
}
