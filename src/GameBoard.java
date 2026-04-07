import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
//timelien imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
// list of plants to GameBoard.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class GameBoard extends GridPane {

    private static final int ROWS = 5;
    private static final int COLUMNS = 10;
    private static final int CELL_WIDTH = 128;
    private static final int CELL_HEIGHT = 128;
    private List<Plant> plants = new ArrayList<>();    //plants stores all plant objects on the board
    private List<Zombie> zombies = new ArrayList<>(); //zombies stores all zombie objects on the board
    private List<Bullet> bullets = new ArrayList<>();  //bullets added to the storage
    private boolean gameOver = false; //Global state
    private int sunPoints = 250; //Player's starting points
    private Timeline zombieSpawner;
    private Timeline sunGenerator;
    private String selectedPlantType = "PeaShooter";
    private Map<String, Long> plantCooldowns = new HashMap<>();
    //Constructor
    public GameBoard() {
        this.setStyle("-fx-background-color: transparent;");
        createBoard();
        spawnZombie();
        startZombieSpawner();
    }

    private void createBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                final int currentRow = row;
                final int currentCol = col;
                Rectangle cellBackground = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
                cellBackground.setFill(Color.rgb(144, 238, 144, 0.25));
                cellBackground.setStroke(Color.rgb(0, 100, 0, 0.3));
                cellBackground.setStrokeWidth(1);
                cellBackground.setOnMouseEntered(e -> 
                cellBackground.setFill(Color.rgb(173, 216, 230, 0.4))
                );
                cellBackground.setOnMouseExited(e -> 
                    cellBackground.setFill(Color.rgb(144, 238, 144, 0.45))
                );

                StackPane cell = new StackPane();
                cell.getChildren().add(cellBackground);
                final boolean[] hasPlant = {false};
                final Plant[] currentPlant = {null};

                cell.setOnMouseClicked(e -> {

                    if (!hasPlant[0]) {
                        if (isOnCooldown(selectedPlantType)) {
                            System.out.println(selectedPlantType + " is on cooldown!");
                            return;
                        }
                        Plant plant;

                        if (selectedPlantType.equals("PeaShooter")) {
                            plant = new PeaShooter(currentRow, currentCol);
                        } else if (selectedPlantType.equals("WallPlant")) {
                            plant = new WallPlant(currentRow, currentCol);
                        } else {
                            plant = new Sunflower(currentRow, currentCol, this);
                        }

                        if (sunPoints < plant.getCost()) {
                            System.out.println("Not enough sun points!");
                            return;
                        }

                        sunPoints -= plant.getCost();
                        plantCooldowns.put(selectedPlantType, System.currentTimeMillis() + (long)(plant.getCooldown() * 1000));
                        System.out.println("Sun points left: " + sunPoints);

                        currentPlant[0] = plant;
                        plants.add(plant);

                        cell.getChildren().add(plant.getView());
                        hasPlant[0] = true;

                        startShooting(plant);

                        System.out.println("Plant placed at row " + currentRow + " col " + currentCol);
                    }
                    // IF PLANT EXISTS → DAMAGE IT
                    else {
                        Plant plant = currentPlant[0];

                        plant.takeDamage(20);

                        if (plant.isDead()) {
                            cell.getChildren().remove(plant.getView());
                            plants.remove(plant); //when it dies remove from list 
                            hasPlant[0] = false;
                            currentPlant[0] = null;

                            System.out.println("Plant died and removed.");
                        }
                    }
                });

                add(cell, col, row);
            }
        }
    }
    public void spawnZombie() {
        Random random = new Random();
        int row = random.nextInt(ROWS);

        Zombie zombie;

        if (Math.random() < 0.5) {
            zombie = new Zombie(row);
        } else {
            zombie = new FastZombie(row);
        }

        zombies.add(zombie);

        double zombieWidth = zombie.getView().getBoundsInLocal().getWidth();
        double zombieHeight = zombie.getView().getBoundsInLocal().getHeight();

        double x = (COLUMNS * CELL_WIDTH) - zombieWidth;
        double y = (row * CELL_HEIGHT) + 10;

        zombie.getView().setTranslateX(x);
        zombie.getView().setTranslateY(y);

        getChildren().add(zombie.getView());
        startGameLoop(zombie);
    }
    public void startGameLoop(Zombie zombie) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
                if (gameOver) { //Guard clause
                    zombie.stopAllActions();
                    return;
                }
                boolean colliding = checkCollisions(zombie);

                if (colliding) {
                    zombie.stopMoving();  //	if touching a plant → no movement happens
                } else {
                    zombie.startMoving();  //	if not touching → movement happens
                    zombie.moveLeft();
                    checkGameOver(zombie);
                }
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    Bullet bullet = bullets.get(i);
                    bullet.moveRight();
                    checkBulletHits();

                    if (bullet.isOffScreen()) {
                        getChildren().remove(bullet.getView());
                        bullets.remove(i);
                    }
                }
            })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        zombie.setMovementTimeline(timeline);
        timeline.play();
    }
    
    public boolean checkCollisions(Zombie zombie) {
    for (Plant plant : plants) {
        if (zombie.getView().localToScene(zombie.getView().getBoundsInLocal())
                .intersects(plant.getView().localToScene(plant.getView().getBoundsInLocal()))) {

            plant.takeDamage(1);
            System.out.println("Zombie is attacking a plant!");

            if (plant.isDead()) {
                plant.stopShooting();
                if (plant.getView().getParent() instanceof StackPane parentCell) {//if that parent is a StackPane, store it in a variable called parentCell
                    parentCell.getChildren().remove(plant.getView());//remove the plant from the cell that actually contains it
                }

                plants.remove(plant);
                zombie.startMoving();
                System.out.println("Plant destroyed by zombie!");
            }
            return true;
        }
    }

    return false;
    }
    public void shootFromPlant(Plant plant) {
        double bulletX = (plant.getCol() * CELL_WIDTH) + 110;
        double bulletY = (plant.getRow() * CELL_HEIGHT) - 13;

        Bullet bullet = new Bullet(bulletX, bulletY);
        bullets.add(bullet);

        getChildren().add(bullet.getView());
    }
    // auto shooting system 
    public void startShooting(Plant plant) {
        if (plant.getShootingInterval() <= 0) {
            return;
        }
        Timeline shooter = new Timeline(
            new KeyFrame(Duration.seconds(plant.getShootingInterval()), e -> {
                if (plant.isDead() || !plants.contains(plant)) {
                    return;
                }

                if (hasZombieInLane(plant.getRow())) {
                    shootFromPlant(plant);
                }
            })
        );

        shooter.setCycleCount(Timeline.INDEFINITE);
        plant.setShootingTimeline(shooter);
        shooter.play();
    }
    public void checkBulletHits() {
        for (int i = bullets.size() - 1; i >= 0; i--) { //We loop backward through bullets so we can safely remove them.
            Bullet bullet = bullets.get(i);

            for (int j = zombies.size() - 1; j >= 0; j--) { //We also loop through zombies.
                Zombie zombie = zombies.get(j);

                if (bullet.getView().getBoundsInParent().intersects(zombie.getView().getBoundsInParent())) { //Checks whether bullet and zombie overlap visually.
                    zombie.takeDamage(bullet.getDamage());

                    getChildren().remove(bullet.getView());
                    bullets.remove(i);

                    System.out.println("Bullet hit zombie!");

                    if (zombie.isDead()) {
                        zombie.stopAllActions();
                        getChildren().remove(zombie.getView());
                        zombies.remove(j);
                        System.out.println("Zombie died!");
                    }
                    break;
                }
            }
        }
    }
    public void startZombieSpawner() {
        zombieSpawner = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                spawnZombie();
                System.out.println("New zombie spawned!");
            })
        );

        zombieSpawner.setCycleCount(Timeline.INDEFINITE);
        zombieSpawner.play();
    }
    public boolean hasZombieInLane(int row) {
        for (Zombie zombie : zombies) {
            if (zombie.getRow() == row) {
                return true;
            }
        }

        return false;
    }
    public void checkGameOver(Zombie zombie) {
        if (zombie.getView().getTranslateX() <= 0) {
            gameOver = true;
            zombie.stopAllActions();
            if (zombieSpawner != null) {
                zombieSpawner.stop();
            }
            if (sunGenerator != null) {
                sunGenerator.stop();
            }
            System.out.println("GAME OVER! A zombie reached the house.");
        }
    }

    public void startSunGenerator() {
        sunGenerator = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                sunPoints += 25;
                System.out.println("Sun points: " + sunPoints);
            })
        );

        sunGenerator.setCycleCount(Timeline.INDEFINITE);
        sunGenerator.play();
    }
    public int getSunPoints() {
        return sunPoints;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setSelectedPlantType(String type) {
        this.selectedPlantType = type;
        System.out.println("Selected plant: " + type);
    }
    public String getSelectedPlantType() {
        return selectedPlantType;
    }
    public boolean isOnCooldown(String plantType) {
        long currentTime = System.currentTimeMillis();

        if (!plantCooldowns.containsKey(plantType)) {
            return false;
        }

        return currentTime < plantCooldowns.get(plantType);
    }
    public void addSunPoints(int amount) {
        sunPoints += amount;
        System.out.println("Sun points: " + sunPoints);
    }
}
