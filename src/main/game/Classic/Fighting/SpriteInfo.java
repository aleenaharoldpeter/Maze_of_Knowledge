package src.main.game.Classic.Fighting;

import javafx.scene.image.Image;

public class SpriteInfo {
    private Image image;
    private int framesMax;

    public SpriteInfo(String imagePath, int framesMax) {
        this.image = new Image(imagePath);
        this.framesMax = framesMax;
    }

    public Image getImage() {
        return image;
    }

    public int getFramesMax() {
        return framesMax;
    }
}
