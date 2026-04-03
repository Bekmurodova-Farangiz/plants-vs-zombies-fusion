import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
        GameBoard board = new GameBoard();
        StackPane root = new StackPane();

        Label sunLabel = new Label("Sun: 200");
        Label gameOverLabel = new Label("");
        gameOverLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: red;");
        sunLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: yellow;");

        root.getChildren().addAll(board, sunLabel, gameOverLabel);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                sunLabel.setText("Sun: " + board.getSunPoints());
                if (board.isGameOver()) {
                    gameOverLabel.setText("GAME OVER");
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