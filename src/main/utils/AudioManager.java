package src.main.utils;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.io.File;
import java.net.URL;

public class AudioManager {
    // Audio clip instances for different sounds
    private static AudioClip applauseClip;
    private static AudioClip wrongClip;
    private static AudioClip tileFlipClip;
    
    /**
     * Initializes the audio clips from resource files.
     * Sets the volume based on global preferences.
     * Logs an error if a resource is not found.
     */
    public static void initialize() {
        String baseDir = System.getProperty("user.dir");

        try {
            // Applause sound
            File applauseFile = new File(baseDir + "/assets/sounds/applause.mp3");
            URL applauseURL = applauseFile.toURI().toURL();
            applauseClip = new AudioClip(applauseURL.toExternalForm());
            applauseClip.setVolume(PreferencesManager.getGlobalVolume());

            // Wrong sound
            File wrongFile = new File(baseDir + "/assets/sounds/wrong.mp3");
            URL wrongURL = wrongFile.toURI().toURL();
            wrongClip = new AudioClip(wrongURL.toExternalForm());
            wrongClip.setVolume(PreferencesManager.getGlobalVolume());

            // Tile flip sound
            File tileFlipFile = new File(baseDir + "/assets/sounds/tileflip.mp3");
            URL tileFlipURL = tileFlipFile.toURI().toURL();
            tileFlipClip = new AudioClip(tileFlipURL.toExternalForm());
            tileFlipClip.setVolume(PreferencesManager.getGlobalVolume());

        } catch (Exception e) {
            System.err.println("Error loading audio files: " + e.getMessage());
            e.printStackTrace();
        }
    }


    
    /**
     * Plays the applause sound if sound is enabled.
     */
    public static void playApplause() {
        if (PreferencesManager.getSoundEnabled() && applauseClip != null) {
            applauseClip.play();
        }
    }
    
    /**
     * Plays the wrong answer sound if sound is enabled.
     */
    public static void playWrong() {
        if (PreferencesManager.getSoundEnabled() && wrongClip != null) {
            wrongClip.play();
        }
    }

    /**
     * Plays the tile flip sound if sound is enabled.
     */
    public static void playTileFlip() {
        if (PreferencesManager.getSoundEnabled() && tileFlipClip != null) {
            tileFlipClip.play();
        }
    }
    
    /**
     * Updates the volume of all audio clips.
     * 
     * @param volume The new volume level to be set for each audio clip.
     */
    public static void updateVolume(double volume) {
        if (applauseClip != null) applauseClip.setVolume(volume);
        if (wrongClip != null) wrongClip.setVolume(volume);
        if (tileFlipClip != null) tileFlipClip.setVolume(volume);
    }
}
