import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Zombie {

    private ImageView view;
    private int health;
    private boolean moving;
    private Timeline movementTimeline;
    private int row;
    private double speed;

    //constructor
    public Zombie(int row) {
        view = new ImageView(new Image("file:src/assets/zombie.png"));
        view.setFitWidth(130);
        view.setFitHeight(130);
        health = 100;
        moving = true;
        this.row = row;
        speed = 1.0;
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
    public void setZombieImage(String imagePath) {
        view.setImage(new Image(imagePath));
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
}