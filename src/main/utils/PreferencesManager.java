
package src.main.utils;

import src.main.quiz.QuizScene;
import src.main.app.VTUGamifiedQuizApp;
import java.util.prefs.Preferences;

public class PreferencesManager {
    // Preferences instance used to store and retrieve user settings.
    private static Preferences prefs;
    
    /**
     * Initializes the PreferencesManager by obtaining the Preferences node for the VTUGamifiedQuizApp package.
     */
    public static void initialize() {
        prefs = Preferences.userNodeForPackage(VTUGamifiedQuizApp.class);
    }
    
    /**
     * Retrieves the global volume setting.
     *
     * @return the global volume (default is 1.0 if not set).
     */
    public static double getGlobalVolume() {
        return prefs.getDouble("globalVolume", 1.0);
    }
    
    /**
     * Checks if dark mode is enabled.
     *
     * @return true if dark mode is enabled, false otherwise (default is false).
     */
    public static boolean isDarkMode() {
        return prefs.getBoolean("darkMode", false);
    }
    
    /**
     * Retrieves the initial time setting.
     *
     * @return the initial time in seconds (default is 60 seconds).
     */
    public static int getInitialTime() {
        return prefs.getInt("initialTime", 60);
    }
    
    /**
     * Retrieves the custom background setting.
     *
     * @return the custom background string (default is an empty string).
     */
    public static String getCustomBackground() {
        return prefs.get("customBackground", "");
    }
    
    /**
     * Retrieves the selected theme.
     *
     * @return the theme string (default is "light").
     */
    public static String getTheme() {
        return prefs.get("selectedTheme", "light");
    }
    
    /**
     * Sets the global volume and updates the AudioManager with the new volume.
     *
     * @param volume the new volume level.
     */
    public static void setGlobalVolume(double volume) {
        prefs.putDouble("globalVolume", volume);
        AudioManager.updateVolume(volume);
    }
    
    /**
     * Enables or disables dark mode.
     *
     * @param darkMode true to enable dark mode, false to disable.
     */
    public static void setDarkMode(boolean darkMode) {
        prefs.putBoolean("darkMode", darkMode);
    }
    
    /**
     * Sets the initial time setting.
     *
     * @param seconds the initial time in seconds.
     */
    public static void setInitialTime(int seconds) {
        prefs.putInt("initialTime", seconds);
    }
    
    /**
     * Sets the custom background.
     *
     * @param bg the background string.
     */
    public static void setCustomBackground(String bg) {
        prefs.put("customBackground", bg);
    }
    
    /**
     * Sets the selected theme.
     *
     * @param theme the theme string.
     */
    public static void setTheme(String theme) {
        prefs.put("selectedTheme", theme);
    }
    
    /**
     * Retrieves the selected computer character folder name.
     *
     * @return the selected computer (default is "Demon King").
     */
    public static String getSelectedComputer() {
        return prefs.get("selectedComputer", "Demon King");
    }
    
    /**
     * Sets the selected computer character.
     *
     * @param computer the computer character folder name.
     */
    public static void setSelectedComputer(String computer) {
        prefs.put("selectedComputer", computer);
    }
    
    /**
     * Sets the difficulty level by adjusting the initial time, prints the level name,
     * and applies the new difficulty settings in the QuizScene.
     *
     * @param seconds   the new initial time in seconds.
     * @param levelName the name of the difficulty level.
     */
    public static void setDifficulty(int seconds, String levelName) {
        setInitialTime(seconds);
        System.out.println("Difficulty set to " + levelName);
        QuizScene.applyNewDifficulty();
    }
    
    /**
     * Returns a string representation of the difficulty level based on the initial time.
     *
     * @return the difficulty level as a String.
     */
    public static String getDifficultyString() {
        int initialTime = getInitialTime();
        if (initialTime == 600) return "Amateur";
        if (initialTime == 420) return "Beginner";
        if (initialTime == 300) return "Elite";
        if (initialTime == 180) return "Pro";
        if (initialTime == 60) return "Grandmaster";
        return "Unknown";
    }
    
    /**
     * Returns the number of questions for the question bank based on the difficulty.
     *
     * @return the number of questions (default is 10 for most cases).
     */
    public static int getDifficultyQuestionBankNumbers() {
        int initialTime = getInitialTime();
        if (initialTime == 600) return 10;
        if (initialTime == 420) return 10;
        if (initialTime == 300) return 10;
        if (initialTime == 180) return 9;
        if (initialTime == 60) return 8;
        return 10;
    }
    
    /**
     * Returns the number of coding questions (Java/Python) based on the difficulty.
     *
     * @return the number of coding questions.
     */
    public static int getDifficultyJavaPythonCodingNumbers() {
        int initialTime = getInitialTime();
        if (initialTime == 600) return 0;
        if (initialTime == 420) return 0;
        if (initialTime == 300) return 0;
        if (initialTime == 180) return 1;
        if (initialTime == 60) return 2;        
        return 0;
    }
    
    /**
     * Checks if sound is enabled.
     *
     * @return true if sound is enabled (default is true).
     */
    public static boolean getSoundEnabled() {
        return prefs.getBoolean("soundEnabled", true);
    }
    
    /**
     * Enables or disables sound.
     *
     * @param enabled true to enable sound, false to disable.
     */
    public static void setSoundEnabled(boolean enabled) {
        prefs.putBoolean("soundEnabled", enabled);
    }
    
    // -------------------- Player Selection Methods --------------------
    // The default selected player is "Professor Grumps" to ensure proper mascot loading.
    
    /**
     * Retrieves the selected player.
     *
     * @return the selected player's name (default is "Professor Grumps").
     */
    public static String getSelectedPlayer() {
        return prefs.get("selectedPlayer", "Professor Grumps");
    }
    
    /**
     * Sets the selected player.
     *
     * @param player the player's name.
     */
    public static void setSelectedPlayer(String player) {
        prefs.put("selectedPlayer", player);
    }
}
