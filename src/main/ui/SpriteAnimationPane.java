package src.main.ui;

import src.main.utils.PreferencesManager;
import src.main.utils.SceneManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SpriteAnimationPane extends Pane {
    // Dimensions for each frame of the sprite.
    private static final int FRAME_WIDTH = 64;
    private static final int FRAME_HEIGHT = 64;
    // Total number of frames in the animation.
    private static final int ANIMATION_FRAMES = 9;
    // Number of pixels to move per update.
    private static final int STEP = 2;
    
    // Current horizontal position of the sprite.
    private double posX = 0;
    // Vertical position is calculated based on pane height.
    private double posY;
    // Current frame index for the sprite animation.
    private int currentFrame = 0;
    
    // Enumeration for the direction in which the sprite is moving.
    private enum Direction { RIGHT, LEFT }
    // Initially, the sprite is moving to the right.
    private Direction currentDirection = Direction.RIGHT;
    
    // Images for the sprite facing right and left.
    private Image rightImage;
    private Image leftImage;
    // ImageView to display the sprite.
    private ImageView imageView;
    // Current Directory Path
    private static final String BASE_DIR = System.getProperty("user.dir");    
    
    /**
     * Constructs a SpriteAnimationPane.
     * Loads the sprite images based on the selected player,
     * sets the initial sprite position and viewport,
     * and starts the movement and frame animations.
     */
    public SpriteAnimationPane() {
        // Set preferred size for the pane that will hold the mascot sprite.
        setPrefSize(150, 150);
        // Set the vertical position so that the sprite appears at the bottom of the pane.
        posY = getPrefHeight() - FRAME_HEIGHT;
        
        // Retrieve the selected player's name from preferences.
        String selectedPlayer = PreferencesManager.getSelectedPlayer();
        // Build the path to the player's sprite folder.
        String basePath = BASE_DIR + "/assets/Sprite/Player/" + selectedPlayer + "/Walking/";
        // Load the sprite images for right and left movement.
        rightImage = new Image("file:" + basePath + "right_walk.png");
        leftImage = new Image("file:" + basePath + "left_walk.png");
        
        // Initialize the ImageView with the right-facing sprite.
        imageView = new ImageView(rightImage);
        // Set the viewport to display only the first frame.
        imageView.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_HEIGHT));
        // Position the ImageView at the initial coordinates.
        imageView.setLayoutX(posX);
        imageView.setLayoutY(posY);
        // Add the ImageView to the pane.
        getChildren().add(imageView);
        
        // Start the animation for movement and frame changes.
        startAnimation();
    }
    
    /**
     * Starts the movement and frame animation timelines.
     * One timeline updates the position of the sprite,
     * while the other updates the displayed animation frame.
     */
    private void startAnimation() {
        // Movement timeline: update position every 20 milliseconds.
        Timeline movementTimeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            updatePosition();
            imageView.setLayoutX(posX);
        }));
        movementTimeline.setCycleCount(Timeline.INDEFINITE);
        movementTimeline.play();
        
        // Frame animation timeline: update sprite frame every 150 milliseconds.
        Timeline animationTimeline = new Timeline(new KeyFrame(Duration.millis(150), event -> {
            // Cycle through frames, then loop back to the first frame.
            currentFrame = (currentFrame + 1) % ANIMATION_FRAMES;
            imageView.setViewport(new Rectangle2D(currentFrame * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
        }));
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }
    
    /**
     * Updates the sprite's horizontal position based on the current direction.
     * When the sprite reaches the edge of the pane, it reverses direction and updates the sprite image.
     */
    private void updatePosition() {
        // Determine the width of the pane, defaulting to preferred width if actual width is not set.
        double paneWidth = getWidth() > 0 ? getWidth() : getPrefWidth();
        switch (currentDirection) {
            case RIGHT:
                posX += STEP;
                // If the sprite reaches the right edge, reverse direction.
                if (posX >= paneWidth - FRAME_WIDTH) {
                    posX = paneWidth - FRAME_WIDTH;
                    currentDirection = Direction.LEFT;
                    currentFrame = 0; // Reset frame animation.
                    imageView.setImage(leftImage);
                }
                break;
            case LEFT:
                posX -= STEP;
                // If the sprite reaches the left edge, reverse direction.
                if (posX <= 0) {
                    posX = 0;
                    currentDirection = Direction.RIGHT;
                    currentFrame = 0; // Reset frame animation.
                    imageView.setImage(rightImage);
                }
                break;
        }
    }
}
