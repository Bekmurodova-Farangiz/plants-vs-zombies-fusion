import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.control.Button;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
    
        GameBoard board = new GameBoard();
        Label sunLabel = new Label("Sun: 200");
        Label selectedPlantLabel = new Label("Selected: PeaShooter");
        selectedPlantLabel.setStyle("-fx-font-size: 16px;");
        sunLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");

        Label gameOverLabel = new Label("");
        gameOverLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-size: 16px;");
        restartButton.setVisible(false);

        restartButton.setOnAction(e -> {
            stage.close();
            new GameApp().start(new Stage());
        });
        Button peaShooterButton = new Button("PeaShooter");
        Button wallPlantButton = new Button("WallPlant");

        peaShooterButton.setOnAction(e -> {
            board.setSelectedPlantType("PeaShooter");

            peaShooterButton.setStyle("-fx-background-color: lightgreen;");
            wallPlantButton.setStyle("");
        });

        wallPlantButton.setOnAction(e -> {
            board.setSelectedPlantType("WallPlant");

            wallPlantButton.setStyle("-fx-background-color: lightgreen;");
            peaShooterButton.setStyle("");
        });
        peaShooterButton.setStyle("-fx-background-color: lightgreen;");

        HBox topBar = new HBox(sunLabel, selectedPlantLabel, peaShooterButton, wallPlantButton);
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(20);

        StackPane root = new StackPane();

        VBox layout = new VBox(topBar, board, gameOverLabel, restartButton);
        layout.setSpacing(10);
        layout.setAlignment(Pos.TOP_CENTER);

        root.getChildren().add(layout);
        BackgroundSize backgroundSize = new BackgroundSize(
        100, 100, true, true, true, false
);

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:src/assets/backgound.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        root.setBackground(new Background(backgroundImage));
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                sunLabel.setText("Sun: " + board.getSunPoints());
                selectedPlantLabel.setText("Selected: " + board.getSelectedPlantType());

                if (board.isGameOver()) {
                    gameOverLabel.setText("GAME OVER");
                    restartButton.setVisible(true);
                }
            }
        };
           
        timer.start();
        
        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Plantz Vs Zombiie Fusion");
        stage.setScene(scene);
        stage.show();
    }
}