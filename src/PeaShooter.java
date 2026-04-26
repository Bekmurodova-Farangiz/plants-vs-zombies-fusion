import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class PeaShooter extends Plant {

    private static final Image[] ANIMATION_FRAMES = ImageAssets.loadAll(
        "/assets/peashooter_animation/peashooter1.png",
        "/assets/peashooter_animation/peashooter2.png",
        "/assets/peashooter_animation/peashooter3.png"
    );
    private static final double SHOOTING_INTERVAL_SECONDS = 2.5;
    private static final Duration FRAME_DURATION = Duration.millis(165);
    private static final int IDLE_FRAME_INDEX = 0;
    private static final int FIRE_FRAME_INDEX = 2;
    private static final int FINAL_FRAME_INDEX = ANIMATION_FRAMES.length - 1;
    private static final int MAX_FUSION_LEVEL = 2;
    private static final double FUSED_GLOW_LEVEL = 0.4;
    private static final double DOUBLE_SHOT_VERTICAL_OFFSET = 18.0;

    private Timeline animationTimeline;
    private int currentFrame = IDLE_FRAME_INDEX;
    private final Glow fusedGlow = new Glow(FUSED_GLOW_LEVEL);
    private int fusionLevel = 1;

    public PeaShooter(int row, int col) {
        super(row, col);

        setShootingInterval(SHOOTING_INTERVAL_SECONDS);
        setCooldown(1.5);
        setWaterCost(20);
        resetToIdleFrame();
    }

    public void playShootAnimation(GameBoard board) {
        if (board == null || isDead() || isInfected()) {
            return;
        }

        stopAnimation();
        resetToIdleFrame();

        animationTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> applyFrame(IDLE_FRAME_INDEX)),
            new KeyFrame(FRAME_DURATION, e -> applyFrame(1)),
            new KeyFrame(FRAME_DURATION.multiply(FIRE_FRAME_INDEX), e -> {
                applyFrame(FIRE_FRAME_INDEX);
                fireShots(board);
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
        getView().setEffect(null);
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
        if (isInfected()) {
            return;
        }

        if (animationTimeline != null && animationTimeline.getStatus() == Timeline.Status.PAUSED) {
            animationTimeline.play();
        }
    }

    @Override
    protected void onInfected() {
        stopAnimation();
        resetToIdleFrame();
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        stopAnimation();
        getView().setEffect(null);
        resetToIdleFrame();
    }

    @Override
    public void act(GameBoard board) {
        playShootAnimation(board);
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Pea shooters rely on their regular attack cycle rather than a placement ability.
    }

    public boolean canFuse() {
        return fusionLevel < MAX_FUSION_LEVEL;
    }

    public void fuse() {
        if (!canFuse()) {
            return;
        }

        fusionLevel++;
        updateFusionVisual();
    }

    public int getFusionLevel() {
        return fusionLevel;
    }

    private void resetToIdleFrame() {
        applyFrame(IDLE_FRAME_INDEX);
    }

    private void applyFrame(int frameIndex) {
        currentFrame = frameIndex;
        getView().setImage(ANIMATION_FRAMES[currentFrame]);
        updateFusionVisual();
    }

    private void fireShots(GameBoard board) {
        if (isInfected()) {
            return;
        }

        if (fusionLevel <= 1) {
            board.fireBulletFromPlant(this);
            return;
        }

        double bulletX = getShootOriginX() - (Bullet.WIDTH / 2.0);
        double bulletY = getShootOriginY() - (Bullet.HEIGHT / 2.0);

        board.fireBullet(bulletX, bulletY - DOUBLE_SHOT_VERTICAL_OFFSET);
        board.fireBullet(bulletX, bulletY + DOUBLE_SHOT_VERTICAL_OFFSET);
    }

    private void updateFusionVisual() {
        getView().setEffect(fusionLevel > 1 ? fusedGlow : null);
    }

    private void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
    }
}
