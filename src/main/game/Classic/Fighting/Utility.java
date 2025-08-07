package src.main.game.Classic.Fighting;

import javafx.geometry.Rectangle2D;

public class Utility {
    public static boolean rectangularCollision(Rectangle2D r1, Rectangle2D r2) {
        return r1.intersects(r2);
    }
}
