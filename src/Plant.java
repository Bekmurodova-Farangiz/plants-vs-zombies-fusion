import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;

public class Plant {

    private Rectangle view;
    private int health;
    private int row;
    private int col;
    private Timeline shootingTimeline;

    public Plant(int row, int col) {
        view = new Rectangle(60, 60);
        view.setFill(Color.GREEN);

        health = 100;
        this.row = row;
        this.col = col;
    }
    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Plant health: " + health);
    }
    public int getHealth() {
        return health;
    }
    public boolean isDead() {
        return health <= 0;
    }
    public Rectangle getView() {
        return view;
    }
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    public void setShootingTimeline(Timeline shootingTimeline) {
        this.shootingTimeline = shootingTimeline;
    }

    public void stopShooting() {
        if (shootingTimeline != null) {
            shootingTimeline.stop();
        }
    }
    
}
