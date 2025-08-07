package src.main.auth;

import java.util.prefs.Preferences;

public class SessionManager {
    private static final String PREFS_NODE = "com.example.vtuquiz";
    private static final String KEY_TOKEN = "authToken";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_USERNAME = "username";

    private static final Preferences prefs = Preferences.userRoot().node(PREFS_NODE);

    public static void saveSession(String token, String email, String username) {
        prefs.put(KEY_TOKEN, token);
        prefs.put(KEY_EMAIL, email);
        prefs.put(KEY_USERNAME, username);
    }

    // New method to save username separately (used by signup)
    public static void saveUsername(String username) {
        prefs.put(KEY_USERNAME, username);
    }

    public static String getToken() {
        return prefs.get(KEY_TOKEN, null);
    }

    public static String getEmail() {
        return prefs.get(KEY_EMAIL, null);
    }

    public static String getUsername() {
        return prefs.get(KEY_USERNAME, null);
    }

    public static void clearSession() {
        prefs.remove(KEY_TOKEN);
        prefs.remove(KEY_EMAIL);
        prefs.remove(KEY_USERNAME);
    }

    // Dummy method for classic games played
    public static int getClassicGamesPlayed() {
        return 0; // Return a placeholder value
    }

    // Dummy method for adventure games played
    public static int getAdventureGamesPlayed() {
        return 0; // Return a placeholder value
    }

    // Dummy method for highest score in quizzes
    public static int getHighestScoreQuiz() {
        return 0; // Return a placeholder value
    }

    // Dummy method for highest score in matching games
    public static int getHighestScoreMatching() {
        return 0; // Return a placeholder value
    }

    // Dummy method for highest score in adventure games
    public static int getHighestScoreAdventure() {
        return 0; // Return a placeholder value
    }

}

