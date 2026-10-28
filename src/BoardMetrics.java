public final class BoardMetrics {

    public static final int ROWS = 5;
    public static final int COLUMNS = 10;
    public static final int CELL_WIDTH = 150;
    public static final int CELL_HEIGHT = 132;

    /** A bullet past this x position has left the board and is removed. */
    public static final double BULLET_DESPAWN_X = 1462;

    /** Where produced sun flies to: the sun counter in the top-left corner. */
    public static final double SUN_COUNTER_X = 20;
    public static final double SUN_COUNTER_Y = -150;

    private BoardMetrics() {
    }
}
