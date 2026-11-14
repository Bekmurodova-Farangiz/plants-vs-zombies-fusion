import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bullet implements Movable {

    private static final Image BULLET_IMAGE = ImageAssets.load("/assets/bullet.png");

    public static final double WIDTH = 100;
    public static final double HEIGHT = 100;

    private final ImageView view;
    private int damage;

    public Bullet(double x, double y) {
        view = new ImageView(BULLET_IMAGE);
        view.setFitWidth(WIDTH);
        view.setFitHeight(HEIGHT);
        view.setPreserveRatio(true);

        view.setTranslateX(x);
        view.setTranslateY(y);

        damage = 10;
    }

    public ImageView getView() {
        return view;
    }

    public int getDamage() {
        return damage;
    }

    // Movable: a bullet always flies to the right, so move() delegates to moveRight().
    @Override
    public void move() {
        moveRight();
    }

    public void moveRight() {
        view.setTranslateX(view.getTranslateX() + 5);
    }

    public boolean isOffScreen() {
        return view.getTranslateX() > BoardMetrics.BULLET_DESPAWN_X;
    }
}
