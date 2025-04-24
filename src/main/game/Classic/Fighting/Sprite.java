package src.main.game.Classic.Fighting;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
    public Image image;
    protected double x, y;
    protected double width, height;

    // For sprite-sheet animation.
    protected int framesMax = 1;
    protected int currentFrame = 0;
    protected double frameInterval = 100; // milliseconds per frame
    protected long lastFrameTime = 0;

    public Sprite(Image image, double x, double y, double width, double height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Update animation frame (if applicable).
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime > frameInterval) {
            currentFrame = (currentFrame + 1) % framesMax;
            lastFrameTime = now;
        }
    }

    // Draw the current frame or full image.
    public void draw(GraphicsContext gc) {
        if (framesMax > 1) {
            double spriteWidth = image.getWidth() / framesMax;
            gc.drawImage(image,
                    currentFrame * spriteWidth, 0, spriteWidth, image.getHeight(),
                    x, y, width, height);
        } else {
            gc.drawImage(image, x, y, width, height);
        }
    }

    // Getters (useful for collision detection).
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
