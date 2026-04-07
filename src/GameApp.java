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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.Pane;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {

        final double DESIGN_WIDTH = 1600;
        final double DESIGN_HEIGHT = 900;

        MenuPage menu = new MenuPage();
        SettingsPage settingsPage = new SettingsPage();

        final GameBoard[] boardRef = {null};

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
        Button sunflowerButton = new Button("Sunflower");

        peaShooterButton.setStyle("-fx-background-color: lightgreen;");

        HBox topBar = new HBox(sunLabel, selectedPlantLabel, peaShooterButton, wallPlantButton, sunflowerButton);
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(20);

        StackPane root = new StackPane();

        Pane gameSurface = new Pane();
        gameSurface.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        Group gameGroup = new Group(gameSurface);
        root.getChildren().add(gameGroup);

        menu.getStartButton().setOnAction(e -> {
            GameBoard board = new GameBoard();
            boardRef[0] = board;

            peaShooterButton.setOnAction(e2 -> {
                board.setSelectedPlantType("PeaShooter");
                peaShooterButton.setStyle("-fx-background-color: lightgreen;");
                wallPlantButton.setStyle("");
            });

            wallPlantButton.setOnAction(e2 -> {
                board.setSelectedPlantType("WallPlant");
                wallPlantButton.setStyle("-fx-background-color: lightgreen;");
                peaShooterButton.setStyle("");
            });
            sunflowerButton.setOnAction(e2 -> {
                board.setSelectedPlantType("Sunflower");

                sunflowerButton.setStyle("-fx-background-color: lightgreen;");
                peaShooterButton.setStyle("");
                wallPlantButton.setStyle("");
            });

            VBox layout = new VBox(topBar, board, gameOverLabel, restartButton);
            layout.setSpacing(10);
            layout.setAlignment(Pos.TOP_LEFT);
            layout.setPadding(new Insets(120, 0, 0, 160));

            root.getChildren().clear();
            root.getChildren().add(layout);
        });

        menu.getSettingsButton().setOnAction(e -> {
            root.getChildren().clear();
            root.getChildren().add(settingsPage);
        });

        settingsPage.getBackButton().setOnAction(e -> {
            root.getChildren().clear();
            root.getChildren().add(menu);
        });

        menu.getQuitButton().setOnAction(e -> {
            stage.close();
        });

        root.getChildren().add(menu);

        BackgroundSize backgroundSize = new BackgroundSize(
                100, 100, true, true, false, true
        );

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("file:src/assets/battlezone.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        root.setBackground(new Background(backgroundImage));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (boardRef[0] != null) {
                    sunLabel.setText("Sun: " + boardRef[0].getSunPoints());
                    selectedPlantLabel.setText("Selected: " + boardRef[0].getSelectedPlantType());

                    if (boardRef[0].isGameOver()) {
                        gameOverLabel.setText("GAME OVER");
                        restartButton.setVisible(true);
                    }
                }
            }
        };

        timer.start();

        Scene scene = new Scene(root, 1400, 900);

        stage.setTitle("Plantz Vs Zombiie Fusion");
        stage.setScene(scene);
        // stage.setFullScreen(true);
        stage.show();
    }
}
