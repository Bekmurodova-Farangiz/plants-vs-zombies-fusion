import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class PeaShooter extends Plant {

    private static final Image[] ANIMATION_FRAMES = ImageAssets.loadAll(
        "file:src/assets/peashooter_animation/peashooter1.png",
        "file:src/assets/peashooter_animation/peashooter2.png",
        "file:src/assets/peashooter_animation/peashooter3.png"
    );
    private static final double SHOOTING_INTERVAL_SECONDS = 2.5;
    private static final Duration FRAME_DURATION = Duration.millis(165);
    private static final int IDLE_FRAME_INDEX = 0;
    private static final int FIRE_FRAME_INDEX = 2;
    private static final int FINAL_FRAME_INDEX = ANIMATION_FRAMES.length - 1;

    private Timeline animationTimeline;
    private int currentFrame = IDLE_FRAME_INDEX;

    public PeaShooter(int row, int col) {
        super(row, col);

        setShootingInterval(SHOOTING_INTERVAL_SECONDS);
        setCooldown(1.5);
        setWaterCost(20);
        resetToIdleFrame();
    }

    public void playShootAnimation(GameBoard board) {
        if (board == null || isDead()) {
            return;
        }

        stopAnimation();
        resetToIdleFrame();

        animationTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> applyFrame(IDLE_FRAME_INDEX)),
            new KeyFrame(FRAME_DURATION, e -> applyFrame(1)),
            new KeyFrame(FRAME_DURATION.multiply(FIRE_FRAME_INDEX), e -> {
                applyFrame(FIRE_FRAME_INDEX);
                board.fireBulletFromPlant(this);
            }),
            // Keep the last frame on screen briefly before returning to idle.
            new KeyFrame(FRAME_DURATION.multiply(FINAL_FRAME_INDEX + 1))
        );

        animationTimeline.setCycleCount(1);
        animationTimeline.setOnFinished(e -> {
            resetToIdleFrame();
            animationTimeline = null;
        });
        animationTimeline.playFromStart();
    }

    @Override
    public void onRemoved() {
        stopAnimation();
        resetToIdleFrame();
    }

    @Override
    public void onGamePaused() {
        if (animationTimeline != null) {
            animationTimeline.pause();
        }
    }

    @Override
    public void onGameResumed(GameBoard board) {
        if (animationTimeline != null && animationTimeline.getStatus() == Timeline.Status.PAUSED) {
            animationTimeline.play();
        }
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        stopAnimation();
        resetToIdleFrame();
    }

    private void resetToIdleFrame() {
        applyFrame(IDLE_FRAME_INDEX);
    }

    private void applyFrame(int frameIndex) {
        currentFrame = frameIndex;
        getView().setImage(ANIMATION_FRAMES[currentFrame]);
    }

    private void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
    }
}
