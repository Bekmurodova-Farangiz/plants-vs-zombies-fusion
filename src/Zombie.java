import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Zombie {

    private static final Image DEFAULT_IMAGE = ImageAssets.load("file:src/assets/zombie.png");

    private final ImageView view;
    private int health;
    private boolean moving;
    private Timeline movementTimeline;
    private int row;
    private double speed;
    private int attackDamage;

    //constructor
    public Zombie(int row) {
        view = new ImageView(DEFAULT_IMAGE);
        view.setFitWidth(130);
        view.setFitHeight(130);
        health = 100;
        moving = true;
        this.row = row;
        speed = 1.0;
        attackDamage = 1;
    }

    public ImageView getView() {
        return view;
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Zombie health: " + health);
    }

    public boolean isDead() {
        return health <= 0;
    }
    public void moveLeft() {
        view.setTranslateX(view.getTranslateX() - speed);
    }
    public void stopMoving() {
        moving = false;
    }

    public void startMoving() {
        moving = true;
    }
    public void setMovementTimeline(Timeline movementTimeline) {
        this.movementTimeline = movementTimeline;
    }

    public void stopAllActions() {
        moving = false;

        if (movementTimeline != null) {
            movementTimeline.stop();
        }
    }
    public int getRow() {
        return row;
    }
    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void setZombieImage(Image image) {
        view.setImage(image);
    }
    public void setZombieImage(String imagePath) {
        setZombieImage(ImageAssets.load(imagePath));
    }
    public void pauseActions() {
        if (movementTimeline != null) {
            movementTimeline.pause();
        }
    }

    public void resumeActions() {
        if (movementTimeline != null) {
            movementTimeline.play();
        }
    }
    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }
    
}
