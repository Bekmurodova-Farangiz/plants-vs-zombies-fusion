import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;


public class Zombie {

    private Rectangle view;
    private int health;
    private boolean moving;
    private Timeline movementTimeline;
    private int row;

    //constructor
    public Zombie(int row) {
        view = new Rectangle(60, 60);
        view.setFill(Color.BROWN);
        health = 100;
        moving = true;
        this.row = row;
    }

    public Rectangle getView() {
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
        view.setTranslateX(view.getTranslateX() - 2);
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
}