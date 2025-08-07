package src.main.leaderboard;

import src.main.utils.SceneManager;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

public class LeaderboardScene {

    // Static array for difficulty filtering order.
    private static final String[] DIFFICULTY_ORDER = {"Grandmaster", "Pro", "Elite", "Beginner", "Amateur"};
    // Index to track the currently selected difficulty level.
    private static int currentDifficultyIndex = 0;

    private static String baseDir = System.getProperty("user.dir");    

    /**
     * Creates and returns the root node for the leaderboard scene.
     * This method constructs the background with particles and an image,
     * sets up the UI container with a title, difficulty filter controls, leaderboard content,
     * navigation buttons, and a back button.
     *
     * @return the Parent node representing the complete leaderboard scene.
     */
    public static Parent createRoot() {
        // 1) BACKGROUND: Create a particle pane and add a background image.
        Pane particlePane = createParticlePane();

        // Load the background image from the resources.
        // ImageView backgroundImage = new ImageView(
        //     LeaderboardScene.class.getResource("/assets/images/arena_bg.jpeg").toExternalForm()
        // );
        // Load the background image using the base directory path
        ImageView backgroundImage = new ImageView(
            new File(baseDir + "/assets/images/arena_bg.jpeg").toURI().toString()
        );        
        backgroundImage.setFitWidth(1920);
        backgroundImage.setFitHeight(1080);
        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);

        // Create a semi-transparent overlay rectangle for visual effect.
        Rectangle overlayRect = new Rectangle(1920, 1080, Color.color(0, 0, 0, 0.4));
        // Combine background image, overlay, and particle effects into a StackPane.
        StackPane backgroundLayer = new StackPane(backgroundImage, overlayRect, particlePane);
        backgroundLayer.setAlignment(Pos.CENTER);

        // 2) MAIN UI CONTAINER: Set up a BorderPane with padding and a fade-in effect.
        BorderPane mainContainer = new BorderPane();
        mainContainer.setPadding(new Insets(20));

        FadeTransition sceneFadeIn = new FadeTransition(Duration.seconds(1.2), mainContainer);
        sceneFadeIn.setFromValue(0);
        sceneFadeIn.setToValue(1);
        sceneFadeIn.play();

        // 3) TITLE LABEL and Difficulty Filter Controls.
        Label titleLabel = new Label("Arena of Glory\nClassic Leaderboard");
        titleLabel.setFont(Font.font("Verdana", 48));
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.setEffect(new DropShadow(15, Color.BLACK));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Difficulty arrow button (difficulty label is commented out).
        Button difficultyArrow = new Button("➤");
        difficultyArrow.setFont(Font.font("Verdana", 20));
        difficultyArrow.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: white;");
        // Add a subtle translate animation to the arrow for visual appeal.
        TranslateTransition arrowTransition = new TranslateTransition(Duration.seconds(0.3), difficultyArrow);
        arrowTransition.setFromX(0);
        arrowTransition.setToX(5);
        arrowTransition.setCycleCount(TranslateTransition.INDEFINITE);
        arrowTransition.setAutoReverse(true);
        arrowTransition.play();

        // On click, cycle through difficulty levels and update the center content with a fade transition.
        difficultyArrow.setOnAction(e -> {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % DIFFICULTY_ORDER.length;
            Parent newCenter = buildLeaderboardCenter();
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), newCenter);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            mainContainer.setCenter(newCenter);
        });

        // Combine the title label and difficulty arrow in an HBox.
        HBox titleBox = new HBox(20, titleLabel, difficultyArrow);
        titleBox.setAlignment(Pos.CENTER);
        mainContainer.setTop(titleBox);

        // 4) Build the leaderboard center content based on current difficulty filter.
        Parent centerContent = buildLeaderboardCenter();
        mainContainer.setCenter(centerContent);

        // 7) RIGHT-SIDE ARROW BUTTON for navigating to the Adventure Leaderboard.
        Button arrowButton = new Button("➤");
        arrowButton.setFont(Font.font("Verdana", 30));
        arrowButton.setTextFill(Color.WHITE);
        arrowButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(68, 68, 68, 0.9), new CornerRadii(40), Insets.EMPTY)));
        arrowButton.setPrefSize(70, 70);
        arrowButton.setStyle("-fx-cursor: hand;");
        ScaleTransition arrowScale = new ScaleTransition(Duration.seconds(0.3), arrowButton);
        arrowButton.setOnMouseEntered(e -> {
            arrowScale.setToX(1.3);
            arrowScale.setToY(1.3);
            arrowScale.playFromStart();
        });
        arrowButton.setOnMouseExited(e -> {
            arrowScale.setToX(1.0);
            arrowScale.setToY(1.0);
            arrowScale.playFromStart();
        });
        arrowButton.setOnAction(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(300), arrowButton);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(evt -> SceneManager.showAdventureLeaderboard());
            fade.play();
        });
        BorderPane.setAlignment(arrowButton, Pos.CENTER_RIGHT);
        mainContainer.setRight(arrowButton);

        // 8) BOTTOM BUTTON: "Back to Main Menu" remains unchanged.
        Button backButton = new Button("Back to Main Menu");
        backButton.setFont(Font.font("Verdana", 22));
        backButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(68, 68, 68, 0.9), new CornerRadii(15), Insets.EMPTY)));
        backButton.setTextFill(Color.WHITE);
        backButton.setPadding(new Insets(12, 30, 12, 30));
        backButton.setStyle("-fx-cursor: hand;");
        backButton.setOnAction(e -> SceneManager.showMainMenu());
        backButton.setOnMouseEntered(e -> backButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(88, 88, 88, 0.9), new CornerRadii(15), Insets.EMPTY))));
        backButton.setOnMouseExited(e -> backButton.setBackground(new Background(new BackgroundFill(
                Color.rgb(68, 68, 68, 0.9), new CornerRadii(15), Insets.EMPTY))));
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        mainContainer.setBottom(bottomBox);

        // Combine the background and main UI container in a StackPane.
        StackPane root = new StackPane(backgroundLayer, mainContainer);
        return root;
    }

    /**
     * Builds the center content of the leaderboard scene based on the current difficulty filter.
     *
     * @return a Parent node containing the leaderboard entries.
     */
    private static Parent buildLeaderboardCenter() {
        // Load all leaderboard entries.
        List<LeaderboardEntry> allEntries = LeaderboardService.loadLeaderboard();
        // Map difficulty levels to ranking values.
        Map<String, Integer> difficultyRank = new HashMap<>();
        difficultyRank.put("Grandmaster", 1);
        difficultyRank.put("Pro", 2);
        difficultyRank.put("Elite", 3);
        difficultyRank.put("Beginner", 4);
        difficultyRank.put("Amateur", 5);
        // Sort entries by difficulty, then by score (descending), then by name.
        List<LeaderboardEntry> classicEntries = allEntries.stream()
            .sorted(Comparator.comparingInt((LeaderboardEntry e) -> 
                    difficultyRank.getOrDefault(e.getTime(), Integer.MAX_VALUE))
                    .thenComparing(LeaderboardEntry::getScore, Comparator.reverseOrder())
                    .thenComparing(LeaderboardEntry::getName))
            .collect(Collectors.toList());
        
        // Filter entries based on the selected difficulty.
        String filter = DIFFICULTY_ORDER[currentDifficultyIndex];
        List<LeaderboardEntry> filteredEntries = classicEntries.stream()
            .filter(e -> e.getTime().equalsIgnoreCase(filter))
            .collect(Collectors.toList());

        // Build a container for the top 3 leaderboard entries.
        ObservableList<LeaderboardEntry> top3 = FXCollections.observableArrayList();
        if (!filteredEntries.isEmpty()) {
            top3.addAll(filteredEntries.subList(0, Math.min(filteredEntries.size(), 3)));
        }
        HBox topThreeContainer = new HBox(40);
        topThreeContainer.setAlignment(Pos.CENTER);
        for (int i = 0; i < top3.size(); i++) {
            LeaderboardEntry entry = top3.get(i);
            VBox podiumCard = new VBox(10);
            podiumCard.setAlignment(Pos.CENTER);
            podiumCard.setPadding(new Insets(20));
            podiumCard.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255, 255, 255, 0.2), new CornerRadii(20), Insets.EMPTY)));
            podiumCard.setEffect(new DropShadow(25, Color.BLACK));
            Reflection reflection = new Reflection();
            reflection.setFraction(0.6);
            Glow glow = new Glow(0.3);
            reflection.setInput(glow);
            podiumCard.setEffect(reflection);
            String borderColor;
            switch (i) {
                case 0: borderColor = "gold"; break;
                case 1: borderColor = "silver"; break;
                case 2: borderColor = "#cd7f32"; break;
                default: borderColor = "white";
            }
            podiumCard.setStyle(
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 5px;" +
                "-fx-border-radius: 20px;" +
                "-fx-background-radius: 20px;"
            );
            String rankText = (i == 0 ? "GOLD" : i == 1 ? "SILVER" : "BRONZE");
            Label rankLabel = new Label(rankText);
            rankLabel.setFont(Font.font("Verdana", 26));
            rankLabel.setTextFill(i == 0 ? Color.GOLD : (i == 1 ? Color.SILVER : Color.web("#cd7f32")));
            rankLabel.setEffect(new DropShadow(5, Color.BLACK));
            ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.0), rankLabel);
            pulse.setFromX(1.0);
            pulse.setToX(1.15);
            pulse.setFromY(1.0);
            pulse.setToY(1.15);
            pulse.setCycleCount(Animation.INDEFINITE);
            pulse.setAutoReverse(true);
            pulse.play();
            RotateTransition rotate = new RotateTransition(Duration.seconds(1.0), rankLabel);
            rotate.setFromAngle(-3);
            rotate.setToAngle(3);
            rotate.setCycleCount(Animation.INDEFINITE);
            rotate.setAutoReverse(true);
            rotate.play();
            Label nameLabel = new Label(entry.getName());
            nameLabel.setFont(Font.font("Verdana", 22));
            nameLabel.setTextFill(Color.WHITE);
            nameLabel.setEffect(new DropShadow(5, Color.BLACK));
            Label scoreLabel = new Label(String.valueOf(entry.getScore()));
            scoreLabel.setFont(Font.font("Verdana", 20));
            scoreLabel.setTextFill(Color.LIGHTGRAY);
            scoreLabel.setEffect(new DropShadow(3, Color.BLACK));
            Label difficultyLabelEntry = new Label(entry.getTime());
            difficultyLabelEntry.setFont(Font.font("Verdana", 18));
            difficultyLabelEntry.setTextFill(Color.WHITE);
            difficultyLabelEntry.setEffect(new DropShadow(3, Color.BLACK));
            String gamePlayed = entry.getMode();
            if(gamePlayed.equalsIgnoreCase("Classic")){
                gamePlayed = "Quiz";
            } else if(gamePlayed.equalsIgnoreCase("Matching")){
                gamePlayed = "Tiles";
            }
            Label gameLabel = new Label(gamePlayed);
            gameLabel.setFont(Font.font("Verdana", 16));
            gameLabel.setTextFill(Color.WHITE);
            gameLabel.setEffect(new DropShadow(3, Color.BLACK));
            TranslateTransition bounceTrans = new TranslateTransition(Duration.seconds(1.2), podiumCard);
            bounceTrans.setFromY(-15);
            bounceTrans.setToY(0);
            bounceTrans.play();
            podiumCard.getChildren().addAll(rankLabel, nameLabel, scoreLabel, difficultyLabelEntry, gameLabel);
            topThreeContainer.getChildren().add(podiumCard);
        }
        VBox topContainer = new VBox(30, topThreeContainer);
        topContainer.setAlignment(Pos.CENTER);
        
        // Build the container for remaining leaderboard entries.
        VBox remainingContainer = new VBox(12);
        remainingContainer.setAlignment(Pos.TOP_CENTER);
        for (int i = 3; i < filteredEntries.size(); i++) {
            LeaderboardEntry entry = filteredEntries.get(i);
            GridPane rowGrid = new GridPane();
            rowGrid.setHgap(20);
            rowGrid.setPadding(new Insets(8));
            rowGrid.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255,255,255,0.2), new CornerRadii(10), Insets.EMPTY)));
            rowGrid.setEffect(new DropShadow(5, Color.BLACK));
            ColumnConstraints c1 = new ColumnConstraints(60);
            ColumnConstraints c2 = new ColumnConstraints(100);
            ColumnConstraints c3 = new ColumnConstraints(100);
            ColumnConstraints c4 = new ColumnConstraints(150);
            ColumnConstraints c5 = new ColumnConstraints(150);
            c1.setHalignment(HPos.CENTER);
            c2.setHalignment(HPos.LEFT);
            c3.setHalignment(HPos.RIGHT);
            c4.setHalignment(HPos.CENTER);
            c5.setHalignment(HPos.CENTER);
            rowGrid.getColumnConstraints().addAll(c1, c2, c3, c4, c5);
            Label rankLabel = new Label(String.valueOf(i + 1));
            rankLabel.setFont(Font.font("Verdana", 18));
            rankLabel.setTextFill(Color.ORANGE);
            rowGrid.add(rankLabel, 0, 0);
            Label nameLabel = new Label(entry.getName());
            nameLabel.setFont(Font.font("Verdana", 18));
            nameLabel.setTextFill(Color.WHITE);
            rowGrid.add(nameLabel, 1, 0);
            Label scoreLabel = new Label(String.valueOf(entry.getScore()));
            scoreLabel.setFont(Font.font("Verdana", 18));
            scoreLabel.setTextFill(Color.LIGHTGRAY);
            rowGrid.add(scoreLabel, 2, 0);
            Label diffLabel = new Label(entry.getTime());
            diffLabel.setFont(Font.font("Verdana", 18));
            diffLabel.setTextFill(Color.WHITE);
            rowGrid.add(diffLabel, 3, 0);
            String gamePlayed = entry.getMode();
            if(gamePlayed == null || gamePlayed.trim().isEmpty()){
                gamePlayed = "N/A";
            } else if(gamePlayed.equalsIgnoreCase("Classic")){
                gamePlayed = "Quiz";
            } else if(gamePlayed.equalsIgnoreCase("Matching") || gamePlayed.equalsIgnoreCase("Matching Tiles")){
                gamePlayed = "Tiles";
            }
            Label gameLabel = new Label(gamePlayed);
            gameLabel.setFont(Font.font("Verdana", 18));
            gameLabel.setTextFill(Color.WHITE);
            rowGrid.add(gameLabel, 4, 0);
            // Change background opacity on mouse hover.
            rowGrid.setOnMouseEntered(e -> rowGrid.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255,255,255,0.3), new CornerRadii(10), Insets.EMPTY))));
            rowGrid.setOnMouseExited(e -> rowGrid.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255,255,255,0.2), new CornerRadii(10), Insets.EMPTY))));
            // Fade in each row with a slight delay.
            rowGrid.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), rowGrid);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.seconds(0.02 * i));
            ft.play();
            remainingContainer.getChildren().add(rowGrid);
        }
        // Wrap remaining entries in a ScrollPane with custom scroll behavior.
        ScrollPane scrollPane = new ScrollPane(remainingContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMaxWidth(600);
        scrollPane.setMinWidth(600);
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY() * 3.5;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());
            event.consume();
        });
        VBox centerVBox = new VBox(40, topContainer, scrollPane);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setMaxWidth(600);
        centerVBox.setMinWidth(600);
        return centerVBox;
    }

    /**
     * Creates a particle pane that continuously generates animated particles as a background effect.
     *
     * @return a Pane containing the particle animations.
     */
    private static Pane createParticlePane() {
        Pane pane = new Pane();
        pane.setPrefSize(800, 600);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // With a 2% chance per frame, create a particle.
                if (Math.random() < 0.02) {
                    Circle particle = new Circle(Math.random() * 3 + 2, Color.web("rgba(255,255,255,0.6)"));
                    particle.setTranslateX(Math.random() * pane.getWidth());
                    particle.setTranslateY(pane.getHeight());
                    pane.getChildren().add(particle);
                    double duration = 3 + Math.random() * 2;
                    TranslateTransition trans = new TranslateTransition(Duration.seconds(duration), particle);
                    trans.setFromY(pane.getHeight());
                    trans.setToY(-10);
                    FadeTransition fade = new FadeTransition(Duration.seconds(duration), particle);
                    fade.setFromValue(1.0);
                    fade.setToValue(0.0);
                    trans.setOnFinished(e -> pane.getChildren().remove(particle));
                    trans.play();
                    fade.play();
                }
            }  
        };
        timer.start();
        return pane;
    }
}
