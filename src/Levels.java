import java.util.Collections;
import java.util.List;

public final class Levels {

    private static final String DAY_BACKGROUND = "/assets/battlezone.PNG";
    private static final String NIGHT_BACKGROUND = "/assets/nightmade.png";
    private static final String GRAVE_1 = "/assets/grave1.png";
    private static final String GRAVE_2 = "/assets/grave2.png";
    private static final List<String> NIGHT_GRAVE_IMAGES = List.of(GRAVE_1, GRAVE_2);
    private static final LevelVisualTheme DAY_THEME = new LevelVisualTheme(
            DAY_BACKGROUND,
            "#000000",
            0.0,
            "-fx-background-color: transparent;",
            "#3b2204",
            0.20,
            9.0,
            "-fx-background-color: rgba(20,20,20,0.45);" +
                    "-fx-padding: 12;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-color: rgba(255,255,255,0.18);" +
                    "-fx-border-width: 1.5;",
            "-fx-background-color: rgba(0,0,0,0.35);" +
                    "-fx-padding: 10;" +
                    "-fx-background-radius: 12;",
            "-fx-accent: #d34d3f;",
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f5d44d;",
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #8ee7ff;",
            "-fx-font-size: 18px; -fx-text-fill: #d94841;",
            "-fx-font-size: 18px; -fx-text-fill: gray;",
            "#000000",
            0.0,
            0.0,
            0.0,
            "#ffffff",
            0.0,
            0.0
    );
    private static final LevelVisualTheme NIGHT_THEME = new LevelVisualTheme(
            NIGHT_BACKGROUND,
            "#2c2f72",
            0.18,
            "-fx-background-color: transparent;",
            "#b7c0ff",
            0.46,
            16.0,
            "-fx-background-color: rgba(9,14,34,0.68);" +
                    "-fx-padding: 12;" +
                    "-fx-background-radius: 20;" +
                    "-fx-border-radius: 20;" +
                    "-fx-border-color: rgba(170,190,255,0.26);" +
                    "-fx-border-width: 1.5;",
            "-fx-background-color: rgba(7,12,32,0.62);" +
                    "-fx-padding: 10;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: rgba(170,190,255,0.20);" +
                    "-fx-border-radius: 12;" +
                    "-fx-border-width: 1;",
            "-fx-accent: #8f82ff;",
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f4ddb1;",
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #9edfff;",
            "-fx-font-size: 18px; -fx-text-fill: #c7baff;",
            "-fx-font-size: 18px; -fx-text-fill: #7c86a9;",
            "#9ea9ff",
            0.42,
            0.18,
            14.0,
            "#9aa6ff",
            0.08,
            42.0
    );

    private Levels() {
    }

    public static Level create(LevelType levelType) {
        switch (levelType) {
            case NIGHT:
                return createNightLevel();
            case DAY:
            default:
                return createDayLevel();
        }
    }

    private static Level createDayLevel() {
        return new Level(
                LevelType.DAY,
                "Level 1 - Day",
                DAY_THEME,
                0,
                0,
                0,
                Collections.emptyList(),
                0.0,
                0.0
        );
    }

    private static Level createNightLevel() {
        return new Level(
                LevelType.NIGHT,
                "Level 2 - Night",
                NIGHT_THEME,
                6,
                3,
                8,
                NIGHT_GRAVE_IMAGES,
                0.25,
                0.40
        );
    }
}
