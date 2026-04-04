import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsPage extends VBox {

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private Button backButton;

    public SettingsPage() {

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 30px;");

        easyButton = new Button("Easy");
        mediumButton = new Button("Medium");
        hardButton = new Button("Hard");
        backButton = new Button("Back");

        easyButton.setStyle("-fx-font-size: 18px;");
        mediumButton.setStyle("-fx-font-size: 18px;");
        hardButton.setStyle("-fx-font-size: 18px;");
        backButton.setStyle("-fx-font-size: 18px;");

        setAlignment(Pos.CENTER);
        setSpacing(15);

        getChildren().addAll(title, easyButton, mediumButton, hardButton, backButton);
    }

    public Button getEasyButton() {
        return easyButton;
    }

    public Button getMediumButton() {
        return mediumButton;
    }

    public Button getHardButton() {
        return hardButton;
    }

    public Button getBackButton() {
        return backButton;
    }
}