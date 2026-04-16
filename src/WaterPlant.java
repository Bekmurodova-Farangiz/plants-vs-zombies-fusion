import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class WaterPlant extends Plant {

    private static final Image[] ANIMATION_FRAMES = ImageAssets.loadAll(
        "file:src/assets/waterplant1.png",
        "file:src/assets/waterplant2.png",
        "file:src/assets/waterplant3.png",
        "file:src/assets/waterplant4.png"
    );
    private static final Duration PRODUCTION_CYCLE = Duration.seconds(10);
    private static final Duration FRAME_DURATION = Duration.seconds(0.3);

    private Timeline waterTimeline;
    private Timeline animationTimeline;
    private GameBoard board;
    private int currentFrame;

    public WaterPlant(int row, int col, GameBoard board) {
        super(row, col);

        setShootingInterval(0);
        setCooldown(2.5);
        setWaterCost(0);
        resetToIdleFrame();

        startWaterProduction(board);
        startAnimation();
    }

    private void startWaterProduction(GameBoard board) {
        if (isInfected()) {
            return;
        }

        this.board = board;
        stopTimeline(waterTimeline);

        waterTimeline = new Timeline(
            new KeyFrame(PRODUCTION_CYCLE, e -> produceWater())
        );
        waterTimeline.setCycleCount(Timeline.INDEFINITE);
        waterTimeline.playFromStart();
    }

    private void startAnimation() {
        if (isInfected()) {
            return;
        }

        stopTimeline(animationTimeline);

        animationTimeline = new Timeline(
            new KeyFrame(FRAME_DURATION, e -> advanceAnimationFrame())
        );
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.playFromStart();
    }

    @Override
    public void onGamePaused() {
        stopProduction();
    }

    @Override
    public void onGameResumed(GameBoard board) {
        resumeProduction(board);
    }

    @Override
    protected void onInfected() {
        stopTimeline(waterTimeline);
        stopTimeline(animationTimeline);
        waterTimeline = null;
        animationTimeline = null;
        board = null;
        resetToIdleFrame();
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        stopTimeline(waterTimeline);
        stopTimeline(animationTimeline);
        waterTimeline = null;
        animationTimeline = null;
        board = null;
    }

    @Override
    public void onRemoved() {
        stopTimeline(waterTimeline);
        stopTimeline(animationTimeline);
        waterTimeline = null;
        animationTimeline = null;
        board = null;
    }

    public void stopProduction() {
        pauseTimeline(waterTimeline);
        pauseTimeline(animationTimeline);
    }

    public void resumeProduction(GameBoard board) {
        this.board = board;

        if (isDead() || isInfected()) {
            return;
        }

        if (waterTimeline == null) {
            startWaterProduction(board);
        } else if (waterTimeline.getStatus() == Timeline.Status.PAUSED) {
            waterTimeline.play();
        } else if (waterTimeline.getStatus() == Timeline.Status.STOPPED) {
            startWaterProduction(board);
        }

        if (animationTimeline == null) {
            startAnimation();
        } else if (animationTimeline.getStatus() == Timeline.Status.PAUSED) {
            animationTimeline.play();
        } else if (animationTimeline.getStatus() == Timeline.Status.STOPPED) {
            startAnimation();
        }
    }

    private void produceWater() {
        if (isDead() || isInfected() || board == null) {
            return;
        }

        board.spawnWaterDropFromPlant(this);
        System.out.println("WaterPlant produced water! +25");
    }

    private void advanceAnimationFrame() {
        if (isInfected()) {
            return;
        }

        currentFrame = (currentFrame + 1) % ANIMATION_FRAMES.length;
        getView().setImage(ANIMATION_FRAMES[currentFrame]);
    }

    private void resetToIdleFrame() {
        currentFrame = 0;
        getView().setImage(ANIMATION_FRAMES[currentFrame]);
    }

    private void pauseTimeline(Timeline timeline) {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.pause();
        }
    }

    private void stopTimeline(Timeline timeline) {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
