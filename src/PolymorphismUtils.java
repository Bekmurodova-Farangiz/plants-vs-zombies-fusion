import java.util.ArrayList;
import java.util.List;

public final class PolymorphismUtils {

    private PolymorphismUtils() {
    }

    // Parametric polymorphism: the same generic method works for Zombie, Plant, or Movable lists.
    public static <T> List<T> snapshotEntities(List<? extends T> source) {
        return new ArrayList<>(source);
    }

    public static <T> void printEntities(List<T> entities) {
        for (T entity : entities) {
            System.out.println(entity.getClass().getSimpleName());
        }
    }
}
