import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class BombPlant extends Plant {

    private static final String IDLE_IMAGE_PATH = "file:src/assets/bombplant.png";
    private static final Image EXPLOSION_SPRITE_SHEET = ImageAssets.load("file:src/assets/frams.png");
    private static final Duration FUSE_DURATION = Duration.seconds(2);
    private static final Duration FRAME_DURATION = Duration.millis(100);
    private static final int EXPLOSION_FRAME_COUNT = 3;
    private static final int BLAST_DAMAGE = 250;
    private static final BlastArea BLAST_AREA = BlastArea.rectangle(1, 2);
    private static final double EXPLOSION_VISUAL_SCALE = 1.1;

    private final double frameWidth = EXPLOSION_SPRITE_SHEET.getWidth() / EXPLOSION_FRAME_COUNT;
    private final double frameHeight = EXPLOSION_SPRITE_SHEET.getHeight();

    private Timeline fuseTimeline;
    private Timeline explosionTimeline;
    private GameBoard board;
    private boolean exploding;
    private boolean detonated;
    private int currentExplosionFrame;

    public BombPlant(int row, int col) {
        super(row, col);

        setPlantImage(IDLE_IMAGE_PATH);
        setShootingInterval(0);
        setCooldown(5.0);
        setWaterCost(0);
    }

    @Override
    public boolean blocksZombies() {
        return !exploding;
    }

    @Override
    public double getVisualFitWidth() {
        if (!exploding) {
            return super.getVisualFitWidth();
        }

        return getExplosionWidth();
    }

    @Override
    public double getVisualFitHeight() {
        if (!exploding) {
            return super.getVisualFitHeight();
        }

        return getExplosionHeight();
    }

    @Override
    public double getVisualOffsetX() {
        if (!exploding) {
            return super.getVisualOffsetX();
        }

        return getExplosionOffsetX();
    }

    @Override
    public double getVisualOffsetY() {
        if (!exploding) {
            return super.getVisualOffsetY();
        }

        return getExplosionOffsetY();
    }

    @Override
    public boolean shouldPreserveAspectRatio() {
        return !exploding;
    }

    @Override
    public void onPlaced(GameBoard board) {
        this.board = board;
        startFuse();
    }

    @Override
    public void onRemoved() {
        stopTimeline(fuseTimeline);
        stopTimeline(explosionTimeline);
        fuseTimeline = null;
        explosionTimeline = null;
        board = null;
        exploding = false;
    }

    @Override
    public void onGamePaused() {
        if (fuseTimeline != null) {
            fuseTimeline.pause();
        }

        if (explosionTimeline != null) {
            explosionTimeline.pause();
        }
    }

    @Override
    public void onGameResumed(GameBoard board) {
        this.board = board;

        if (explosionTimeline != null) {
            explosionTimeline.play();
            return;
        }

        if (fuseTimeline != null && !detonated) {
            fuseTimeline.play();
        }
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        stopTimeline(fuseTimeline);
        stopTimeline(explosionTimeline);
        fuseTimeline = null;
        explosionTimeline = null;
        board = null;
    }

    private void startFuse() {
        if (detonated || fuseTimeline != null) {
            return;
        }

        fuseTimeline = new Timeline(
            new KeyFrame(FUSE_DURATION, e -> beginExplosion())
        );
        fuseTimeline.setCycleCount(1);
        fuseTimeline.play();
    }

    private void beginExplosion() {
        if (detonated || isDead() || board == null) {
            return;
        }

        detonated = true;
        exploding = true;
        currentExplosionFrame = 0;
        stopTimeline(fuseTimeline);
        fuseTimeline = null;

        ImageView view = getView();
        view.setImage(EXPLOSION_SPRITE_SHEET);
        configureView();
        view.setViewport(createFrameViewport(currentExplosionFrame));

        board.damageZombiesInArea(getRow(), getCol(), getBlastArea(), BLAST_DAMAGE);

        explosionTimeline = new Timeline(
            new KeyFrame(FRAME_DURATION, e -> advanceExplosionFrame())
        );
        explosionTimeline.setCycleCount(Timeline.INDEFINITE);
        explosionTimeline.play();
    }

    public BlastArea getBlastArea() {
        return BLAST_AREA;
    }

    public double getExplosionWidth() {
        return getBlastArea().getColumnSpan() * BoardMetrics.CELL_WIDTH * EXPLOSION_VISUAL_SCALE;
    }

    public double getExplosionHeight() {
        return getBlastArea().getRowSpan() * BoardMetrics.CELL_HEIGHT * EXPLOSION_VISUAL_SCALE;
    }

    public double getExplosionOffsetX() {
        return 0;
    }

    public double getExplosionOffsetY() {
        return 0;
    }

    private void advanceExplosionFrame() {
        currentExplosionFrame++;

        if (currentExplosionFrame >= EXPLOSION_FRAME_COUNT) {
            finishExplosion();
            return;
        }

        getView().setViewport(createFrameViewport(currentExplosionFrame));
    }

    private Rectangle2D createFrameViewport(int frameIndex) {
        return new Rectangle2D(frameIndex * frameWidth, 0, frameWidth, frameHeight);
    }

    private void finishExplosion() {
        stopTimeline(explosionTimeline);
        explosionTimeline = null;

        if (board != null) {
            board.removePlant(this);
        }
    }

    private void stopTimeline(Timeline timeline) {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
