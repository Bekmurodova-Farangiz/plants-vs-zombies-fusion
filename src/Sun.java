import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Sun {

    private ImageView view;
    private int value;

    private double targetX;
    private double targetY;

    public Sun(double x, double y, double targetX, double targetY) {
        view = new ImageView(new Image("file:src/assets/sun.png"));

        view.setFitWidth(90);
        view.setFitHeight(60);

        view.setTranslateX(x);
        view.setTranslateY(y);

        this.targetX = targetX;
        this.targetY = targetY;

        value = 25;
    }

    public void update() {
        double currentX = view.getTranslateX();
        double currentY = view.getTranslateY();

        double dx = targetX - currentX;
        double dy = targetY - currentY;

        view.setTranslateX(currentX + dx * 0.03);
        view.setTranslateY(currentY + dy * 0.03);
    }

    public boolean hasReachedTarget() {
        double dx = targetX - view.getTranslateX();
        double dy = targetY - view.getTranslateY();

        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < 15;
    }

    public ImageView getView() {
        return view;
    }

    public int getValue() {
        return value;
    }
}