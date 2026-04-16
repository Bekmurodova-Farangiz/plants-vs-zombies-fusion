import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Level {

    private final LevelType type;
    private final String displayName;
    private final LevelVisualTheme visualTheme;
    private final int graveCount;
    private final int graveMinColumn;
    private final int graveMaxColumn;
    private final List<String> graveImagePaths;
    private final double waveTwoGraveSpawnChance;
    private final double laterWaveGraveSpawnChance;

    public Level(
            LevelType type,
            String displayName,
            LevelVisualTheme visualTheme,
            int graveCount,
            int graveMinColumn,
            int graveMaxColumn,
            List<String> graveImagePaths,
            double waveTwoGraveSpawnChance,
            double laterWaveGraveSpawnChance
    ) {
        this.type = type;
        this.displayName = displayName;
        this.visualTheme = visualTheme;
        this.graveCount = graveCount;
        this.graveMinColumn = graveMinColumn;
        this.graveMaxColumn = graveMaxColumn;
        this.graveImagePaths = Collections.unmodifiableList(new ArrayList<>(graveImagePaths));
        this.waveTwoGraveSpawnChance = waveTwoGraveSpawnChance;
        this.laterWaveGraveSpawnChance = laterWaveGraveSpawnChance;
    }

    public LevelType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBackgroundPath() {
        return visualTheme.getBackgroundPath();
    }

    public LevelVisualTheme getVisualTheme() {
        return visualTheme;
    }

    public int getGraveCount() {
        return graveCount;
    }

    public boolean hasGraves() {
        return graveCount > 0 && !graveImagePaths.isEmpty();
    }

    public List<Grave> generateGravesForMatch() {
        if (!hasGraves()) {
            return Collections.emptyList();
        }

        List<Grave> generatedGraves = new ArrayList<>();
        Set<Integer> usedPositions = new HashSet<>();
        int availableColumns = graveMaxColumn - graveMinColumn + 1;
        int maxGraves = Math.min(graveCount, BoardMetrics.ROWS * availableColumns);

        while (generatedGraves.size() < maxGraves) {
            int row = ThreadLocalRandom.current().nextInt(BoardMetrics.ROWS);
            int col = ThreadLocalRandom.current().nextInt(graveMinColumn, graveMaxColumn + 1);
            int positionKey = row * BoardMetrics.COLUMNS + col;

            if (!usedPositions.add(positionKey)) {
                continue;
            }

            String imagePath = graveImagePaths.get(ThreadLocalRandom.current().nextInt(graveImagePaths.size()));
            generatedGraves.add(new Grave(row, col, imagePath, true));
        }

        return generatedGraves;
    }

    public double getGraveZombieSpawnChance(int waveNumber) {
        if (!hasGraves() || waveNumber <= 1) {
            return 0.0;
        }

        if (waveNumber == 2) {
            return waveTwoGraveSpawnChance;
        }

        return laterWaveGraveSpawnChance;
    }
}
