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
import javafx.scene.control.ProgressBar;

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

        Label waterLabel = new Label("Water: 100");
        waterLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: blue;");

        Label selectedPlantLabel = new Label("Selected: PeaShooter");
        selectedPlantLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        
        Label waveLabel = new Label("Wave: 1/3");
        waveLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: darkred;");

        ProgressBar waveProgressBar = new ProgressBar(0);
        waveProgressBar.setPrefWidth(180);

        Label flag1 = new Label("⚑");
        Label flag2 = new Label("⚑");
        Label flag3 = new Label("⚑");

        flag1.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");
        flag2.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");
        flag3.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");

        HBox flagBar = new HBox(flag1, flag2, flag3);
        flagBar.setSpacing(55);
        flagBar.setAlignment(Pos.CENTER_LEFT);

        // Game-over text
        Label gameOverLabel = new Label("");
        gameOverLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");

        // Restart button
        Button restartButton = new Button("Restart");
        restartButton.setStyle("-fx-font-size: 16px;");
        restartButton.setVisible(false);

        Button pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-font-size: 16px;");

        Button resumeButton = new Button("Resume");
        resumeButton.setStyle("-fx-font-size: 16px;");
        resumeButton.setVisible(false);

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setStyle("-fx-font-size: 16px;");

        PlantCard peaShooterCard = new PlantCard("PeaShooter", "file:src/assets/peashooter.png", 50, 20);
        PlantCard wallPlantCard = new PlantCard("WallPlant", "file:src/assets/wallplant.png", 50, 40);
        PlantCard sunflowerCard = new PlantCard("Sunflower", "file:src/assets/sunflower.png", 50, 10);
        PlantCard waterPlantCard = new PlantCard("WaterPlant", "file:src/assets/waterplant1.png", 50, 0);

        // Default selected style
        peaShooterCard.setSelected(true);

        // Top bar shown during gameplay
        VBox waveBox = new VBox(waveLabel, waveProgressBar, flagBar);
        waveBox.setSpacing(4);

        HBox topBar = new HBox(
                sunLabel,
                waterLabel,
                selectedPlantLabel,
                waveBox,
                peaShooterCard,
                wallPlantCard,
                sunflowerCard,
                waterPlantCard
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
            peaShooterCard.setSelected(true);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
            // Connect plant selection buttons to THIS board
            setupPlantButtons(boardRef, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard);

            // Restart should also create a fresh new board
            restartButton.setOnAction(e2 -> {
                GameBoard newBoard = new GameBoard();
                boardRef[0] = newBoard;

                gameOverLabel.setText("");
                restartButton.setVisible(false);

                newBoard.setSelectedPlantType("PeaShooter");
                peaShooterCard.setSelected(true);
                wallPlantCard.setSelected(false);
                sunflowerCard.setSelected(false);
                waterPlantCard.setSelected(false);

                setupPlantButtons(boardRef, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard);
                

                VBox newGameLayout = buildGameLayout(
                        newBoard,
                        topBar,
                        gameOverLabel,
                        restartButton,
                        pauseButton,
                        resumeButton,
                        mainMenuButton,
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
                    pauseButton,
                    resumeButton,
                    mainMenuButton,
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
                    int currentWave = boardRef[0].getCurrentWave();

                    flag1.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (currentWave >= 1 ? "red;" : "gray;"));
                    flag2.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (currentWave >= 2 ? "red;" : "gray;"));
                    flag3.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (currentWave >= 3 ? "red;" : "gray;"));
                    sunLabel.setText("Sun: " + boardRef[0].getSunPoints());
                    waterLabel.setText("Water: " + boardRef[0].getWaterPoints());
                    selectedPlantLabel.setText("Selected: " + boardRef[0].getSelectedPlantType());
                    peaShooterCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis("PeaShooter") > 0);
                    wallPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis("WallPlant") > 0);
                    sunflowerCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis("Sunflower") > 0);
                    waterPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis("WaterPlant") > 0);
                    waveLabel.setText("Wave: " + boardRef[0].getCurrentWave() + "/" + boardRef[0].getTotalWaves());
                    waveProgressBar.setProgress(boardRef[0].getWaveProgress());

                    if (boardRef[0].isGameOver()) {
                        gameOverLabel.setText("GAME OVER");
                        restartButton.setVisible(true);
                    }
                }
                pauseButton.setOnAction(e -> {
                    if (boardRef[0] != null) {
                        boardRef[0].pauseGame();
                        pauseButton.setVisible(false);
                        resumeButton.setVisible(true);
                    }
                });

                resumeButton.setOnAction(e -> {
                    if (boardRef[0] != null) {
                        boardRef[0].resumeGame();
                        pauseButton.setVisible(true);
                        resumeButton.setVisible(false);
                    }
                });
                mainMenuButton.setOnAction(e -> {
                    if (boardRef[0] != null) {
                        boardRef[0].setPaused(false);
                        boardRef[0] = null;
                    }

                    pauseButton.setVisible(true);
                    resumeButton.setVisible(false);
                    restartButton.setVisible(false);
                    gameOverLabel.setText("");

                    contentLayer.getChildren().setAll(menu);
                });
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
            Button pauseButton,
            Button resumeButton,
            Button mainMenuButton,
            double designWidth,
            double designHeight
    ) {

        Pane battlefieldPane = new Pane();
        battlefieldPane.setPrefSize(1600, 700);

        board.setLayoutX(40);
        board.setLayoutY(55);

        battlefieldPane.getChildren().add(board);

        HBox controlBar = new HBox(pauseButton, resumeButton, restartButton, mainMenuButton);
        controlBar.setSpacing(10);
        controlBar.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(topBar, battlefieldPane, gameOverLabel, controlBar);
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
            GameBoard[] boardRef,
            PlantCard peaShooterCard,
            PlantCard wallPlantCard,
            PlantCard sunflowerCard,
            PlantCard waterPlantCard
    ) {
        peaShooterCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType("PeaShooter");
            }
            peaShooterCard.setSelected(true);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
        });

        wallPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType("WallPlant");
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(true);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
        });

        sunflowerCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType("Sunflower");
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(true);
            waterPlantCard.setSelected(false);
        });

        waterPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType("WaterPlant");
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(true);
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