import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet {

    private Rectangle view;
    private int damage;
    

    public Bullet(double x, double y) {
        view = new Rectangle(20, 20);
        view.setFill(Color.YELLOW);

        view.setTranslateX(x);
        view.setTranslateY(y);

        damage = 10;
    }

    public Rectangle getView() {
        return view;
    }

    public int getDamage() {
        return damage;
    }

    public void moveRight() {
        view.setTranslateX(view.getTranslateX() + 5);
    }
      public boolean isOffScreen() {
        return view.getTranslateX() > 1510;
    }
}
