import java.util.EnumMap;
import java.util.Map;
import java.util.function.IntFunction;

public final class ZombieFactory {

    private static final Map<ZombieType, IntFunction<? extends Zombie>> REGISTRY = new EnumMap<>(ZombieType.class);

    static {
        registerZombie(ZombieType.NORMAL, NormalZombie::new);
        registerZombie(ZombieType.FAST, FastZombie::new);
        registerZombie(ZombieType.FAT, FatZombie::new);
        registerZombie(ZombieType.TANK, TankZombie::new);
        registerZombie(ZombieType.PARASITE, ParasiteZombie::new);
    }

    private ZombieFactory() {
    }

    public static void registerZombie(ZombieType type, IntFunction<? extends Zombie> creator) {
        if (type == null) {
            throw new IllegalArgumentException("Zombie type must not be null.");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Zombie creator must not be null.");
        }

        REGISTRY.put(type, creator);
    }

    public static Zombie createZombie(ZombieType type, int row) {
        if (type == null) {
            throw new IllegalArgumentException("Zombie type must not be null.");
        }

        IntFunction<? extends Zombie> creator = REGISTRY.get(type);

        if (creator == null) {
            throw new IllegalArgumentException("Unsupported zombie type: " + type);
        }

        return creator.apply(row);
    }

    public static Zombie createZombie(String identifier, int row) {
        ZombieType type = ZombieType.tryFromIdentifier(identifier).orElse(ZombieType.NORMAL);

        if (type == ZombieType.NORMAL && !ZombieType.NORMAL.name().equalsIgnoreCase(identifier)) {
            System.err.println("Unknown zombie type '" + identifier + "'. Falling back to NORMAL.");
        }

        return createZombie(type, row);
    }
}
