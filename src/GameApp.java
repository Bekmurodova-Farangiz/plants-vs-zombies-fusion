import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        ImageView sunStorageIcon = new ImageView(ImageAssets.load("file:src/assets/sun.png"));
        sunStorageIcon.setFitWidth(120);
        sunStorageIcon.setFitHeight(120);
        sunStorageIcon.setPreserveRatio(true);

        Label sunLabel = new Label("200");
        sunLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: yellow;");

        HBox sunBox = new HBox(sunStorageIcon, sunLabel);
        sunBox.setSpacing(6);
        sunBox.setAlignment(Pos.CENTER_LEFT);

        ImageView waterStorageIcon = new ImageView(ImageAssets.load("file:src/assets/water.png"));
        waterStorageIcon.setFitWidth(120);
        waterStorageIcon.setFitHeight(130);
        waterStorageIcon.setPreserveRatio(true);

        Label waterLabel = new Label("100");
        waterLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: lightblue;");

        HBox waterBox = new HBox(waterStorageIcon, waterLabel);
        waterBox.setSpacing(6);
        waterBox.setAlignment(Pos.CENTER_LEFT);

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

        ImageView gameOverImage = new ImageView(ImageAssets.load("file:src/assets/Gameover.png"));
        gameOverImage.setFitWidth(620);
        gameOverImage.setPreserveRatio(true);

        Button restartButton = new Button("Restart");
        restartButton.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #f2d25c;" +
                "-fx-text-fill: #1f1605;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 14 40 14 40;"
        );

        Button gameOverMenuButton = new Button("Menu");
        gameOverMenuButton.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: rgba(255,255,255,0.92);" +
                "-fx-text-fill: #1d1d1d;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 12 34 12 34;"
        );

        VBox gameOverOverlay = new VBox(gameOverImage, restartButton, gameOverMenuButton);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setSpacing(24);
        gameOverOverlay.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        gameOverOverlay.setStyle("-fx-background-color: transparent;");
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setMouseTransparent(true);
        //shifts everything upward visually
        gameOverOverlay.setTranslateY(-190);

        Label winLabel = new Label("YOU WON THE GAME!");
        winLabel.setStyle("-fx-font-size: 42px; -fx-text-fill: gold; -fx-font-weight: bold;");
        winLabel.setVisible(false);

        Button winRestartButton = new Button("Restart");
        winRestartButton.setStyle("-fx-font-size: 18px;");
        winRestartButton.setVisible(false);

        Button winMenuButton = new Button();
        ImageView winMenuIcon = new ImageView(ImageAssets.load("file:src/assets/menu_icon.png"));
        winMenuIcon.setFitWidth(55);
        winMenuIcon.setFitHeight(55);
        winMenuButton.setGraphic(winMenuIcon);
        winMenuButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        winMenuButton.setVisible(false);

        VBox winOverlay = new VBox(winLabel, winRestartButton, winMenuButton);
        winOverlay.setSpacing(18);
        winOverlay.setAlignment(Pos.CENTER);
        winOverlay.setVisible(false);

        ImageView pauseIcon = new ImageView(ImageAssets.load("file:src/assets/pause_icon.png"));
        pauseIcon.setFitWidth(100);
        pauseIcon.setFitHeight(100);

        Button pauseButton = new Button();
        pauseButton.setGraphic(pauseIcon);
        pauseButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        ImageView resumeIcon = new ImageView(ImageAssets.load("file:src/assets/resume_icon.png"));
        resumeIcon.setFitWidth(100);
        resumeIcon.setFitHeight(100);

        Button resumeButton = new Button();
        resumeButton.setGraphic(resumeIcon);
        resumeButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        resumeButton.setVisible(false);

        ImageView menuIcon = new ImageView(ImageAssets.load("file:src/assets/menu_icon.png"));
        menuIcon.setFitWidth(100);
        menuIcon.setFitHeight(100);

        Button mainMenuButton = new Button();
        mainMenuButton.setGraphic(menuIcon);
        mainMenuButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        pauseButton.setFocusTraversable(false);
        resumeButton.setFocusTraversable(false);
        mainMenuButton.setFocusTraversable(false);
        restartButton.setFocusTraversable(false);
        gameOverMenuButton.setFocusTraversable(false);

        PlantCard peaShooterCard = new PlantCard(
                PlantType.PEA_SHOOTER.getIdentifier(),
                PlantType.PEA_SHOOTER.getImagePath(),
                PlantType.PEA_SHOOTER.getSunCost(),
                PlantType.PEA_SHOOTER.getWaterCost()
        );
        PlantCard wallPlantCard = new PlantCard(
                PlantType.WALL_PLANT.getIdentifier(),
                PlantType.WALL_PLANT.getImagePath(),
                PlantType.WALL_PLANT.getSunCost(),
                PlantType.WALL_PLANT.getWaterCost()
        );
        PlantCard sunflowerCard = new PlantCard(
                PlantType.SUNFLOWER.getIdentifier(),
                PlantType.SUNFLOWER.getImagePath(),
                PlantType.SUNFLOWER.getSunCost(),
                PlantType.SUNFLOWER.getWaterCost()
        );
        PlantCard waterPlantCard = new PlantCard(
                PlantType.WATER_PLANT.getIdentifier(),
                PlantType.WATER_PLANT.getImagePath(),
                PlantType.WATER_PLANT.getSunCost(),
                PlantType.WATER_PLANT.getWaterCost()
        );
        PlantCard bombPlantCard = new PlantCard(
                PlantType.BOMB_PLANT.getIdentifier(),
                PlantType.BOMB_PLANT.getImagePath(),
                PlantType.BOMB_PLANT.getSunCost(),
                PlantType.BOMB_PLANT.getWaterCost()
        );

        // Default selected style
        peaShooterCard.setSelected(true);

        // Top bar shown during gameplay
        VBox waveBox = new VBox(waveProgressBar, flagBar);
        waveBox.setSpacing(4);
        waveBox.setStyle("-fx-background-color: rgba(0,0,0,0.35); -fx-padding: 10; -fx-background-radius: 12;");
        waveBox.setPrefWidth(240);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        StackPane pauseResumeSlot = new StackPane(pauseButton, resumeButton);
        pauseResumeSlot.setPrefWidth(50);
        pauseResumeSlot.setPrefHeight(50);
        
        HBox plantPanel = new HBox(
        peaShooterCard,
        wallPlantCard,
        sunflowerCard,
        waterPlantCard,
        bombPlantCard
        );

        plantPanel.setSpacing(10);
        plantPanel.setAlignment(Pos.CENTER_LEFT);

        plantPanel.setStyle(
            "-fx-background-color: rgba(20,20,20,0.45);" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-border-color: rgba(255,255,255,0.18);" +
            "-fx-border-width: 1.5;"
        );

        HBox topBar = new HBox(
                sunBox,
                waterBox,
                plantPanel,
                spacer,
                pauseResumeSlot,
                mainMenuButton
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
        ImageView globalBg = new ImageView(ImageAssets.load("file:src/assets/battlezone.png"));
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
            restartButton.setVisible(true);
            gameOverOverlay.setVisible(false);
            gameOverOverlay.setMouseTransparent(true);
            pauseButton.setVisible(true);
            resumeButton.setVisible(false);
            mainMenuButton.setVisible(true);
            winOverlay.setVisible(false);

            // Default selected plant when game starts
            board.setSelectedPlantType(PlantType.PEA_SHOOTER);
            peaShooterCard.setSelected(true);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
            bombPlantCard.setSelected(false);
            // Connect plant selection buttons to THIS board
            setupPlantButtons(boardRef, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard);

            // Restart should also create a fresh new board
            restartButton.setOnAction(e2 -> {
                GameBoard newBoard = new GameBoard();
                boardRef[0] = newBoard;

                restartButton.setVisible(true);
                gameOverOverlay.setVisible(false);
                gameOverOverlay.setMouseTransparent(true);
                pauseButton.setVisible(true);
                resumeButton.setVisible(false);
                mainMenuButton.setVisible(true);
                winOverlay.setVisible(false);

                newBoard.setSelectedPlantType(PlantType.PEA_SHOOTER);
                peaShooterCard.setSelected(true);
                wallPlantCard.setSelected(false);
                sunflowerCard.setSelected(false);
                waterPlantCard.setSelected(false);
                bombPlantCard.setSelected(false);

                setupPlantButtons(boardRef, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard);
                

                VBox newGameLayout = buildGameLayout(
                        newBoard,
                        topBar,
                        waveBox,
                        gameOverOverlay,
                        pauseButton,
                        resumeButton,
                        mainMenuButton,
                        winOverlay,
                        DESIGN_WIDTH,
                        DESIGN_HEIGHT
                );

                contentLayer.getChildren().setAll(newGameLayout);
            });

            VBox gameLayout = buildGameLayout(
                    board,
                    topBar,
                    waveBox,
                    gameOverOverlay,
                    pauseButton,
                    resumeButton,
                    mainMenuButton,
                    winOverlay,
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
                    GameState gameState = boardRef[0].getGameState();
                    boolean isLost = gameState == GameState.LOST;
                    boolean isWon = gameState == GameState.WON;
                    boolean isPaused = gameState == GameState.PAUSED;

                    flag1.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (currentWave >= 1 ? "red;" : "gray;"));
                    flag2.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (currentWave >= 2 ? "red;" : "gray;"));
                    flag3.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (currentWave >= 3 ? "red;" : "gray;"));
                    sunLabel.setText("" + boardRef[0].getSunPoints());
                    waterLabel.setText("" + boardRef[0].getWaterPoints());
                    peaShooterCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.PEA_SHOOTER) > 0);
                    wallPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.WALL_PLANT) > 0);
                    sunflowerCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.SUNFLOWER) > 0);
                    waterPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.WATER_PLANT) > 0);
                    bombPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.BOMB_PLANT) > 0);
                    waveProgressBar.setProgress(boardRef[0].getWaveProgress());

                    gameOverOverlay.setVisible(isLost);
                    gameOverOverlay.setMouseTransparent(!isLost);

                    winOverlay.setVisible(isWon);
                    winLabel.setVisible(isWon);
                    winRestartButton.setVisible(isWon);
                    winMenuButton.setVisible(isWon);

                    pauseButton.setVisible(gameState == GameState.RUNNING);
                    resumeButton.setVisible(isPaused);
                    mainMenuButton.setVisible(!isLost && !isWon);
                }
                pauseButton.setOnAction(e -> {
                    if (boardRef[0] != null && boardRef[0].getGameState() == GameState.RUNNING) {
                        boardRef[0].pauseGame();
                        pauseButton.setVisible(false);
                        resumeButton.setVisible(true);
                    }
                });

                resumeButton.setOnAction(e -> {
                    if (boardRef[0] != null && boardRef[0].getGameState() == GameState.PAUSED) {
                        boardRef[0].resumeGame();
                        pauseButton.setVisible(true);
                        resumeButton.setVisible(false);
                    }
                });
                mainMenuButton.setOnAction(e -> {
                    if (boardRef[0] != null) {
                        boardRef[0] = null;
                    }

                    pauseButton.setVisible(true);
                    resumeButton.setVisible(false);
                    mainMenuButton.setVisible(true);
                    gameOverOverlay.setVisible(false);
                    gameOverOverlay.setMouseTransparent(true);

                    contentLayer.getChildren().setAll(menu);
                });
                winRestartButton.setOnAction(e -> {
                    if (restartButton.getOnAction() != null) {
                        restartButton.fire();
                    }
                });
                winMenuButton.setOnAction(e -> {
                    if (boardRef[0] != null) {
                        boardRef[0] = null;
                    }

                    pauseButton.setVisible(true);
                    resumeButton.setVisible(false);
                    mainMenuButton.setVisible(true);
                    winOverlay.setVisible(false);
                    gameOverOverlay.setVisible(false);
                    gameOverOverlay.setMouseTransparent(true);

                    contentLayer.getChildren().setAll(menu);
                });
            }
        };
        timer.start();

        gameOverMenuButton.setOnAction(e -> {
            if (boardRef[0] != null) {
                boardRef[0] = null;
            }

            pauseButton.setVisible(true);
            resumeButton.setVisible(false);
            mainMenuButton.setVisible(true);
            gameOverOverlay.setVisible(false);
            gameOverOverlay.setMouseTransparent(true);

            contentLayer.getChildren().setAll(menu);
        });

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
        stage.setFullScreen(true);//opens the game in fullscreen
        stage.setFullScreenExitHint(""); //removes the default JavaFX messag
        stage.show();
    }

    /**
     * Builds the visible gameplay layout:
     * top bar + battlefield pane + overlays
     */
    private VBox buildGameLayout(
            GameBoard board,
            HBox topBar,
            VBox waveBox,
            VBox gameOverOverlay,
            Button pauseButton,
            Button resumeButton,
            Button mainMenuButton,
            VBox winOverlay,
            double designWidth,
            double designHeight
    ) {
        GameBoardView boardView = new GameBoardView();
        boardView.setCellClickHandler((row, col) -> {
            Plant plant = board.placePlantAt(row, col);

            if (plant != null) {
                boardView.addPlantToCell(row, col, plant);
            }
        });
        board.setPlantRemovedHandler(boardView::removePlantFromCell);
            
        Pane battlefieldPane = new Pane();
        battlefieldPane.setPrefSize(1600, 700);

        boardView.setLayoutX(40);
        boardView.setLayoutY(-15);

        board.setLayoutX(40);
        board.setLayoutY(-15);
        board.setPickOnBounds(false);

        battlefieldPane.getChildren().addAll(boardView, board, waveBox, winOverlay, gameOverOverlay);

        waveBox.setLayoutX(1200);
        waveBox.setLayoutY(640);

        winOverlay.setLayoutX(600);
        winOverlay.setLayoutY(220);
        gameOverOverlay.relocate(0, 0);

        VBox layout = new VBox(topBar, battlefieldPane);
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
            PlantCard waterPlantCard,
            PlantCard bombPlantCard
    ) {
        peaShooterCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.PEA_SHOOTER);
            }
            peaShooterCard.setSelected(true);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
            bombPlantCard.setSelected(false);
        });

        wallPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.WALL_PLANT);
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(true);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
            bombPlantCard.setSelected(false);
        });

        sunflowerCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.SUNFLOWER);
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(true);
            waterPlantCard.setSelected(false);
            bombPlantCard.setSelected(false);
        });

        waterPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.WATER_PLANT);
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(true);
            bombPlantCard.setSelected(false);
        });

        bombPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.BOMB_PLANT);
            }
            peaShooterCard.setSelected(false);
            wallPlantCard.setSelected(false);
            sunflowerCard.setSelected(false);
            waterPlantCard.setSelected(false);
            bombPlantCard.setSelected(true);
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
