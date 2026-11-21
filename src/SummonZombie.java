import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * A captain zombie that calls reinforcements: 10 seconds after it spawns,
 * four normal zombies rise from the ground in the neighbouring cells
 * (in front of it, behind it, above it and below it).
 *
 * Open-Closed principle: this new type only extends Zombie and overrides
 * inherited hooks. GameBoard is unchanged: it already calls zombie.onSpawn(this)
 * and updates every zombie each tick, and dynamic binding runs these versions.
 */
public class SummonZombie extends Zombie {

    // The board updates each zombie every 50 ms (see GameBoard.startGameLoop).
    private static final int TICK_MILLIS = 50;
    private static final int SUMMON_DELAY_MILLIS = 10_000;
    private static final int SUMMON_DELAY_TICKS = SUMMON_DELAY_MILLIS / TICK_MILLIS;

    // The captain is drawn bigger than normal zombies (1.0 = normal size).
    private static final double CAPTAIN_SCALE = 1.35;

    // "Rising from the ground" animation of each summoned zombie.
    private static final double RISE_DEPTH = 90;
    private static final double RISE_SECONDS = 0.8;
    private static final double ZOMBIE_VIEW_SIZE = 130;

    // Each summoned zombie is one cell away: {row offset, column offset}.
    private static final int[][] SUMMON_OFFSETS = {
            {0, -1},  // in front (to the left, the direction zombies walk)
            {0, 1},   // behind (to the right)
            {-1, 0},  // the cell above
            {1, 0}    // the cell below
    };

    private GameBoard board;
    private int ticksSinceSpawn;
    private boolean hasSummoned;

    public SummonZombie(int row) {
        super(row);

        setHealth(150);
        setSpeed(0.8);
        setAttackDamage(1);
        setZombieImage("/assets/summon_zombie.png");

        // Make the whole zombie view bigger (scaled around its centre).
        getView().setScaleX(CAPTAIN_SCALE);
        getView().setScaleY(CAPTAIN_SCALE);
    }

    @Override
    public void act(GameBoard board) {
        move();
    }

    /** Called once by GameBoard.onSpawn(...): remember the board, the summon happens later. */
    @Override
    public void specialAbility(GameBoard board) {
        this.board = board;
    }

    /**
     * Called by the board every tick (50 ms), also while the zombie is eating a plant.
     * Counting ticks instead of starting a separate timer means the delay
     * automatically stops when the game is paused or the match ends.
     */
    @Override
    public void updateStatusEffects() {
        super.updateStatusEffects();

        if (hasSummoned || board == null || !isAlive()) {
            return;
        }

        ticksSinceSpawn++;
        if (ticksSinceSpawn >= SUMMON_DELAY_TICKS) {
            hasSummoned = true;
            summonAround();
        }
    }

    private void summonAround() {
        double summonerX = getView().getTranslateX();

        for (int[] offset : SUMMON_OFFSETS) {
            int targetRow = getRow() + offset[0];

            // Skip cells outside the board (no row above the top row, no row below the bottom row).
            if (targetRow < 0 || targetRow >= BoardMetrics.ROWS) {
                continue;
            }

            Zombie minion = board.spawnZombie(ZombieType.NORMAL, targetRow);

            // spawnZombie returns null when the match has already ended.
            if (minion != null) {
                // The row (y) is already set by the board; we only move it next to the summoner (x).
                minion.getView().setTranslateX(summonerX + offset[1] * BoardMetrics.CELL_WIDTH);
                riseFromGround(minion.getView());
            }
        }
    }

    /**
     * Makes a zombie look like it climbs out of the ground: it starts lower, and a clip
     * hides everything below the ground line; both move together until it is fully visible.
     */
    private static void riseFromGround(Node view) {
        double finalY = view.getTranslateY();
        Rectangle groundClip = new Rectangle(ZOMBIE_VIEW_SIZE, ZOMBIE_VIEW_SIZE - RISE_DEPTH);
        view.setClip(groundClip);
        view.setTranslateY(finalY + RISE_DEPTH);

        Timeline rise = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(view.translateYProperty(), finalY + RISE_DEPTH),
                        new KeyValue(groundClip.heightProperty(), ZOMBIE_VIEW_SIZE - RISE_DEPTH)),
                new KeyFrame(Duration.seconds(RISE_SECONDS),
                        new KeyValue(view.translateYProperty(), finalY),
                        new KeyValue(groundClip.heightProperty(), ZOMBIE_VIEW_SIZE))
        );
        rise.setOnFinished(e -> view.setClip(null));
        rise.play();
    }
}
