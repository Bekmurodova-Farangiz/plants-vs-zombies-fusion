import java.util.EnumMap;
import java.util.Map;

public final class PlantFactory {

    @FunctionalInterface
    public interface PlantCreator {
        Plant create(int row, int col, GameBoard board);
    }

    private static final Map<PlantType, PlantCreator> REGISTRY = new EnumMap<>(PlantType.class);

    static {
        registerPlant(PlantType.PEA_SHOOTER, (row, col, board) -> new PeaShooter(row, col));
        registerPlant(PlantType.WALL_PLANT, (row, col, board) -> new WallPlant(row, col));
        registerPlant(PlantType.SUNFLOWER, Sunflower::new);
        registerPlant(PlantType.WATER_PLANT, WaterPlant::new);
        registerPlant(PlantType.BOMB_PLANT, (row, col, board) -> new BombPlant(row, col));
    }

    private PlantFactory() {
    }

    public static void registerPlant(PlantType type, PlantCreator creator) {
        if (type == null) {
            throw new IllegalArgumentException("Plant type must not be null.");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Plant creator must not be null.");
        }

        REGISTRY.put(type, creator);
    }

    public static Plant createPlant(PlantType type, int row, int col, GameBoard board) {
        if (type == null) {
            throw new IllegalArgumentException("Plant type must not be null.");
        }

        PlantCreator creator = REGISTRY.get(type);

        if (creator == null) {
            throw new IllegalArgumentException("Unsupported plant type: " + type);
        }

        return creator.create(row, col, board);
    }

    public static Plant createPlant(String identifier, int row, int col, GameBoard board) {
        PlantType type = PlantType.tryFromIdentifier(identifier).orElse(PlantType.PEA_SHOOTER);

        if (type == PlantType.PEA_SHOOTER && !PlantType.PEA_SHOOTER.getIdentifier().equalsIgnoreCase(identifier)
                && !PlantType.PEA_SHOOTER.name().equalsIgnoreCase(identifier)) {
            System.err.println("Unknown plant type '" + identifier + "'. Falling back to PeaShooter.");
        }

        return createPlant(type, row, col, board);
    }
}
