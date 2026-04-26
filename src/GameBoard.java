import javafx.scene.layout.Pane;
//timelien imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
// list of plants to GameBoard.
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
public class GameBoard extends Pane {

    private static final int ROWS = BoardMetrics.ROWS;
    private static final int COLUMNS = BoardMetrics.COLUMNS;
    private static final int CELL_WIDTH = BoardMetrics.CELL_WIDTH;
    private static final int CELL_HEIGHT = BoardMetrics.CELL_HEIGHT;
    private static final double INITIAL_PREPARATION_SECONDS = 12.0;
    private static final double BETWEEN_WAVES_BREAK_SECONDS = 5.0;
    private static final double CAGE_TRAP_DURATION_SECONDS = 10.0;
    private static final long CAGE_COOLDOWN_MILLIS = 8_000L;
    private static final int CAGE_SUN_COST = 50;
    private static final int CAGE_WATER_COST = 50;
    private final Level level;
    private final Difficulty difficulty;
    private final DifficultySettings difficultySettings;
    private List<Plant> plants = new ArrayList<>();    //plants stores all plant objects on the board
    private List<Zombie> zombies = new ArrayList<>(); //zombies stores all zombie objects on the board
    private List<Bullet> bullets = new ArrayList<>();  //bullets added to the storage
    private ArrayList<Sun> suns = new ArrayList<>();
    private ArrayList<WaterDrop> waterDrops = new ArrayList<>();
    private GameState gameState = GameState.RUNNING;
    private int sunPoints;
    private int waterPoints;
    private Timeline zombieSpawner;
    private Timeline waveStartDelayTimeline;
    private Timeline sunGenerator;
    private PlantType selectedPlantType = PlantType.PEA_SHOOTER;
    private Map<PlantType, Long> plantCooldowns = new HashMap<>();
    private Timeline globalLoop;
    private int currentWave = 1;
    private int totalWaves;
    private int zombiesSpawnedInWave = 0;
    private int zombiesPerWave = 8;
    private boolean waveInProgress = true;
    private double spawnIntervalSeconds = 3.5;
    private List<Wave> waves = new ArrayList<>();
    private BoardCell[][] cells = new BoardCell[ROWS][COLUMNS];
    private final List<Grave> graves = new ArrayList<>();
    private Consumer<Plant> plantRemovedHandler;
    private boolean cageModeActive;
    private long cageCooldownEndMillis;
    
    //Constructor
    public GameBoard() {
        this(Levels.create(LevelType.DAY), Difficulty.MEDIUM);
    }

    public GameBoard(Level level) {
        this(level, Difficulty.MEDIUM);
    }

    public GameBoard(Level level, Difficulty difficulty) {
        this.level = level;
        this.difficulty = difficulty != null ? difficulty : Difficulty.MEDIUM;
        this.difficultySettings = this.difficulty.getSettings();
        sunPoints = difficultySettings.getStartingSun();
        waterPoints = difficultySettings.getStartingWater();
        setPrefSize(COLUMNS * CELL_WIDTH, ROWS * CELL_HEIGHT);
        setPickOnBounds(false);
        initializeCells();
        initializeMatchGraves();
        applyLevelLayout();
        initializeWaves();
        totalWaves = waves.size();
        configureWave(waves.get(0));
        scheduleCurrentWaveStart(INITIAL_PREPARATION_SECONDS, "Prepare your defenses before Wave 1.");
        startGlobalLoop();
    }
    private void initializeCells() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                cells[row][col] = new BoardCell(row, col);
            }
        }
    }

    private void applyLevelLayout() {
        for (Grave grave : graves) {
            cells[grave.getRow()][grave.getCol()].setGrave(grave);
        }
    }

    private void initializeMatchGraves() {
        graves.clear();
        graves.addAll(level.generateGravesForMatch());
    }
    private void initializeWaves() {

        // Wave 1 (easy)
        waves.add(new Wave(
            6,
            4.5,
            createZombieProbabilities(0.85, 0.15, 0.0, 0.0, 0.0)
        ));

        // Wave 2 (medium)
        waves.add(new Wave(
            10,
            3.0,
            createZombieProbabilities(0.45, 0.25, 0.20, 0.0, 0.10)
        ));

        // Wave 3 (hard)
        waves.add(new Wave(
            14,
            2.2,
            createZombieProbabilities(0.30, 0.20, 0.20, 0.15, 0.15)
        ));
    }
    private Map<ZombieType, Double> createZombieProbabilities(
            double normalChance,
            double fastChance,
            double fatChance,
            double tankChance,
            double parasiteChance
    ) {
        Map<ZombieType, Double> probabilities = new EnumMap<>(ZombieType.class);
        probabilities.put(ZombieType.NORMAL, normalChance);
        probabilities.put(ZombieType.FAST, fastChance);
        probabilities.put(ZombieType.FAT, fatChance);
        probabilities.put(ZombieType.TANK, tankChance);
        probabilities.put(ZombieType.PARASITE, parasiteChance);
        return probabilities;
    }
    private void configureWave(Wave wave) {
        zombiesPerWave = wave.getTotalZombies();
        spawnIntervalSeconds = wave.getSpawnInterval() * difficultySettings.getSpawnIntervalMultiplier();
    }
    // Overloading polymorphism: same method name, different parameter lists.
    public Zombie spawnZombie() {
        Wave wave = waves.get(currentWave - 1);
        ZombieSpawnLocation spawnLocation = chooseZombieSpawnLocation();
        return spawnZombie(wave.pickZombieType(), spawnLocation);
    }

    public Zombie spawnZombie(int row) {
        Wave wave = waves.get(currentWave - 1);
        return spawnZombie(wave.pickZombieType(), ZombieSpawnLocation.forEdge(normalizeRow(row)));
    }

    public Zombie spawnZombie(ZombieType type, int row) {
        return spawnZombie(type, ZombieSpawnLocation.forEdge(normalizeRow(row)));
    }

    private Zombie spawnZombie(Wave wave) {
        ZombieSpawnLocation spawnLocation = chooseZombieSpawnLocation();
        return spawnZombie(wave.pickZombieType(), spawnLocation);
    }

    private Zombie spawnZombie(ZombieType zombieType, ZombieSpawnLocation spawnLocation) {
        if (isMatchEnded()) {
            return null;
        }

        Zombie zombie = ZombieFactory.createZombie(zombieType, spawnLocation.getRow());
        applyDifficultySettings(zombie);

        positionZombie(zombie, spawnLocation);
        configureZombieInteractions(zombie);
        zombie.onSpawn(this);
        zombies.add(zombie);
        getChildren().add(zombie.getView());
        startGameLoop(zombie);
        return zombie;
    }
    private void applyDifficultySettings(Zombie zombie) {
        zombie.applyStatMultipliers(
                difficultySettings.getZombieHealthMultiplier(),
                difficultySettings.getZombieSpeedMultiplier()
        );
    }
    private void spawnNextZombieInWave() {
        if (isMatchEnded()) {
            return;
        }

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
                if (isMatchEnded()) {
                    zombie.stopAllActions();
                    return;
                }

                zombie.updateStatusEffects();

                if (zombie.isTrapped()) {
                    return;
                }

                boolean colliding = checkCollisions(zombie);

                if (colliding) {
                    zombie.stopMoving();  //	if touching a plant → no movement happens
                } else {
                    zombie.startMoving();  //	if not touching → movement happens
                    zombie.act(this);
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

            if (!plant.blocksZombies()) {
                continue;
            }

            if (zombie.getBodyView().localToScene(zombie.getBodyView().getBoundsInLocal())
                    .intersects(plant.getView().localToScene(plant.getView().getBoundsInLocal()))) {
                return handlePlantCollision(zombie, plant, iterator);
            }
        }

        return false;
    }
    private boolean handlePlantCollision(Zombie zombie, Plant plant, Iterator<Plant> iterator) {
        if (zombie instanceof ParasiteZombie) {
            if (!plant.isInfected()) {
                plant.infect();
                System.out.println("Plant infected by parasite zombie!");
            }
            return false;
        }

        zombie.attack(plant);
        System.out.println("Zombie is attacking a plant!");

        if (plant.isDead()) {
            removePlant(plant, iterator);
            zombie.startMoving();
            System.out.println("Plant destroyed by zombie!");
        }
        return true;
    }
    public void fireBulletFromPlant(Plant plant) {
        if (isMatchEnded() || plant == null || plant.isInfected()) {
            return;
        }

        double bulletX = plant.getShootOriginX() - (Bullet.WIDTH / 2.0);
        double bulletY = plant.getShootOriginY() - (Bullet.HEIGHT / 2.0);

        fireBullet(bulletX, bulletY);
    }

    public void fireBullet(double bulletX, double bulletY) {
        if (isMatchEnded()) {
            return;
        }

        Bullet bullet = new Bullet(bulletX, bulletY);
        bullet.getView().setMouseTransparent(true);
        bullets.add(bullet);

        getChildren().add(bullet.getView());
    }
    public void shootFromPlant(Plant plant) {
        if (isMatchEnded() || plant == null || plant.isInfected()) {
            return;
        }

        plant.act(this);
    }
    // auto shooting system 
    public void startShooting(Plant plant) {
        if (plant == null || plant.isInfected() || plant.getShootingInterval() <= 0 || isMatchEnded()) {
            return;
        }
        Timeline shooter = new Timeline(
            new KeyFrame(Duration.seconds(plant.getShootingInterval()), e -> {
                if (isMatchEnded() || plant.isDead() || plant.isInfected() || !plants.contains(plant)) {
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

                if (bullet.getView().localToScene(bullet.getView().getBoundsInLocal())
                        .intersects(zombie.getBodyView().localToScene(zombie.getBodyView().getBoundsInLocal()))) { //Checks whether bullet and zombie overlap visually.
                    zombie.takeDamage(bullet.getDamage());

                    getChildren().remove(bullet.getView());
                    bullets.remove(i);

                    System.out.println("Bullet hit zombie!");

                    if (zombie.isDead()) {
                        removeZombieAt(j);
                    }
                    break;
                }
            }
        }
    }
    public void damageZombiesInArea(int centerRow, int centerCol, int rowRadius, int colRadius, int damage) {
        damageZombiesInArea(centerRow, centerCol, BlastArea.rectangle(rowRadius, colRadius), damage);
    }

    public void damageZombiesInArea(int centerRow, int centerCol, BlastArea blastArea, int damage) {
        for (int i = zombies.size() - 1; i >= 0; i--) {
            Zombie zombie = zombies.get(i);
            int zombieCol = getZombieColumn(zombie);

            if (!blastArea.contains(centerRow, centerCol, zombie.getRow(), zombieCol)) {
                continue;
            }

            zombie.takeDamage(damage);

            if (zombie.isDead()) {
                removeZombieAt(i);
            }
        }
    }

    private int getZombieColumn(Zombie zombie) {
        double zombieWidth = zombie.getVisualWidth();
        double zombieCenterX = zombie.getView().getTranslateX() + (zombieWidth / 2.0);
        return (int) Math.floor(zombieCenterX / CELL_WIDTH);
    }

    private void removeZombieAt(int zombieIndex) {
        Zombie zombie = zombies.get(zombieIndex);
        zombie.stopAllActions();
        getChildren().remove(zombie.getView());
        zombies.remove(zombieIndex);
        System.out.println("Zombie died!");
    }
    private void scheduleCurrentWaveStart(double delaySeconds, String announcement) {
        stopWaveStartDelay();
        waveInProgress = true;

        if (announcement != null && !announcement.isEmpty()) {
            System.out.println(announcement);
        }

        waveStartDelayTimeline = new Timeline(
            new KeyFrame(Duration.seconds(delaySeconds), e -> {
                waveStartDelayTimeline = null;
                beginCurrentWave();
            })
        );
        waveStartDelayTimeline.setCycleCount(1);
        waveStartDelayTimeline.play();
    }

    private void beginCurrentWave() {
        if (!isRunning()) {
            return;
        }

        System.out.println("Starting wave " + currentWave);
        startZombieSpawner();
    }

    private void stopWaveStartDelay() {
        if (waveStartDelayTimeline != null) {
            waveStartDelayTimeline.stop();
            waveStartDelayTimeline = null;
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
                if (!isRunning()) {
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
            endGameAsLost();
            System.out.println("GAME OVER! A zombie reached the house.");
        }
    }

    public void startSunGenerator() {
        if (isMatchEnded()) {
            return;
        }

        sunGenerator = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                if (isMatchEnded()) {
                    return;
                }

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

    public Level getLevel() {
        return level;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<Grave> getGraves() {
        return Collections.unmodifiableList(graves);
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean hasGraveAt(int row, int col) {
        return cells[row][col].hasGrave();
    }

    public Grave getGraveAt(int row, int col) {
        return cells[row][col].getGrave();
    }

    public boolean isGameOver() {
        return gameState == GameState.LOST;
    }
    public void setSelectedPlantType(PlantType type) {
        deactivateCageMode();
        this.selectedPlantType = type;
        System.out.println("Selected plant: " + type.getIdentifier());
    }
    public void setSelectedPlantType(String type) {
        PlantType.tryFromIdentifier(type).ifPresentOrElse(
                this::setSelectedPlantType,
                () -> System.err.println("Unknown plant type selection '" + type + "'. Keeping current selection.")
        );
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
        return PlantType.tryFromIdentifier(plantType)
                .map(this::isOnCooldown)
                .orElseGet(() -> {
                    System.err.println("Unknown plant type cooldown lookup '" + plantType + "'. Returning false.");
                    return false;
                });
    }
    public void addSunPoints(int amount) {
        if (isMatchEnded()) {
            return;
        }

        sunPoints += amount;
        System.out.println("Sun points: " + sunPoints);
    }
    public void spawnSunFromPlant(Plant plant) {
        spawnSunFromPlant(plant, 25);
    }
    public void spawnSunFromPlant(Plant plant, int amount) {
        if (isMatchEnded() || plant == null || plant.isInfected()) {
            return;
        }

        double x = plant.getCenterX();
        double y = plant.getCenterY();

        // target near the sun counter area
        double targetX = 20;
        double targetY = -150;

        Sun sun = new Sun(x, y, targetX, targetY, amount);
        sun.getView().setMouseTransparent(true);
        suns.add(sun);
        getChildren().add(sun.getView());
    }
    public int getWaterPoints() {
        return waterPoints;
    }
    public void addWaterPoints(int amount) {
        if (isMatchEnded()) {
            return;
        }

        waterPoints += amount;
        System.out.println("Water points: " + waterPoints);
    }
    public void spawnWaterDropFromPlant(Plant plant) {
        if (isMatchEnded() || plant == null || plant.isInfected()) {
            return;
        }

        double x = plant.getCenterX();
        double y = plant.getCenterY();

        WaterDrop drop = new WaterDrop(x, y);

        // click behavior
        drop.getView().setOnMouseClicked(e -> {
            if (isMatchEnded()) {
                return;
            }

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
        return PlantType.tryFromIdentifier(plantType)
                .map(this::getRemainingCooldownMillis)
                .orElseGet(() -> {
                    System.err.println("Unknown plant type cooldown lookup '" + plantType + "'. Returning 0.");
                    return 0L;
                });
    }
    public boolean activateCageMode() {
        if (!isRunning() || getRemainingCageCooldownMillis() > 0) {
            return false;
        }

        if (!hasEnoughResources(CAGE_SUN_COST, CAGE_WATER_COST)) {
            return false;
        }

        cageModeActive = true;
        return true;
    }

    public void deactivateCageMode() {
        cageModeActive = false;
    }

    public boolean isCageModeActive() {
        return cageModeActive;
    }

    public long getRemainingCageCooldownMillis() {
        return Math.max(0, cageCooldownEndMillis - System.currentTimeMillis());
    }

    public boolean tryUseCageOnZombie(Zombie zombie) {
        if (!cageModeActive || zombie == null || zombie.isDead() || !zombies.contains(zombie)) {
            return false;
        }

        if (!hasEnoughResources(CAGE_SUN_COST, CAGE_WATER_COST)) {
            cageModeActive = false;
            return false;
        }

        sunPoints -= CAGE_SUN_COST;
        waterPoints -= CAGE_WATER_COST;
        zombie.trapForSeconds(CAGE_TRAP_DURATION_SECONDS);
        cageModeActive = false;
        cageCooldownEndMillis = System.currentTimeMillis() + CAGE_COOLDOWN_MILLIS;
        return true;
    }
    public boolean isWaveCleared() {
        return !waveInProgress && zombies.isEmpty();
    }
    public void startNextWave() {
        if (currentWave >= totalWaves) {
            endGameAsWon();
            System.out.println("All waves completed! YOU WON!");
            return;
        }

        currentWave++;
        zombiesSpawnedInWave = 0;

        Wave wave = waves.get(currentWave - 1);
        configureWave(wave);

        scheduleCurrentWaveStart(BETWEEN_WAVES_BREAK_SECONDS, "Wave " + currentWave + " will start soon...");
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

                if (!isRunning()) {
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
        return gameState == GameState.PAUSED;
    }

    public void pauseGame() {
        if (!isRunning()) {
            return;
        }

        gameState = GameState.PAUSED;

        if (globalLoop != null) {
            globalLoop.pause();
        }

        if (zombieSpawner != null) {
            zombieSpawner.pause();
        }

        if (waveStartDelayTimeline != null) {
            waveStartDelayTimeline.pause();
        }

        for (Plant plant : plants) {
            plant.stopShooting();
            plant.onGamePaused();
        }
        for (Zombie zombie : zombies) {
            zombie.pauseActions();
        }
    }

    public void resumeGame() {
        if (gameState != GameState.PAUSED) {
            return;
        }

        gameState = GameState.RUNNING;

        if (globalLoop != null) {
            globalLoop.play();
        }

        if (zombieSpawner != null && waveInProgress) {
            zombieSpawner.play();
        }

        if (waveStartDelayTimeline != null) {
            waveStartDelayTimeline.play();
        }

        for (Plant plant : plants) {
            startShooting(plant);
            plant.onGameResumed(this);
        }
        for (Zombie zombie : zombies) {
            zombie.resumeActions();
        }
    }

    public boolean isGameWon(){
        return gameState == GameState.WON;
    }

    public void setPlantRemovedHandler(Consumer<Plant> plantRemovedHandler) {
        this.plantRemovedHandler = plantRemovedHandler;
    }
    // Overloading polymorphism: the board can place plants from enum or String input.
    public Plant placePlant(PlantType type, int row, int col) {
        return placePlant(type, row, col, false);
    }

    public Plant placePlant(String type, int row, int col) {
        return PlantType.tryFromIdentifier(type)
                .map(plantType -> placePlant(plantType, row, col, false))
                .orElseGet(() -> {
                    System.err.println("Unknown plant type '" + type + "'. Plant placement cancelled.");
                    return null;
                });
    }

    public Plant placePlant(PlantType type, int row, int col, boolean restorePreviousSelection) {
        PlantType previousSelection = selectedPlantType;
        setSelectedPlantType(type);
        Plant placedPlant = placePlantAt(row, col);

        if (restorePreviousSelection) {
            setSelectedPlantType(previousSelection);
        }

        return placedPlant;
    }

    public Plant placePlantAt(int row, int col) {
        if (!isRunning()) {
            return null;
        }

        if (cageModeActive) {
            return null;
        }

        BoardCell cell = cells[row][col];

        if (cell.hasGrave()) {
            System.out.println("Cannot place a plant on a grave.");
            return null;
        }

        if (cell.isOccupied()) {
            if (cell.getOccupant().isInfected()) {
                System.out.println("Cannot place or fuse on an infected plant.");
                return null;
            }
            return tryFusePlant(cell);
        }

        if (isOnCooldown(selectedPlantType)) {
            System.out.println(selectedPlantType.getIdentifier() + " is on cooldown!");
            return null;
        }

        if (!hasEnoughResources(selectedPlantType)) {
            return null;
        }

        Plant plant = PlantFactory.createPlant(selectedPlantType, row, col, this);

        consumeResourcesFor(selectedPlantType);
        applyCooldownFor(selectedPlantType);

        cell.setOccupant(plant);
        plants.add(plant);

        startShooting(plant);
        plant.onPlaced(this);

        System.out.println("Placed " + selectedPlantType.getIdentifier() + " at row " + row + ", col " + col);

        return plant;
    }

    private Plant tryFusePlant(BoardCell cell) {
        if (selectedPlantType == PlantType.SUNFLOWER) {
            return tryFuseSunflower(cell);
        }

        if (selectedPlantType == PlantType.PEA_SHOOTER) {
            return tryFusePeaShooter(cell);
        }

        return null;
    }

    private Plant tryFuseSunflower(BoardCell cell) {
        Plant occupant = cell.getOccupant();

        if (!(occupant instanceof Sunflower)) {
            return null;
        }

        Sunflower sunflower = (Sunflower) occupant;

        if (!sunflower.canFuse()) {
            System.out.println("Sunflower is already at maximum fusion level.");
            return null;
        }

        if (isOnCooldown(selectedPlantType)) {
            System.out.println(selectedPlantType.getIdentifier() + " is on cooldown!");
            return null;
        }

        if (!hasEnoughResources(selectedPlantType)) {
            return null;
        }

        consumeResourcesFor(selectedPlantType);
        applyCooldownFor(selectedPlantType);
        sunflower.fuse();

        System.out.println("Fused Sunflower at row " + cell.getRow() + ", col " + cell.getCol());
        return sunflower;
    }

    private Plant tryFusePeaShooter(BoardCell cell) {
        Plant occupant = cell.getOccupant();

        if (!(occupant instanceof PeaShooter)) {
            return null;
        }

        PeaShooter peaShooter = (PeaShooter) occupant;

        if (!peaShooter.canFuse()) {
            System.out.println("PeaShooter is already at maximum fusion level.");
            return null;
        }

        if (isOnCooldown(selectedPlantType)) {
            System.out.println(selectedPlantType.getIdentifier() + " is on cooldown!");
            return null;
        }

        if (!hasEnoughResources(selectedPlantType)) {
            return null;
        }

        consumeResourcesFor(selectedPlantType);
        applyCooldownFor(selectedPlantType);
        peaShooter.fuse();

        System.out.println("Fused PeaShooter at row " + cell.getRow() + ", col " + cell.getCol());
        return peaShooter;
    }

    private boolean hasEnoughResources(PlantType plantType) {
        if (sunPoints < plantType.getSunCost() || waterPoints < plantType.getWaterCost()) {
            System.out.println("Not enough resources!");
            return false;
        }

        return true;
    }

    private boolean hasEnoughResources(int sunCost, int waterCost) {
        if (sunPoints < sunCost || waterPoints < waterCost) {
            System.out.println("Not enough resources!");
            return false;
        }

        return true;
    }

    private void consumeResourcesFor(PlantType plantType) {
        sunPoints -= plantType.getSunCost();
        waterPoints -= plantType.getWaterCost();
    }

    private void applyCooldownFor(PlantType plantType) {
        plantCooldowns.put(
                plantType,
                System.currentTimeMillis() + (long) (plantType.getCooldown() * 1000)
        );
    }

    public void removePlant(Plant plant) {
        if (plant == null) {
            return;
        }

        if (!plants.remove(plant)) {
            return;
        }

        cleanupRemovedPlant(plant);
    }

    private void removePlant(Plant plant, Iterator<Plant> iterator) {
        iterator.remove();
        cleanupRemovedPlant(plant);
    }

    private void cleanupRemovedPlant(Plant plant) {
        plant.stopShooting();
        cells[plant.getRow()][plant.getCol()].clearOccupant();
        plant.onRemoved();

        if (plantRemovedHandler != null) {
            plantRemovedHandler.accept(plant);
        }
    }

    private void endGameAsLost() {
        if (isMatchEnded()) {
            return;
        }

        gameState = GameState.LOST;
        stopAllGameplay();
    }

    private void endGameAsWon() {
        if (isMatchEnded()) {
            return;
        }

        gameState = GameState.WON;
        stopAllGameplay();
    }

    private void stopAllGameplay() {
        waveInProgress = false;
        cageModeActive = false;

        stopTimeline(globalLoop);
        stopTimeline(zombieSpawner);
        stopWaveStartDelay();
        stopTimeline(sunGenerator);

        for (Plant plant : plants) {
            plant.onGameEnded();
        }

        for (Zombie zombie : zombies) {
            zombie.stopAllActions();
        }

        for (WaterDrop drop : waterDrops) {
            drop.getView().setOnMouseClicked(null);
            drop.getView().setMouseTransparent(true);
        }
    }

    private boolean isMatchEnded() {
        return gameState == GameState.WON || gameState == GameState.LOST;
    }

    private boolean isRunning() {
        return gameState == GameState.RUNNING;
    }

    private void stopTimeline(Timeline timeline) {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private int normalizeRow(int row) {
        return Math.max(0, Math.min(ROWS - 1, row));
    }

    public List<Zombie> getZombiesSnapshot() {
        return PolymorphismUtils.snapshotEntities(zombies);
    }

    public List<Plant> getPlantsSnapshot() {
        return PolymorphismUtils.snapshotEntities(plants);
    }

    // Parametric polymorphism with an interface type: the same list abstraction stores Movable objects.
    public List<Movable> getMovableEntities() {
        List<Movable> movableEntities = new ArrayList<>();
        movableEntities.addAll(zombies);
        return movableEntities;
    }

    public void printEntityCollections() {
        PolymorphismUtils.printEntities(getZombiesSnapshot());
        PolymorphismUtils.printEntities(getPlantsSnapshot());
        PolymorphismUtils.printEntities(getMovableEntities());
    }

    private ZombieSpawnLocation chooseZombieSpawnLocation() {
        if (shouldSpawnFromGrave()) {
            return createGraveSpawnLocation();
        }

        return createEdgeSpawnLocation();
    }

    private boolean shouldSpawnFromGrave() {
        double spawnChance = level.getGraveZombieSpawnChance(currentWave);

        if (spawnChance <= 0.0) {
            return false;
        }

        for (Grave grave : graves) {
            if (grave.isSpawnSource()) {
                return ThreadLocalRandom.current().nextDouble() < spawnChance;
            }
        }

        return false;
    }

    private ZombieSpawnLocation createEdgeSpawnLocation() {
        int row = ThreadLocalRandom.current().nextInt(ROWS);
        return ZombieSpawnLocation.forEdge(row);
    }

    private ZombieSpawnLocation createGraveSpawnLocation() {
        List<Grave> spawnableGraves = new ArrayList<>();

        for (Grave grave : graves) {
            if (grave.isSpawnSource()) {
                spawnableGraves.add(grave);
            }
        }

        if (spawnableGraves.isEmpty()) {
            return createEdgeSpawnLocation();
        }

        Grave grave = spawnableGraves.get(ThreadLocalRandom.current().nextInt(spawnableGraves.size()));
        return ZombieSpawnLocation.forGrave(grave.getRow(), grave.getCol());
    }

    private void positionZombie(Zombie zombie, ZombieSpawnLocation spawnLocation) {
        double zombieWidth = zombie.getVisualWidth();
        double x;

        if (spawnLocation.isFromGrave()) {
            double cellCenterX = (spawnLocation.getColumn() * CELL_WIDTH) + (CELL_WIDTH / 2.0);
            x = cellCenterX - (zombieWidth / 2.0);
        } else {
            x = (COLUMNS * CELL_WIDTH) - zombieWidth;
        }

        double y = (spawnLocation.getRow() * CELL_HEIGHT) + 10;

        zombie.getView().setTranslateX(x);
        zombie.getView().setTranslateY(y);
        zombie.getView().setMouseTransparent(false);
    }

    private void configureZombieInteractions(Zombie zombie) {
        zombie.getView().setOnMouseClicked(event -> {
            if (tryUseCageOnZombie(zombie)) {
                event.consume();
            }
        });
    }

    private static final class ZombieSpawnLocation {

        private final int row;
        private final int column;
        private final boolean fromGrave;

        private ZombieSpawnLocation(int row, int column, boolean fromGrave) {
            this.row = row;
            this.column = column;
            this.fromGrave = fromGrave;
        }

        private static ZombieSpawnLocation forEdge(int row) {
            return new ZombieSpawnLocation(row, COLUMNS - 1, false);
        }

        private static ZombieSpawnLocation forGrave(int row, int column) {
            return new ZombieSpawnLocation(row, column, true);
        }

        private int getRow() {
            return row;
        }

        private int getColumn() {
            return column;
        }

        private boolean isFromGrave() {
            return fromGrave;
        }
    }
}
