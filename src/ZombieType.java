import java.util.Optional;

public enum ZombieType {
    NORMAL,
    FAST,
    FAT,
    TANK,
    PARASITE;

    public static ZombieType fromIdentifier(String identifier) {
        return tryFromIdentifier(identifier)
                .orElseThrow(() -> new IllegalArgumentException("Unknown zombie type: " + identifier));
    }

    public static Optional<ZombieType> tryFromIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }

        for (ZombieType type : values()) {
            if (type.name().equalsIgnoreCase(identifier)) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
