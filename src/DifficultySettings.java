public final class DifficultySettings {

    private final double zombieSpeedMultiplier;
    private final double zombieHealthMultiplier;
    private final double spawnIntervalMultiplier;
    private final int startingSun;
    private final int startingWater;

    public DifficultySettings(
            double zombieSpeedMultiplier,
            double zombieHealthMultiplier,
            double spawnIntervalMultiplier,
            int startingSun,
            int startingWater
    ) {
        this.zombieSpeedMultiplier = zombieSpeedMultiplier;
        this.zombieHealthMultiplier = zombieHealthMultiplier;
        this.spawnIntervalMultiplier = spawnIntervalMultiplier;
        this.startingSun = startingSun;
        this.startingWater = startingWater;
    }

    public double getZombieSpeedMultiplier() {
        return zombieSpeedMultiplier;
    }

    public double getZombieHealthMultiplier() {
        return zombieHealthMultiplier;
    }

    public double getSpawnIntervalMultiplier() {
        return spawnIntervalMultiplier;
    }

    public int getStartingSun() {
        return startingSun;
    }

    public int getStartingWater() {
        return startingWater;
    }
}
