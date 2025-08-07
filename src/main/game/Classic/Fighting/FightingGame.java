package src.main.game.Classic.Fighting;

import src.main.utils.SceneManager;
import src.main.auth.SessionManager;
import src.main.leaderboard.LeaderboardService;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.io.File;

public class FightingGame extends Application {
    private final double GRAVITY = 0.7;
    private final double TOP_MARGIN = 190;

    private Canvas canvas;
    private GraphicsContext gc;

    private Sprite background, shop;
    private Fighter player, enemy;

    // New: game over flags
    private boolean gameOver = false;
    private String winnerText = null;

    // Health UI elements
    private StackPane playerHealthPane, enemyHealthPane;
    private Rectangle playerHealthFill, enemyHealthFill;
    
    // Timer UI
    private StackPane timerPane;
    private Label timerLabel;
    
    // Special Attack UI
    private Rectangle playerSpecialBar, enemySpecialBar;
    private ScaleTransition playerPulse, enemyPulse;

    private HashMap<KeyCode, Boolean> keys = new HashMap<>();

    private AnimationTimer gameLoop;
    private double canvasWidth, canvasHeight;

    // Timer
    private long startTime;
    private final int gameDuration = 60;

    // Jump landing flags
    private boolean playerJustLanded = false;
    private boolean enemyJustLanded = false;

    // Bonus fireball
    private Fireball bonusFireball = null;
    private QuestionManager questionManager;
    private boolean bonusTriggered = false;
    private boolean inBonus = false;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        canvasWidth = screenBounds.getWidth();
        canvasHeight = screenBounds.getHeight();

        Pane root = new Pane();
        Scene scene = new Scene(root, canvasWidth, canvasHeight);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);

        canvas = new Canvas(canvasWidth, canvasHeight);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // Health bars setup
        double playerHealthBarWidth = canvasWidth * 0.45;
        double enemyHealthBarWidth  = canvasWidth * 0.46;
        double healthHeight = 30;
        double uiTopPadding = 20;

        Rectangle playerHealthBg = new Rectangle(playerHealthBarWidth, healthHeight, Color.BLACK);
        playerHealthFill = new Rectangle(playerHealthBarWidth, healthHeight, Color.GREEN);
        playerHealthPane = new StackPane(playerHealthBg, playerHealthFill);
        StackPane.setAlignment(playerHealthFill, Pos.CENTER_LEFT);
        playerHealthPane.setLayoutX(20);
        playerHealthPane.setLayoutY(uiTopPadding);
        playerHealthPane.setPrefSize(playerHealthBarWidth, healthHeight);
        playerHealthPane.setStyle("-fx-border-color: white; -fx-border-width: 4 0 4 4;");

        Rectangle enemyHealthBg = new Rectangle(enemyHealthBarWidth, healthHeight, Color.BLACK);
        enemyHealthFill = new Rectangle(enemyHealthBarWidth, healthHeight, Color.GREEN);
        enemyHealthPane = new StackPane(enemyHealthBg, enemyHealthFill);
        StackPane.setAlignment(enemyHealthFill, Pos.CENTER_RIGHT);
        enemyHealthPane.setLayoutX(canvasWidth - enemyHealthBarWidth - 20);
        enemyHealthPane.setLayoutY(uiTopPadding);
        enemyHealthPane.setPrefSize(enemyHealthBarWidth, healthHeight);
        enemyHealthPane.setStyle("-fx-border-color: white; -fx-border-width: 4 4 4 0;");

        root.getChildren().addAll(playerHealthPane, enemyHealthPane);
        
        // Special attack bars
        playerSpecialBar = new Rectangle(playerHealthBarWidth, 10, Color.PURPLE);
        playerSpecialBar.setOpacity(0);
        playerSpecialBar.setLayoutX(playerHealthPane.getLayoutX());
        playerSpecialBar.setLayoutY(playerHealthPane.getLayoutY() + healthHeight + 5);

        enemySpecialBar = new Rectangle(enemyHealthBarWidth, 10, Color.PURPLE);
        enemySpecialBar.setOpacity(0);
        enemySpecialBar.setLayoutX(enemyHealthPane.getLayoutX());
        enemySpecialBar.setLayoutY(enemyHealthPane.getLayoutY() + healthHeight + 5);

        root.getChildren().addAll(playerSpecialBar, enemySpecialBar);

        playerPulse = new ScaleTransition(Duration.seconds(1.5), playerSpecialBar);
        playerPulse.setFromX(0.95); playerPulse.setToX(1.05);
        playerPulse.setCycleCount(ScaleTransition.INDEFINITE);
        playerPulse.setAutoReverse(true);

        enemyPulse = new ScaleTransition(Duration.seconds(1.5), enemySpecialBar);
        enemyPulse.setFromX(0.95); enemyPulse.setToX(1.05);
        enemyPulse.setCycleCount(ScaleTransition.INDEFINITE);
        enemyPulse.setAutoReverse(true);

        // Timer panel
        timerPane = new StackPane();
        timerPane.setPrefSize(100, 50);
        timerPane.setLayoutX(canvasWidth/2 - 50);
        timerPane.setLayoutY(uiTopPadding);
        timerPane.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 4; -fx-alignment: center;");
        timerLabel = new Label("60");
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle("-fx-font-family: 'Press Start 2P'; -fx-font-size: 12;");
        timerPane.getChildren().add(timerLabel);
        root.getChildren().add(timerPane);

        // Background & shop
        // background = new Sprite(new Image("file:Sprite/FightingGame/background.png"), 0, 0, canvasWidth, canvasHeight);
        String baseDir = System.getProperty("user.dir");
        File bgFile = new File(baseDir + "/assets/Sprite/FightingGame/background.png");
        Image bgImage = new Image(bgFile.toURI().toString()); // URI to string gives "file:/..." format
        background = new Sprite(bgImage, 0, 0, canvasWidth, canvasHeight);
        // shop = new Sprite(
        //     new Image("file:Sprite/FightingGame/shop.png"),
        //     canvasWidth * 0.6 + 56,
        //     canvasHeight * 0.2 + TOP_MARGIN - 150,
        //     canvasWidth * 0.3,
        //     canvasHeight * 0.5 + 76
        // );
        File shopFile = new File(baseDir, "/assets/Sprite/FightingGame/shop.png");
        Image shopImage = new Image(shopFile.toURI().toString());

        shop = new Sprite(
            shopImage,
            canvasWidth * 0.6 + 56,
            canvasHeight * 0.2 + TOP_MARGIN - 150,
            canvasWidth * 0.3,
            canvasHeight * 0.5 + 76
        );        
        shop.framesMax = 6;
        shop.frameInterval = 150;

        // Player sprites
        HashMap<String, SpriteInfo> playerSprites = new HashMap<>();
        // playerSprites.put("idle",    new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Idle.png",    8));
        // playerSprites.put("run",     new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Run.png",     8));
        // playerSprites.put("jump",    new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Jump.png",    2));
        // playerSprites.put("fall",    new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Fall.png",    2));
        // playerSprites.put("attack",  new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Attack1.png", 6));
        // playerSprites.put("takeHit", new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Take Hit - white silhouette.png", 4));
        // playerSprites.put("death",   new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Death.png",   6));
        // playerSprites.put("specialAttack", new SpriteInfo("file:Sprite/FightingGame/samuraiMack/Attack2.png", 6));
        playerSprites.put("idle",    new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Idle.png").toURI().toString(),    8));
        playerSprites.put("run",     new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Run.png").toURI().toString(),     8));
        playerSprites.put("jump",    new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Jump.png").toURI().toString(),    2));
        playerSprites.put("fall",    new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Fall.png").toURI().toString(),    2));
        playerSprites.put("attack",  new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Attack1.png").toURI().toString(), 6));
        playerSprites.put("takeHit", new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Take Hit - white silhouette.png").toURI().toString(), 4));
        playerSprites.put("death",   new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Death.png").toURI().toString(),   6));
        playerSprites.put("specialAttack", new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/samuraiMack/Attack2.png").toURI().toString(), 6));        

        double shopBase = shop.getY() + shop.getHeight();
        double verticalOffset = 80;
        double playerHeight = 157;
        double playerY = shopBase + verticalOffset - playerHeight;
        player = new Fighter(
            playerSprites.get("idle").getImage(),
            120, playerY, 2.5, 8,
            215, playerHeight,
            GRAVITY, playerSprites,
            20, 50, 40, 50
        );

        // Enemy sprites
        HashMap<String, SpriteInfo> enemySprites = new HashMap<>();
        // enemySprites.put("idle",    new SpriteInfo("file:Sprite/FightingGame/kenji/Idle.png",    4));
        // enemySprites.put("run",     new SpriteInfo("file:Sprite/FightingGame/kenji/Run.png",     8));
        // enemySprites.put("jump",    new SpriteInfo("file:Sprite/FightingGame/kenji/Jump.png",    2));
        // enemySprites.put("fall",    new SpriteInfo("file:Sprite/FightingGame/kenji/Fall.png",    2));
        // enemySprites.put("attack",  new SpriteInfo("file:Sprite/FightingGame/kenji/Attack1.png", 4));
        // enemySprites.put("takeHit", new SpriteInfo("file:Sprite/FightingGame/kenji/Take hit.png",  3));
        // enemySprites.put("death",   new SpriteInfo("file:Sprite/FightingGame/kenji/Death.png",   7));
        // enemySprites.put("specialAttack", new SpriteInfo("file:Sprite/FightingGame/kenji/Attack2.png", 4));
        enemySprites.put("idle",    new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Idle.png").toURI().toString(),    4));
        enemySprites.put("run",     new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Run.png").toURI().toString(),     8));
        enemySprites.put("jump",    new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Jump.png").toURI().toString(),    2));
        enemySprites.put("fall",    new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Fall.png").toURI().toString(),    2));
        enemySprites.put("attack",  new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Attack1.png").toURI().toString(), 4));
        enemySprites.put("takeHit", new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Take hit.png").toURI().toString(), 3));
        enemySprites.put("death",   new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Death.png").toURI().toString(),   7));
        enemySprites.put("specialAttack", new SpriteInfo(new File(baseDir, "/assets/Sprite/FightingGame/kenji/Attack2.png").toURI().toString(), 4));        

        double enemyHeight = 167;
        double enemyY = shopBase + verticalOffset - enemyHeight;
        enemy = new Fighter(
            enemySprites.get("idle").getImage(),
            1300, enemyY, 2.5, 4,
            215, enemyHeight,
            GRAVITY, enemySprites,
            -10, 50, 10, 50
        );

        // Key handlers
        // scene.setOnKeyPressed(e -> keys.put(e.getCode(), true));
        scene.setOnKeyPressed(e -> {
            keys.put(e.getCode(), true);

            // If the game is over, handle R and M
            if (gameOver) {
                if (e.getCode() == KeyCode.R) {
                    // Stop loop, close this window, re-launch FightingGame
                    gameLoop.stop();
                    primaryStage.close();
                    Platform.runLater(FightingGame::launchGame);
                }
                else if (e.getCode() == KeyCode.M) {
                    // Stop loop, close this window, show Main Menu
                    gameLoop.stop();
                    primaryStage.close();
                    Platform.runLater(() -> {
                        Stage main = SceneManager.getPrimaryStage();
                        main.show();
                        main.setFullScreen(true);
                        SceneManager.showMainMenu();
                    });
                }
                else if (e.getCode() == KeyCode.S) {
                    // ← NEW: Save score under the logged‑in user
                    String user = SessionManager.getUsername();
                    int finalScore = 50;  // or currentDistance, whichever you track
                    LeaderboardService.pushLeaderboardDataAsync(
                        user,
                        finalScore,
                        "Pro", //PreferencesManager.getDifficultyString()
                        "Fighting"
                    );
                    System.out.println("Score saved for " + user + "!");
                    // 2) Return to main menu
                    // gameLoop.stop();
                    // primaryStage.close();
                    // Platform.runLater(() -> {
                    //     Stage main = SceneManager.getPrimaryStage();
                    //     main.show();
                    //     main.setFullScreen(true);
                    //     SceneManager.showMainMenu();
                    // });
                    primaryStage.close();
                    Platform.runLater(() -> {
                        SceneManager.getPrimaryStage().show();
                        SceneManager.getPrimaryStage().setFullScreen(true);
                        SceneManager.showMainMenu();
                    });                    
                }
            }
        });
        scene.setOnKeyReleased(e -> keys.put(e.getCode(), false));

        // Initialize questions
        // questionManager = new QuestionManager("questions_cache.json");
        File questionFile = new File(baseDir, "resources/questions/questions_cache.json");
        questionManager = new QuestionManager(questionFile.getAbsolutePath());

        startTime = System.nanoTime();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
                render();
            }
        };
        gameLoop.start();
        primaryStage.show();
    }


    /**
     * VTU App entry point for Fighting Game.
     */
    public static void launchGame() {
        Platform.runLater(() -> {
            try {
                new FightingGame().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void update(long now) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        background.update();
        shop.update();

        // Reset horizontal velocity
        player.setVelocityX(0);
        enemy.setVelocityX(0);

        // If game is over, keep idle animations and skip logic
        if (gameOver) {
            player.switchSprite("idle");
            enemy.switchSprite("idle");
            player.update(canvasWidth, canvasHeight);
            enemy.update(canvasWidth, canvasHeight);
            return;
        }

        // → freeze both fighters into idle while we're in the bonus Q&A
        if (inBonus) {
            player.setVelocityX(0);
            enemy.setVelocityX(0);
            player.switchSprite("idle");
            enemy.switchSprite("idle");
            player.update(canvasWidth, canvasHeight);
            enemy.update(canvasWidth, canvasHeight);
            return;
        }

        // -- Player Controls --
        if (!player.isAttacking()) {
            if (player.hasSpecialAttack()) {
                player.setVelocityX(0);
                if (player.getVelocityY() == 0) player.switchSprite("idle");
            } else {
                if (isPressed(KeyCode.A)) {
                    player.setVelocityX(-5); player.setLastKey("A");
                    if (player.getVelocityY() == 0) { player.switchSprite("run"); playerJustLanded = false; }
                } else if (isPressed(KeyCode.D)) {
                    player.setVelocityX(5); player.setLastKey("D");
                    if (player.getVelocityY() == 0) { player.switchSprite("run"); playerJustLanded = false; }
                } else {
                    if (player.getVelocityY() == 0) {
                        if (!playerJustLanded) playerJustLanded = true;
                        else { player.switchSprite("idle"); playerJustLanded = false; }
                    }
                }
            }
        }
        if (isPressed(KeyCode.W) && player.getVelocityY() == 0) player.jump(-20, canvasHeight);
        if (isPressed(KeyCode.SPACE)) player.attack();
        if (!player.isAttacking() && player.getVelocityY() != 0) {
            player.switchSprite(player.getVelocityY() < 0 ? "jump" : "fall");
        }
        if (isPressed(KeyCode.Q) && player.hasSpecialAttack() && !player.getCurrentState().equals("specialAttack")) {
            player.triggerSpecialAttack();
            int deduction = (int)(enemy.getHealth() * 0.4);
            enemy.setHealth(Math.max(0, enemy.getHealth() - deduction));
            updateHealthBar(false, enemy.getHealth());
            enemy.switchSprite("takeHit");
        }

        // -- Enemy Controls --
        if (!enemy.isAttacking()) {
            if (enemy.hasSpecialAttack()) {
                enemy.setVelocityX(0);
                if (enemy.getVelocityY() == 0) enemy.switchSprite("idle");
            } else {
                if (isPressed(KeyCode.LEFT)) {
                    enemy.setVelocityX(-5); enemy.setLastKey("LEFT");
                    if (enemy.getVelocityY() == 0) { enemy.switchSprite("run"); enemyJustLanded = false; }
                } else if (isPressed(KeyCode.RIGHT)) {
                    enemy.setVelocityX(5); enemy.setLastKey("RIGHT");
                    if (enemy.getVelocityY() == 0) { enemy.switchSprite("run"); enemyJustLanded = false; }
                } else {
                    if (enemy.getVelocityY() == 0) {
                        if (!enemyJustLanded) enemyJustLanded = true;
                        else { enemy.switchSprite("idle"); enemyJustLanded = false; }
                    }
                }
            }
        }
        if (isPressed(KeyCode.UP) && enemy.getVelocityY() == 0) enemy.jump(-20, canvasHeight);
        if (isPressed(KeyCode.DOWN)) enemy.attack();
        if (!enemy.isAttacking() && enemy.getVelocityY() != 0) {
            enemy.switchSprite(enemy.getVelocityY() < 0 ? "jump" : "fall");
        }
        if (isPressed(KeyCode.SHIFT) && enemy.hasSpecialAttack() && !enemy.getCurrentState().equals("specialAttack")) {
            enemy.triggerSpecialAttack();
            int deduction = (int)(player.getHealth() * 0.4);
            player.setHealth(Math.max(0, player.getHealth() - deduction));
            updateHealthBar(true, player.getHealth());
            player.switchSprite("takeHit");
        }

        // Update fighters
        player.update(canvasWidth, canvasHeight);
        enemy.update(canvasWidth, canvasHeight);

        // -- Collision detection for attacks --
        if (player.isAttacking() && player.getCurrentFrame() == 4 &&
            Utility.rectangularCollision(player.getAttackBox(), enemy.getBounds()) &&
            ((player.getLastKey().equals("D") && player.getX() < enemy.getX()) ||
             (player.getLastKey().equals("A") && player.getX() > enemy.getX()))) {
            enemy.takeHit(); player.resetAttack(); updateHealthBar(false, enemy.getHealth());
        }
        if (player.isAttacking() && player.getCurrentFrame() == 4 &&
            !Utility.rectangularCollision(player.getAttackBox(), enemy.getBounds())) {
            player.resetAttack();
        }
        if (enemy.isAttacking() && enemy.getCurrentFrame() == 2 &&
            Utility.rectangularCollision(enemy.getAttackBox(), player.getBounds()) &&
            ((enemy.getLastKey().equals("RIGHT") && player.getX() > enemy.getX()) ||
             (enemy.getLastKey().equals("LEFT") && player.getX() < enemy.getX()))) {
            player.takeHit(); enemy.resetAttack(); updateHealthBar(true, player.getHealth());
        }
        if (enemy.isAttacking() && enemy.getCurrentFrame() == 2 &&
            !Utility.rectangularCollision(enemy.getAttackBox(), player.getBounds())) {
            enemy.resetAttack();
        }

        // -- Bonus Fireball Logic --
        // if (!bonusTriggered && bonusFireball == null && (player.getHealth() <= 20 || enemy.getHealth() <= 20)) {
        //     double fireballWidth = 50, fireballHeight = 50;
        //     double randomX = Math.random() * (canvasWidth - fireballWidth);
        //     bonusFireball = new Fireball(new Image("file:Sprite/FightingGame/fireball.png"), randomX, 0, fireballWidth, fireballHeight);
        // }
        if (!bonusTriggered && bonusFireball == null && (player.getHealth() <= 20 || enemy.getHealth() <= 20)) {
            double fireballWidth = 50, fireballHeight = 50;
            double randomX = Math.random() * (canvasWidth - fireballWidth);
            
            String baseDir = System.getProperty("user.dir");
            File fireballFile = new File(baseDir, "assets/Sprite/FightingGame/fireball.png");
            Image fireballImage = new Image(fireballFile.toURI().toString());

            bonusFireball = new Fireball(fireballImage, randomX, 0, fireballWidth, fireballHeight);
        }        
        if (bonusFireball != null) {
            bonusFireball.update(canvasHeight);
            if (bonusFireball.isOffScreen(canvasHeight)) bonusFireball=null;
            else {
                Rectangle2D fBounds = bonusFireball.getBounds();
                Rectangle2D pBounds = new Rectangle2D(player.getX(), player.getY(), player.getWidth(), player.getHeight());
                Rectangle2D eBounds = new Rectangle2D(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                if (Utility.rectangularCollision(fBounds, pBounds)) { triggerBonus(player); bonusFireball=null; }
                else if (Utility.rectangularCollision(fBounds, eBounds)) { triggerBonus(enemy); bonusFireball=null; }
            }
        }

        // -- Timer --
        int elapsed = (int)((now - startTime)/1_000_000_000);
        int remainingTime = gameDuration - elapsed;
        timerLabel.setText(String.valueOf(remainingTime));
        if (remainingTime <= 0 && !gameOver) {
            if (player.getHealth() == enemy.getHealth()) winnerText = "Draw!";
            else winnerText = player.getHealth() < enemy.getHealth() ? "Enemy Wins!" : "Player Wins!";
            gameOver=true;
            player.setVelocityX(0); enemy.setVelocityX(0);
            player.switchSprite("idle"); enemy.switchSprite("idle");
        }

        // -- Player death --
        if (player.getHealth() <= 0 && player.isOnGround(canvasHeight) && player.isDeathAnimationFinished() && !gameOver) {
            winnerText="Enemy Wins!"; gameOver=true;
            player.setVelocityX(0); enemy.setVelocityX(0);
            player.switchSprite("idle"); enemy.switchSprite("idle");
        }

        // -- Enemy death --
        if (enemy.getHealth() <= 0 && enemy.isOnGround(canvasHeight) && enemy.isDeathAnimationFinished() && !gameOver) {
            winnerText="Player Wins!"; gameOver=true;
            player.setVelocityX(0); enemy.setVelocityX(0);
            player.switchSprite("idle"); enemy.switchSprite("idle");
        }

        if (!player.hasSpecialAttack() && playerSpecialBar.getOpacity()!=0.0) {
            playerSpecialBar.setOpacity(0.0); playerPulse.stop();
        }
    }

    private void render() {
        gc.setFill(Color.rgb(255,255,255,0.15));
        gc.fillRect(0,0,canvasWidth,canvasHeight);

        background.draw(gc);
        shop.draw(gc);
        player.draw(gc);
        enemy.draw(gc);
        if (bonusFireball != null) bonusFireball.draw(gc);

        // Draw game-over text
        if (winnerText != null) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Press Start 2P", 50));
            gc.fillText(winnerText, canvasWidth/2 - 300, canvasHeight/2);

            // —— ADD THESE LINES BELOW —— 
            gc.setFill(Color.YELLOW);
            gc.setFont(javafx.scene.text.Font.font("Press Start 2P", 24));
            gc.fillText("PRESS R TO RESTART", 
                        canvasWidth/2 - 200,    // adjust X offset as needed
                        canvasHeight/2 + 60);   // a bit below the winner text
            gc.fillText("PRESS M TO RETURN TO MAIN MENU", 
                        canvasWidth/2 - 330, 
                        canvasHeight/2 + 100);
            gc.fillText("PRESS S TO SAVE SCORE", 
                        canvasWidth/2 - 440, 
                        canvasHeight/2 + 140);                        
            // ————————————————————————            
        }
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    private void updateHealthBar(boolean isPlayer, int health) {
        double fullWidth = canvasWidth * 0.40;
        double newWidth = (health / 100.0) * fullWidth;
        if (isPlayer) playerHealthFill.setWidth(newWidth);
        else enemyHealthFill.setWidth(newWidth);
    }

    private void showGlow(Fighter fighter, Color glowColor, Duration duration) {
        double centerX = fighter.getX() + fighter.getWidth()/2;
        double centerY = fighter.getY() + 5;
        double radius = Math.min(fighter.getWidth(), fighter.getHeight())/4;
        Circle glow = new Circle(centerX, centerY, radius, glowColor);
        glow.setOpacity(0.8);
        Pane root = (Pane) primaryStage.getScene().getRoot();
        root.getChildren().add(glow);
        PauseTransition pt = new PauseTransition(duration);
        pt.setOnFinished(e -> root.getChildren().remove(glow));
        pt.play();
    }

    // Bonus trigger
    private void triggerBonus(Fighter fighter) {
        bonusTriggered=true;
        // Switch to idle during question
        fighter.setVelocityX(0);
        fighter.switchSprite("idle");
        showGlow(fighter, Color.WHITE, Duration.seconds(0.5));
        inBonus = true;
        // immediately force *both* into idle
        player.setVelocityX(0);
        enemy.setVelocityX(0);
        player.switchSprite("idle");
        enemy.switchSprite("idle");
        showGlow(fighter, Color.WHITE, Duration.seconds(0.5));        
        Platform.runLater(() -> {
            Alert questionAlert = new Alert(Alert.AlertType.CONFIRMATION);
            questionAlert.initOwner(primaryStage);
            questionAlert.setTitle("Bonus Question!");
            Question q = questionManager.getRandomQuestion();
            if (q==null) return;
            questionAlert.setHeaderText(q.question);
            ButtonType btnA=new ButtonType("A: "+q.optionA);
            ButtonType btnB=new ButtonType("B: "+q.optionB);
            ButtonType btnC=new ButtonType("C: "+q.optionC);
            ButtonType btnD=new ButtonType("D: "+q.optionD);
            questionAlert.getButtonTypes().setAll(btnA,btnB,btnC,btnD);

            Optional<ButtonType> resp = questionAlert.showAndWait();
            int chosen=-1;
            if (resp.isPresent()) {
                if (resp.get()==btnA) chosen=0;
                else if (resp.get()==btnB) chosen=1;
                else if (resp.get()==btnC) chosen=2;
                else if (resp.get()==btnD) chosen=3;
            }
            if (chosen==q.correctOption) {
                Alert bonusAlert=new Alert(Alert.AlertType.CONFIRMATION);
                bonusAlert.initOwner(primaryStage);
                bonusAlert.setTitle("Correct!");
                bonusAlert.setHeaderText("Choose your bonus:");
                ButtonType bonusHeal=new ButtonType("Increase Health by 40%...");
                ButtonType bonusAttack=new ButtonType("Activate Special Attack");
                bonusAlert.getButtonTypes().setAll(bonusHeal,bonusAttack);
                Optional<ButtonType> bonusResp=bonusAlert.showAndWait();
                if (bonusResp.isPresent()) {
                    if (bonusResp.get()==bonusHeal) {
                        fighter.setHealth(Math.min(100,fighter.getHealth()+40));
                        updateHealthBar(fighter==player, fighter.getHealth());
                        fighter.switchSprite("idle");
                        showGlow(fighter, Color.GREEN, Duration.seconds(0.5));
                    } else {
                        fighter.setSpecialAttack(true);
                        if (fighter==player) {
                            playerSpecialBar.setOpacity(1.0); playerPulse.play();
                        } else {
                            enemySpecialBar.setOpacity(1.0); enemyPulse.play();
                        }
                    }
                }
            } else {
                Alert wrong = new Alert(Alert.AlertType.INFORMATION);
                wrong.initOwner(primaryStage);
                wrong.setTitle("Incorrect");
                wrong.setHeaderText("Incorrect answer. No bonus awarded.");
                wrong.showAndWait();
            }
        });
        // all done with bonus, hand control back
        inBonus = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

