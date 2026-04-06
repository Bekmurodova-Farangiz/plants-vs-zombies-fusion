import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Sunflower extends Plant {

    private Timeline sunTimeline;

    public Sunflower(int row, int col, GameBoard board) {
        super(row, col);

        setShootingInterval(0); // no shooting
        setCooldown(2.5);

        

        startSunProduction(board);
    }

    private void startSunProduction(GameBoard board) {
        sunTimeline = new Timeline(
            new KeyFrame(Duration.seconds(5), e -> {
                if (!isDead()) {
                    board.addSunPoints(25);
                    System.out.println("Sunflower produced sun! +25");
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
}