import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Plant {

    private Rectangle view;
    private int health;

    public Plant() {
        view = new Rectangle(60, 60);
        view.setFill(Color.GREEN);

        health = 100;
    }
    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Plant health: " + health);
    }
    public int getHealth() {
        return health;
    }
    public boolean isDead() {
        return health <= 0;
    }
    public Rectangle getView() {
        return view;
    }
    
}
