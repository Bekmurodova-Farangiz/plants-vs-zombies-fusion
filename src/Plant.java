import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Plant {

    private ImageView view;
    private int health;
    private int row;
    private int col;
    private Timeline shootingTimeline;
    private int cost;
    private double shootingInterval;
    private double cooldown;
    // constructor
    public Plant(int row, int col) {
        view = new ImageView(new Image("file:src/assets/peashooter.png"));
        view.setFitWidth(70);
        view.setFitHeight(70);

        health = 100;
        this.row = row;
        this.col = col;
        cost = 50;
        shootingInterval = 2.0;
        cooldown = 2.0;

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
    public ImageView getView() {
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
    public int getCost() {
        return cost;
    }
    public double getShootingInterval() {
        return shootingInterval;
    }
    public void setShootingInterval(double shootingInterval) {
        this.shootingInterval = shootingInterval;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }
}
