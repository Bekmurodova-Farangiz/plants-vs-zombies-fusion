import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MenuPage extends VBox {

    private Button startButton;
    private Button nightModeButton;
    private Button settingsButton;
    private Button quitButton;

    public MenuPage() {
        Label titleLabel = new Label("Plantz Vs Zombiie Fusion");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: white;");

        startButton = new Button("Start Day");
        nightModeButton = new Button("Night Mode");
        settingsButton = new Button("Settings");
        quitButton = new Button("Quit");

        startButton.setStyle("-fx-font-size: 20px;");
        nightModeButton.setStyle("-fx-font-size: 20px;");
        settingsButton.setStyle("-fx-font-size: 20px;");
        quitButton.setStyle("-fx-font-size: 20px;");

        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPrefSize(1600, 900);
        setMaxSize(1600, 900); // giving it the full game-surface size

        getChildren().addAll(titleLabel, startButton, nightModeButton, settingsButton, quitButton);
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }

    public Button getNightModeButton() {
        return nightModeButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }
}
