import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsPage extends VBox {

    private static final double CONTROL_WIDTH = 220;

    private final ComboBox<Difficulty> difficultySelector;
    private final Button backButton;

    public SettingsPage() {
        Label titleLabel = new Label("Settings");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");

        Label difficultyLabel = new Label("Difficulty");
        difficultyLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: white;");

        difficultySelector = new ComboBox<>();
        difficultySelector.getItems().addAll(Difficulty.values());
        difficultySelector.getSelectionModel().select(Difficulty.MEDIUM);
        difficultySelector.setPrefWidth(CONTROL_WIDTH);
        difficultySelector.setMaxWidth(CONTROL_WIDTH);
        difficultySelector.setStyle("-fx-font-size: 18px;");
        difficultySelector.setFocusTraversable(false);

        backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 20px;");
        backButton.setPrefWidth(CONTROL_WIDTH);
        backButton.setFocusTraversable(false);

        VBox difficultyBox = new VBox(difficultyLabel, difficultySelector);
        difficultyBox.setAlignment(Pos.CENTER);
        difficultyBox.setSpacing(8);

        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPrefSize(1600, 900);
        setMaxSize(1600, 900);

        getChildren().addAll(titleLabel, difficultyBox, backButton);
    }

    public Difficulty getSelectedDifficulty() {
        Difficulty selectedDifficulty = difficultySelector.getValue();
        return selectedDifficulty != null ? selectedDifficulty : Difficulty.MEDIUM;
    }

    public Button getBackButton() {
        return backButton;
    }
}
