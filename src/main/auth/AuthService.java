package src.main.auth;

import java.util.Properties;
import src.main.config.ConfigLoader;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;

public class AuthService {
    // Declare the variables
    private static final String SUPABASE_URL;
    private static final String SUPABASE_API_KEY;

    // Initialize them in the static block
    static {
        Properties config = ConfigLoader.loadConfig();
        SUPABASE_URL = config.getProperty("SUPABASE_API_AUTHENTICATION_URL");
        SUPABASE_API_KEY = config.getProperty("SUPABASE_API_KEY");
    }

    private final HttpClient client = HttpClient.newHttpClient();

    public CompletableFuture<JSONObject> login(String email, String password) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", email);
        requestBody.put("password", password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/auth/v1/token?grant_type=password"))
                .header("Content-Type", "application/json")
                .header("apikey", SUPABASE_API_KEY)
                .POST(BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return new JSONObject(response.body());
                    } else {
                        throw new RuntimeException("Login failed: " + response.body());
                    }
                });
    }

    public CompletableFuture<JSONObject> signup(String username, String email, String password) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", email);
        requestBody.put("password", password);

        // Include username as metadata
        JSONObject data = new JSONObject();
        data.put("username", username);
        requestBody.put("data", data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/auth/v1/signup"))
                .header("Content-Type", "application/json")
                .header("apikey", SUPABASE_API_KEY)
                .POST(BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        return new JSONObject(response.body());
                    } else {
                        throw new RuntimeException("Signup failed: " + response.body());
                    }
                });
    }

    public CompletableFuture<JSONObject> deleteAccount(String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/auth/v1/user"))
                .header("Authorization", "Bearer " + token)
                .header("apikey", SUPABASE_API_KEY)
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200 || response.statusCode() == 204) {
                        return new JSONObject("{\"status\":\"deleted\"}");
                    } else {
                        throw new RuntimeException("Delete failed: " + response.body());
                    }
                });
    }
}
