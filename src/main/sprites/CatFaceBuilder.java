package src.main.sprites;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class CatFaceBuilder {

    /**
     * Builds a high-quality cat/tiger face inspired by the CSS panda design.
     * @param eyesClosed whether the cat's eyes are closed
     * @return a styled and polished cat face
     */
    public static Node createCatFace(boolean eyesClosed) {
        Group faceGroup = new Group();

        // Head
        Circle head = new Circle(60, 60, 60);
        head.setFill(Color.web("#FEE1A8"));
        head.setStroke(Color.web("#CC9933"));
        head.setStrokeWidth(2);

        // Ears
        Circle leftEar = new Circle(20, 10, 20);
        leftEar.setFill(Color.web("#FEE1A8"));
        leftEar.setStroke(Color.web("#CC9933"));
        leftEar.setStrokeWidth(2);
        Circle rightEar = new Circle(100, 10, 20);
        rightEar.setFill(Color.web("#FEE1A8"));
        rightEar.setStroke(Color.web("#CC9933"));
        rightEar.setStrokeWidth(2);

        // Inner ears
        Circle innerLeft = new Circle(20, 10, 10);
        innerLeft.setFill(Color.PINK);
        Circle innerRight = new Circle(100, 10, 10);
        innerRight.setFill(Color.PINK);

        // Eyes
        Shape leftEye;
        Shape rightEye;
        if (eyesClosed) {
            leftEye = new Line(40, 60, 48, 60);
            rightEye = new Line(72, 60, 80, 60);
            leftEye.setStroke(Color.BLACK);
            leftEye.setStrokeWidth(2);
            rightEye.setStroke(Color.BLACK);
            rightEye.setStrokeWidth(2);
        } else {
            leftEye = new Circle(44, 60, 6, Color.BLACK);
            rightEye = new Circle(76, 60, 6, Color.BLACK);

            // Eye highlights
            Circle leftHighlight = new Circle(42, 58, 2, Color.WHITE);
            Circle rightHighlight = new Circle(74, 58, 2, Color.WHITE);
            faceGroup.getChildren().addAll(leftHighlight, rightHighlight);
        }

        // Nose
        Polygon nose = new Polygon(
            60.0, 72.0,
            64.0, 76.0,
            56.0, 76.0
        );
        nose.setFill(Color.web("#CC6633"));

        // Mouth (U shape)
        Arc mouth = new Arc(60, 80, 10, 8, 0, -180);
        mouth.setFill(null);
        mouth.setStroke(Color.BLACK);
        mouth.setStrokeWidth(2);

        // Whiskers
        Line[] whiskers = new Line[]{
            new Line(10, 65, 35, 65),
            new Line(10, 70, 35, 68),
            new Line(10, 75, 35, 72),
            new Line(85, 65, 110, 65),
            new Line(85, 70, 110, 68),
            new Line(85, 75, 110, 72),
        };
        for (Line whisker : whiskers) {
            whisker.setStroke(Color.GRAY);
        }

        // Optional tiger stripes
        Rectangle stripe1 = new Rectangle(52, 35, 3, 12);
        stripe1.setRotate(-20);
        stripe1.setFill(Color.ORANGE);

        Rectangle stripe2 = new Rectangle(65, 35, 3, 12);
        stripe2.setRotate(20);
        stripe2.setFill(Color.ORANGE);

        faceGroup.getChildren().addAll(
            head, leftEar, rightEar, innerLeft, innerRight,
            leftEye, rightEye, nose, mouth
        );
        faceGroup.getChildren().addAll(whiskers);
        faceGroup.getChildren().addAll(stripe1, stripe2);


        // 👉 Fix: shift the whole group so it's centered visually
        faceGroup.setTranslateX(650);
        faceGroup.setTranslateY(0);        

        return faceGroup;
    }
}
