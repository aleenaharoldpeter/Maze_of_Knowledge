package src.main.game.Classic.Fighting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

public class Fireball extends Sprite {
    // Falling speed (pixels per frame)
    private double speedY = 5;
    // For pulsating effect
    private double pulsateTime = 0;
    // Store original dimensions for scale adjustments.
    private double origWidth, origHeight;
    
    public Fireball(Image image, double x, double y, double width, double height) {
        // Call base sprite constructor.
        super(image, x, y, width, height);
        origWidth = width;
        origHeight = height;
    }
    
    // Update method: move the fireball downward and update pulsation.
    public void update(double canvasHeight) {
        y += speedY;
        pulsateTime += 0.1;
    }
    
    // Draw the fireball with a pulsating scale.
    @Override
    public void draw(GraphicsContext gc) {
        double scale = 1 + 0.1 * Math.sin(pulsateTime);
        double drawWidth = origWidth * scale;
        double drawHeight = origHeight * scale;
        gc.drawImage(image, x, y, drawWidth, drawHeight);
    }
    
    // Check if the fireball has fallen off the bottom.
    public boolean isOffScreen(double canvasHeight) {
        return y > canvasHeight;
    }
    
    // Get the current bounding rectangle (for collision detection).
    public Rectangle2D getBounds() {
        double scale = 1 + 0.1 * Math.sin(pulsateTime);
        double drawWidth = origWidth * scale;
        double drawHeight = origHeight * scale;
        return new Rectangle2D(x, y, drawWidth, drawHeight);
    }
}
