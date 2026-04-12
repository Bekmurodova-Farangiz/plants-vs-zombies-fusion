import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class WaterPlant extends Plant {

    private Timeline waterTimeline;
    private Timeline animationTimeline;
    private int currentFrame = 1;

    public WaterPlant(int row, int col, GameBoard board) {
        super(row, col);

        setShootingInterval(0);
        setCooldown(2.5);
        setPlantImage("file:src/assets/waterplant1.png");
        setWaterCost(0);

        waterTimeline = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> {
                if (!isDead()) {
                    board.spawnWaterDropFromPlant(this);
                    System.out.println("WaterPlant produced water! +25");
                }
            })
        );

        waterTimeline.setCycleCount(Timeline.INDEFINITE);
        waterTimeline.play();
        startAnimation();
    }
    private void startAnimation() {
        animationTimeline = new Timeline(
            new KeyFrame(Duration.seconds(0.4), e -> {
                currentFrame++;

                if (currentFrame > 4) {
                    currentFrame = 1;
                }

                setPlantImage("file:src/assets/waterplant" + currentFrame + ".png");
            })
        );

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }

    public void stopProduction() {
        if (waterTimeline != null) {
            waterTimeline.stop();
        }

        if (animationTimeline != null) {
            animationTimeline.stop();
        }
    }
    public void resumeProduction(GameBoard board) {
        if (waterTimeline == null || waterTimeline.getStatus() != Timeline.Status.RUNNING) {
            waterTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    if (!isDead()) {
                        board.spawnWaterDropFromPlant(this);
                        System.out.println("WaterPlant produced water!");
                    }
                })
            );

            waterTimeline.setCycleCount(Timeline.INDEFINITE);
            waterTimeline.play();
        }
    }
}