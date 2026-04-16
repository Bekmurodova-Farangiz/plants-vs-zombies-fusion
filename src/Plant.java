import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;

public class Plant {

    private static final double DEFAULT_VIEW_SIZE = Math.min(BoardMetrics.CELL_WIDTH, BoardMetrics.CELL_HEIGHT) * 0.83;
    private static final Image DEFAULT_IMAGE = ImageAssets.load("file:src/assets/peashooter.png");

    private final ImageView view;
    private int health;
    private int row;
    private int col;
    private Timeline shootingTimeline;
    private int cost;
    private double shootingInterval;
    private double cooldown;
    private int waterCost;
    // constructor
    public Plant(int row, int col) {
        view = new ImageView(DEFAULT_IMAGE);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        view.setFitWidth(DEFAULT_VIEW_SIZE);
        view.setFitHeight(DEFAULT_VIEW_SIZE);

        health = 100;
        this.row = row;
        this.col = col;
        cost = 50;
        shootingInterval = 2.0;
        cooldown = 2.0;
        waterCost = 0;

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
    public final void configureView() {
        view.setPreserveRatio(shouldPreserveAspectRatio());
        view.setSmooth(true);
        view.setFitWidth(getVisualFitWidth());
        view.setFitHeight(getVisualFitHeight());
        view.setTranslateX(getVisualOffsetX());
        view.setTranslateY(getVisualOffsetY());
    }
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    public double getCenterX() {
        return (col * BoardMetrics.CELL_WIDTH) + (BoardMetrics.CELL_WIDTH / 2.0);
    }

    public double getCenterY() {
        return (row * BoardMetrics.CELL_HEIGHT) + (BoardMetrics.CELL_HEIGHT / 2.0);
    }

    public double getShootOriginX() {
        double plantWidth = view.getFitWidth() > 0 ? view.getFitWidth() : view.getBoundsInLocal().getWidth();
        return getCenterX() + (plantWidth / 2.0);
    }

    public double getShootOriginY() {
        return getCenterY();
    }
    public double getVisualFitWidth() {
        return DEFAULT_VIEW_SIZE;
    }

    public double getVisualFitHeight() {
        return DEFAULT_VIEW_SIZE;
    }

    public double getVisualOffsetX() {
        return 0;
    }

    public double getVisualOffsetY() {
        return 0;
    }
    public boolean shouldPreserveAspectRatio() {
        return true;
    }
    public boolean blocksZombies() {
        return true;
    }
    public void onPlaced(GameBoard board) {
    }
    public void onRemoved() {
    }
    public void onShotFired() {
    }
    public void onGamePaused() {
    }
    public void onGameResumed(GameBoard board) {
    }
    public void onGameEnded() {
        stopShooting();
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
    public void setPlantImage(Image image) {
        view.setViewport(null);
        view.setImage(image);
    }
    public void setPlantImage(String imagePath) {
        setPlantImage(ImageAssets.load(imagePath));
    }
    public int getWaterCost() {
    return waterCost;
    }

    public void setWaterCost(int waterCost) {
        this.waterCost = waterCost;
    }
}
