import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            GameApp.launch(GameApp.class, args);
        } catch (IllegalStateException exception) {
            GameLogger.error("JavaFX launch failed because the application state was invalid.", exception);
            throw exception;
        } catch (RuntimeException exception) {
            GameLogger.error("Unexpected JavaFX startup failure.", exception);
            throw exception;
        }
    }

    public static void demonstratePolymorphism(GameBoard board) {
        Zombie z1 = new FastZombie(0);
        Zombie z2 = new TankZombie(1);

        // Superclass reference invoking subclass implementation.
        z1.act(board);
        z2.act(board);

        List<Zombie> zombieReferences = new ArrayList<>();
        zombieReferences.add(z1);
        zombieReferences.add(z2);
        PolymorphismUtils.printEntities(zombieReferences);
    }
}
