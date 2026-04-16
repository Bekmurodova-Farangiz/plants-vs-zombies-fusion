public class Grave {

    private final int row;
    private final int col;
    private final String imagePath;
    private final boolean spawnSource;

    public Grave(int row, int col, String imagePath) {
        this(row, col, imagePath, true);
    }

    public Grave(int row, int col, String imagePath, boolean spawnSource) {
        this.row = row;
        this.col = col;
        this.imagePath = imagePath;
        this.spawnSource = spawnSource;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isSpawnSource() {
        return spawnSource;
    }
}
