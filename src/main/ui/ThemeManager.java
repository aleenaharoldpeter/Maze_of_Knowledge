package src.main.ui;

import src.main.utils.SceneManager;
import src.main.utils.PreferencesManager;

import javafx.scene.Scene;

public class ThemeManager {

    /**
     * Applies the theme to the given scene based on user preferences.
     * This method sets the background color, text color, and optionally a custom background image,
     * then explicitly updates the text color for all nodes with the ".label" style class.
     *
     * @param scene the Scene to which the theme will be applied.
     */
    public static void applyTheme(Scene scene) {
        // Retrieve the dark mode setting from preferences.
        boolean darkMode = PreferencesManager.isDarkMode();
        // Retrieve the custom background image URL from preferences.
        String customBg = PreferencesManager.getCustomBackground();
        // String to accumulate CSS style settings.
        String themeStyle;

        // Determine the base theme style based on the dark mode setting.
        if (darkMode) {
            themeStyle = "-fx-background-color: #333333; -fx-text-fill: white;";
        } else {
            themeStyle = "-fx-background-color: #ffffff; -fx-text-fill: black;";
        }

        // If a custom background is specified, add it to the theme style.
        if (!customBg.isEmpty()) {
            themeStyle += " -fx-background-image: url('" + customBg + "'); -fx-background-size: cover;";
        }

        // Apply the accumulated style to the root node of the scene.
        scene.getRoot().setStyle(themeStyle);

        // Explicitly update text color for all nodes with the ".label" style class.
        // This ensures that all labels display the correct text color based on the theme.
        scene.getRoot().lookupAll(".label").forEach(node ->
            node.setStyle("-fx-text-fill: " + (darkMode ? "white" : "black") + ";")
        );
    }

    /**
     * Applies the theme to all scenes by retrieving the main scene from the SceneManager.
     */
    public static void applyThemeToAllScenes() {
        applyTheme(SceneManager.getScene());
    }
}
