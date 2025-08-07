
package src.main.game.Adventure;

import src.main.utils.SceneManager;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AdventureMode extends Application {

    // Game dimensions for fighting game logic
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 576;

    // Key state tracking map
    private Map<String, Boolean> keys = new HashMap<>();

    // Fighters
    private Fighter player, enemy;
    // Canvas and graphics context for drawing
    private Canvas canvas;
    private GraphicsContext gc;
    // Game timer (in seconds)
    private int gameTime = 60;
    private Label timerLabel;
    // For delta time calculation
    private long lastTime;

    @Override
    public void start(Stage primaryStage) {
        // This start method is used when running as a standalone application.
        Parent root = createRoot(primaryStage);
        Scene scene = new Scene(root);
        primaryStage.setTitle("Fighting Game in Adventure Mode");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Instance method to create the game root.
     * If stage is null (as when called from SceneManager), the back button will navigate to the main menu.
     */
    public Parent createRoot(Stage stage) {
        // Create the main game pane and set its preferred size.
        Pane gamePane = new Pane();
        gamePane.setPrefSize(WIDTH, HEIGHT);
        gamePane.setStyle("-fx-background-color: black;");

        // Create the canvas used for rendering the fighting game.
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gamePane.getChildren().add(canvas);

        // Create a timer label (placed at the top-center).
        timerLabel = new Label(Integer.toString(gameTime));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle("-fx-font-family: 'Press Start 2P'; -fx-font-size: 24px;");
        timerLabel.setLayoutX(WIDTH / 2 - 20);
        timerLabel.setLayoutY(20);
        gamePane.getChildren().add(timerLabel);

        // Create a back button (for example, to return to a main menu).
        Button backButton = new Button("Back to Main Menu");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> {
            if (stage != null) {
                stage.close();
            } else {
                // If no stage is provided, navigate via SceneManager.
                SceneManager.showMainMenu();
            }
        });
        gamePane.getChildren().add(backButton);

        // Create the scene root as a StackPane to mimic the AdventureMode structure.
        StackPane root = new StackPane(gamePane);

        // Set up keyboard event handlers on the root node.
        root.setOnKeyPressed((KeyEvent e) -> keys.put(e.getCode().toString(), true));
        root.setOnKeyReleased((KeyEvent e) -> keys.put(e.getCode().toString(), false));

        // Ensure the root has focus so key events are captured.
        root.requestFocus();

        // Create fighters.
        player = new Fighter(100, HEIGHT - 200, "player");
        enemy = new Fighter(WIDTH - 200, HEIGHT - 200, "enemy");

        // Initialize time variables and start the game loop.
        lastTime = System.nanoTime();
        startGameLoop();

        return root;
    }

    /**
     * Static method to allow SceneManager to call createRoot() without parameters.
     */
    public static Parent createRoot() {
        return new AdventureMode().createRoot(null);
    }

    /**
     * Starts the game loop using an AnimationTimer.
     */
    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastSecondUpdate = 0;

            @Override
            public void handle(long now) {
                // Calculate delta time (in seconds)
                double deltaTime = (now - lastTime) / 1e9;
                lastTime = now;

                update(deltaTime);
                render();

                // Update the countdown timer every second.
                if (now - lastSecondUpdate >= 1e9) {
                    gameTime--;
                    timerLabel.setText(Integer.toString(gameTime));
                    lastSecondUpdate = now;
                    if (gameTime <= 0) {
                        stop();
                        timerLabel.setText("Game Over");
                    }
                }
            }
        };
        timer.start();
    }

    /**
     * Update game logic based on keyboard input and fighter states.
     */
    private void update(double deltaTime) {
        // Handle player movement and actions.
        if (keys.getOrDefault("LEFT", false)) {
            player.move(-200 * deltaTime, 0);
            player.setState("run");
        } else if (keys.getOrDefault("RIGHT", false)) {
            player.move(200 * deltaTime, 0);
            player.setState("run");
        } else {
            player.setState("idle");
        }
        if (keys.getOrDefault("UP", false)) {
            player.jump();
        }
        if (keys.getOrDefault("SPACE", false)) {
            player.attack();
        }

        // Update both fighters.
        player.update(deltaTime);
        enemy.update(deltaTime);

        // Simple enemy AI: move toward the player and attack when in range.
        if (enemy.x > player.x + 50) {
            enemy.move(-100 * deltaTime, 0);
            enemy.setState("run");
        } else if (enemy.x < player.x - 50) {
            enemy.move(100 * deltaTime, 0);
            enemy.setState("run");
        } else {
            enemy.setState("idle");
        }
        if (Math.abs(enemy.x - player.x) < 60 && !enemy.isAttacking) {
            enemy.attack();
        }

        // Check collision for attacks and update health.
        if (player.isAttacking && player.attackBox().intersects(enemy.getBounds())) {
            enemy.takeHit(10);
            player.isAttacking = false;
        }
        if (enemy.isAttacking && enemy.attackBox().intersects(player.getBounds())) {
            player.takeHit(10);
            enemy.isAttacking = false;
        }
    }

    /**
     * Render the current game state.
     */
    private void render() {
        // Clear the canvas.
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw a simple background.
        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw health bars for both fighters.
        gc.setFill(Color.RED);
        gc.fillRect(50, 30, 300, 20);
        gc.fillRect(WIDTH - 350, 30, 300, 20);
        gc.setFill(Color.BLUE);
        gc.fillRect(50, 30, 300 * (player.health / 100.0), 20);
        gc.fillRect(WIDTH - 350, 30, 300 * (enemy.health / 100.0), 20);

        // Render both fighters.
        player.render(gc);
        enemy.render(gc);
    }

    /**
     * The Fighter class represents a fighter (player or enemy) including movement,
     * jumping, attacking, and health.
     */
    class Fighter {
        // Position and dimensions.
        double x, y;
        double width = 50, height = 150;
        // Velocity for simple physics.
        double velocityX = 0, velocityY = 0;
        boolean onGround = true;
        // Health (max 100).
        double health = 100;
        // Attack state.
        boolean isAttacking = false;
        long attackTime = 0;
        // Current animation state.
        String currentState = "idle";
        // Identity string: "player" or "enemy"
        String identity;

        // Map of state -> image (for simplicity, one image per state)
        Map<String, Image> images = new HashMap<>();

        /**
         * Constructor.
         *
         * @param startX   initial x-position
         * @param startY   initial y-position
         * @param identity "player" or "enemy"
         */
        public Fighter(double startX, double startY, String identity) {
            this.x = startX;
            this.y = startY;
            this.identity = identity;
            loadImages();
        }

        /**
         * Loads images from resources.
         * Assumes images are located under:
         *   /img/kenji/ for player and /img/samuraiMack/ for enemy.
         */
        private void loadImages() {
            try {
                String folder = identity.equals("player") ? "kenji" : "samuraiMack";
                images.put("idle", new Image(getClass().getResourceAsStream("/img/" + folder + "/Idle.png")));
                images.put("run", new Image(getClass().getResourceAsStream("/img/" + folder + "/Run.png")));
                images.put("jump", new Image(getClass().getResourceAsStream("/img/" + folder + "/Jump.png")));
                images.put("attack", new Image(getClass().getResourceAsStream("/img/" + folder + "/Attack1.png")));
                images.put("takeHit", new Image(getClass().getResourceAsStream("/img/" + folder + "/Take hit.png")));
                images.put("death", new Image(getClass().getResourceAsStream("/img/" + folder + "/Death.png")));
            } catch (Exception e) {
                System.out.println("Error loading images for " + identity + ": " + e.getMessage());
            }
        }

        /**
         * Moves the fighter by the given offset.
         */
        public void move(double dx, double dy) {
            x += dx;
            y += dy;
        }

        /**
         * Makes the fighter jump if on the ground.
         */
        public void jump() {
            if (onGround) {
                velocityY = -400;
                onGround = false;
                setState("jump");
            }
        }

        /**
         * Initiates an attack.
         */
        public void attack() {
            if (!isAttacking) {
                isAttacking = true;
                setState("attack");
                attackTime = System.nanoTime();
            }
        }

        /**
         * Processes damage from an opponent’s attack.
         */
        public void takeHit(double damage) {
            health -= damage;
            if (health < 0) {
                health = 0;
                setState("death");
            } else {
                setState("takeHit");
            }
        }

        /**
         * Updates physics and resets the attack state after a fixed duration.
         */
        public void update(double deltaTime) {
            // Apply gravity.
            velocityY += 800 * deltaTime;
            y += velocityY * deltaTime;

            // Simple ground collision (assumes ground level is at HEIGHT - 50)
            if (y >= HEIGHT - height - 50) {
                y = HEIGHT - height - 50;
                velocityY = 0;
                onGround = true;
            }

            // Reset attack state after 0.5 seconds.
            if (isAttacking && (System.nanoTime() - attackTime) / 1e9 > 0.5) {
                isAttacking = false;
                setState("idle");
            }
        }

        /**
         * Renders the fighter using the current animation state's image.
         */
        public void render(GraphicsContext gc) {
            Image img = images.get(currentState);
            if (img != null) {
                gc.drawImage(img, x, y, width, height);
            } else {
                // Fallback drawing (rectangle) if no image is loaded.
                gc.setFill(identity.equals("player") ? Color.BLUE : Color.RED);
                gc.fillRect(x, y, width, height);
            }
        }

        /**
         * Sets the current animation state.
         */
        public void setState(String state) {
            if (!currentState.equals(state)) {
                currentState = state;
            }
        }

        /**
         * Returns the fighter's bounds for collision detection.
         */
        public Rectangle2D getBounds() {
            return new Rectangle2D(x, y, width, height);
        }

        /**
         * Defines the fighter’s attack hitbox.
         */
        public Rectangle2D attackBox() {
            double attackWidth = 50;
            double attackHeight = height / 2;
            double attackX = identity.equals("player") ? x + width : x - attackWidth;
            double attackY = y + height / 4;
            return new Rectangle2D(attackX, attackY, attackWidth, attackHeight);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}





