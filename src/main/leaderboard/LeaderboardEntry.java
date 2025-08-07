package src.main.leaderboard;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeaderboardEntry {
    // Properties to hold leaderboard entry details:
    // 'name' stores the player's name.
    private final SimpleStringProperty name;
    // 'score' stores the player's score.
    private final SimpleIntegerProperty score;
    // 'time' stores the time associated with the entry (e.g., completion time).
    private final SimpleStringProperty time;
    // 'mode' stores the game mode related to the entry.
    private final SimpleStringProperty mode;
    
    /**
     * Constructs a new LeaderboardEntry with the specified details.
     *
     * @param name  The player's name.
     * @param score The player's score.
     * @param time  The time associated with the entry.
     * @param mode  The game mode for this entry.
     */
    public LeaderboardEntry(String name, int score, String time, String mode) {
        // Initialize the properties with the provided values.
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleIntegerProperty(score);
        this.time = new SimpleStringProperty(time);
        this.mode = new SimpleStringProperty(mode);
    }
    
    /**
     * Returns the name of the player.
     *
     * @return the player's name as a String.
     */
    public String getName() { 
        return name.get(); 
    }
    
    /**
     * Returns the player's score.
     *
     * @return the score as an integer.
     */
    public int getScore() { 
        return score.get(); 
    }
    
    /**
     * Returns the time associated with the leaderboard entry.
     *
     * @return the time as a String.
     */
    public String getTime() { 
        return time.get(); 
    }
    
    /**
     * Returns the game mode for this leaderboard entry.
     *
     * @return the mode as a String.
     */
    public String getMode() { 
        return mode.get(); 
    }
}
