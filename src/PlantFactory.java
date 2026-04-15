public final class PlantFactory {

    private PlantFactory() {
    }

    public static Plant createPlant(PlantType type, int row, int col, GameBoard board) {
        switch (type) {
            case PEA_SHOOTER:
                return new PeaShooter(row, col);
            case WALL_PLANT:
                return new WallPlant(row, col);
            case SUNFLOWER:
                return new Sunflower(row, col, board);
            case WATER_PLANT:
                return new WaterPlant(row, col, board);
            case BOMB_PLANT:
                return new BombPlant(row, col);
            default:
                throw new IllegalArgumentException("Unsupported plant type: " + type);
        }
    }
}
