public final class BlastArea {

    private final int rowRadius;
    private final int columnRadius;

    private BlastArea(int rowRadius, int columnRadius) {
        if (rowRadius < 0 || columnRadius < 0) {
            throw new IllegalArgumentException("Blast radii must be non-negative.");
        }

        this.rowRadius = rowRadius;
        this.columnRadius = columnRadius;
    }

    public static BlastArea square(int radius) {
        return new BlastArea(radius, radius);
    }

    public static BlastArea rectangle(int rowRadius, int columnRadius) {
        return new BlastArea(rowRadius, columnRadius);
    }

    public boolean contains(int centerRow, int centerCol, int targetRow, int targetCol) {
        return Math.abs(targetRow - centerRow) <= rowRadius
            && Math.abs(targetCol - centerCol) <= columnRadius;
    }

    public int getRowRadius() {
        return rowRadius;
    }

    public int getColumnRadius() {
        return columnRadius;
    }

    public int getRowSpan() {
        return (rowRadius * 2) + 1;
    }

    public int getColumnSpan() {
        return (columnRadius * 2) + 1;
    }
}
