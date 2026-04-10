import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WaterDrop {

    private ImageView view;
    private int value;

    private boolean collected = false;
    private double animationScale = 1.0;
    private int animationFrames = 0;

    public WaterDrop(double x, double y) {
        view = new ImageView(new Image("file:src/assets/water.png"));
        view.setFitWidth(80);
        view.setFitHeight(150);

        view.setTranslateX(x);
        view.setTranslateY(y);

        value = 25;
    }

    public ImageView getView() {
        return view;
    }

    public int getValue() {
        return value;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public void update() {
        if (!collected) {
            return;
        }

        animationFrames++;

        // squeeze / evaporate effect
        animationScale -= 0.08;
        if (animationScale < 0) {
            animationScale = 0;
        }

        view.setScaleX(1.2);
        view.setScaleY(animationScale);
        view.setOpacity(Math.max(0, 1.0 - animationFrames * 0.12));
    }

    public boolean isFinished() {
        return collected && animationFrames > 8;
    }
}