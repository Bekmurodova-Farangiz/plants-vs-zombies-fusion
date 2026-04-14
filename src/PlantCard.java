import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PlantCard extends VBox {

    private String plantType;
    private Label costLabel;
    private Label waterCostLabel;
    private Label cooldownLabel;

    public PlantCard(String plantType, String imagePath, int sunCost, int waterCost) {
        this.plantType = plantType;

        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);

        cooldownLabel = new Label("");

        setAlignment(Pos.CENTER);
        setSpacing(2);
        setStyle("-fx-border-color: transparent; -fx-padding: 3; -fx-background-color: transparent;");

        getChildren().addAll(imageView, cooldownLabel);
    }

    public String getPlantType() {
        return plantType;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStyle("-fx-border-color: lime; -fx-border-width: 3; -fx-padding: 3; -fx-background-color: transparent;");
        } else {
            setStyle("-fx-border-color: transparent; -fx-padding: 3; -fx-background-color: transparent;");
        }
    }

    public void setOnCooldown(boolean onCooldown) {
        if (onCooldown) {
            setOpacity(0.5);
            cooldownLabel.setText("Cooling...");
        } else {
            setOpacity(1.0);
            cooldownLabel.setText("");
        }
    }
}
