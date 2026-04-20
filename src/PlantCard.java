import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PlantCard extends VBox {

    private static final String SUN_ICON_PATH = "/assets/sun.png";
    private static final String WATER_ICON_PATH = "/assets/water.png";
    private static final double PLANT_IMAGE_SIZE = 60;
    private static final double COST_ICON_SIZE = 26;

    private String plantType;
    private Label costLabel;
    private Label waterCostLabel;
    private Label cooldownLabel;

    public PlantCard(String plantType, String imagePath, int sunCost, int waterCost) {
        this.plantType = plantType;

        ImageView imageView = new ImageView(ImageAssets.load(imagePath));
        imageView.setFitWidth(PLANT_IMAGE_SIZE);
        imageView.setFitHeight(PLANT_IMAGE_SIZE);
        imageView.setPreserveRatio(true);

        ImageView sunIconView = createCostIcon(SUN_ICON_PATH);
        ImageView waterIconView = createCostIcon(WATER_ICON_PATH);

        costLabel = createCostLabel(sunCost);
        waterCostLabel = createCostLabel(waterCost);

        HBox sunCostBox = new HBox(3, sunIconView, costLabel);
        sunCostBox.setAlignment(Pos.CENTER);

        HBox waterCostBox = new HBox(3, waterIconView, waterCostLabel);
        waterCostBox.setAlignment(Pos.CENTER);

        VBox costColumn = new VBox(2, sunCostBox, waterCostBox);
        costColumn.setAlignment(Pos.CENTER);

        cooldownLabel = new Label("");
        cooldownLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        cooldownLabel.setMinHeight(14);
        cooldownLabel.setAlignment(Pos.CENTER);

        setAlignment(Pos.CENTER);
        setSpacing(2);
        setStyle("-fx-border-color: transparent; -fx-padding: 3; -fx-background-color: transparent;");

        getChildren().addAll(imageView, costColumn, cooldownLabel);
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

    private ImageView createCostIcon(String imagePath) {
        ImageView iconView = new ImageView(ImageAssets.load(imagePath));
        iconView.setFitWidth(COST_ICON_SIZE);
        iconView.setFitHeight(COST_ICON_SIZE);
        iconView.setPreserveRatio(true);
        return iconView;
    }

    private Label createCostLabel(int cost) {
        Label label = new Label(String.valueOf(cost));
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-font-weight: bold;");
        return label;
    }
}
