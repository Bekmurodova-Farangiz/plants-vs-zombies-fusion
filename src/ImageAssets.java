import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;

public final class ImageAssets {

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private ImageAssets() {
    }

    public static Image load(String imagePath) {
        String resourcePath = toResourcePath(imagePath);
        return CACHE.computeIfAbsent(resourcePath, ImageAssets::loadFromResource);
    }

    public static Image[] loadAll(String... imagePaths) {
        Image[] images = new Image[imagePaths.length];

        for (int index = 0; index < imagePaths.length; index++) {
            images[index] = load(imagePaths[index]);
        }

        return images;
    }

    public static String asset(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Asset path must not be blank.");
        }

        String normalizedPath = relativePath.replace('\\', '/');

        if (normalizedPath.startsWith("/assets/")) {
            return normalizedPath;
        }

        if (normalizedPath.startsWith("assets/")) {
            return "/" + normalizedPath;
        }

        return "/assets/" + normalizedPath;
    }

    private static Image loadFromResource(String resourcePath) {
        URL resourceUrl = ImageAssets.class.getResource(resourcePath);

        if (resourceUrl == null) {
            resourceUrl = findFileSystemResource(resourcePath);
        }

        if (resourceUrl == null) {
            throw new IllegalArgumentException("Missing image resource: " + resourcePath);
        }

        return new Image(resourceUrl.toExternalForm());
    }

    private static URL findFileSystemResource(String resourcePath) {
        String relativePath = resourcePath.startsWith("/")
                ? resourcePath.substring(1)
                : resourcePath;

        Path workingDirectory = Paths.get(System.getProperty("user.dir"));
        Path[] candidates = new Path[] {
                workingDirectory.resolve("src").resolve(relativePath),
                workingDirectory.resolve(relativePath)
        };

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                try {
                    return candidate.toUri().toURL();
                } catch (Exception ignored) {
                    return null;
                }
            }
        }

        return null;
    }

    private static String toResourcePath(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            throw new IllegalArgumentException("Image path must not be blank.");
        }

        String normalizedPath = imagePath.replace('\\', '/');

        if (normalizedPath.startsWith("/")) {
            return normalizedPath;
        }

        if (normalizedPath.startsWith("file:src/")) {
            return "/" + normalizedPath.substring("file:src/".length());
        }

        if (normalizedPath.startsWith("src/")) {
            return "/" + normalizedPath.substring("src/".length());
        }

        return asset(normalizedPath);
    }
}
