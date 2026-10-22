import java.util.EnumMap;
import java.util.Map;

/**
 * Owns the player's economy: sun, water and the per-plant cooldowns.
 * GameBoard asks this class "can I afford it?" and "spend it" instead of
 * changing the numbers itself (single responsibility, information hiding).
 */
public final class ResourceManager {

    private int sunPoints;
    private int waterPoints;
    private final Map<PlantType, Long> plantCooldowns = new EnumMap<>(PlantType.class);

    public ResourceManager(int startingSun, int startingWater) {
        this.sunPoints = startingSun;
        this.waterPoints = startingWater;
    }

    public int getSunPoints() {
        return sunPoints;
    }

    public int getWaterPoints() {
        return waterPoints;
    }

    public void addSun(int amount) {
        sunPoints += amount;
    }

    public void addWater(int amount) {
        waterPoints += amount;
    }

    public boolean canAfford(int sunCost, int waterCost) {
        return sunPoints >= sunCost && waterPoints >= waterCost;
    }

    public boolean canAfford(PlantType plantType) {
        return canAfford(plantType.getSunCost(), plantType.getWaterCost());
    }

    public void spend(int sunCost, int waterCost) {
        sunPoints -= sunCost;
        waterPoints -= waterCost;
    }

    public void spendFor(PlantType plantType) {
        spend(plantType.getSunCost(), plantType.getWaterCost());
    }

    public void startCooldown(PlantType plantType) {
        plantCooldowns.put(
                plantType,
                System.currentTimeMillis() + (long) (plantType.getCooldown() * 1000)
        );
    }

    public boolean isOnCooldown(PlantType plantType) {
        return getRemainingCooldownMillis(plantType) > 0;
    }

    public long getRemainingCooldownMillis(PlantType plantType) {
        Long cooldownEnd = plantCooldowns.get(plantType);

        if (cooldownEnd == null) {
            return 0;
        }

        return Math.max(0, cooldownEnd - System.currentTimeMillis());
    }
}
