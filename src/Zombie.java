import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Zombie implements Movable, Attackable {

    private static final Image DEFAULT_IMAGE = ImageAssets.load("/assets/zombie.png");
    private static final Image CAGE_IMAGE = ImageAssets.load("/assets/cage.png");
    private static final double VIEW_WIDTH = 130;
    private static final double VIEW_HEIGHT = 130;

    private final Pane view;
    private final ImageView bodyView;
    private final ImageView cageView;
    private int health;
    private boolean moving;
    private Timeline movementTimeline;
    private int row;
    private double speed;
    private int attackDamage;
    private long trappedUntilMillis;
    private long pausedTrapRemainingMillis;

    //constructor
    public Zombie(int row) {
        bodyView = new ImageView(DEFAULT_IMAGE);
        bodyView.setFitWidth(VIEW_WIDTH);
        bodyView.setFitHeight(VIEW_HEIGHT);
        bodyView.setPreserveRatio(true);

        cageView = new ImageView(CAGE_IMAGE);
        cageView.setFitWidth(BoardMetrics.CELL_WIDTH);
        cageView.setFitHeight(BoardMetrics.CELL_HEIGHT);
        cageView.setPreserveRatio(true);
        cageView.setVisible(false);
        cageView.setMouseTransparent(true);

        view = new Pane();
        view.setPrefSize(VIEW_WIDTH, VIEW_HEIGHT);
        view.setMinSize(VIEW_WIDTH, VIEW_HEIGHT);
        view.setMaxSize(VIEW_WIDTH, VIEW_HEIGHT);
        view.resize(VIEW_WIDTH, VIEW_HEIGHT);
        view.setPickOnBounds(false);
        view.setMouseTransparent(true);

        bodyView.setLayoutX(0);
        bodyView.setLayoutY(0);
        cageView.setLayoutX((VIEW_WIDTH - BoardMetrics.CELL_WIDTH) / 2.0);
        cageView.setLayoutY((VIEW_HEIGHT - BoardMetrics.CELL_HEIGHT) / 2.0);
        view.getChildren().addAll(bodyView, cageView);

        health = 100;
        moving = true;
        this.row = row;
        speed = 1.0;
        attackDamage = 1;
    }

    public Node getView() {
        return view;
    }

    public ImageView getBodyView() {
        return bodyView;
    }

    public void takeDamage(int damage) {
        if (damage <= 0) {
            return;
        }

        health -= damage;
        System.out.println("Zombie health: " + health);
    }

    public void heal(int amount) {
        if (amount <= 0 || !isAlive()) {
            return;
        }

        health += amount;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    @Override
    public void move() {
        moveLeft();
    }

    public void moveLeft() {
        if (!moving || isTrapped()) {
            return;
        }

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

    public void onSpawn(GameBoard board) {
        specialAbility(board);
    }

    public abstract void act(GameBoard board);

    public abstract void specialAbility(GameBoard board);

    public int getRow() {
        return row;
    }
    public double getSpeed() {
        return speed;
    }

    protected void setSpeed(double speed) {
        this.speed = speed;
    }

    protected void setHealth(int health) {
        this.health = health;
    }

    protected void setZombieImage(Image image) {
        bodyView.setImage(image);
    }

    protected void setZombieImage(String imagePath) {
        setZombieImage(ImageAssets.load(imagePath));
    }
    public void pauseActions() {
        if (isTrapped()) {
            pausedTrapRemainingMillis = Math.max(0, trappedUntilMillis - System.currentTimeMillis());
        }

        if (movementTimeline != null) {
            movementTimeline.pause();
        }
    }

    public void resumeActions() {
        if (pausedTrapRemainingMillis > 0) {
            trappedUntilMillis = System.currentTimeMillis() + pausedTrapRemainingMillis;
            pausedTrapRemainingMillis = 0;
        }

        if (movementTimeline != null) {
            movementTimeline.play();
        }
    }
    public int getAttackDamage() {
        return attackDamage;
    }

    @Override
    public void attack(Plant target) {
        if (target == null || target.isInfected()) {
            return;
        }

        target.takeDamage(getAttackDamage());
    }

    protected void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void applyStatMultipliers(double healthMultiplier, double speedMultiplier) {
        health = Math.max(1, (int) Math.round(health * healthMultiplier));
        speed = Math.max(0.1, speed * speedMultiplier);
    }

    public void trapForSeconds(double seconds) {
        long durationMillis = Math.max(0L, (long) (seconds * 1000));
        long now = System.currentTimeMillis();

        trappedUntilMillis = Math.max(trappedUntilMillis, now) + durationMillis;
        cageView.setVisible(durationMillis > 0);
    }

    public boolean isTrapped() {
        return System.currentTimeMillis() < trappedUntilMillis;
    }

    public void updateStatusEffects() {
        if (!isTrapped() && cageView.isVisible()) {
            cageView.setVisible(false);
            pausedTrapRemainingMillis = 0;
        }
    }

    public double getVisualWidth() {
        return VIEW_WIDTH;
    }
}
