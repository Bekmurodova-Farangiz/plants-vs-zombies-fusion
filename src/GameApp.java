import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
        GameBoard board = new GameBoard();
        StackPane root = new StackPane();
        root.getChildren().add(board);
        
        Scene scene = new Scene(root, 1000, 700);

        stage.setTitle("Plantz Vs Zombiie Fusion");
        stage.setScene(scene);
        stage.show();
    }
}