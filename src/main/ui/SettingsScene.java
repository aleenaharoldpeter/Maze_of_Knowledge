package src.main.ui;

import src.main.utils.PreferencesManager;
import src.main.utils.SceneManager;
import src.main.utils.AudioManager;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsScene {
    
    /**
     * Creates and returns the root node for the settings scene.
     * This method builds an overlay with various settings controls such as gameplay options,
     * difficulty selection, themes, sound toggle, player selection, and computer selection.
     *
     * @return a Parent node representing the complete settings UI.
     */
    public static Parent createRoot() {
        // Root StackPane to hold the overlay pane.
        StackPane rootStack = new StackPane();
        
        // Create the overlay pane with specified size, background color, rounded corners, and padding.
        BorderPane overlayPane = new BorderPane();
        overlayPane.setPrefSize(400, 500);
        overlayPane.setMaxSize(400, 500);
        overlayPane.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-background-radius: 10; -fx-padding: 20px;");
        
        // Title label for settings.
        Label settingsTitle = new Label("Settings");
        settingsTitle.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        overlayPane.setTop(settingsTitle);
        BorderPane.setAlignment(settingsTitle, Pos.CENTER);
        
        // Center content container for settings options.
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        
        // ------------------ Gameplay Section ------------------
        Label gameplayLabel = new Label("Gameplay");
        gameplayLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        
        // Container for gameplay options buttons.
        HBox gameplayOptions = new HBox(20);
        gameplayOptions.setAlignment(Pos.CENTER);
        Button classicButton = new Button("Classic");
        Button adventureButton = new Button("Adventure");
        gameplayOptions.getChildren().addAll(classicButton, adventureButton);
        // Initially hide gameplay options.
        gameplayOptions.setVisible(false);
        
        // When mouse enters the gameplay label, hide the label and show the options.
        gameplayLabel.setOnMouseEntered(e -> {
            gameplayLabel.setVisible(false);
            gameplayOptions.setVisible(true);
        });
        // When mouse exits the gameplay options, hide them and show the label.
        gameplayOptions.setOnMouseExited(e -> {
            gameplayOptions.setVisible(false);
            gameplayLabel.setVisible(true);
        });
        
        // ------------------ Difficulty Selection ------------------
        // Create a context menu for selecting difficulty.
        ContextMenu difficultyMenu = new ContextMenu();
        MenuItem amateur = new MenuItem("Amateur (10 min)");
        MenuItem beginner = new MenuItem("Beginner (7 min)");
        MenuItem elite = new MenuItem("Elite (5 min)");
        MenuItem pro = new MenuItem("Pro (3 min)");
        MenuItem grandmaster = new MenuItem("Grandmaster (1 min)");
        difficultyMenu.getItems().addAll(amateur, beginner, elite, pro, grandmaster);
        
        // Set actions for each difficulty option to update preferences.
        amateur.setOnAction(e -> PreferencesManager.setDifficulty(600, "Amateur"));
        beginner.setOnAction(e -> PreferencesManager.setDifficulty(420, "Beginner"));
        elite.setOnAction(e -> PreferencesManager.setDifficulty(300, "Elite"));
        pro.setOnAction(e -> PreferencesManager.setDifficulty(180, "Pro"));
        grandmaster.setOnAction(e -> PreferencesManager.setDifficulty(60, "Grandmaster"));
        
        // Show the difficulty menu when hovering over the classic gameplay button.
        classicButton.setOnMouseEntered(e -> difficultyMenu.show(classicButton, javafx.geometry.Side.RIGHT, 0, 0));
        classicButton.setOnMouseExited(e -> difficultyMenu.hide());
        
        // ------------------ Themes Section ------------------
        Label themesLabel = new Label("Themes");
        themesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        
        // Container for theme options.
        HBox themeOptions = new HBox(20);
        themeOptions.setAlignment(Pos.CENTER);
        Button lightButton = new Button("Light");
        Button darkButton = new Button("Dark");
        Button customButton = new Button("Custom");
        themeOptions.getChildren().addAll(lightButton, darkButton, customButton);
        // Initially hide theme options.
        themeOptions.setVisible(false);
        
        // Toggle visibility between label and options on mouse enter/exit.
        themesLabel.setOnMouseEntered(e -> {
            themesLabel.setVisible(false);
            themeOptions.setVisible(true);
        });
        themeOptions.setOnMouseExited(e -> {
            themeOptions.setVisible(false);
            themesLabel.setVisible(true);
        });
        
        // Set actions for theme buttons.
        lightButton.setOnAction(e -> {
            PreferencesManager.setTheme("light");
            PreferencesManager.setDarkMode(false);
            ThemeManager.applyThemeToAllScenes();
        });
        darkButton.setOnAction(e -> {
            PreferencesManager.setTheme("dark");
            PreferencesManager.setDarkMode(true);
            ThemeManager.applyThemeToAllScenes();
        });
        customButton.setOnAction(e -> {
            // Open a FileChooser to select a custom background image.
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                PreferencesManager.setCustomBackground(file.toURI().toString());
                ThemeManager.applyThemeToAllScenes();
            }
        });
        
        // ------------------ Sound Toggle Section ------------------
        Label soundLabel = new Label("Sound:");
        soundLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        ToggleButton soundToggle = new ToggleButton();
        // Initialize sound toggle based on saved preference.
        soundToggle.setSelected(PreferencesManager.getSoundEnabled());
        updateSoundToggleStyle(soundToggle);
        // Update preferences and style when toggle state changes.
        soundToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            PreferencesManager.setSoundEnabled(newVal);
            updateSoundToggleStyle(soundToggle);
        });
        HBox soundBox = new HBox(10, soundLabel, soundToggle);
        soundBox.setAlignment(Pos.CENTER);
        
        // ------------------ Player Selection Section ------------------
        Label playerLabel = new Label("Player");
        playerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        HBox playerSelectionBox = new HBox(10);
        playerSelectionBox.setAlignment(Pos.CENTER);
        
        Button leftArrow = new Button("<");
        Button rightArrow = new Button(">");
        // ImageView to display the current player icon.
        javafx.scene.image.ImageView playerIconView = new javafx.scene.image.ImageView();
        playerIconView.setFitWidth(64);
        playerIconView.setFitHeight(64);
        
        // Load available player options from the specified folder.
            // Current Directory Path
        String BASE_DIR = System.getProperty("user.dir");
        String playerIconBasePath = BASE_DIR + "/assets/Icon/Player";
        File playerDir = new File(playerIconBasePath);
        List<String> playerOptions = new ArrayList<>();
        if (playerDir.exists() && playerDir.isDirectory()) {
            File[] dirs = playerDir.listFiles(File::isDirectory);
            if (dirs != null) {
                for (File f : dirs) {
                    playerOptions.add(f.getName());
                }
            }
        }
        // Use a default player if no options are available.
        if (playerOptions.isEmpty()) {
            playerOptions.add("Professor Grumps");
        }
        
        // Determine current selected index based on saved preference.
        String selectedPlayer = PreferencesManager.getSelectedPlayer();
        int[] currentIndex = {0};
        for (int i = 0; i < playerOptions.size(); i++) {
            if (playerOptions.get(i).equalsIgnoreCase(selectedPlayer)) {
                currentIndex[0] = i;
                break;
            }
        }
        
        // Runnable to update and display the current player icon.
        Runnable updatePlayerIcon = () -> {
            String playerName = playerOptions.get(currentIndex[0]);
            PreferencesManager.setSelectedPlayer(playerName);
            String iconPath = "file:" + playerIconBasePath + "/" + playerName + "/icon.png";
            javafx.scene.image.Image img = new javafx.scene.image.Image(iconPath);
            playerIconView.setImage(img);
        };
        updatePlayerIcon.run();
        
        leftArrow.setOnAction(e -> {
            currentIndex[0] = (currentIndex[0] - 1 + playerOptions.size()) % playerOptions.size();
            updatePlayerIcon.run();
        });
        rightArrow.setOnAction(e -> {
            currentIndex[0] = (currentIndex[0] + 1) % playerOptions.size();
            updatePlayerIcon.run();
        });
        playerSelectionBox.getChildren().addAll(leftArrow, playerIconView, rightArrow);
        
        // ------------------ Computer Selection Section ------------------
        Label computerLabel = new Label("Computer");
        computerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        HBox computerSelectionBox = new HBox(10);
        computerSelectionBox.setAlignment(Pos.CENTER);
        
        Button compLeftArrow = new Button("<");
        Button compRightArrow = new Button(">");
        // ImageView for the current computer icon.
        javafx.scene.image.ImageView compIconView = new javafx.scene.image.ImageView();
        compIconView.setFitWidth(64);
        compIconView.setFitHeight(64);
        
        // Load available computer options from the specified folder.
        String compIconBasePath = BASE_DIR + "/assets/Icon/Computer";
        File compDir = new File(compIconBasePath);
        List<String> compOptions = new ArrayList<>();
        if (compDir.exists() && compDir.isDirectory()) {
            File[] compDirs = compDir.listFiles(File::isDirectory);
            if (compDirs != null) {
                for (File f : compDirs) {
                    compOptions.add(f.getName());
                }
            }
        }
        if (compOptions.isEmpty()) {
            compOptions.add("Demon King");
        }
        
        // Determine current selected computer index based on saved preference.
        String selectedComputer = PreferencesManager.getSelectedComputer();
        int[] compCurrentIndex = {0};
        for (int i = 0; i < compOptions.size(); i++) {
            if (compOptions.get(i).equalsIgnoreCase(selectedComputer)) {
                compCurrentIndex[0] = i;
                break;
            }
        }
        
        // Runnable to update and display the current computer icon.
        Runnable updateCompIcon = () -> {
            String compName = compOptions.get(compCurrentIndex[0]);
            PreferencesManager.setSelectedComputer(compName);
            // Assuming the computer icon file is named "Demon King.png" (adjust if needed).
            String compIconPath = "file:" + compIconBasePath + "/" + compName + "/" +compName+".png";
            System.out.println(compIconBasePath);
            System.out.println("///////////////////////////////////////////");
            System.out.println(compIconPath);
            javafx.scene.image.Image img = new javafx.scene.image.Image(compIconPath);
            compIconView.setImage(img);
        };
        updateCompIcon.run();
        
        compLeftArrow.setOnAction(e -> {
            compCurrentIndex[0] = (compCurrentIndex[0] - 1 + compOptions.size()) % compOptions.size();
            updateCompIcon.run();
        });
        compRightArrow.setOnAction(e -> {
            compCurrentIndex[0] = (compCurrentIndex[0] + 1) % compOptions.size();
            updateCompIcon.run();
        });
        computerSelectionBox.getChildren().addAll(compLeftArrow, compIconView, compRightArrow);
        
        // ------------------ Assemble Center Content ------------------
        centerContent.getChildren().addAll(
            gameplayLabel, gameplayOptions,
            themesLabel, themeOptions,
            soundBox,
            playerLabel, playerSelectionBox,
            computerLabel, computerSelectionBox
        );
        overlayPane.setCenter(centerContent);
        
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
        
        // Add the overlay pane to the root stack.
        rootStack.getChildren().add(overlayPane);
        
        // Apply a fade transition to the overlay pane for smooth appearance.
        FadeTransition overlayFade = new FadeTransition(Duration.millis(300), overlayPane);
        overlayFade.setFromValue(0);
        overlayFade.setToValue(1);
        overlayFade.play();
        
        return rootStack;
    }

    /**
     * Updates the visual style of the sound toggle button based on its state.
     *
     * @param toggle the ToggleButton representing the sound state.
     */
    private static void updateSoundToggleStyle(ToggleButton toggle) {
        if (toggle.isSelected()) {
            toggle.setText("ON");
            toggle.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        } else {
            toggle.setText("OFF");
            toggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        }
    }
}
