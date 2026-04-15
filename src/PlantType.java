public enum PlantType {
    PEA_SHOOTER("PeaShooter", "Pea Shooter", 50, 20, 1.5, "file:src/assets/peashooter.png"),
    WALL_PLANT("WallPlant", "Wall Plant", 50, 40, 3.0, "file:src/assets/wallplant.png"),
    SUNFLOWER("Sunflower", "Sunflower", 50, 10, 2.5, "file:src/assets/sunflower.png"),
    WATER_PLANT("WaterPlant", "Water Plant", 50, 0, 2.5, "file:src/assets/waterplant1.png");

    private final String identifier;
    private final String displayName;
    private final int sunCost;
    private final int waterCost;
    private final double cooldown;
    private final String imagePath;

    PlantType(String identifier, String displayName, int sunCost, int waterCost, double cooldown, String imagePath) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.sunCost = sunCost;
        this.waterCost = waterCost;
        this.cooldown = cooldown;
        this.imagePath = imagePath;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSunCost() {
        return sunCost;
    }

    public int getWaterCost() {
        return waterCost;
    }

    public double getCooldown() {
        return cooldown;
    }

    public String getImagePath() {
        return imagePath;
    }

    public static PlantType fromIdentifier(String identifier) {
        for (PlantType type : values()) {
            if (type.identifier.equalsIgnoreCase(identifier) || type.name().equalsIgnoreCase(identifier)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown plant type: " + identifier);
    }
}
