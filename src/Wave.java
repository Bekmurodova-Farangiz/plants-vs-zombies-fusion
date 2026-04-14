public class Wave {

    private int totalZombies;
    private double spawnInterval;

    private double normalChance;
    private double fastChance;
    private double fatChance;
    private double tankChance;

    public Wave(int totalZombies, double spawnInterval,
                double normalChance, double fastChance,
                double fatChance, double tankChance) {
        this.totalZombies = totalZombies;
        this.spawnInterval = spawnInterval;
        this.normalChance = normalChance;
        this.fastChance = fastChance;
        this.fatChance = fatChance;
        this.tankChance = tankChance;
    }

    public int getTotalZombies() {
        return totalZombies;
    }

    public double getSpawnInterval() {
        return spawnInterval;
    }

    public double getNormalChance() {
        return normalChance;
    }

    public double getFastChance() {
        return fastChance;
    }

    public double getFatChance() {
        return fatChance;
    }

    public double getTankChance() {
        return tankChance;
    }
}