import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Wave {

    private static final double PROBABILITY_EPSILON = 0.0001;

    private final int totalZombies;
    private final double spawnInterval;
    private final Map<ZombieType, Double> probabilities;

    public Wave(int totalZombies, double spawnInterval, Map<ZombieType, Double> probabilities) {
        if (totalZombies <= 0) {
            throw new IllegalArgumentException("totalZombies must be greater than 0");
        }

        if (spawnInterval <= 0) {
            throw new IllegalArgumentException("spawnInterval must be greater than 0");
        }

        this.totalZombies = totalZombies;
        this.spawnInterval = spawnInterval;
        this.probabilities = buildProbabilityMap(probabilities);
    }

    public int getTotalZombies() {
        return totalZombies;
    }

    public double getSpawnInterval() {
        return spawnInterval;
    }

    public Map<ZombieType, Double> getProbabilities() {
        return Collections.unmodifiableMap(probabilities);
    }

    public ZombieType pickZombieType() {
        double roll = ThreadLocalRandom.current().nextDouble();
        double cumulativeProbability = 0.0;
        ZombieType fallbackType = ZombieType.NORMAL;

        for (ZombieType type : ZombieType.values()) {
            double probability = probabilities.getOrDefault(type, 0.0);

            if (probability > 0.0) {
                fallbackType = type;
            }

            cumulativeProbability += probability;

            if (roll < cumulativeProbability) {
                return type;
            }
        }

        return fallbackType;
    }

    private Map<ZombieType, Double> buildProbabilityMap(Map<ZombieType, Double> source) {
        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException("probabilities must not be empty");
        }

        Map<ZombieType, Double> normalizedProbabilities = new EnumMap<>(ZombieType.class);
        double totalProbability = 0.0;

        for (ZombieType type : ZombieType.values()) {
            double probability = source.getOrDefault(type, 0.0);

            if (probability < 0.0) {
                throw new IllegalArgumentException("probability cannot be negative for " + type);
            }

            normalizedProbabilities.put(type, probability);
            totalProbability += probability;
        }

        if (Math.abs(totalProbability - 1.0) > PROBABILITY_EPSILON) {
            throw new IllegalArgumentException("probabilities must sum to 1.0");
        }

        return normalizedProbabilities;
    }
}
