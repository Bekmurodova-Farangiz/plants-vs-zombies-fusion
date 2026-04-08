import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
        // Fixed "design size" of the whole game world.
        // Everything will be built for this size, then scaled together.
        final double DESIGN_WIDTH = 1600;
        final double DESIGN_HEIGHT = 900;

        // Pages
        MenuPage menu = new MenuPage();
        SettingsPage settingsPage = new SettingsPage();

        // We keep the current GameBoard inside an array so inner lambdas can modify it.
        // This is a common trick in Java when lambdas need a mutable reference.
        final GameBoard[] boardRef = {null};

        // Top HUD labels
        Label sunLabel = new Label("Sun: 200");
        sunLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black;");

        Label selectedPlantLabel = new Label("Selected: PeaShooter");
        selectedPlantLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

        // Game-over text
        Label gameOverLabel = new Label("");
        gameOverLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        // Restart button
        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-size: 16px;");
        restartButton.setVisible(false);

        // Plant selection buttons
        Button peaShooterButton = new Button("PeaShooter");
        Button wallPlantButton = new Button("WallPlant");
        Button sunflowerButton = new Button("Sunflower");

        // Default selected style
        peaShooterButton.setStyle("-fx-background-color: lightgreen;");

        // Top bar shown during gameplay
        HBox topBar = new HBox(
                sunLabel,
                selectedPlantLabel,
                peaShooterButton,
                wallPlantButton,
                sunflowerButton
        );
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(20);
        topBar.setPadding(new Insets(10, 0, 0, 20));

        // Root of whole scene
        StackPane root = new StackPane();

        // gameSurface = fixed-size game world
        Pane gameSurface = new Pane();
        gameSurface.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        // Background image for the whole game world
        ImageView globalBg = new ImageView(new Image("file:src/assets/battlezone.png"));
        globalBg.setFitWidth(DESIGN_WIDTH);
        globalBg.setFitHeight(DESIGN_HEIGHT);

        // contentLayer = what changes (menu, settings, game layout)
        Pane contentLayer = new Pane();
        contentLayer.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        // Put background first, then changing content on top
        gameSurface.getChildren().addAll(globalBg, contentLayer);

        // Group lets us scale the whole game world together
        Group gameGroup = new Group(gameSurface);
        root.getChildren().add(gameGroup);

        // Menu page should fill the game world so its centered alignment works properly
        menu.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        settingsPage.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        // Show menu first
        contentLayer.getChildren().setAll(menu);

        // Start button -> create a fresh board and show the game page
        menu.getStartButton().setOnAction(e -> {
            GameBoard board = new GameBoard();
            boardRef[0] = board;

            // Reset UI state for a new game
            gameOverLabel.setText("");
            restartButton.setVisible(false);

            // Default selected plant when game starts
            board.setSelectedPlantType("PeaShooter");
            peaShooterButton.setStyle("-fx-background-color: lightgreen;");
            wallPlantButton.setStyle("");
            sunflowerButton.setStyle("");

            // Connect plant selection buttons to THIS board
            setupPlantButtons(board, peaShooterButton, wallPlantButton, sunflowerButton);

            // Restart should also create a fresh new board
            restartButton.setOnAction(e2 -> {
                GameBoard newBoard = new GameBoard();
                boardRef[0] = newBoard;

                gameOverLabel.setText("");
                restartButton.setVisible(false);

                newBoard.setSelectedPlantType("PeaShooter");
                peaShooterButton.setStyle("-fx-background-color: lightgreen;");
                wallPlantButton.setStyle("");
                sunflowerButton.setStyle("");

                setupPlantButtons(newBoard, peaShooterButton, wallPlantButton, sunflowerButton);

                VBox newGameLayout = buildGameLayout(
                        newBoard,
                        topBar,
                        gameOverLabel,
                        restartButton,
                        DESIGN_WIDTH,
                        DESIGN_HEIGHT
                );

                contentLayer.getChildren().setAll(newGameLayout);
            });

            VBox gameLayout = buildGameLayout(
                    board,
                    topBar,
                    gameOverLabel,
                    restartButton,
                    DESIGN_WIDTH,
                    DESIGN_HEIGHT
            );

            contentLayer.getChildren().setAll(gameLayout);
        });

        // Settings page
        menu.getSettingsButton().setOnAction(e -> {
            contentLayer.getChildren().setAll(settingsPage);
        });

        // Back from settings to menu
        settingsPage.getBackButton().setOnAction(e -> {
            contentLayer.getChildren().setAll(menu);
        });

        // Quit button closes app
        menu.getQuitButton().setOnAction(e -> {
            stage.close();
        });

        // Live UI updater
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

        // Window size
        Scene scene = new Scene(root, 1400, 900);

        // Scale the whole game world together whenever width changes
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateScale(scene, gameGroup, DESIGN_WIDTH, DESIGN_HEIGHT);
        });

        // Scale the whole game world together whenever height changes
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateScale(scene, gameGroup, DESIGN_WIDTH, DESIGN_HEIGHT);
        });

        // Initial scale setup
        updateScale(scene, gameGroup, DESIGN_WIDTH, DESIGN_HEIGHT);

        stage.setTitle("Plantz Vs Zombiie Fusion");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Builds the visible gameplay layout:
     * top bar + battlefield pane + game over label + restart button
     */
    private VBox buildGameLayout(
            GameBoard board,
            HBox topBar,
            Label gameOverLabel,
            Button restartButton,
            double designWidth,
            double designHeight
    ) {
        // battlefieldPane holds the board in exact coordinates over the background area
        Pane battlefieldPane = new Pane();
        battlefieldPane.setPrefSize(1600, 700);

        // These coordinates place the board over the playable tile area.
        // You can fine-tune them later if needed.
        board.setLayoutX(40);
        board.setLayoutY(55);

        battlefieldPane.getChildren().add(board);

        VBox layout = new VBox(topBar, battlefieldPane, gameOverLabel, restartButton);
        layout.setSpacing(10);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(0, 0, 0, 0));

        return layout;
    }

    /**
     * Connects the plant-selection buttons to the given board.
     * This is separated into a method so we can reuse it for Start and Restart.
     */
    private void setupPlantButtons(
            GameBoard board,
            Button peaShooterButton,
            Button wallPlantButton,
            Button sunflowerButton
    ) {
        peaShooterButton.setOnAction(e -> {
            board.setSelectedPlantType("PeaShooter");
            peaShooterButton.setStyle("-fx-background-color: lightgreen;");
            wallPlantButton.setStyle("");
            sunflowerButton.setStyle("");
        });

        wallPlantButton.setOnAction(e -> {
            board.setSelectedPlantType("WallPlant");
            wallPlantButton.setStyle("-fx-background-color: lightgreen;");
            peaShooterButton.setStyle("");
            sunflowerButton.setStyle("");
        });

        sunflowerButton.setOnAction(e -> {
            board.setSelectedPlantType("Sunflower");
            sunflowerButton.setStyle("-fx-background-color: lightgreen;");
            peaShooterButton.setStyle("");
            wallPlantButton.setStyle("");
        });
    }

    /**
     * Scales and centers the whole game world together.
     * This keeps background, board, plants, zombies, and UI aligned.
     */
    private void updateScale(Scene scene, Group gameGroup, double designWidth, double designHeight) {
        double scaleX = scene.getWidth() / designWidth;
        double scaleY = scene.getHeight() / designHeight;

        // Use the smaller scale so the whole game fits without distortion
        double scale = Math.min(scaleX, scaleY);

        gameGroup.setScaleX(scale);
        gameGroup.setScaleY(scale);

        // Center the scaled game world in the window
        gameGroup.setLayoutX((scene.getWidth() - designWidth * scale) / 2);
        gameGroup.setLayoutY((scene.getHeight() - designHeight * scale) / 2);
    }
}