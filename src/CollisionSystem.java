import javafx.scene.Node;

/**
 * Answers geometry questions only: "do these two things touch?" and
 * "which column is this zombie in?". It never changes game state;
 * GameBoard decides what a collision means.
 */
public final class CollisionSystem {

    private CollisionSystem() {
    }

    public static boolean overlaps(Node first, Node second) {
        return first.localToScene(first.getBoundsInLocal())
                .intersects(second.localToScene(second.getBoundsInLocal()));
    }

    public static boolean zombieTouchesPlant(Zombie zombie, Plant plant) {
        return plant.blocksZombies() && overlaps(zombie.getBodyView(), plant.getView());
    }

    public static boolean bulletHitsZombie(Bullet bullet, Zombie zombie) {
        return overlaps(bullet.getView(), zombie.getBodyView());
    }

    public static int columnOf(Zombie zombie) {
        double zombieCenterX = zombie.getView().getTranslateX() + (zombie.getVisualWidth() / 2.0);
        return (int) Math.floor(zombieCenterX / BoardMetrics.CELL_WIDTH);
    }
}
