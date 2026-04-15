import javafx.scene.layout.Pane;
//timelien imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
// list of plants to GameBoard.
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.PauseTransition;
import java.util.function.Consumer;
public class GameBoard extends Pane {

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
    private PlantType selectedPlantType = PlantType.PEA_SHOOTER;
    private Map<PlantType, Long> plantCooldowns = new HashMap<>();
    private Timeline globalLoop;
    private boolean paused = false;
    private boolean gameWon = false;
    private int currentWave = 1;
    private int totalWaves;
    private int zombiesSpawnedInWave = 0;
    private int zombiesPerWave = 8;
    private boolean waveInProgress = true;
    private double spawnIntervalSeconds = 3.5;
    private List<Wave> waves = new ArrayList<>();
    private BoardCell[][] cells = new BoardCell[ROWS][COLUMNS];
    private Consumer<Plant> plantRemovedHandler;
    
    //Constructor
    public GameBoard() {
        setPrefSize(COLUMNS * CELL_WIDTH, ROWS * CELL_HEIGHT);
        setPickOnBounds(false);
        initializeCells();
        initializeWaves();
        totalWaves = waves.size();
        configureWave(waves.get(0));
        startZombieSpawner();
        startGlobalLoop();
    }
    private void initializeCells() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                cells[row][col] = new BoardCell(row, col);
            }
        }
    }
    private void initializeWaves() {

        // Wave 1 (easy)
        waves.add(new Wave(
            8,      // zombies
            3.5,    // spawn interval
            createZombieProbabilities(0.7, 0.25, 0.05, 0.0)
        ));

        // Wave 2 (medium)
        waves.add(new Wave(
            12,
            2.5,
            createZombieProbabilities(0.45, 0.30, 0.20, 0.05)
        ));

        // Wave 3 (hard)
        waves.add(new Wave(
            16,
            1.8,
            createZombieProbabilities(0.25, 0.25, 0.25, 0.25)
        ));
    }
    private Map<ZombieType, Double> createZombieProbabilities(
            double normalChance,
            double fastChance,
            double fatChance,
            double tankChance
    ) {
        Map<ZombieType, Double> probabilities = new EnumMap<>(ZombieType.class);
        probabilities.put(ZombieType.NORMAL, normalChance);
        probabilities.put(ZombieType.FAST, fastChance);
        probabilities.put(ZombieType.FAT, fatChance);
        probabilities.put(ZombieType.TANK, tankChance);
        return probabilities;
    }
    private void configureWave(Wave wave) {
        zombiesPerWave = wave.getTotalZombies();
        spawnIntervalSeconds = wave.getSpawnInterval();
    }
    private void spawnZombie(Wave wave) {
        int row = ThreadLocalRandom.current().nextInt(ROWS);
        ZombieType zombieType = wave.pickZombieType();
        Zombie zombie = ZombieFactory.createZombie(zombieType, row);

        zombies.add(zombie);

        double zombieWidth = zombie.getView().getBoundsInLocal().getWidth();
        // double zombieHeight = zombie.getView().getBoundsInLocal().getHeight();

        double x = (COLUMNS * CELL_WIDTH) - zombieWidth;
        double y = (row * CELL_HEIGHT) + 10;

        zombie.getView().setTranslateX(x);
        zombie.getView().setTranslateY(y);
        zombie.getView().setMouseTransparent(true);

        getChildren().add(zombie.getView());
        startGameLoop(zombie);
    }
    private void spawnNextZombieInWave() {
        if (zombiesSpawnedInWave >= zombiesPerWave) {
            finishWaveSpawning();
            return;
        }

        Wave wave = waves.get(currentWave - 1);
        spawnZombie(wave);
        zombiesSpawnedInWave++;
        System.out.println("Zombie spawned in wave " + currentWave + ": " + zombiesSpawnedInWave + "/" + zombiesPerWave);

        if (zombiesSpawnedInWave >= zombiesPerWave) {
            finishWaveSpawning();
        }
    }
    private void finishWaveSpawning() {
        waveInProgress = false;

        if (zombieSpawner != null) {
            zombieSpawner.stop();
        }

        System.out.println("Wave " + currentWave + " spawn completed.");
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
        Iterator<Plant> iterator = plants.iterator();

        while (iterator.hasNext()) {
            Plant plant = iterator.next();

            if (zombie.getView().localToScene(zombie.getView().getBoundsInLocal())
                    .intersects(plant.getView().localToScene(plant.getView().getBoundsInLocal()))) {

                plant.takeDamage(zombie.getAttackDamage());
                System.out.println("Zombie is attacking a plant!");

                if (plant.isDead()) {
                    removePlant(plant, iterator);
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
        bullet.getView().setMouseTransparent(true);
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
        if (zombieSpawner != null) {
            zombieSpawner.stop();
        }

        if (!waveInProgress || zombiesSpawnedInWave >= zombiesPerWave) {
            return;
        }

        spawnNextZombieInWave();

        if (!waveInProgress) {
            return;
        }

        zombieSpawner = new Timeline(
            new KeyFrame(Duration.seconds(spawnIntervalSeconds), e -> {
                if (paused || gameOver) {
                    return;
                }
                if (!waveInProgress) {
                    return;
                }

                spawnNextZombieInWave();
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
    public void setSelectedPlantType(PlantType type) {
        this.selectedPlantType = type;
        System.out.println("Selected plant: " + type.getIdentifier());
    }
    public void setSelectedPlantType(String type) {
        setSelectedPlantType(PlantType.fromIdentifier(type));
    }
    public PlantType getSelectedPlantType() {
        return selectedPlantType;
    }
    public boolean isOnCooldown(PlantType plantType) {
        long currentTime = System.currentTimeMillis();

        if (!plantCooldowns.containsKey(plantType)) {
            return false;
        }

        return currentTime < plantCooldowns.get(plantType);
    }
    public boolean isOnCooldown(String plantType) {
        return isOnCooldown(PlantType.fromIdentifier(plantType));
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
        sun.getView().setMouseTransparent(true);
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
    public long getRemainingCooldownMillis(PlantType plantType) {
        if (!plantCooldowns.containsKey(plantType)) {
            return 0;
        }

        long remaining = plantCooldowns.get(plantType) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    public long getRemainingCooldownMillis(String plantType) {
        return getRemainingCooldownMillis(PlantType.fromIdentifier(plantType));
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
        waveInProgress = true;

        Wave wave = waves.get(currentWave - 1);
        configureWave(wave);

        System.out.println("Wave " + currentWave + " will start soon...");

        PauseTransition breakBetweenWaves = new PauseTransition(Duration.seconds(5));
        breakBetweenWaves.setOnFinished(e -> {
            System.out.println("Starting wave " + currentWave);
            startZombieSpawner();
        });
        breakBetweenWaves.play();
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
    public void setPlantRemovedHandler(Consumer<Plant> plantRemovedHandler) {
        this.plantRemovedHandler = plantRemovedHandler;
    }
    public Plant placePlantAt(int row, int col) {
        if (paused || gameOver) {
            return null;
        }

        BoardCell cell = cells[row][col];

        if (cell.isOccupied()) {
            return null;
        }

        if (isOnCooldown(selectedPlantType)) {
            System.out.println(selectedPlantType.getIdentifier() + " is on cooldown!");
            return null;
        }

        if (sunPoints < selectedPlantType.getSunCost() || waterPoints < selectedPlantType.getWaterCost()) {
            System.out.println("Not enough resources!");
            return null;
        }

        Plant plant = PlantFactory.createPlant(selectedPlantType, row, col, this);

        sunPoints -= selectedPlantType.getSunCost();
        waterPoints -= selectedPlantType.getWaterCost();

        plantCooldowns.put(
                selectedPlantType,
                System.currentTimeMillis() + (long) (selectedPlantType.getCooldown() * 1000)
        );

        cell.setOccupant(plant);
        plants.add(plant);

        startShooting(plant);

        System.out.println("Placed " + selectedPlantType.getIdentifier() + " at row " + row + ", col " + col);

        return plant;
    }

    private void removePlant(Plant plant, Iterator<Plant> iterator) {
        plant.stopShooting();

        if (plant instanceof Sunflower) {
            ((Sunflower) plant).stopProduction();
        }

        if (plant instanceof WaterPlant) {
            ((WaterPlant) plant).stopProduction();
        }

        cells[plant.getRow()][plant.getCol()].clearOccupant();
        iterator.remove();

        if (plantRemovedHandler != null) {
            plantRemovedHandler.accept(plant);
        }
    }
}
