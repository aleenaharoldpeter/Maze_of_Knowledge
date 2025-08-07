package src.main.sprites;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class individualSpriteAnimation {
    // The ImageView that displays the current frame of the animation.
    private ImageView imageView;
    // List of image frames loaded from the specified folder.
    private List<Image> frames;
    // Timeline used to cycle through the frames.
    private Timeline timeline;
    // Duration for each frame in milliseconds.
    private int frameDuration;

    /**
     * Constructs a sprite animation from individual image files.
     *
     * @param folderPath    the folder path containing PNG images for this animation.
     * @param imageView     the ImageView to update with each frame.
     * @param frameDuration duration in milliseconds for each frame.
     */
    public individualSpriteAnimation(String folderPath, ImageView imageView, int frameDuration) {
        this.imageView = imageView;
        this.frameDuration = frameDuration;
        loadFrames(folderPath);
    }

    /**
     * Loads PNG images from the specified folder.
     * The images are sorted alphabetically to ensure the correct order of animation frames.
     *
     * @param folderPath the folder containing the PNG image files.
     */
    private void loadFrames(String folderPath) {
        frames = new ArrayList<>();
        File folder = new File(folderPath);
        // Check if the folder exists and is a directory.
        if (folder.exists() && folder.isDirectory()) {
            // Filter for PNG files (case insensitive).
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (files != null && files.length > 0) {
                // Sort the files alphabetically so the animation frames play in order.
                Arrays.sort(files);
                for (File file : files) {
                    // Load each image and add it to the frames list.
                    frames.add(new Image("file:" + file.getAbsolutePath()));
                }
                // Set the initial frame in the ImageView.
                imageView.setImage(frames.get(0));
            }
        }
    }

    /**
     * Starts the animation by cycling through all frames indefinitely.
     */
    public void play() {
        // Do nothing if no frames were loaded.
        if (frames == null || frames.isEmpty()) {
            return;
        }
        // Create a Timeline that changes the frame at a fixed interval.
        timeline = new Timeline(new KeyFrame(Duration.millis(frameDuration), e -> {
            // Determine the current frame index.
            int currentIndex = frames.indexOf(imageView.getImage());
            // Calculate the next frame index, wrapping around if at the end.
            int nextIndex = (currentIndex + 1) % frames.size();
            // Update the ImageView with the next frame.
            imageView.setImage(frames.get(nextIndex));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        // Start the animation.
        timeline.play();
    }

    /**
     * Stops the animation if it is currently running.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
