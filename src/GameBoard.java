import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    private static final int CELL_WIDTH = 150;
    private static final int CELL_HEIGHT = 132;
    private List<Plant> plants = new ArrayList<>();    //plants stores all plant objects on the board
    private List<Zombie> zombies = new ArrayList<>(); //zombies stores all zombie objects on the board
    private List<Bullet> bullets = new ArrayList<>();  //bullets added to the storage
    private ArrayList<Sun> suns = new ArrayList<>();
    private ArrayList<WaterDrop> waterDrops = new ArrayList<>();
    private boolean gameOver = false; //Global state
    private int sunPoints = 250; //Player's starting points
    private int waterPoints = 100;
    private Timeline zombieSpawner;
    private Timeline sunGenerator;
    private String selectedPlantType = "PeaShooter";
    private Map<String, Long> plantCooldowns = new HashMap<>();
    private int currentWave = 1;
    private int totalWaves = 3;
    private int zombiesSpawnedInWave = 0;
    private int zombiesPerWave = 5;
    private boolean waveInProgress = true;
    private Timeline globalLoop;
    private boolean paused = false;
    private boolean gameWon = false;
    
    //Constructor
    public GameBoard() {
        this.setStyle("-fx-background-color: transparent;");
        createBoard();
        spawnZombie();
        startZombieSpawner();
        startGlobalLoop();
    }

    private void createBoard() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                final int currentRow = row;
                final int currentCol = col;
                Rectangle cellBackground = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
                cellBackground.setFill(Color.TRANSPARENT);
                //cellBackground.setStroke(Color.RED);
                cellBackground.setStrokeWidth(1);

                StackPane cell = new StackPane();
                cell.getChildren().add(cellBackground);
                final boolean[] hasPlant = {false};
                final Plant[] currentPlant = {null};

                cell.setOnMouseClicked(e -> {
                    if (paused || gameOver) {
                        return;
                    }

                    if (!hasPlant[0]) {
                        if (isOnCooldown(selectedPlantType)) {
                            System.out.println(selectedPlantType + " is on cooldown!");
                            return;
                        }

                        int requiredSun;
                        int requiredWater;

                        if (selectedPlantType.equals("PeaShooter")) {
                            requiredSun = 50;
                            requiredWater = 20;
                        } else if (selectedPlantType.equals("WallPlant")) {
                            requiredSun = 50;
                            requiredWater = 40;
                        } else if (selectedPlantType.equals("Sunflower")) {
                            requiredSun = 50;
                            requiredWater = 10;
                        } else {
                            requiredSun = 50;
                            requiredWater = 0;
                        }

                        if (sunPoints < requiredSun || waterPoints < requiredWater) {
                            System.out.println("Not enough resources!");
                            return;
                        }

                        Plant plant;

                        if (selectedPlantType.equals("PeaShooter")) {
                            plant = new PeaShooter(currentRow, currentCol);
                        } else if (selectedPlantType.equals("WallPlant")) {
                            plant = new WallPlant(currentRow, currentCol);
                        } else if (selectedPlantType.equals("Sunflower")) {
                            plant = new Sunflower(currentRow, currentCol, this);
                        } else {
                            plant = new WaterPlant(currentRow, currentCol, this);
                        }

                        sunPoints -= plant.getCost();
                        waterPoints -= plant.getWaterCost();
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
        // double zombieHeight = zombie.getView().getBoundsInLocal().getHeight();

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
                if (plant instanceof Sunflower) {
                    ((Sunflower) plant).stopProduction();
                }
                if (plant instanceof WaterPlant) {
                    ((WaterPlant) plant).stopProduction();
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
        double bulletY = (plant.getRow() * CELL_HEIGHT) - 25;

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
                if (paused || gameOver) {
                    return;
                }
                if (!waveInProgress) {
                    return;
                }

                if (zombiesSpawnedInWave < zombiesPerWave) {
                    spawnZombie();
                    zombiesSpawnedInWave++;
                    System.out.println("Zombie spawned in wave " + currentWave + ": " + zombiesSpawnedInWave + "/" + zombiesPerWave);
                } else {
                    waveInProgress = false;
                    zombieSpawner.stop();
                    System.out.println("Wave " + currentWave + " spawn completed.");
                }
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
    public void spawnSunFromPlant(Plant plant) {
        double x = (plant.getCol() * CELL_WIDTH) + (CELL_WIDTH / 2.0);
        double y = (plant.getRow() * CELL_HEIGHT) + (CELL_HEIGHT / 2.0);

        // target near the sun counter area
        double targetX = 20;
        double targetY = -150;

        Sun sun = new Sun(x, y, targetX, targetY);
        suns.add(sun);
        getChildren().add(sun.getView());
    }
    public int getWaterPoints() {
        return waterPoints;
    }
    public void addWaterPoints(int amount) {
        waterPoints += amount;
        System.out.println("Water points: " + waterPoints);
    }
    public void spawnWaterDropFromPlant(Plant plant) {
        double x = (plant.getCol() * CELL_WIDTH) + (CELL_WIDTH / 2.0);
        double y = (plant.getRow() * CELL_HEIGHT) + (CELL_HEIGHT / 2.0);

        WaterDrop drop = new WaterDrop(x, y);

        // click behavior
        drop.getView().setOnMouseClicked(e -> {
            if (!drop.isCollected()) {
                drop.collect();
                addWaterPoints(drop.getValue());
            }
        });

        waterDrops.add(drop);
        getChildren().add(drop.getView());
    }
    public long getRemainingCooldownMillis(String plantType) {
        if (!plantCooldowns.containsKey(plantType)) {
            return 0;
        }

        long remaining = plantCooldowns.get(plantType) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    public boolean isWaveCleared() {
        return !waveInProgress && zombies.isEmpty();
    }
    public void startNextWave() {
        if (currentWave >= totalWaves) {
            gameWon = true;

            if (zombieSpawner != null) {
                zombieSpawner.stop();
            }

            if (globalLoop != null) {
                globalLoop.stop();
            }

            System.out.println("All waves completed! YOU WON!");
            return;
        }

        currentWave++;
        zombiesSpawnedInWave = 0;
        zombiesPerWave += 3; // each wave gets bigger
        waveInProgress = true;

        System.out.println("Starting wave " + currentWave);
        startZombieSpawner();
    }
    public int getCurrentWave() {
        return currentWave;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public double getWaveProgress() {
        double completedWaves = currentWave - 1;
        double currentWavePart = (double) zombiesSpawnedInWave / zombiesPerWave;
        return (completedWaves + currentWavePart) / totalWaves;
    }
    public void startGlobalLoop() {
        globalLoop = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {

                if (paused || gameOver) {
                    return;
                }

                // Update bullets
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    Bullet bullet = bullets.get(i);
                    bullet.moveRight();
                    checkBulletHits();

                    if (bullet.isOffScreen()) {
                        getChildren().remove(bullet.getView());
                        bullets.remove(i);
                    }
                }

                // Update suns
                for (int i = suns.size() - 1; i >= 0; i--) {
                    Sun sun = suns.get(i);

                    sun.update();

                    if (sun.hasReachedTarget()) {
                        addSunPoints(sun.getValue());
                        getChildren().remove(sun.getView());
                        suns.remove(i);
                    }
                }

                // Update water drops
                for (int i = waterDrops.size() - 1; i >= 0; i--) {
                    WaterDrop drop = waterDrops.get(i);

                    drop.update();

                    if (drop.isFinished()) {
                        getChildren().remove(drop.getView());
                        waterDrops.remove(i);
                    }
                }

                // Check if wave ended and next wave should start
                if (isWaveCleared()) {
                    startNextWave();
                }
            })
        );

        globalLoop.setCycleCount(Timeline.INDEFINITE);
        globalLoop.play();
    }
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
    public void pauseGame() {
        paused = true;

        if (globalLoop != null) {
            globalLoop.pause();
        }

        if (zombieSpawner != null) {
            zombieSpawner.pause();
        }

        for (Plant plant : plants) {
            plant.stopShooting();

            if (plant instanceof Sunflower) {
                ((Sunflower) plant).stopProduction();
            }

            if (plant instanceof WaterPlant) {
                ((WaterPlant) plant).stopProduction();
            }
        }
        for (Zombie zombie : zombies) {
            zombie.pauseActions();
        }
    }

    public void resumeGame() {
        paused = false;

        if (globalLoop != null) {
            globalLoop.play();
        }

        if (zombieSpawner != null && waveInProgress) {
            zombieSpawner.play();
        }

        for (Plant plant : plants) {
            startShooting(plant);

            if (plant instanceof Sunflower) {
                ((Sunflower) plant).resumeProduction(this);
            }

            if (plant instanceof WaterPlant) {
                ((WaterPlant) plant).resumeProduction(this);
            }
        }
        for (Zombie zombie : zombies) {
            zombie.resumeActions();
        }
    }

    public boolean isGameWon(){
        return gameWon;
    }
    public void setGameWon(boolean gameWon){
        this.gameWon = gameWon;
    }

}
