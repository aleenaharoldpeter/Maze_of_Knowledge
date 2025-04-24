
package src.main.game.Classic.Fighting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionManager {
    private List<Question> questions;
    
    public QuestionManager(String filePath) {
        questions = new ArrayList<>();
        loadQuestions(filePath);
    }
    
    private void loadQuestions(String filePath) {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        String json = sb.toString().trim();
        // Remove the surrounding [ and ] if they exist.
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }
        // Split the JSON objects by "},{"
        String[] items = json.split("\\},\\{");
        for (int i = 0; i < items.length; i++){
            String item = items[i];
            if(!item.startsWith("{")) item = "{" + item;
            if(!item.endsWith("}")) item = item + "}";
            Question q = parseQuestion(item);
            if (q != null) {
                questions.add(q);
            }
        }
    }
    
    private Question parseQuestion(String jsonObj) {
        Question q = new Question();
        q.id = extractInt(jsonObj, "\"id\":", ",");
        q.question = extractString(jsonObj, "\"question\":\"", "\"");
        q.optionA = extractString(jsonObj, "\"optionA\":\"", "\"");
        q.optionB = extractString(jsonObj, "\"optionB\":\"", "\"");
        q.optionC = extractString(jsonObj, "\"optionC\":\"", "\"");
        q.optionD = extractString(jsonObj, "\"optionD\":\"", "\"");
        q.correctOption = extractInt(jsonObj, "\"correctOption\":", ",");
        q.hint = extractString(jsonObj, "\"hint\":\"", "\"");
        q.keywords = extractString(jsonObj, "\"keywords\":\"", "\"");
        q.explanation = extractString(jsonObj, "\"explanation\":\"", "\"");
        return q;
    }
    
    private int extractInt(String source, String key, String delimiter) {
        try {
            int index = source.indexOf(key);
            if (index == -1) return 0;
            int start = index + key.length();
            int end = source.indexOf(delimiter, start);
            if(end == -1) end = source.length();
            return Integer.parseInt(source.substring(start, end).trim());
        } catch (Exception e) {
            return 0;
        }
    }
    
    private String extractString(String source, String key, String delimiter) {
        try{
            int index = source.indexOf(key);
            if(index == -1) return "";
            int start = index + key.length();
            int end = source.indexOf(delimiter, start);
            if(end == -1) end = source.length();
            return source.substring(start, end);
        } catch(Exception e){
            return "";
        }
    }
    
    public Question getRandomQuestion() {
        if(questions.isEmpty()){
            return null;
        }
        Random rand = new Random();
        return questions.get(rand.nextInt(questions.size()));
    }
}


// class Question {
//     public int id;
//     public String question;
//     public String optionA, optionB, optionC, optionD;
//     public int correctOption;
//     public String hint, keywords, explanation;
// }
