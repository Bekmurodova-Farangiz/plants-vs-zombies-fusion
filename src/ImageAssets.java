import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;

public final class ImageAssets {

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private ImageAssets() {
    }

    public static Image load(String imagePath) {
        return CACHE.computeIfAbsent(imagePath, Image::new);
    }

    public static Image[] loadAll(String... imagePaths) {
        Image[] images = new Image[imagePaths.length];

        for (int index = 0; index < imagePaths.length; index++) {
            images[index] = load(imagePaths[index]);
        }

        return images;
    }
}
