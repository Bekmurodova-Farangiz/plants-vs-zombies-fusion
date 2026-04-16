import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MenuPage extends VBox {

    private static final double MENU_BUTTON_WIDTH = 200;
    private static final double MENU_BUTTON_SHADOW_RADIUS = 20;

    private final Button dayModeButton;
    private final Button nightModeButton;
    private final Button settingsButton;
    private final Button quitButton;

    public MenuPage() {
        Label titleLabel = new Label("Plantz Vs Zombiie Fusion");
        titleLabel.setStyle(
                "-fx-font-size: 40px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: linear-gradient(to bottom, #fff8c6, #f4d95e);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.65), 16, 0.35, 0, 5);"
        );

        dayModeButton = createMenuButton("file:src/assets/menu_buttons/day_mode_button.png", "Day Mode");
        nightModeButton = createMenuButton("file:src/assets/menu_buttons/night_mode_button.png", "Night Mode");
        settingsButton = createMenuButton("file:src/assets/menu_buttons/settings_button.png", "Settings");
        quitButton = createMenuButton("file:src/assets/menu_buttons/quit_button.png", "Quit");

        setAlignment(Pos.CENTER);
        setSpacing(18);
        setPrefSize(1600, 900);
        setMaxSize(1600, 900); // giving it the full game-surface size

        getChildren().addAll(titleLabel, dayModeButton, nightModeButton, settingsButton, quitButton);
    }

    private Button createMenuButton(String imagePath, String accessibleText) {
        ImageView imageView = new ImageView(ImageAssets.load(imagePath));
        imageView.setFitWidth(MENU_BUTTON_WIDTH);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        DropShadow defaultShadow = new DropShadow(MENU_BUTTON_SHADOW_RADIUS, Color.rgb(0, 0, 0, 0.45));
        defaultShadow.setOffsetY(8);

        DropShadow hoverShadow = new DropShadow(MENU_BUTTON_SHADOW_RADIUS + 4, Color.rgb(0, 0, 0, 0.55));
        hoverShadow.setOffsetY(12);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setAccessibleText(accessibleText);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        button.setPrefWidth(MENU_BUTTON_WIDTH);
        button.setMaxWidth(MENU_BUTTON_WIDTH);
        button.setFocusTraversable(false);
        button.setEffect(defaultShadow);

        button.setOnMouseEntered(event -> {
            button.setScaleX(1.03);
            button.setScaleY(1.03);
            button.setEffect(hoverShadow);
        });
        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setTranslateY(0);
            button.setEffect(defaultShadow);
        });
        button.setOnMousePressed(event -> {
            button.setScaleX(0.98);
            button.setScaleY(0.98);
            button.setTranslateY(4);
        });
        button.setOnMouseReleased(event -> {
            boolean hovering = button.isHover();
            button.setScaleX(hovering ? 1.03 : 1.0);
            button.setScaleY(hovering ? 1.03 : 1.0);
            button.setTranslateY(0);
            button.setEffect(hovering ? hoverShadow : defaultShadow);
        });

        return button;
    }

    public Button getDayModeButton() {
        return dayModeButton;
    }

    public Button getNightModeButton() {
        return nightModeButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}
