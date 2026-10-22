import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Owns the wave rules: which wave we are in, how many zombies it has,
 * how many were already spawned and how fast they come.
 * GameBoard still runs the timers; this class only knows the state and the rules.
 */
public final class WaveManager {

    private final List<Wave> waves;
    private final double spawnIntervalMultiplier;
    private int currentWave = 1;
    private int zombiesSpawnedInWave = 0;
    private int zombiesPerWave;
    private double spawnIntervalSeconds;
    private boolean waveInProgress = true;

    public WaveManager(List<Wave> waves, double spawnIntervalMultiplier) {
        if (waves == null || waves.isEmpty()) {
            throw new IllegalArgumentException("A match needs at least one wave.");
        }

        this.waves = new ArrayList<>(waves);
        this.spawnIntervalMultiplier = spawnIntervalMultiplier;
        configureCurrentWave();
    }

    /** The three waves used by every match. */
    public static List<Wave> createDefaultWaves() {
        List<Wave> defaultWaves = new ArrayList<>();

        // Wave 1 (easy)
        defaultWaves.add(new Wave(6, 4.5, probabilities(0.85, 0.15, 0.0, 0.0, 0.0)));
        // Wave 2 (medium)
        defaultWaves.add(new Wave(10, 3.0, probabilities(0.45, 0.25, 0.20, 0.0, 0.10)));
        // Wave 3 (hard)
        defaultWaves.add(new Wave(14, 2.2, probabilities(0.30, 0.20, 0.20, 0.15, 0.15)));

        return defaultWaves;
    }

    private static Map<ZombieType, Double> probabilities(
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

    private void configureCurrentWave() {
        Wave wave = getCurrentWaveDefinition();
        zombiesPerWave = wave.getTotalZombies();
        spawnIntervalSeconds = wave.getSpawnInterval() * spawnIntervalMultiplier;
    }

    public Wave getCurrentWaveDefinition() {
        return waves.get(currentWave - 1);
    }

    public ZombieType pickZombieType() {
        return getCurrentWaveDefinition().pickZombieType();
    }

    public boolean hasMoreZombiesToSpawn() {
        return zombiesSpawnedInWave < zombiesPerWave;
    }

    public void recordSpawn() {
        zombiesSpawnedInWave++;
    }

    public void markWaveInProgress() {
        waveInProgress = true;
    }

    public void finishSpawning() {
        waveInProgress = false;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public boolean isLastWave() {
        return currentWave >= waves.size();
    }

    public void advanceToNextWave() {
        currentWave++;
        zombiesSpawnedInWave = 0;
        configureCurrentWave();
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getTotalWaves() {
        return waves.size();
    }

    public int getZombiesSpawnedInWave() {
        return zombiesSpawnedInWave;
    }

    public int getZombiesPerWave() {
        return zombiesPerWave;
    }

    public double getSpawnIntervalSeconds() {
        return spawnIntervalSeconds;
    }

    public double getProgress() {
        double completedWaves = currentWave - 1;
        double currentWavePart = (double) zombiesSpawnedInWave / zombiesPerWave;
        return (completedWaves + currentWavePart) / waves.size();
    }

    public List<Wave> getWaves() {
        return Collections.unmodifiableList(waves);
    }
}
