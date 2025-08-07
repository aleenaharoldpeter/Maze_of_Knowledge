package src.main.quiz;

import java.util.Properties;
import src.main.config.ConfigLoader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionService {
    // Declare the variables
    private static final String QUESTIONBANK_API_URL;
    private static final String SUPABASE_API_KEY;

    // Initialize them in the static block
    static {
        Properties config = ConfigLoader.loadConfig();
        // Supabase API URL for the question bank table.
        QUESTIONBANK_API_URL = config.getProperty("SUPABASE_API_QUESTIONBANK_URL");
        // Supabase API key used for authentication.
        SUPABASE_API_KEY = config.getProperty("SUPABASE_API_KEY");
    }    
    // HttpClient instance for sending HTTP requests.
    private static HttpClient httpClient = HttpClient.newHttpClient();
    // Gson instance for JSON parsing.
    private static Gson gson = new Gson();

    private static String baseDir = System.getProperty("user.dir");
    
    /**
     * Loads questions using a cache mechanism.
     * It first loads cached questions from a local JSON file, computes their hash,
     * then fetches remote questions from Supabase and compares hashes.
     * If the remote questions differ, the cache is updated.
     * Finally, the remote questions (or cached questions as fallback) are converted into Question objects.
     *
     * @return a shuffled list of Question objects.
     */
    public static List<Question> loadQuestionsWithCache() {
        List<Question> questionList = new ArrayList<>();
        try {
            // Load questions from the local cache.
            JsonArray cachedQuestions = loadCachedQuestions();
            String cachedHash = computeHash(cachedQuestions.toString());
            System.out.println("Cached questions count: " + cachedQuestions.size());
            
            // Fetch questions from Supabase.
            JsonArray remoteQuestions = fetchQuestionsFromSupabase();
            if (remoteQuestions == null) remoteQuestions = cachedQuestions;
            String remoteHash = computeHash(remoteQuestions.toString());
            System.out.println("Remote questions count: " + remoteQuestions.size());
            
            // If the remote hash differs, update the cache.
            if (!remoteHash.equals(cachedHash)) {
                saveQuestionsToCache(remoteQuestions);
                System.out.println("Cache updated with remote questions.");
            } else {
                System.out.println("Cached questions are up-to-date.");
            }
            
            // Convert the JSON array into Question objects.
            for (int i = 0; i < remoteQuestions.size(); i++) {
                JsonObject obj = remoteQuestions.get(i).getAsJsonObject();
                // Check that required fields exist.
                if (obj.get("question") == null ||
                    obj.get("optionA") == null ||
                    obj.get("optionB") == null ||
                    obj.get("optionC") == null ||
                    obj.get("optionD") == null ||
                    obj.get("correctOption") == null) {
                    System.out.println("Skipping question at index " + i + " due to missing field.");
                    continue;
                }
                // Extract fields from the JSON object.
                String qText = obj.get("question").getAsString();
                String optionA = obj.get("optionA").getAsString();
                String optionB = obj.get("optionB").getAsString();
                String optionC = obj.get("optionC").getAsString();
                String optionD = obj.get("optionD").getAsString();
                int correct = obj.get("correctOption").getAsInt();
                // Get the hint if provided; default to empty string.
                String hint = obj.has("hint") ? obj.get("hint").getAsString() : "";    
                // New fields: keywords and explanation.
                String keywords = obj.has("keywords") ? obj.get("keywords").getAsString() : "";
                String explanation = obj.has("explanation") ? obj.get("explanation").getAsString() : "";
                            
                // Create a new Question object and add it to the list.
                questionList.add(new Question(qText, new String[]{optionA, optionB, optionC, optionD}, correct, hint, keywords, explanation));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // If no questions were loaded, fall back to hardcoded questions.
        if (questionList.isEmpty()) {
            System.out.println("No questions loaded. Falling back to hardcoded questions.");
            questionList = initializeHardcodedQuestions();
        }
        // Shuffle the list of questions randomly.
        Collections.shuffle(questionList, new Random());
        return questionList;
    }
    
    /**
     * Loads cached questions from a local file ("questions_cache.json").
     *
     * @return a JsonArray containing cached questions, or an empty JsonArray if none exist.
     */
    private static JsonArray loadCachedQuestions() {
        // File cacheFile = new File("questions_cache.json");
        // private final String baseDir = System.getProperty("user.dir");
        File cacheFile = new File(baseDir, "resources/questions/questions_cache.json");        
        if (cacheFile.exists()) {
            try (FileReader reader = new FileReader(cacheFile)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonArray()) {
                    return element.getAsJsonArray();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new JsonArray();
    }
    
    /**
     * Saves the given questions JsonArray to the local cache file ("questions_cache.json").
     *
     * @param questions the JsonArray of questions to save.
     */
    private static void saveQuestionsToCache(JsonArray questions) {
        // File cacheFile = new File("questions_cache.json");
        // private final String baseDir = System.getProperty("user.dir");
        File cacheFile = new File(baseDir, "resources/questions/questions_cache.json"); 
        try (FileWriter writer = new FileWriter(cacheFile)) {
            writer.write(questions.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Fetches questions from the Supabase question bank via an HTTP GET request.
     *
     * @return a JsonArray containing remote questions, or an empty JsonArray if the request fails.
     */
    private static JsonArray fetchQuestionsFromSupabase() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QUESTIONBANK_API_URL + "?select=*"))
                .header("apikey", SUPABASE_API_KEY)
                .header("Authorization", "Bearer " + SUPABASE_API_KEY)
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement element = JsonParser.parseString(response.body());
                if (element.isJsonArray()) {
                    return element.getAsJsonArray();
                } else {
                    System.out.println("Remote questions not in array format: " + response.body());
                    return new JsonArray();
                }
            } else {
                System.out.println("Failed to fetch questions: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new JsonArray();
    }
    
    /**
     * Computes an MD5 hash for the given data.
     *
     * @param data the input string.
     * @return the MD5 hash of the data as a hexadecimal string.
     * @throws Exception if the MD5 algorithm is not available.
     */
    private static String computeHash(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Provides a fallback list of hardcoded questions.
     *
     * @return a List of Question objects representing hardcoded questions.
     */
    public static List<Question> initializeHardcodedQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("What is the capital of France?", new String[]{"Paris", "Lyon", "Marseille", "Nice"}, 0, "Starts with P", "", ""));
        questions.add(new Question("What is the largest planet in our Solar System?", new String[]{"Earth", "Mars", "Jupiter", "Saturn"}, 2, "Starts with J", "", ""));
        questions.add(new Question("Which element has the chemical symbol 'O'?", new String[]{"Gold", "Oxygen", "Silver", "Iron"}, 1, "Starts with O", "", ""));
        questions.add(new Question("What is the fastest land animal?", new String[]{"Cheetah", "Lion", "Leopard", "Gazelle"}, 0, "Starts with C", "", ""));
        questions.add(new Question("Who painted the Mona Lisa?", new String[]{"Mark Twain", "Charles Dickens", "William Shakespeare", "Leonardo da Vinci"}, 3, "Starts with L", "", ""));
        questions.add(new Question("What is the boiling point of water (°C)?", new String[]{"90°C", "100°C", "110°C", "120°C"}, 1, "NO hints for obvious Quesitions!!", "", ""));
        questions.add(new Question("Which gas do plants absorb from the atmosphere?", new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Helium"}, 2, "NO hints for obvious Quesitions!!", "", ""));
        questions.add(new Question("What is the largest ocean on Earth?", new String[]{"Atlantic", "Indian", "Arctic", "Pacific"}, 3, "NO hints for obvious Quesitions!!", "", ""));
        questions.add(new Question("Who is known as the father of computers?", new String[]{"Albert Einstein", "Charles Babbage", "Isaac Newton", "Nikola Tesla"}, 1, "NO hints for obvious Quesitions!!", "", ""));
        questions.add(new Question("What is H2O?", new String[]{"Hydrogen", "Helium", "Water", "Oxygen"}, 2, "NO hints for obvious Quesitions!!", "", ""));
        return questions;
    }
}
