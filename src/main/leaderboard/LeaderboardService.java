package src.main.leaderboard;


import java.util.Properties;
import src.main.config.ConfigLoader;

import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LeaderboardService {
    // Declare the variables
    private static final String SUPABASE_API_URL;
    private static final String SUPABASE_API_KEY;      
    // Initialize them in the static block
    static {
        Properties config = ConfigLoader.loadConfig();
        SUPABASE_API_URL = config.getProperty("SUPABASE_API_LEADERBOARD_URL");
        // Supabase API key for authentication.
        SUPABASE_API_KEY = config.getProperty("SUPABASE_API_KEY");
    }
    // System.out.println("Leaderboard URL: " + SUPABASE_API_URL);


    // HttpClient instance used for sending HTTP requests.
    private static HttpClient httpClient = HttpClient.newHttpClient();
    // Observable list to hold leaderboard entries.
    private static ObservableList<LeaderboardEntry> leaderboard = FXCollections.observableArrayList();
    
    /**
     * Pushes leaderboard data asynchronously by creating a new thread.
     *
     * @param name       The name of the player.
     * @param score      The score to be added.
     * @param difficulty The difficulty level of the game.
     * @param mode       The game mode.
     */
    public static void pushLeaderboardDataAsync(String name, int score, String difficulty, String mode) {
        new Thread(() -> pushLeaderboardData(name, score, difficulty, mode)).start();
    }
    
    /**
     * Pushes leaderboard data to the Supabase API.
     * If an entry with the given name exists, it updates the score by adding the new score.
     * Otherwise, it creates a new leaderboard entry.
     *
     * @param name       The name of the player.
     * @param score      The score to be added.
     * @param difficulty The difficulty level of the game.
     * @param mode       The game mode.
     */
    public static void pushLeaderboardData(String name, int score, String difficulty, String mode) {
        try {
            // Build URL query to search for an existing entry by name.
            String queryUrl = SUPABASE_API_URL + "?Name=eq." + name;
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(queryUrl))
                    .header("apikey", SUPABASE_API_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .GET()
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            String responseBody = getResponse.body();
            
            // Check if the response contains an existing entry.
            if (JsonParser.parseString(responseBody).isJsonArray() &&
                JsonParser.parseString(responseBody).getAsJsonArray().size() > 0) {
                // Existing entry found; update the score.
                JsonObject existingEntry = JsonParser.parseString(responseBody).getAsJsonArray().get(0).getAsJsonObject();
                int currentScore = existingEntry.get("Score").getAsInt();
                int newScore = currentScore + score;
                JsonObject patchObj = new JsonObject();
                patchObj.addProperty("Score", newScore);
                
                // Create PATCH request to update the score.
                HttpRequest patchRequest = HttpRequest.newBuilder()
                        .uri(URI.create(SUPABASE_API_URL + "?Name=eq." + name))
                        .header("apikey", SUPABASE_API_KEY)
                        .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .header("Content-Type", "application/json")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(patchObj.toString()))
                        .build();
                HttpResponse<String> patchResponse = httpClient.send(patchRequest, HttpResponse.BodyHandlers.ofString());
                if (patchResponse.statusCode() >= 400) {
                    System.out.println("PATCH failed: " + patchResponse.statusCode() + " " + patchResponse.body());
                }
            } else {
                // No existing entry; create a new leaderboard entry.
                JsonObject newEntry = new JsonObject();
                newEntry.addProperty("Name", name);
                newEntry.addProperty("Score", score);
                newEntry.addProperty("Difficulty", difficulty);
                newEntry.addProperty("Mode", mode);
                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(URI.create(SUPABASE_API_URL))
                        .header("apikey", SUPABASE_API_KEY)
                        .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(newEntry.toString()))
                        .build();
                HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
                if (postResponse.statusCode() >= 400) {
                    System.out.println("POST failed: " + postResponse.statusCode() + " " + postResponse.body());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Loads the leaderboard data from the Supabase API.
     * Parses the JSON response and populates the observable list with LeaderboardEntry objects.
     *
     * @return an ObservableList of LeaderboardEntry objects representing the leaderboard.
     */
    public static ObservableList<LeaderboardEntry> loadLeaderboard() {
        try {
            // Build GET request to retrieve all leaderboard entries.
            HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_API_URL))
                .header("apikey", SUPABASE_API_KEY)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            // Clear current leaderboard list.
            leaderboard.clear();
            // Parse the response JSON array.
            JsonArray result = JsonParser.parseString(responseBody).getAsJsonArray();
            for (int i = 0; i < result.size(); i++) {
                JsonObject obj = result.get(i).getAsJsonObject();
                String entryName = obj.get("Name").getAsString();
                int entryScore = obj.get("Score").getAsInt();
                // Check for optional fields "Difficulty" and "Mode".
                String entryDifficulty = obj.has("Difficulty") ? obj.get("Difficulty").getAsString() : "Unknown";
                String entryMode = obj.has("Mode") ? obj.get("Mode").getAsString() : "Unknown";
                // Add a new LeaderboardEntry to the observable list.
                leaderboard.add(new LeaderboardEntry(entryName, entryScore, entryDifficulty, entryMode));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return leaderboard;
    }
}
