package src.main.game.Classic.RetroRunning;

import src.main.utils.SceneManager;
import src.main.auth.SessionManager;
import src.main.leaderboard.LeaderboardService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.File;
import java.util.*;

public class PixelRetroRunner extends Application {

    // Base settings
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;
    private static final int GROUND_HEIGHT = 50;
    private static final int SCALE = 5; // Scale for pixel art

    // Stage reference.
    private Stage primaryStage;

    // Game state variables.
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private boolean pausedForQuiz = false;
    
    // Day/Night & Weather settings.
    private boolean isDay = true;
    private String currentWeather = "sunny";  // "sunny", "rainy", "snow", "autumn"
    private Random rand = new Random();
    private int lastWeatherChangeScore = 0;
    private boolean meteorScene = false;
    private long meteorStartTime = 0;
    
    // Weather sound media player.
    private MediaPlayer weatherPlayer = null;

    // Player settings.
    private static final int RUNNER_PIXEL_WIDTH = 8;
    private static final int RUNNER_PIXEL_HEIGHT = 10;
    private double runnerX = 100;
    private double runnerY;
    private double runnerVelocityY = 0;
    private final double GRAVITY = 0.5;
    private final double JUMP_STRENGTH = -10;
    private int jumpCount = 0;
    private int runnerAnimationCounter = 0;

    // Obstacles: "cactus" or "pine"
    private static final int OBSTACLE_PIXEL_WIDTH = 6;
    private static final int OBSTACLE_PIXEL_HEIGHT = 20;
    private double baseObstacleSpeed = 4;
    private static class Obstacle {
        double x, y;
        boolean quizTriggered = false;
        String type;
        public Obstacle(double x, double y, String type) {
            this.x = x; this.y = y; this.type = type;
        }
    }
    private List<Obstacle> obstacles = new LinkedList<>();
    
    // Enemies (e.g., pterodactyls)
    private double baseEnemySpeed = 5;
    private static class Enemy {
        double x, y;
        boolean quizTriggered = false;
        public Enemy(double x, double y) { this.x = x; this.y = y; }
    }
    private List<Enemy> enemies = new LinkedList<>();
    
    // Coins – animated coin with 4 frames.
    private static class Coin {
        double x, y;
        public Coin(double x, double y) { this.x = x; this.y = y; }
    }
    private List<Coin> coins = new LinkedList<>();
    private int coinCombo = 0;
    private Timeline comboResetTimeline;
    private int coinAnimationCounter = 0;
    
    private int thunderbolts = 10;
    private static final int MAX_THUNDERBOLTS = 20;
    private List<Thunderbolt> thunderShots = new LinkedList<>();

    private static class Thunderbolt {
        double x, y, speed = 10;
        public Thunderbolt(double x, double y) { this.x = x; this.y = y; }
    }

    // Power-Ups: shield, magnet, thunder.
    private static class PowerUp {
        double x, y;
        String type;
        public PowerUp(double x, double y, String type) { this.x = x; this.y = y; this.type = type; }
    }
    private List<PowerUp> powerUps = new LinkedList<>();
    private boolean shieldActive = false;
    private boolean magnetActive = false;
    
    // Boss enemy – a spaceship.
    private static class Boss {
        double x, y;
        int health;
        boolean quizTriggered = false;
        int flashCounter = 0;
        int bossTimer;
        int bulletTimer;
        public Boss(double x, double y, int health, int timer) {
            this.x = x; this.y = y; this.health = health; this.bossTimer = timer;
            this.bulletTimer = 50;
        }
    }
    private Boss boss = null;
    private int bossAnimationCounter = 0;
    
    // Boss bullets.
    private static class BossBullet {
        double x, y;
        double speedX, speedY;
        public BossBullet(double x, double y, double speedX, double speedY) {
            this.x = x; this.y = y; this.speedX = speedX; this.speedY = speedY;
        }
    }
    private List<BossBullet> bossBullets = new LinkedList<>();
    
    // Meteors.
    private static class Meteor {
        double x, y;
        double speedY;
        public Meteor(double x, double y, double speedY) { this.x = x; this.y = y; this.speedY = speedY; }
    }
    private List<Meteor> meteors = new LinkedList<>();
    
    // Starfield background.
    private static class Star {
        double x, y, speed;
        public Star(double x, double y, double speed) { this.x = x; this.y = y; this.speed = speed; }
    }
    private List<Star> stars = new LinkedList<>();
    
    // Autumn leaves.
    private static class Leaf {
        double x, y;
        double speedY;
        public Leaf(double x, double y, double speedY) {
            this.x = x; this.y = y; this.speedY = speedY;
        }
    }
    private List<Leaf> autumnLeaves = new LinkedList<>();
    
    // Score, lives, level.
    private int score = 0;
    private int lives = 10;
    private int level = 1;
    
    // Quiz questions.
    // private final List<Map.Entry<String, String>> questions = Arrays.asList(
    //     new AbstractMap.SimpleEntry<>("What is 2 + 2?", "4"),
    //     new AbstractMap.SimpleEntry<>("What is 5 - 3?", "2"),
    //     new AbstractMap.SimpleEntry<>("What is 3 * 3?", "9"),
    //     new AbstractMap.SimpleEntry<>("What is 10 / 2?", "5"),
    //     new AbstractMap.SimpleEntry<>("What is 7 + 3?", "10"),
    //     new AbstractMap.SimpleEntry<>("What is 8 - 5?", "3"),
    //     new AbstractMap.SimpleEntry<>("What is 9 - 6?", "3"),
    //     new AbstractMap.SimpleEntry<>("What is 12 / 3?", "4")
    // );
    private final List<Map.Entry<String, String>> questions = Arrays.asList(
        // 1–10: Basic Arithmetic
        new AbstractMap.SimpleEntry<>("What is 15 + 27?", "42"),
        new AbstractMap.SimpleEntry<>("What is 144 - 78?", "66"),
        new AbstractMap.SimpleEntry<>("What is 12 * 13?", "156"),
        new AbstractMap.SimpleEntry<>("What is 144 / 12?", "12"),
        new AbstractMap.SimpleEntry<>("What is 2 raised to the power of 5?", "32"),
        new AbstractMap.SimpleEntry<>("What is the square root of 169?", "13"),
        new AbstractMap.SimpleEntry<>("What is the cube of 4?", "64"),
        new AbstractMap.SimpleEntry<>("What is 27% of 200?", "54"),
        new AbstractMap.SimpleEntry<>("What is the factorial of 5?", "120"),
        new AbstractMap.SimpleEntry<>("What is the remainder when 29 is divided by 5?", "4"),

        // 11–20: Number Series
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 2, 4, 8, 16, ?", "32"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 3, 6, 11, 18, ?", "27"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 5, 10, 20, 40, ?", "80"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 7, 14, 28, 56, ?", "112"),
        new AbstractMap.SimpleEntry<>("What is the next number in the Fibonacci series: 1, 1, 2, 3, 5, ?", "13"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 2, 3, 5, 8, 12, ?", "17"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 4, 9, 19, 39, ?", "79"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 10, 9, 7, 4, 0, ?", "-5"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 1, 4, 9, 16, ?", "25"),
        new AbstractMap.SimpleEntry<>("What is the next number in the series: 2, 6, 12, 20, 30, ?", "42"),

        // 21–30: Percentages
        new AbstractMap.SimpleEntry<>("What is 20% of 150?", "30"),
        new AbstractMap.SimpleEntry<>("What is 25% of 360?", "90"),
        new AbstractMap.SimpleEntry<>("What is 12.5% of 400?", "50"),
        new AbstractMap.SimpleEntry<>("What is 30% of 250?", "75"),
        new AbstractMap.SimpleEntry<>("If a price is 200 and is increased by 15%, what is the new price?", "230"),
        new AbstractMap.SimpleEntry<>("If a salary of 5000 is decreased by 10%, what is the new salary?", "4500"),
        new AbstractMap.SimpleEntry<>("20 is what percent of 50?", "40%"),
        new AbstractMap.SimpleEntry<>("72 is what percent of 120?", "60%"),
        new AbstractMap.SimpleEntry<>("What is 80 increased by 25%?", "100"),
        new AbstractMap.SimpleEntry<>("A population increases from 1000 to 1150. What is the percentage increase?", "15%"),

        // 31–40: Ratio & Proportion
        new AbstractMap.SimpleEntry<>("In the ratio 3:4, the sum of parts is 140. What is the larger part?", "80"),
        new AbstractMap.SimpleEntry<>("Divide 63 in the ratio 2:5. What is the larger part?", "45"),
        new AbstractMap.SimpleEntry<>("If a:b = 3:7 and a + b = 40, what is b?", "28"),
        new AbstractMap.SimpleEntry<>("If 5 pens cost 15, how much would 8 pens cost?", "24"),
        new AbstractMap.SimpleEntry<>("If speeds are in the ratio 3:5 and the faster train takes 6 hours, how long will the slower train take?", "10"),
        new AbstractMap.SimpleEntry<>("In ratios a:b = 4:9 and b:c = 1:3, what is a:c?", "4:27"),
        new AbstractMap.SimpleEntry<>("A mixture of milk and water is in ratio 5:2. If the total is 140ml, how much is milk?", "100"),
        new AbstractMap.SimpleEntry<>("If a:b:c = 2:3:5 and a + b + c = 200, what is b?", "60"),
        new AbstractMap.SimpleEntry<>("A recipe requires flour and sugar in ratio 7:3. For 700g of mixture, how much is sugar?", "210"),
        new AbstractMap.SimpleEntry<>("If a is to b as 6:7 and b = 49, what is a?", "42"),

        // 41–50: Time & Work
        new AbstractMap.SimpleEntry<>("If 4 workers can complete a job in 12 days, how many days will 6 workers take?", "8"),
        new AbstractMap.SimpleEntry<>("A and B together can do a job in 15 days. B alone can do it in 30 days. How long will A alone take?", "30"),
        new AbstractMap.SimpleEntry<>("A is twice as efficient as B. Together they finish in 9 days. How many days will A alone take?", "13.5"),
        new AbstractMap.SimpleEntry<>("5 men can do a work in 10 days, and 10 women can do the same work in 15 days. In how many days will 3 men and 5 women do the work together?", "10.714"),
        new AbstractMap.SimpleEntry<>("If half a work is done in 6 days, how many days for the full work?", "12"),
        new AbstractMap.SimpleEntry<>("Two pipes fill a tank in 10 and 15 hours respectively. Together with a third pipe, they fill it in 4 hours. How long will the third pipe take alone?", "12"),
        new AbstractMap.SimpleEntry<>("A and B together finish in 10 days. A is twice as fast as B. How many days will B take alone?", "15"),
        new AbstractMap.SimpleEntry<>("A, B and C can do a job in 20, 30, and 60 days respectively. How long will they take together?", "10"),
        new AbstractMap.SimpleEntry<>("A does a job in 16 days and B in 24 days. How many days to complete half the job together?", "4.8"),
        new AbstractMap.SimpleEntry<>("A and B can finish in 12 days, B and C in 15 days, and A and C in 20 days. How long will A, B, and C take together?", "10"),

        // 51–60: Time, Speed & Distance
        new AbstractMap.SimpleEntry<>("A covers 60 km in 1.5 hours. What is his speed?", "40"),
        new AbstractMap.SimpleEntry<>("How long will it take to travel 180 km at 60 km/h?", "3"),
        new AbstractMap.SimpleEntry<>("Two trains are 120 km apart and approach each other at 50 km/h and 70 km/h. When will they meet?", "1"),
        new AbstractMap.SimpleEntry<>("A man walks at 5 km/h and returns at 3 km/h. What is his average speed?", "3.75"),
        new AbstractMap.SimpleEntry<>("A boat's speed is 12 km/h upstream and 18 km/h downstream. What is the speed of the stream?", "3"),
        new AbstractMap.SimpleEntry<>("What is the average speed for a 240 km journey done in 4 hours?", "60"),
        new AbstractMap.SimpleEntry<>("How long will it take a train moving at 54 km/h to cover 270 km?", "5"),
        new AbstractMap.SimpleEntry<>("A biker travels 200 km at 25 km/h and returns at 40 km/h. What is the average speed for the round trip?", "30.77"),
        new AbstractMap.SimpleEntry<>("How long will it take to walk 20 km at 5 km/h?", "4"),
        new AbstractMap.SimpleEntry<>("If a runner covers 400 m in 50 seconds, what is his speed in km/h?", "28.8"),

        // 61–70: Profit & Loss
        new AbstractMap.SimpleEntry<>("If cost price is 100 and selling price is 120, what is the profit percent?", "20%"),
        new AbstractMap.SimpleEntry<>("If cost price is 200 and selling price is 150, what is the loss percent?", "25%"),
        new AbstractMap.SimpleEntry<>("A gain of 25% is made on cost price of 80. What is the selling price?", "100"),
        new AbstractMap.SimpleEntry<>("A seller sells at 10% loss and receives 180. What was the cost price?", "200"),
        new AbstractMap.SimpleEntry<>("On one item there is 20% gain and on another equal item 20% loss, both sold at cost price. What is the overall profit or loss percent?", "4% loss"),
        new AbstractMap.SimpleEntry<>("If cost price is 500 and profit is 12%, what is the selling price?", "560"),
        new AbstractMap.SimpleEntry<>("A trader marks goods at 10% above cost and allows a discount of 5%. What is the profit percent?", "4.5%"),
        new AbstractMap.SimpleEntry<>("If marked price is 1000 and discount is 20%, what is the selling price?", "800"),
        new AbstractMap.SimpleEntry<>("If marked price is 500 and successive discounts are 10% and 5%, what is the net selling price?", "427.5"),
        new AbstractMap.SimpleEntry<>("A trader uses 950g weight as 1kg. If he sells at cost price, what is his gain percent?", "5.26%"),

        // 71–80: Simple & Compound Interest
        new AbstractMap.SimpleEntry<>("What is the simple interest on 1000 at 5% per annum for 2 years?", "100"),
        new AbstractMap.SimpleEntry<>("What is the compound interest on 1000 at 10% per annum for 2 years?", "210"),
        new AbstractMap.SimpleEntry<>("What is the simple interest on 5000 at 8% per annum for 3 years?", "1200"),
        new AbstractMap.SimpleEntry<>("What is the compound interest on 1500 at 5% per annum for 3 years?", "236.44"),
        new AbstractMap.SimpleEntry<>("What principal yields 400 interest at 4% per annum in 5 years?", "2000"),
        new AbstractMap.SimpleEntry<>("What is the amount on 2000 at 10% simple interest for 3 years?", "2600"),
        new AbstractMap.SimpleEntry<>("What is the compound interest on 10000 at 12% for 1 year?", "1200"),
        new AbstractMap.SimpleEntry<>("What is the compound interest on 2000 at 5% p.a. compounded half-yearly for 1 year?", "101.25"),
        new AbstractMap.SimpleEntry<>("What is the amount on 5000 at 8% compound interest for 2 years?", "5832"),
        new AbstractMap.SimpleEntry<>("What is the effective annual rate for 12% nominal rate compounded monthly?", "12.68%"),

        // 81–90: Probability & Permutation
        new AbstractMap.SimpleEntry<>("What is the probability of getting a head when a fair coin is tossed?", "1/2"),
        new AbstractMap.SimpleEntry<>("What is the probability of getting a sum of 7 when two dice are rolled?", "1/6"),
        new AbstractMap.SimpleEntry<>("What is the probability of drawing a heart from a standard deck of 52 cards?", "1/4"),
        new AbstractMap.SimpleEntry<>("What is the probability of drawing an ace from a standard deck of cards?", "1/13"),
        new AbstractMap.SimpleEntry<>("In a bag of 5 red and 7 blue balls, what is the probability of drawing a red ball?", "5/12"),
        new AbstractMap.SimpleEntry<>("What is the probability of getting exactly 2 heads in 3 tosses of a fair coin?", "3/8"),
        new AbstractMap.SimpleEntry<>("How many permutations can be made from the letters ABC?", "6"),
        new AbstractMap.SimpleEntry<>("How many ways can you choose 2 objects from 5?", "10"),
        new AbstractMap.SimpleEntry<>("How many ways can 4 distinct books be arranged on a shelf?", "24"),
        new AbstractMap.SimpleEntry<>("In how many ways can a committee of 3 be formed from 6 people?", "20"),

        // 91–100: Ages & Mixtures
        new AbstractMap.SimpleEntry<>("John's age is twice Mary's and their sum is 36. How old is Mary?", "12"),
        new AbstractMap.SimpleEntry<>("A is 4 years older than B. After 6 years, their combined age will be 40. What is B's present age?", "12"),
        new AbstractMap.SimpleEntry<>("If the ages of A and B are in the ratio 3:4 and their sum is 35, what is A's age?", "15"),
        new AbstractMap.SimpleEntry<>("The ages of two siblings are in the ratio 5:7 and their sum is 96. How old is the younger sibling?", "40"),
        new AbstractMap.SimpleEntry<>("A's age is twice B's and the sum of their ages is 25. How old is B?", "5"),
        new AbstractMap.SimpleEntry<>("If A is thrice B and A is 21, what is B?", "7"),
        new AbstractMap.SimpleEntry<>("A mixture contains milk and water in the ratio 5:2 and the total volume is 280ml. How much milk is in the mixture?", "200ml"),
        new AbstractMap.SimpleEntry<>("You need to mix 20% and 50% acid solutions to get 35% acid. If you use 100ml of the 50% solution, how much 20% solution should you use?", "100ml"),
        new AbstractMap.SimpleEntry<>("A liquid mixture has water and milk in the ratio 3:1 and the total is 200ml. How much milk is there?", "50ml"),
        new AbstractMap.SimpleEntry<>("If water and milk are mixed in the ratio 2:3 to make 250ml, how much milk is there?", "150ml")
    );    
    
    // The game canvas (used during gameplay).
    private Canvas canvas;
    private Timeline gameLoop;
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage; // store the primary stage
        primaryStage.setTitle("Pixel Retro Runner - Green Human Edition");
        primaryStage.setFullScreen(true);
        showIntroScreen(primaryStage);
    }
    
    /**
     * Call this from anywhere in your JavaFX app to launch
     * the retro‑runner in its own window.
     */
    public static void launchGame() {
        // ensure we’re on the JavaFX thread
        Platform.runLater(() -> {
            try {
                new PixelRetroRunner().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Intro Screen: shows title, rules, and game elements.
    private void showIntroScreen(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: black; -fx-padding: 20;");

        Label title = new Label("PIXEL RETRO RUNNER");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 36));
        title.setTextFill(Color.LIME);

        Label rules = new Label("Rules:\n" +
                "1. Use SPACE to jump (double jump enabled).\n" +
                "2. Avoid obstacles like cacti and pine trees.\n" +
                "3. Collect coins & power-ups (shield, magnet, thunder).\n" +
                "4. Battle enemies & bosses; answer quick questions to proceed.\n" +
                "5. Stay alive and score high!");
        rules.setFont(Font.font("Monospaced", 16));
        rules.setTextFill(Color.WHITE);
        rules.setAlignment(Pos.CENTER);

        // GridPane for 2-column layout
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(50);
        detailsGrid.setVgap(20);
        detailsGrid.setAlignment(Pos.CENTER);

        // List of previews and labels
        Object[][] elements = {
            { createPreviewCanvas(g -> drawRunner(g, 20, 20), 100, 100), "Runner: The main character." },
            { createPreviewCanvas(g -> drawBoss(g, 10, 10, 5, false), 120, 120), "Boss: A powerful enemy spaceship." },
            { createPreviewCanvas(g -> drawEnemy(g, 20, 20), 100, 100), "Enemy: A flying obstacle." },
            { createPreviewCanvas(g -> drawObstacle(g, 0, 90, "pine"), 100, 100), "Pine Tree: An obstacle (tree)." },
            { createPreviewCanvas(g -> drawObstacle(g, 0, 90, "cactus"), 100, 100), "Cactus: A desert obstacle." },
            { createPreviewCanvas(g -> drawPowerUp(g, 20, 20, "shield"), 100, 100), "Shield: Temporary protection from damage." },
            { createPreviewCanvas(g -> drawPowerUp(g, 20, 20, "thunder"), 100, 100), "Thunder: Shoots enemies/bosses (press F)." },
            { createPreviewCanvas(g -> drawCoin(g, 20, 20), 100, 100), "Coin: Increases score with combo bonus." },
            { createPreviewCanvas(g -> drawPowerUp(g, 20, 20, "magnet"), 100, 100), "Magnet: Attracts coins for 5 seconds." },
        };

        for (int i = 0; i < elements.length; i++) {
            Canvas canvas = (Canvas) elements[i][0];
            Label label = new Label((String) elements[i][1]);
            label.setFont(Font.font("Monospaced", 14));
            label.setTextFill(Color.WHITE);
            HBox item = new HBox(10, canvas, label);
            item.setAlignment(Pos.CENTER_LEFT);
            int row = i / 2;
            int col = i % 2;
            detailsGrid.add(item, col, row);
        }

        Label instruction = new Label("Press ENTER to start the game");
        instruction.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        instruction.setTextFill(Color.YELLOW);
        instruction.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, rules, detailsGrid, instruction);

        Scene introScene = new Scene(root, BASE_WIDTH, BASE_HEIGHT);
        introScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                primaryStage.setFullScreen(true); // Make sure full screen is set again here
                startGame();
            }
        });
        stage.setScene(introScene);
        stage.show();
    }

    private Canvas createPreviewCanvas(java.util.function.Consumer<GraphicsContext> drawer, int width, int height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawer.accept(gc);
        return canvas;
    }
        
    // Load weather sounds.
    private void loadWeatherSounds() {
        try {
            String baseDir = System.getProperty("user.dir");
            Media autumnMedia = new Media(new File(baseDir +"/assets/sounds/Autumn.mp3").toURI().toString());
            Media rainMedia = new Media(new File(baseDir +"/assets/sounds/Rain.mp3").toURI().toString());
            Media summerMedia = new Media(new File(baseDir +"/assets/sounds/Summer.mp3").toURI().toString());
            Media snowMedia = new Media(new File(baseDir +"/assets/sounds/Snow.mp3").toURI().toString());
        } catch (Exception ex) {
            System.out.println("Error loading media: " + ex.getMessage());
        }
    }
    
    // Update weather sound.
    private void updateWeatherSound() {
        String baseDir = System.getProperty("user.dir");
        if (weatherPlayer != null) {
            weatherPlayer.stop();
        }
        Media mediaToPlay = null;
        try {
            if (currentWeather.equals("sunny")) {
                mediaToPlay = new Media(new File(baseDir +"/assets/sounds/Summer.mp3").toURI().toString());
            } else if (currentWeather.equals("rainy")) {
                mediaToPlay = new Media(new File(baseDir +"/assets/sounds/Rain.mp3").toURI().toString());
            } else if (currentWeather.equals("snow")) {
                mediaToPlay = new Media(new File(baseDir +"/assets/sounds/Snow.mp3").toURI().toString());
            } else if (currentWeather.equals("autumn")) {
                mediaToPlay = new Media(new File(baseDir +"/assets/sounds/Autumn.mp3").toURI().toString());
            }
        } catch (Exception ex) {
            System.out.println("Error loading media for weather: " + ex.getMessage());
        }
        if (mediaToPlay != null) {
            weatherPlayer = new MediaPlayer(mediaToPlay);
            weatherPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            weatherPlayer.play();
        }
    }
    
    // getRandomWeather: rainy and snow force night; sunny forces day; autumn is random.
    private String getRandomWeather() {
        String[] weathers = {"sunny", "rainy", "snow", "autumn"};
        String w = weathers[rand.nextInt(weathers.length)];
        if (w.equals("rainy") || w.equals("snow")) {
            isDay = false;
        } else if (w.equals("sunny")) {
            isDay = true;
        } else if (w.equals("autumn")) {
            isDay = rand.nextBoolean();
        }
        return w;
    }
    
    // startGame: initializes the game scene and starts the game.
    private void startGame() {
        gameStarted = true;
        gameOver = false;
        pausedForQuiz = false;
        shieldActive = false;
        magnetActive = false;
        score = 0;
        lives = 10;
        level = 1;
        currentWeather = getRandomWeather();
        lastWeatherChangeScore = 0;
        meteorScene = false;
        runnerX = 100;
        canvas = new Canvas(BASE_WIDTH, BASE_HEIGHT);
        Pane gamePane = new Pane(canvas);
        Scene gameScene = new Scene(gamePane, BASE_WIDTH, BASE_HEIGHT);
        canvas.widthProperty().bind(gameScene.widthProperty());
        canvas.heightProperty().bind(gameScene.heightProperty());
        
        // Set initial runner position.
        runnerY = gameScene.getHeight() - GROUND_HEIGHT - (RUNNER_PIXEL_HEIGHT * SCALE);
        runnerVelocityY = 0;
        jumpCount = 0;
        coinCombo = 0;
        boss = null;
        obstacles.clear();
        enemies.clear();
        coins.clear();
        powerUps.clear();
        bossBullets.clear();
        meteors.clear();
        if (comboResetTimeline != null) {
            comboResetTimeline.stop();
        }
        runnerAnimationCounter = 0;
        bossAnimationCounter = 0;
        coinAnimationCounter = 0;
        if (currentWeather.equals("autumn")) {
            initAutumnLeaves();
        }
        updateWeatherSound();
        
        gameScene.setOnKeyPressed(e -> {
            if (!gameStarted) return;
            // if (gameOver) {
            //     if (e.getCode() == KeyCode.R) {
            //         startGame();
            //     } 
            // }
            if (gameOver) {
                if (e.getCode() == KeyCode.R) {
                    // restart
                    startGame();
                } else if (e.getCode() == KeyCode.M) {
                    // **NEW**: close runner and go back to main menu
                    if (weatherPlayer != null) {
                        weatherPlayer.stop();
                        weatherPlayer.dispose();
                        weatherPlayer = null;
                    }
                    primaryStage.close();
                    Platform.runLater(() -> {
                        SceneManager.getPrimaryStage().show();
                        SceneManager.getPrimaryStage().setFullScreen(true);
                        SceneManager.showMainMenu();
                    });
                } else if (e.getCode() == KeyCode.S) {
                    // ← NEW: Save score under the logged‑in user
                    String user = SessionManager.getUsername();
                    int finalScore = score;  // or currentDistance, whichever you track
                    LeaderboardService.pushLeaderboardDataAsync(
                        user,
                        finalScore,
                        "", //PreferencesManager.getDifficultyString()
                        "Retro"
                    );
                    System.out.println("Score saved for " + user + "!");
                        // 2) Return to main menu
                    primaryStage.close();
                    Platform.runLater(() -> {
                        SceneManager.getPrimaryStage().show();
                        SceneManager.getPrimaryStage().setFullScreen(true);
                        SceneManager.showMainMenu();
                    });
                }
                            
            } else if (e.getCode() == KeyCode.F && thunderbolts > 0) {
                thunderShots.add(new Thunderbolt(runnerX + RUNNER_PIXEL_WIDTH * SCALE, runnerY + 3 * SCALE));
                thunderbolts--;
            } else {
                if (e.getCode() == KeyCode.SPACE) {
                    double groundY = canvas.getHeight() - GROUND_HEIGHT - RUNNER_PIXEL_HEIGHT * SCALE;
                    if (runnerY >= groundY - 1) {
                        jumpCount = 0;
                    }
                    if (jumpCount < 2) {
                        runnerVelocityY = JUMP_STRENGTH;
                        jumpCount++;
                    }
                }
            }
        });
        
        primaryStage.setFullScreen(true); // <-- Ensures full screen
        primaryStage.setScene(gameScene);
        primaryStage.setFullScreen(true); // <-- 
        primaryStage.show();
        initStars();
        initGameLoop();
        gameLoop.play();
    }
    
    private void initStars() {
        stars.clear();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        for (int i = 0; i < 70; i++) {
            double x = rand.nextDouble() * w;
            double y = rand.nextDouble() * (h - GROUND_HEIGHT);
            double speed = 0.2 + rand.nextDouble() * 0.6;
            stars.add(new Star(x, y, speed));
        }
    }
    
    private void initAutumnLeaves() {
        autumnLeaves.clear();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        for (int i = 0; i < 20; i++) {
            double x = rand.nextDouble() * w;
            double y = rand.nextDouble() * (h - GROUND_HEIGHT);
            double speed = 0.3 + rand.nextDouble() * 0.2;
            autumnLeaves.add(new Leaf(x, y, speed));
        }
    }
    
    private void updateAutumnLeaves() {
        double h = canvas.getHeight();
        for (Leaf leaf : autumnLeaves) {
            leaf.y += leaf.speedY;
            if (leaf.y > h - GROUND_HEIGHT) {
                leaf.y = -10;
                leaf.x = rand.nextDouble() * canvas.getWidth();
            }
        }
    }
    
    private void initGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(33), e -> {
            if (!pausedForQuiz && !gameOver) {
                updateGame();
                draw();
            }
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
    }
    
    // Game update loop.
    private void updateGame() {
        double cWidth = canvas.getWidth();
        double cHeight = canvas.getHeight();
        double groundY = cHeight - GROUND_HEIGHT - (RUNNER_PIXEL_HEIGHT * SCALE);
        
        runnerAnimationCounter++;
        bossAnimationCounter++;
        coinAnimationCounter = (coinAnimationCounter + 1) % 32;
        
        // Update runner physics.
        runnerVelocityY += GRAVITY;
        runnerY += runnerVelocityY;
        if (runnerY > groundY) {
            runnerY = groundY;
            runnerVelocityY = 0;
            jumpCount = 0;
        }
        
        for (Star star : stars) {
            star.x -= star.speed;
            if (star.x < 0) {
                star.x = cWidth;
                star.y = rand.nextDouble() * (cHeight - GROUND_HEIGHT);
            }
        }
        
        level = (score / 10) + 1;
        if (score - lastWeatherChangeScore >= 100) {
            currentWeather = getRandomWeather();
            lastWeatherChangeScore = score;
            if (currentWeather.equals("autumn")) {
                initAutumnLeaves();
            }
            updateWeatherSound();
        }
        if (level >= 20) {
            if (!meteorScene) {
                meteorScene = true;
                meteorStartTime = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - meteorStartTime > 10_000) {
                meteorScene = false;
            }
        }
        
        double obstacleSpeed = baseObstacleSpeed + (level - 1) * 0.5;
        double enemySpeed = baseEnemySpeed + (level - 1) * 0.5;
        
        if (rand.nextDouble() < 0.02) spawnObstacle();
        if (rand.nextDouble() < 0.01) spawnEnemy();
        if (rand.nextDouble() < 0.015) spawnCoin();
        if (rand.nextDouble() < 0.005) spawnPowerUp();
        if (meteorScene && rand.nextDouble() < 0.02) spawnMeteor();
        
        if (magnetActive) {
            for (Coin coin : coins) {
                coin.x -= 2;
                double diffY = runnerY - coin.y;
                coin.y += diffY * 0.1;
            }
        }
        
        if (currentWeather.equals("autumn")) {
            updateAutumnLeaves();
        }
        
        Iterator<Obstacle> obsIt = obstacles.iterator();
        while (obsIt.hasNext()) {
            Obstacle obs = obsIt.next();
            obs.x -= obstacleSpeed;
            if (obs.x + OBSTACLE_PIXEL_WIDTH * SCALE < 0) {
                obsIt.remove();
                score += 1;
            } else {
                double runnerRight = runnerX + RUNNER_PIXEL_WIDTH * SCALE;
                if (!obs.quizTriggered && obs.x <= runnerRight + 5 && obs.x >= runnerRight - 5) {
                    if (runnerY >= groundY - 5) {
                        obs.quizTriggered = true;
                        pausedForQuiz = true;
                        gameLoop.pause();
                        Platform.runLater(() -> showQuizDialog("obstacle", obs, null, null));
                    }
                }
            }
        }
        
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy enemy = enemyIt.next();
            enemy.x -= enemySpeed;
            if (enemy.x + OBSTACLE_PIXEL_WIDTH * SCALE < 0) {
                enemyIt.remove();
                score += 2;
            } else {
                double runnerRight = runnerX + RUNNER_PIXEL_WIDTH * SCALE;
                if (!enemy.quizTriggered && enemy.x <= runnerRight + 5 && enemy.x >= runnerRight - 5) {
                    if (!shieldActive && runnerY >= groundY - 5) {
                        enemy.quizTriggered = true;
                        pausedForQuiz = true;
                        gameLoop.pause();
                        Platform.runLater(() -> showQuizDialog("enemy", null, enemy, null));
                    } else if (shieldActive) {
                        enemyIt.remove();
                        score += 4;
                    }
                }
            }
        }
        
        Iterator<Coin> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Coin coin = coinIt.next();
            coin.x -= obstacleSpeed * 0.8;
            if (coin.x + 8 * SCALE < 0) {
                coinIt.remove();
            } else {
                if (coin.x < runnerX + RUNNER_PIXEL_WIDTH * SCALE &&
                    coin.x + 8 * SCALE > runnerX &&
                    coin.y < runnerY + RUNNER_PIXEL_HEIGHT * SCALE &&
                    coin.y + 8 * SCALE > runnerY) {
                    coinIt.remove();
                    coinCombo++;
                    score += 3 * coinCombo;
                    if (comboResetTimeline != null) {
                        comboResetTimeline.stop();
                    }
                    comboResetTimeline = new Timeline(new KeyFrame(Duration.seconds(3), ev -> coinCombo = 0));
                    comboResetTimeline.play();
                }
            }
        }
        
        Iterator<PowerUp> puIt = powerUps.iterator();
        while (puIt.hasNext()) {
            PowerUp pu = puIt.next();
            pu.x -= obstacleSpeed * 0.8;
            if (pu.x + 8 * SCALE < 0) {
                puIt.remove();
            } else {
                if (pu.x < runnerX + RUNNER_PIXEL_WIDTH * SCALE &&
                    pu.x + 8 * SCALE > runnerX &&
                    pu.y < runnerY + RUNNER_PIXEL_HEIGHT * SCALE &&
                    pu.y + 8 * SCALE > runnerY) {
                    if (pu.type.equals("shield")) {
                        activateShield();
                    } else if (pu.type.equals("magnet")) {
                        activateMagnet();
                    } else if (pu.type.equals("thunder")) {
                        thunderbolts = Math.min(thunderbolts + 5, MAX_THUNDERBOLTS);
                    }
                    puIt.remove();
                }
            }
        }
        
        if (meteorScene) {
            Iterator<Meteor> metIt = meteors.iterator();
            while (metIt.hasNext()) {
                Meteor m = metIt.next();
                m.y += m.speedY;
                if (m.y > cHeight - GROUND_HEIGHT) {
                    metIt.remove();
                } else {
                    if (m.x < runnerX + RUNNER_PIXEL_WIDTH * SCALE &&
                        m.x + 8 * SCALE > runnerX &&
                        m.y < runnerY + RUNNER_PIXEL_HEIGHT * SCALE &&
                        m.y + 8 * SCALE > runnerY) {
                        metIt.remove();
                        lives--;
                        if (lives <= 0) {
                            gameOver = true;
                            stopGameLoop();
                        }
                    }
                }
            }
        }
        
        if (level % 10 == 0 && boss == null) {
            spawnBoss();
        }
        if (boss != null) {
            boss.x = runnerX + 200;
            boss.y = cHeight - GROUND_HEIGHT - (12 * SCALE);
            boss.bossTimer--;
            if (boss.bossTimer <= 0) {
                lives--;
                boss = null;
                if (lives <= 0) {
                    gameOver = true;
                    stopGameLoop();
                }
            } else {
                boss.bulletTimer--;
                if (boss.bulletTimer <= 0) {
                    spawnBossBullet();
                    boss.bulletTimer = 50;
                }
                if (boss.flashCounter > 0) {
                    boss.flashCounter--;
                }
                double runnerRight = runnerX + RUNNER_PIXEL_WIDTH * SCALE;
                if (!boss.quizTriggered && boss.x <= runnerRight + 10 && boss.x >= runnerRight - 10) {
                    if (!shieldActive && runnerY >= groundY - 5) {
                        boss.quizTriggered = true;
                        pausedForQuiz = true;
                        gameLoop.pause();
                        Platform.runLater(() -> showQuizDialog("boss", null, null, boss));
                    } else if (shieldActive) {
                        boss.health--;
                        boss.flashCounter = 10;
                        if (boss.health <= 0) {
                            score += 10;
                            boss = null;
                        }
                    }
                }
            }
        }
        
        Iterator<BossBullet> bbIt = bossBullets.iterator();
        while (bbIt.hasNext()) {
            BossBullet bb = bbIt.next();
            bb.x += bb.speedX;
            bb.y += bb.speedY;
            if (bb.x < 0 || bb.x > cWidth || bb.y > cHeight) {
                bbIt.remove();
            } else if (bb.x < runnerX + RUNNER_PIXEL_WIDTH * SCALE &&
                       bb.x + 4 * SCALE > runnerX &&
                       bb.y < runnerY + RUNNER_PIXEL_HEIGHT * SCALE &&
                       bb.y + 4 * SCALE > runnerY) {
                bbIt.remove();
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                    stopGameLoop();
                }
            }
        }
        
        Iterator<Thunderbolt> tIt = thunderShots.iterator();
        while (tIt.hasNext()) {
            Thunderbolt t = tIt.next();
            t.x += t.speed;
            if (t.x > canvas.getWidth()) {
                tIt.remove();
                continue;
            }
            for (Enemy enemy : enemies) {
                if (t.x < enemy.x + 8 * SCALE && t.x + 2 * SCALE > enemy.x &&
                    t.y < enemy.y + 8 * SCALE && t.y + SCALE > enemy.y) {
                    enemies.remove(enemy);
                    tIt.remove();
                    score += 4;
                    break;
                }
            }
            if (boss != null && t.x < boss.x + 12 * SCALE && t.x + 2 * SCALE > boss.x &&
                t.y < boss.y + 12 * SCALE && t.y + SCALE > boss.y) {
                boss.health -= 5;
                tIt.remove();
                if (boss.health <= 0) {
                    boss = null;
                    score += 10;
                }
            }
        }
    }
    
    private void spawnBossBullet() {
        if (boss != null) {
            double leftCannonX = boss.x + 2 * SCALE;
            double leftCannonY = boss.y + 6 * SCALE;
            double rightCannonX = boss.x + 9 * SCALE;
            double rightCannonY = boss.y + 6 * SCALE;
            double dx1 = runnerX - leftCannonX;
            double dy1 = runnerY - leftCannonY;
            double mag1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
            double speed = 4;
            double vx1 = speed * dx1 / mag1;
            double vy1 = speed * dy1 / mag1;
            bossBullets.add(new BossBullet(leftCannonX, leftCannonY, vx1, vy1));
            double dx2 = runnerX - rightCannonX;
            double dy2 = runnerY - rightCannonY;
            double mag2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
            double vx2 = speed * dx2 / mag2;
            double vy2 = speed * dy2 / mag2;
            bossBullets.add(new BossBullet(rightCannonX, rightCannonY, vx2, vy2));
        }
    }
    
    private void spawnObstacle() {
        double canvasHeight = canvas.getHeight();
        double x = canvas.getWidth();
        String type = rand.nextDouble() < 0.5 ? "cactus" : "pine";
        double y;
        if (type.equals("pine")) {
            y = canvasHeight - GROUND_HEIGHT;
        } else {
            y = canvasHeight - GROUND_HEIGHT - (6 * SCALE) + (1 * SCALE);
        }
        obstacles.add(new Obstacle(x, y, type));
    }
    
    private void spawnEnemy() {
        double x = canvas.getWidth();
        double y = canvas.getHeight() - GROUND_HEIGHT - OBSTACLE_PIXEL_WIDTH * SCALE - 30;
        enemies.add(new Enemy(x, y));
    }
    
    private void spawnCoin() {
        double x = canvas.getWidth();
        double y = canvas.getHeight() - GROUND_HEIGHT - (8 * SCALE) - rand.nextDouble() * 80;
        coins.add(new Coin(x, y));
    }
    
    private void spawnPowerUp() {
        double x = canvas.getWidth();
        double y = canvas.getHeight() - GROUND_HEIGHT - (8 * SCALE) - rand.nextDouble() * 100;
        String[] types = {"shield", "magnet", "thunder"};
        String type = types[rand.nextInt(types.length)];
        powerUps.add(new PowerUp(x, y, type));
    }
    
    private void spawnBoss() {
        double x = runnerX + 200;
        double y = canvas.getHeight() - GROUND_HEIGHT - (12 * SCALE);
        int health = 5 + ((level / 10) - 1) * 3;
        int timer = 300;
        boss = new Boss(x, y, health, timer);
    }
    
    private void spawnMeteor() {
        double x = rand.nextDouble() * canvas.getWidth();
        double y = -10;
        double speedY = 2 + rand.nextDouble() * 3;
        meteors.add(new Meteor(x, y, speedY));
    }
    
    private void activateShield() {
        shieldActive = true;
        Timeline shieldTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> shieldActive = false));
        shieldTimeline.setCycleCount(1);
        shieldTimeline.play();
    }
    
    private void activateMagnet() {
        magnetActive = true;
        Timeline magnetTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> magnetActive = false));
        magnetTimeline.setCycleCount(1);
        magnetTimeline.play();
    }
    
    // Drawing routines.
    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double cWidth = canvas.getWidth();
        double cHeight = canvas.getHeight();
        if (isDay) {
            if (currentWeather.equals("sunny"))
                gc.setFill(Color.SKYBLUE);
            else if (currentWeather.equals("autumn"))
                gc.setFill(Color.LIGHTGOLDENRODYELLOW);
            else
                gc.setFill(Color.LIGHTBLUE);
        } else {
            gc.setFill(Color.BLACK);
        }
        gc.fillRect(0, 0, cWidth, cHeight);
        
        if (!isDay && !meteorScene) {
            gc.setFill(Color.WHITE);
            for (Star star : stars)
                gc.fillRect(star.x, star.y, 2, 2);
        }
        
        drawWeather(gc);
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, cHeight - GROUND_HEIGHT, cWidth, GROUND_HEIGHT);
        
        drawRunner(gc, runnerX, runnerY);
        for (Obstacle obs : obstacles) {
            drawObstacle(gc, obs.x, obs.y, obs.type);
        }
        for (Enemy enemy : enemies) {
            drawEnemy(gc, enemy.x, enemy.y);
        }
        for (Coin coin : coins) {
            drawCoin(gc, coin.x, coin.y);
        }
        for (PowerUp pu : powerUps) {
            drawPowerUp(gc, pu.x, pu.y, pu.type);
        }
        if (boss != null) {
            drawBoss(gc, boss.x, boss.y, boss.health, boss.flashCounter > 0);
        }
        for (BossBullet bb : bossBullets) {
            drawBossBullet(gc, bb.x, bb.y);
        }
        if (meteorScene) {
            for (Meteor m : meteors) {
                drawMeteor(gc, m.x, m.y);
            }
        }
        for (Thunderbolt t : thunderShots) {
            gc.setFill(Color.CYAN);
            double[] xPoints = { t.x, t.x + (4 * SCALE / 3.0), t.x + (2 * SCALE), t.x + (4 * SCALE / 3.0) };
            double[] yPoints = { t.y, t.y + (2 * SCALE), t.y, t.y - (2 * SCALE) };
            gc.fillPolygon(xPoints, yPoints, 4);
        }
        gc.fillText("THUNDER: " + thunderbolts, 10, 160);
        Color textColor = isDay ? Color.BLACK : Color.WHITE;
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        gc.setFill(textColor);
        gc.fillText("SCORE: " + score, 10, 20);
        gc.fillText("LIVES: " + lives, 10, 40);
        gc.fillText("LEVEL: " + level, 10, 60);
        gc.fillText("COMBO: " + coinCombo, 10, 80);
        if (shieldActive) {
            gc.fillText("SHIELD ON", 10, 100);
        }
        if (magnetActive) {
            gc.fillText("MAGNET ON", 10, 120);
        }
        if (boss != null) {
            gc.setFill(Color.RED);
            int barWidth = 20 * SCALE;
            int barHeight = 4;
            gc.fillRect(boss.x, boss.y - 10, barWidth, barHeight);
            int maxHealth = 5 + ((level / 10) - 1) * 3;
            gc.setFill(Color.LIME);
            gc.fillRect(boss.x, boss.y - 10, (int)(barWidth * (boss.health / (double) maxHealth)), barHeight);
            gc.fillText("BOSS", boss.x, boss.y - 15);
        }
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER", cWidth / 2 - 40, cHeight / 2);
            gc.fillText("FINAL SCORE: " + score, cWidth / 2 - 45, cHeight / 2 + 20);
            gc.fillText("PRESS R TO RESTART", cWidth / 2 - 55, cHeight / 2 + 40);
            gc.fillText("PRESS M TO GO BACK TO MAIN MENU", cWidth / 2 - 65, cHeight / 2 + 60);
            gc.fillText("PRESS S TO SAVE SCORE", cWidth / 2 - 75, cHeight / 2 + 80);
        }
    }
    
    private void drawWeather(GraphicsContext gc) {
        switch (currentWeather) {
            case "sunny":
                if (isDay) {
                    gc.setFill(Color.GOLD);
                    gc.fillOval(canvas.getWidth() - 80, 20, 40, 40);
                }
                break;
            case "rainy":
                gc.setStroke(Color.LIGHTBLUE);
                for (int i = 0; i < 50; i++) {
                    double rx = rand.nextDouble() * canvas.getWidth();
                    double ry = rand.nextDouble() * (canvas.getHeight() - GROUND_HEIGHT);
                    gc.strokeLine(rx, ry, rx + 2, ry + 10);
                }
                break;
            case "snow":
                gc.setFill(Color.ALICEBLUE);
                for (int i = 0; i < 50; i++) {
                    double sx = rand.nextDouble() * canvas.getWidth();
                    double sy = rand.nextDouble() * (canvas.getHeight() - GROUND_HEIGHT);
                    gc.fillOval(sx, sy, 4, 4);
                }
                break;
            case "autumn":
                drawAutumnLeaves(gc);
                break;
        }
    }
    
    private void drawAutumnLeaves(GraphicsContext gc) {
        for (Leaf leaf : autumnLeaves) {
            Color[][] leafArt = {
                { null, Color.ORANGERED, Color.ORANGERED, null },
                { Color.ORANGERED, Color.ORANGE, Color.ORANGE, Color.ORANGERED },
                { null, Color.ORANGE, Color.ORANGE, null },
                { null, null, null, null }
            };
            drawPixelArt(gc, leaf.x, leaf.y, leafArt);
        }
    }
    
    private void drawPixelArt(GraphicsContext gc, double x, double y, Color[][] art) {
        int rows = art.length;
        int cols = art[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Color col = art[i][j];
                if (col != null) {
                    gc.setFill(col);
                    gc.fillRect(x + j * SCALE, y + i * SCALE, SCALE, SCALE);
                }
            }
        }
    }
    
    // Draw Runner.
    private void drawRunner(GraphicsContext gc, double x, double y) {
        Color[][] runnerFrame1 = {
            { null, null, Color.BROWN, Color.BROWN, Color.BROWN, null, null, null },
            { null, Color.BROWN, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BROWN, null },
            { Color.BROWN, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BROWN },
            { Color.BLUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BLUE },
            { Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE },
            { Color.BLUE, Color.DARKBLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.DARKBLUE, Color.BLUE },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK }
        };
        Color[][] runnerFrame2 = {
            { null, null, Color.BROWN, Color.BROWN, Color.BROWN, null, null, null },
            { null, Color.BROWN, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BROWN, null },
            { Color.BROWN, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BROWN },
            { Color.BLUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BISQUE, Color.BLUE },
            { Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE },
            { Color.BLUE, Color.DARKBLUE, Color.BLUE, Color.BLUE, Color.BLUE, Color.DARKBLUE, Color.BLUE, Color.BLUE },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK },
            { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK },
            { null, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, null }
        };
        int frame = (runnerAnimationCounter / 10) % 2;
        Color[][] human = (frame == 0) ? runnerFrame1 : runnerFrame2;
        drawPixelArt(gc, x, y, human);
    }
    
    // Draw Obstacles.
    private void drawObstacle(GraphicsContext gc, double x, double y, String type) {
        if (type.equals("cactus")) {
            Color[][] cactus = {
                { null, Color.FORESTGREEN, Color.FORESTGREEN, Color.FORESTGREEN, null },
                { Color.FORESTGREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.FORESTGREEN },
                { Color.FORESTGREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.FORESTGREEN },
                { Color.FORESTGREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.FORESTGREEN },
                { Color.FORESTGREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.FORESTGREEN },
                { null, Color.FORESTGREEN, Color.FORESTGREEN, Color.FORESTGREEN, null }
            };
            drawPixelArt(gc, x, y - 5 * SCALE, cactus);
        } else if (type.equals("pine")) {
            Color[][] pine = {
                { null, null, Color.DARKGREEN, null, null },
                { null, Color.DARKGREEN, Color.GREEN, Color.DARKGREEN, null },
                { Color.DARKGREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.DARKGREEN },
                { null, Color.DARKGREEN, Color.GREEN, Color.DARKGREEN, null },
                { null, null, Color.BROWN, null, null },
                { null, null, Color.BROWN, null, null },
                { null, null, Color.BROWN, null, null }
            };
            drawPixelArt(gc, x, y - 6 * SCALE, pine);
        }
    }
    
    // Draw Enemy.
    private void drawEnemy(GraphicsContext gc, double x, double y) {
        Color[][] ant = {
            { null, null, Color.BLACK, null, null, Color.BLACK, null, null },
            { null, Color.BLACK, Color.DARKRED, Color.DARKRED, Color.DARKRED, Color.DARKRED, Color.BLACK, null },
            { Color.BLACK, Color.DARKRED, Color.DARKRED, Color.RED, Color.RED, Color.DARKRED, Color.DARKRED, Color.BLACK },
            { Color.BLACK, Color.DARKRED, Color.RED, Color.RED, Color.RED, Color.RED, Color.DARKRED, Color.BLACK },
            { Color.BLACK, Color.DARKRED, Color.RED, Color.RED, Color.RED, Color.RED, Color.DARKRED, Color.BLACK },
            { Color.BLACK, Color.DARKRED, Color.DARKRED, Color.RED, Color.RED, Color.DARKRED, Color.DARKRED, Color.BLACK },
            { null, Color.BLACK, Color.DARKRED, Color.DARKRED, Color.DARKRED, Color.DARKRED, Color.BLACK, null },
            { null, null, Color.BLACK, null, null, Color.BLACK, null, null }
        };
        drawPixelArt(gc, x, y, ant);
    }
    
    // Draw Coin.
    private void drawCoin(GraphicsContext gc, double x, double y) {
        Color[][] coinFrame0 = {
            { null, Color.GOLD, Color.GOLD, null },
            { Color.GOLD, Color.ORANGE, Color.ORANGE, Color.GOLD },
            { Color.GOLD, Color.ORANGE, Color.ORANGE, Color.GOLD },
            { null, Color.GOLD, Color.GOLD, null }
        };
        Color[][] coinFrame1 = {
            { null, Color.GOLD, null, null },
            { Color.GOLD, Color.ORANGE, Color.ORANGE, Color.GOLD },
            { Color.GOLD, Color.ORANGE, Color.ORANGE, Color.GOLD },
            { null, Color.GOLD, null, null }
        };
        int frame = coinAnimationCounter / 8;
        Color[][] coinArt = (frame % 2 == 0) ? coinFrame0 : coinFrame1;
        drawPixelArt(gc, x, y, coinArt);
    }
    
    // Draw Power-Up.
    private void drawPowerUp(GraphicsContext gc, double x, double y, String type) {
        if (type.equals("shield")) {
            Color[][] shieldArt = {
                { null, Color.CYAN, Color.CYAN, Color.CYAN, null },
                { Color.CYAN, Color.LIGHTCYAN, Color.LIGHTCYAN, Color.LIGHTCYAN, Color.CYAN },
                { Color.CYAN, Color.LIGHTCYAN, Color.LIGHTCYAN, Color.LIGHTCYAN, Color.CYAN },
                { null, Color.CYAN, Color.CYAN, Color.CYAN, null }
            };
            drawPixelArt(gc, x, y, shieldArt);
        } else if (type.equals("magnet")) {
            Color[][] magnetArt = {
                { Color.RED, Color.RED, Color.BLACK, Color.RED, Color.RED },
                { Color.RED, null, null, null, Color.RED },
                { Color.RED, null, null, null, Color.RED },
                { Color.RED, Color.RED, Color.RED, Color.RED, Color.RED }
            };
            drawPixelArt(gc, x, y, magnetArt);
        } else if (type.equals("thunder")) {
            Color[][] bolt = {
                { null, Color.YELLOW, null },
                { Color.YELLOW, Color.YELLOW, Color.YELLOW },
                { null, Color.YELLOW, Color.YELLOW },
                { null, null, Color.YELLOW }
            };
            drawPixelArt(gc, x, y, bolt);
        }
    }
    
    // Draw Boss.
    private void drawBoss(GraphicsContext gc, double x, double y, int health, boolean flashing) {
        Color[][] spaceshipFrame0 = {
            { null, null, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, null, null, null, null },
            { null, Color.DARKSLATEGRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.DARKSLATEGRAY, null, null },
            { Color.DARKSLATEGRAY, Color.GRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.GRAY, Color.GRAY, Color.DARKSLATEGRAY, null },
            { Color.DARKSLATEGRAY, Color.GRAY, Color.LIGHTGRAY, Color.RED, Color.RED, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.RED, Color.RED, Color.LIGHTGRAY, Color.GRAY, Color.DARKSLATEGRAY },
            { Color.DARKSLATEGRAY, Color.GRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.GRAY, Color.DARKSLATEGRAY },
            { null, Color.DARKSLATEGRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.DARKSLATEGRAY, null },
            { null, null, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, null, null, null },
            { null, null, null, null, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, null, null, null, null },
            { null, null, null, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.YELLOW, null, null, null },
            { null, null, null, null, Color.YELLOW, Color.YELLOW, Color.YELLOW, Color.YELLOW, null, null, null, null },
            { null, null, null, null, null, Color.ORANGE, Color.ORANGE, null, null, null, null, null },
            { null, null, null, null, null, null, null, null, null, null, null, null }
        };
        Color[][] spaceshipFrame1 = {
            { null, null, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, null, null, null, null },
            { null, Color.DARKSLATEGRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.DARKSLATEGRAY, null, null },
            { Color.DARKSLATEGRAY, Color.GRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.GRAY, Color.GRAY, Color.DARKSLATEGRAY, null },
            { Color.DARKSLATEGRAY, Color.GRAY, Color.LIGHTGRAY, Color.RED, Color.RED, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.RED, Color.RED, Color.LIGHTGRAY, Color.GRAY, Color.DARKSLATEGRAY },
            { Color.DARKSLATEGRAY, Color.GRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.GRAY, Color.DARKSLATEGRAY },
            { null, Color.DARKSLATEGRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY, Color.DARKSLATEGRAY, null },
            { null, null, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, Color.DARKSLATEGRAY, null, null, null },
            { null, null, null, Color.YELLOW, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.YELLOW, Color.YELLOW, null, null, null },
            { null, null, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.YELLOW, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.YELLOW, null, null },
            { null, null, null, Color.YELLOW, Color.YELLOW, Color.ORANGE, Color.ORANGE, Color.YELLOW, Color.YELLOW, null, null, null },
            { null, null, null, null, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, null, null, null, null },
            { null, null, null, null, null, null, null, null, null, null, null, null }
        };
        int frame = (bossAnimationCounter / 10) % 2;
        Color[][] spaceship = (frame == 0) ? spaceshipFrame0 : spaceshipFrame1;
        drawPixelArt(gc, x, y, spaceship);
    }
    
    // Draw Boss Bullet.
    private void drawBossBullet(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.ORANGERED);
        gc.fillOval(x, y, 4 * SCALE, 4 * SCALE);
    }
    
    // Draw Meteor as a fireball.
    private void drawMeteor(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.ORANGE);
        gc.fillOval(x, y, 12 * SCALE, 12 * SCALE);
        gc.setFill(Color.RED);
        gc.fillOval(x + 2 * SCALE, y + 2 * SCALE, 8 * SCALE, 8 * SCALE);
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, 12 * SCALE, 12 * SCALE);
    }
    
    // Show Quiz Dialog.
    private void showQuizDialog(String type, Obstacle obs, Enemy enemy, Boss bossEnemy) {
        Map.Entry<String, String> qa = questions.get(rand.nextInt(questions.size()));
        Stage dialog = new Stage();
        dialog.initOwner(canvas.getScene().getWindow());
        dialog.initModality(javafx.stage.Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Quick Aptitude Question");
        Label questionLabel = new Label(qa.getKey());
        // questionLabel.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 14;");
        questionLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold;");
        questionLabel.setWrapText(true); // Enable line wrapping
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setMaxWidth(500); // So long questions wrap nicely
        TextField answerField = new TextField();
        answerField.setPromptText("Your answer");
        Button submitButton = new Button("Submit");
        Label feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 12;");
        submitButton.setOnAction(event -> {
            String answer = answerField.getText().trim();
            if (answer.equals(qa.getValue())) {
                feedbackLabel.setText("Correct!");
                if (type.equals("enemy")) { score += 4; enemies.remove(enemy); }
                else if (type.equals("obstacle")) { score += 2; obstacles.remove(obs); }
                else if (type.equals("boss") && bossEnemy != null) {
                    bossEnemy.health--;
                    score += 5;
                    bossEnemy.quizTriggered = false;
                    if (bossEnemy.health <= 0) { score += 10; boss = null; }
                }
                dialog.close();
                resumeGame();
            } else {
                feedbackLabel.setText("Incorrect!");
                if (type.equals("enemy") || type.equals("boss")) {
                    lives--;
                    if (lives <= 0) { gameOver = true; stopGameLoop(); }
                } else {
                    baseObstacleSpeed = 2;
                    Timeline penaltyTimeline = new Timeline(new KeyFrame(Duration.seconds(2), ev -> baseObstacleSpeed = 4));
                    penaltyTimeline.play();
                }
                dialog.close();
                resumeGame();
            }
        });
        VBox dialogVBox = new VBox(10);
        dialogVBox.setAlignment(Pos.CENTER);
        dialogVBox.setStyle("-fx-padding: 20; -fx-background-color: black; -fx-border-color: white;");
        dialogVBox.getChildren().addAll(questionLabel, answerField, submitButton, feedbackLabel);
        // Scene dialogScene = new Scene(dialogVBox, 300, 150);
        Scene dialogScene = new Scene(dialogVBox, 600, 250);
        dialogVBox.setAlignment(Pos.CENTER);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    
    private void resumeGame() {
        pausedForQuiz = false;
        if (!gameOver)
            gameLoop.play();
        else
            draw();
    }
    
    private void stopGameLoop() {
        gameLoop.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

