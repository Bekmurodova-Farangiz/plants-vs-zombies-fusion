public final class ZombieFactory {

    private ZombieFactory() {
    }

    public static Zombie createZombie(ZombieType type, int row) {
        switch (type) {
            case NORMAL:
                return new Zombie(row);
            case FAST:
                return new FastZombie(row);
            case FAT:
                return new FatZombie(row);
            case TANK:
                return new TankZombie(row);
            case PARASITE:
                return new ParasiteZombie(row);
            default:
                throw new IllegalArgumentException("Unsupported zombie type: " + type);
        }
    }
}
