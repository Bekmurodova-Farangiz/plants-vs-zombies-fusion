import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Sunflower extends Plant {

    private static final Image[] ANIMATION_FRAMES = ImageAssets.loadAll(
        "file:src/assets/sunplant_animation/sunflower1.png",
        "file:src/assets/sunplant_animation/sunflower2.png",
        "file:src/assets/sunplant_animation/sunflower3.png",
        "file:src/assets/sunplant_animation/sunflower4.png",
        "file:src/assets/sunplant_animation/sunflower5.png"
    );
    private static final Image FUSED_IMAGE = ImageAssets.load("file:src/assets/sunflower_double.png");
    private static final Duration PRODUCTION_CYCLE = Duration.seconds(10);
    private static final Duration FRAME_DURATION = Duration.millis(150);
    private static final Duration ANIMATION_TRIGGER_OFFSET =
        PRODUCTION_CYCLE.subtract(FRAME_DURATION.multiply(ANIMATION_FRAMES.length));
    private static final int BASE_PRODUCTION_AMOUNT = 25;
    private static final int MAX_FUSION_LEVEL = 2;

    private Timeline productionTimeline;
    private Timeline animationTimeline;
    private GameBoard board;
    private int fusionLevel = 1;

    public Sunflower(int row, int col, GameBoard board) {
        super(row, col);

        setShootingInterval(0);
        setCooldown(2.5);
        resetToIdleFrame();
        setWaterCost(10);

        startSunProduction(board);
    }

    public void startSunProduction(GameBoard board) {
        if (isInfected()) {
            return;
        }

        this.board = board;
        stopTimeline(productionTimeline);
        productionTimeline = new Timeline(
            new KeyFrame(ANIMATION_TRIGGER_OFFSET, e -> {
                if (!isDead() && !isInfected()) {
                    playProductionAnimation(board);
                }
            }),
            new KeyFrame(PRODUCTION_CYCLE, e -> produceSun())
        );

        productionTimeline.setCycleCount(Timeline.INDEFINITE);
        productionTimeline.playFromStart();
    }

    public void playProductionAnimation(GameBoard board) {
        this.board = board;

        if (isDead() || isInfected()) {
            return;
        }

        stopTimeline(animationTimeline);
        resetToAnimationStart();

        animationTimeline = new Timeline();

        for (int frameIndex = 1; frameIndex < ANIMATION_FRAMES.length; frameIndex++) {
            final int nextFrameIndex = frameIndex;
            animationTimeline.getKeyFrames().add(
                new KeyFrame(
                    FRAME_DURATION.multiply(frameIndex),
                    e -> applyAnimationFrame(nextFrameIndex)
                )
            );
        }

        animationTimeline.setCycleCount(1);
        animationTimeline.setOnFinished(e -> animationTimeline = null);
        animationTimeline.playFromStart();
    }

    public void stopProduction() {
        pauseTimeline(productionTimeline);
        pauseTimeline(animationTimeline);
    }

    @Override
    public void onGamePaused() {
        stopProduction();
    }

    public void resumeProduction(GameBoard board) {
        this.board = board;

        if (isDead() || isInfected()) {
            return;
        }

        if (productionTimeline == null) {
            startSunProduction(board);
            return;
        }

        if (productionTimeline.getStatus() == Timeline.Status.PAUSED) {
            productionTimeline.play();
        } else if (productionTimeline.getStatus() == Timeline.Status.STOPPED) {
            startSunProduction(board);
            return;
        }

        if (animationTimeline != null && animationTimeline.getStatus() == Timeline.Status.PAUSED) {
            animationTimeline.play();
        }
    }

    @Override
    public void onGameResumed(GameBoard board) {
        resumeProduction(board);
    }

    @Override
    protected void onInfected() {
        stopTimeline(productionTimeline);
        stopTimeline(animationTimeline);
        productionTimeline = null;
        animationTimeline = null;
        board = null;
        resetToIdleFrame();
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        stopTimeline(productionTimeline);
        stopTimeline(animationTimeline);
        productionTimeline = null;
        animationTimeline = null;
        board = null;
    }

    @Override
    public void onRemoved() {
        stopTimeline(productionTimeline);
        stopTimeline(animationTimeline);
        productionTimeline = null;
        animationTimeline = null;
        board = null;
    }

    private void produceSun() {
        if (isDead() || isInfected() || board == null) {
            return;
        }

        board.spawnSunFromPlant(this, getProductionAmount());
        resetToIdleFrame();
        System.out.println("Sunflower produced sun!");
    }

    private void applyAnimationFrame(int frameIndex) {
        getView().setImage(ANIMATION_FRAMES[frameIndex]);
    }

    private void resetToAnimationStart() {
        applyAnimationFrame(0);
    }

    private void resetToIdleFrame() {
        getView().setImage(getIdleImage());
    }

    private Image getIdleImage() {
        return fusionLevel > 1 ? FUSED_IMAGE : ANIMATION_FRAMES[0];
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

    public boolean canFuse() {
        return fusionLevel < MAX_FUSION_LEVEL;
    }

    public void fuse() {
        if (!canFuse()) {
            return;
        }

        fusionLevel++;
        resetToIdleFrame();
    }

    public int getProductionAmount() {
        return BASE_PRODUCTION_AMOUNT * fusionLevel;
    }

    public int getFusionLevel() {
        return fusionLevel;
    }
}
