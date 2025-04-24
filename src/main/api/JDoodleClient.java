package src.main.api;

import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import src.main.config.ConfigLoader;

public class JDoodleClient {
    // API endpoint for code execution
    private static final String API_URL = "https://api.jdoodle.com/v1/execute";
    // API endpoint for checking credits spent
    private static final String CREDIT_SPENT_URL = "https://api.jdoodle.com/v1/credit-spent";

    // Declare the variables
    private static final String CLIENT_ID;
    private static final String CLIENT_SECRET;

    // Initialize them in the static block
    static {
        // Provided JDoodle credentials
        Properties config = ConfigLoader.loadConfig();
        CLIENT_ID = config.getProperty("JDOODLE_CLIENT_ID");
        CLIENT_SECRET = config.getProperty("JDOODLE_CLIENT_SECRET");
    }


    // Define your daily credit limit (adjust as per your subscription plan)
    private static final int DAILY_CREDIT_LIMIT = 22;
    
    /**
     * Returns the remaining JDoodle credits for today.
     * This method sends a POST request to the credit spent endpoint with the client credentials,
     * parses the JSON response (which contains a "used" field), and calculates the remaining credits.
     * If any error occurs or if the credits used exceed the limit, 0 is returned.
     *
     * @return the number of remaining credits, or 0 if none remain or an error occurs.
     */
    public static int getRemainingCredits() {
        try {
            URL url = new URL(CREDIT_SPENT_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");  // Use POST as per documentation.
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            
            // Build payload including client credentials.
            String payload = "{" +
                    "\"clientId\": \"" + CLIENT_ID + "\"," +
                    "\"clientSecret\": \"" + CLIENT_SECRET + "\"" +
                    "}";
            
            // Write the payload to the request output stream.
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                // Read response from the API.
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                // Parse the JSON response; expected format: {"used": <number>}
                JsonObject obj = new Gson().fromJson(response.toString(), JsonObject.class);
                int used = obj.get("used").getAsInt();
                // Calculate remaining credits
                int remaining = DAILY_CREDIT_LIMIT - used;
                return remaining < 0 ? 0 : remaining;
            } else {
                System.out.println("Failed to fetch credit spent. Response code: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // In case of error, return 0 remaining credits.
        return 0;
    }
    
    /**
     * Executes the provided source code using JDoodle.
     * This method constructs a JSON payload containing the source code, language, input, and credentials,
     * sends a POST request to the JDoodle API, and returns the API response as a String.
     *
     * @param sourceCode The source code to execute.
     * @param stdin The standard input for the code execution.
     * @param language The programming language ("java" or "python").
     * @return The JSON response from JDoodle as a String.
     * @throws Exception if an error occurs during the API call.
     */
    public static String executeCode(String sourceCode, String stdin, String language) throws Exception {
        // Convert language string as required by JDoodle API.
        if (language.equalsIgnoreCase("python")) {
            language = "python3";
        } else if (language.equalsIgnoreCase("java")) {
            language = "java";
        }
        
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        
        // Sanitize the source code and standard input by escaping backslashes, quotes, and newlines.
        String sanitizedSource = sourceCode
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        String sanitizedStdin = stdin
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        
        // Build the JSON payload for the request.
        String jsonPayload = "{"
                + "\"script\": \"" + sanitizedSource + "\","  // Source code to execute
                + "\"language\": \"" + language + "\","        // Programming language
                + "\"versionIndex\": \"3\","                    // Version index (can be adjusted as needed)
                + "\"stdin\": \"" + sanitizedStdin + "\","       // Standard input for the code
                + "\"clientId\": \"" + CLIENT_ID + "\","         // JDoodle client ID
                + "\"clientSecret\": \"" + CLIENT_SECRET + "\""   // JDoodle client secret
                + "}";
        
        // Write the JSON payload to the output stream.
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Read the response from the JDoodle API.
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        // Return the API response as a String.
        return response.toString();
    }    
}
