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
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        Label nameLabel = new Label(plantType);
        costLabel = new Label("Sun: " + sunCost);
        waterCostLabel = new Label("Water: " + waterCost);
        cooldownLabel = new Label("");

        setAlignment(Pos.CENTER);
        setSpacing(4);
        setStyle("-fx-border-color: black; -fx-padding: 6; -fx-background-color: rgba(255,255,255,0.75);");

        getChildren().addAll(imageView, nameLabel, costLabel, waterCostLabel, cooldownLabel);
    }

    public String getPlantType() {
        return plantType;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStyle("-fx-border-color: green; -fx-border-width: 3; -fx-padding: 6; -fx-background-color: rgba(255,255,255,0.9);");
        } else {
            setStyle("-fx-border-color: black; -fx-padding: 6; -fx-background-color: rgba(255,255,255,0.75);");
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
