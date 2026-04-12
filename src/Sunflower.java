import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Sunflower extends Plant {

    private Timeline sunTimeline;

    public Sunflower(int row, int col, GameBoard board) {
        super(row, col);

        setShootingInterval(0);
        setCooldown(2.5);
        setPlantImage("file:src/assets/sunflower.png");
        setWaterCost(10);

        startSunProduction(board);
    }

    private void startSunProduction(GameBoard board) {
        sunTimeline = new Timeline(
            new KeyFrame(Duration.seconds(10), e -> {
                if (!isDead()) {
                    board.spawnSunFromPlant(this);
                    System.out.println("Sunflower produced sun!");
                }
            })
        );

        sunTimeline.setCycleCount(Timeline.INDEFINITE);
        sunTimeline.play();
    }

    public void stopProduction() {
        if (sunTimeline != null) {
            sunTimeline.stop();
        }
    }
    public void resumeProduction(GameBoard board) {
        if (sunTimeline == null || sunTimeline.getStatus() != Timeline.Status.RUNNING) {
            startSunProduction(board);
        }
    }
}