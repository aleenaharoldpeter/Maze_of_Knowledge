package src.main.game.Classic.Fighting;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Map;

/**
 * Fighter.java
 * Extends Sprite to add physics, health, and multi-state animations including a special attack.
 */
public class Fighter extends Sprite {
    // Movement and physics.
    private double velocityX = 0;
    private double velocityY = 0;
    private double gravity;

    // Health and status.
    private int health = 100;
    private boolean attacking = false;
    private boolean dead = false;
    private boolean deathAnimationFinished = false;

    // Sprite state management.
    private Map<String, SpriteInfo> sprites;
    private String currentState = "idle";

    // Offsets for drawing.
    private double offsetX, offsetY;

    // Attack box parameters.
    private double attackBoxOffsetX, attackBoxOffsetY, attackBoxWidth, attackBoxHeight;

    // Direction tracking.
    private String lastKey = "";

    // Special attack charge flag.
    private boolean hasSpecialAttack = false;

    public Fighter(Image image,
                   double x, double y, double scale, int framesMax,
                   double offsetX, double offsetY, double gravity,
                   Map<String, SpriteInfo> sprites,
                   double attackBoxOffsetX, double attackBoxOffsetY,
                   double attackBoxWidth, double attackBoxHeight) {
        super(image, x, y, image.getWidth() / framesMax * scale, image.getHeight() * scale);
        this.framesMax = framesMax;
        this.offsetX = offsetX;
        this.offsetY = offsetY - 210;  
        this.gravity = gravity;
        this.sprites = sprites;
        this.attackBoxOffsetX = attackBoxOffsetX;
        this.attackBoxOffsetY = attackBoxOffsetY;
        this.attackBoxWidth = attackBoxWidth;
        this.attackBoxHeight = attackBoxHeight;
    }

    // --- Movement & Direction ---

    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }

    public void setLastKey(String key) {
        this.lastKey = key;
    }

    public boolean isOnGround(double canvasHeight) {
        return this.y + this.height >= canvasHeight;
    }

    public void jump(double jumpForce, double canvasHeight) {
        if (isOnGround(canvasHeight)) {
            this.velocityY = jumpForce;
        }
    }

    // --- Attacks & Hits ---

    public void attack() {
        if (!attacking) {
            attacking = true;
            currentFrame = 0;
            switchSprite("attack");
        }
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void resetAttack() {
        attacking = false;
    }

    public void takeHit() {
        health -= 20;
        if (health <= 0) {
            health = 0;
            dead = true;
            switchSprite("death");
        } else {
            switchSprite("takeHit");
        }
    }

    public int getHealth() {
        return health;
    }

    // --- State Accessors ---

    /** Returns the current animation state. */
    public String getCurrentState() {
        return currentState;
    }

    // --- Special Attack Charge ---

    public boolean hasSpecialAttack() {
        return hasSpecialAttack;
    }

    public void setSpecialAttack(boolean value) {
        this.hasSpecialAttack = value;
    }

    public void triggerSpecialAttack() {
        switchSprite("specialAttack");
    }

    public void resetSpecialAttack() {
        hasSpecialAttack = false;
        switchSprite("idle");
    }

    // --- Animation State Switching ---

    public void switchSprite(String state) {
        if (currentState.equals("death")) return;
        if (currentState.equals("takeHit") && currentFrame < framesMax - 1) return;
        if (currentState.equals(state)) return;

        SpriteInfo info = sprites.get(state);
        if (info != null) {
            currentState = state;
            image = info.getImage();
            framesMax = info.getFramesMax();
            currentFrame = 0;
            lastFrameTime = System.currentTimeMillis();
        }
    }

    // --- Main Update Loop ---

    public void update(double canvasWidth, double canvasHeight) {
        // ——— Death‑freeze: once death anim is done, hold last frame ———
        if (currentState.equals("death") && deathAnimationFinished) {
            return;
        }        
        // Position and boundaries
        x += velocityX;
        y += velocityY;
        if (x < 0) x = 0;
        if ((x - offsetX - 200) + width > canvasWidth)
            x = canvasWidth - width + offsetX + 200;
        if (y < 0) y = 0;

        // Gravity
        if (y + height < canvasHeight) {
            velocityY += gravity;
        } else {
            y = canvasHeight - height;
            velocityY = 0;
        }

        // Animation frame advance
        super.update();

        // Death animation completion
        if (dead && currentState.equals("death") && currentFrame >= framesMax - 1) {
            deathAnimationFinished = true;
        }

        // Auto-reset normal attack
        if (currentState.equals("attack") && currentFrame >= framesMax - 1) {
            resetAttack();
        }

        // Auto-switch takeHit → idle
        if (currentState.equals("takeHit") && currentFrame >= framesMax - 1) {
            switchSprite("idle");
        }

        // Auto-reset special attack when its last frame finishes
        if (currentState.equals("specialAttack") && currentFrame >= framesMax - 1) {
            resetSpecialAttack();
        }
    }

    // --- Rendering ---

    @Override
    public void draw(GraphicsContext gc) {
        if (framesMax > 1) {
            double spriteWidth = image.getWidth() / framesMax;
            gc.drawImage(
                image,
                currentFrame * spriteWidth, 0, spriteWidth, image.getHeight(),
                x - offsetX, y - offsetY, width, height
            );
        } else {
            gc.drawImage(image, x - offsetX, y - offsetY, width, height);
        }
    }

    // --- Collision Helpers ---

    public Rectangle2D getBounds() {
        return new Rectangle2D(x - offsetX, y - offsetY, width, height);
    }

    public Rectangle2D getAttackBox() {
        return new Rectangle2D(x + attackBoxOffsetX, y + attackBoxOffsetY,
                               attackBoxWidth, attackBoxHeight);
    }

    // --- Misc Helpers ---

    public boolean isDeathAnimationFinished() {
        return deathAnimationFinished;
    }

    public String getLastKey() {
        return lastKey;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    /** Exposed for FightingGame logic */
    public double getVelocityY() {
        return velocityY;
    }

    //     // → if we’re in death state *and* the death animation has played once, do nothing
    // if (currentState.equals("death") && deathAnimationFinished) {
    //     return;
    // }


    /** Allows collision logic to set new health */
    public void setHealth(int newHealth) {
        this.health = newHealth;
    }
}

