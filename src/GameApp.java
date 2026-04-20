import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameApp extends Application {

    private static final String CAGE_CARD_ID = "CageAbility";
    private static final String CAGE_IMAGE_PATH = "/assets/cage.png";
    private static final int CAGE_SUN_COST = 50;
    private static final int CAGE_WATER_COST = 50;

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
        final LevelType[] activeLevelTypeRef = {LevelType.DAY};
        final Difficulty[] activeDifficultyRef = {Difficulty.MEDIUM};
        final Level menuLevel = Levels.create(LevelType.DAY);
        final LevelVisualTheme menuTheme = menuLevel.getVisualTheme();
        final String menuBackgroundPath = menuLevel.getBackgroundPath();

        // Top HUD labels
        ImageView sunStorageIcon = new ImageView(ImageAssets.load("/assets/sun.png"));
        sunStorageIcon.setFitWidth(120);
        sunStorageIcon.setFitHeight(120);
        sunStorageIcon.setPreserveRatio(true);

        Label sunLabel = new Label("" + Difficulty.MEDIUM.getSettings().getStartingSun());
        sunLabel.setStyle(menuTheme.getSunLabelStyle());

        HBox sunBox = new HBox(sunStorageIcon, sunLabel);
        sunBox.setSpacing(6);
        sunBox.setAlignment(Pos.CENTER_LEFT);
        sunBox.setStyle(menuTheme.getResourceBoxStyle());

        ImageView waterStorageIcon = new ImageView(ImageAssets.load("/assets/water.png"));
        waterStorageIcon.setFitWidth(120);
        waterStorageIcon.setFitHeight(130);
        waterStorageIcon.setPreserveRatio(true);

        Label waterLabel = new Label("" + Difficulty.MEDIUM.getSettings().getStartingWater());
        waterLabel.setStyle(menuTheme.getWaterLabelStyle());

        HBox waterBox = new HBox(waterStorageIcon, waterLabel);
        waterBox.setSpacing(6);
        waterBox.setAlignment(Pos.CENTER_LEFT);
        waterBox.setStyle(menuTheme.getResourceBoxStyle());

        ProgressBar waveProgressBar = new ProgressBar(0);
        waveProgressBar.setPrefWidth(180);
        waveProgressBar.setStyle(menuTheme.getWaveProgressBarStyle());

        Label flag1 = new Label("⚑");
        Label flag2 = new Label("⚑");
        Label flag3 = new Label("⚑");

        flag1.setStyle(menuTheme.getInactiveFlagStyle());
        flag2.setStyle(menuTheme.getInactiveFlagStyle());
        flag3.setStyle(menuTheme.getInactiveFlagStyle());

        HBox flagBar = new HBox(flag1, flag2, flag3);
        flagBar.setSpacing(55);
        flagBar.setAlignment(Pos.CENTER_LEFT);

        ImageView gameOverImage = new ImageView(ImageAssets.load("/assets/Gameover.png"));
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
        ImageView winMenuIcon = new ImageView(ImageAssets.load("/assets/menu_icon.png"));
        winMenuIcon.setFitWidth(55);
        winMenuIcon.setFitHeight(55);
        winMenuButton.setGraphic(winMenuIcon);
        winMenuButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        winMenuButton.setVisible(false);

        VBox winOverlay = new VBox(winLabel, winRestartButton, winMenuButton);
        winOverlay.setSpacing(18);
        winOverlay.setAlignment(Pos.CENTER);
        winOverlay.setVisible(false);

        ImageView pauseIcon = new ImageView(ImageAssets.load("/assets/pause_icon.png"));
        pauseIcon.setFitWidth(100);
        pauseIcon.setFitHeight(100);

        Button pauseButton = new Button();
        pauseButton.setGraphic(pauseIcon);
        pauseButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        ImageView resumeIcon = new ImageView(ImageAssets.load("/assets/resume_icon.png"));
        resumeIcon.setFitWidth(100);
        resumeIcon.setFitHeight(100);

        Button resumeButton = new Button();
        resumeButton.setGraphic(resumeIcon);
        resumeButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        resumeButton.setVisible(false);

        ImageView menuIcon = new ImageView(ImageAssets.load("/assets/menu_icon.png"));
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
        PlantCard cageCard = new PlantCard(
                CAGE_CARD_ID,
                CAGE_IMAGE_PATH,
                CAGE_SUN_COST,
                CAGE_WATER_COST
        );

        // Default selected style
        peaShooterCard.setSelected(true);

        // Top bar shown during gameplay
        VBox waveBox = new VBox(waveProgressBar, flagBar);
        waveBox.setSpacing(4);
        waveBox.setStyle(menuTheme.getWaveBoxStyle());
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
        bombPlantCard,
        cageCard
        );

        plantPanel.setSpacing(10);
        plantPanel.setAlignment(Pos.CENTER_LEFT);

        plantPanel.setStyle(menuTheme.getPlantPanelStyle());

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
        StackPane gameSurface = new StackPane();
        gameSurface.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);

        // Background image for the whole game world
        ImageView globalBg = new ImageView();
        configureBackground(globalBg, menuBackgroundPath, DESIGN_WIDTH, DESIGN_HEIGHT);

        // contentLayer = what changes (menu, settings, or gameplay layout)
        Pane contentLayer = new Pane();
        contentLayer.setPrefSize(DESIGN_WIDTH, DESIGN_HEIGHT);
        StackPane.setAlignment(contentLayer, Pos.TOP_LEFT);

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

        menu.getDayModeButton().setOnAction(e -> {
            activeLevelTypeRef[0] = LevelType.DAY;
            activeDifficultyRef[0] = settingsPage.getSelectedDifficulty();
            launchGame(
                    activeLevelTypeRef[0],
                    activeDifficultyRef[0],
                    boardRef,
                    globalBg,
                    contentLayer,
                    sunStorageIcon,
                    waterStorageIcon,
                    sunBox,
                    waterBox,
                    sunLabel,
                    waterLabel,
                    topBar,
                    plantPanel,
                    waveBox,
                    waveProgressBar,
                    flag1,
                    flag2,
                    flag3,
                    gameOverOverlay,
                    pauseButton,
                    resumeButton,
                    mainMenuButton,
                    winOverlay,
                    restartButton,
                    peaShooterCard,
                    wallPlantCard,
                    sunflowerCard,
                    waterPlantCard,
                    bombPlantCard,
                    cageCard,
                    DESIGN_WIDTH,
                    DESIGN_HEIGHT
            );
        });

        menu.getNightModeButton().setOnAction(e -> {
            activeLevelTypeRef[0] = LevelType.NIGHT;
            activeDifficultyRef[0] = settingsPage.getSelectedDifficulty();
            launchGame(
                    activeLevelTypeRef[0],
                    activeDifficultyRef[0],
                    boardRef,
                    globalBg,
                    contentLayer,
                    sunStorageIcon,
                    waterStorageIcon,
                    sunBox,
                    waterBox,
                    sunLabel,
                    waterLabel,
                    topBar,
                    plantPanel,
                    waveBox,
                    waveProgressBar,
                    flag1,
                    flag2,
                    flag3,
                    gameOverOverlay,
                    pauseButton,
                    resumeButton,
                    mainMenuButton,
                    winOverlay,
                    restartButton,
                    peaShooterCard,
                    wallPlantCard,
                    sunflowerCard,
                    waterPlantCard,
                    bombPlantCard,
                    cageCard,
                    DESIGN_WIDTH,
                    DESIGN_HEIGHT
            );
        });

        menu.getSettingsButton().setOnAction(e -> {
            showScreen(contentLayer, globalBg, menuBackgroundPath, settingsPage);
        });

        settingsPage.getBackButton().setOnAction(e -> {
            showScreen(contentLayer, globalBg, menuBackgroundPath, menu);
        });

        restartButton.setOnAction(e -> {
            launchGame(
                    activeLevelTypeRef[0],
                    activeDifficultyRef[0],
                    boardRef,
                    globalBg,
                    contentLayer,
                    sunStorageIcon,
                    waterStorageIcon,
                    sunBox,
                    waterBox,
                    sunLabel,
                    waterLabel,
                    topBar,
                    plantPanel,
                    waveBox,
                    waveProgressBar,
                    flag1,
                    flag2,
                    flag3,
                    gameOverOverlay,
                    pauseButton,
                    resumeButton,
                    mainMenuButton,
                    winOverlay,
                    restartButton,
                    peaShooterCard,
                    wallPlantCard,
                    sunflowerCard,
                    waterPlantCard,
                    bombPlantCard,
                    cageCard,
                    DESIGN_WIDTH,
                    DESIGN_HEIGHT
            );
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
                    LevelVisualTheme activeTheme = boardRef[0].getLevel().getVisualTheme();

                    updateFlagStyles(activeTheme, currentWave, flag1, flag2, flag3);
                    sunLabel.setText("" + boardRef[0].getSunPoints());
                    waterLabel.setText("" + boardRef[0].getWaterPoints());
                    peaShooterCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.PEA_SHOOTER) > 0);
                    wallPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.WALL_PLANT) > 0);
                    sunflowerCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.SUNFLOWER) > 0);
                    waterPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.WATER_PLANT) > 0);
                    bombPlantCard.setOnCooldown(boardRef[0].getRemainingCooldownMillis(PlantType.BOMB_PLANT) > 0);
                    cageCard.setOnCooldown(boardRef[0].getRemainingCageCooldownMillis() > 0);
                    syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
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

                    showScreen(contentLayer, globalBg, menuBackgroundPath, menu);
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

                    showScreen(contentLayer, globalBg, menuBackgroundPath, menu);
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

            showScreen(contentLayer, globalBg, menuBackgroundPath, menu);
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
        GameBoardView boardView = new GameBoardView(board.getLevel(), board.getGraves());
        boardView.setCellClickHandler((row, col) -> {
            Plant plant = board.placePlantAt(row, col);

            if (plant != null) {
                boardView.addPlantToCell(row, col, plant);
            }
        });
        board.setPlantRemovedHandler(boardView::removePlantFromCell);
            
        Pane battlefieldPane = new Pane();
        battlefieldPane.setPrefSize(designWidth, 700);
        battlefieldPane.setStyle("-fx-background-color: transparent;");
        Rectangle battlefieldTint = createBattlefieldTint(board.getLevel(), designWidth, 700);
        Pane atmosphereLayer = createAtmosphereLayer(board.getLevel(), designWidth, 700);

        boardView.setLayoutX(40);
        boardView.setLayoutY(-15);

        board.setLayoutX(40);
        board.setLayoutY(-15);
        board.setPickOnBounds(false);

        if (battlefieldTint != null) {
            battlefieldPane.getChildren().add(battlefieldTint);
        }
        if (atmosphereLayer != null) {
            battlefieldPane.getChildren().add(atmosphereLayer);
        }
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
        layout.setPrefSize(designWidth, designHeight);

        return layout;
    }

    private void launchGame(
            LevelType levelType,
            Difficulty difficulty,
            GameBoard[] boardRef,
            ImageView globalBg,
            Pane contentLayer,
            ImageView sunStorageIcon,
            ImageView waterStorageIcon,
            HBox sunBox,
            HBox waterBox,
            Label sunLabel,
            Label waterLabel,
            HBox topBar,
            HBox plantPanel,
            VBox waveBox,
            ProgressBar waveProgressBar,
            Label flag1,
            Label flag2,
            Label flag3,
            VBox gameOverOverlay,
            Button pauseButton,
            Button resumeButton,
            Button mainMenuButton,
            VBox winOverlay,
            Button restartButton,
            PlantCard peaShooterCard,
            PlantCard wallPlantCard,
            PlantCard sunflowerCard,
            PlantCard waterPlantCard,
            PlantCard bombPlantCard,
            PlantCard cageCard,
            double designWidth,
            double designHeight
    ) {
        Level level = Levels.create(levelType);
        LevelVisualTheme visualTheme = level.getVisualTheme();
        GameBoard board = new GameBoard(level, difficulty);
        boardRef[0] = board;

        configureBackground(globalBg, visualTheme.getBackgroundPath(), designWidth, designHeight);
        applyLevelTheme(
                visualTheme,
                sunStorageIcon,
                waterStorageIcon,
                sunBox,
                waterBox,
                sunLabel,
                waterLabel,
                plantPanel,
                waveBox,
                waveProgressBar,
                flag1,
                flag2,
                flag3
        );
        sunLabel.setText("" + board.getSunPoints());
        waterLabel.setText("" + board.getWaterPoints());
        resetGameplayUi(restartButton, gameOverOverlay, pauseButton, resumeButton, mainMenuButton, winOverlay);
        resetPlantSelection(board, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
        setupPlantButtons(boardRef, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);

        VBox gameLayout = buildGameLayout(
                board,
                topBar,
                waveBox,
                gameOverOverlay,
                pauseButton,
                resumeButton,
                mainMenuButton,
                winOverlay,
                designWidth,
                designHeight
        );

        contentLayer.getChildren().setAll(gameLayout);
    }

    private Rectangle createBattlefieldTint(Level level, double width, double height) {
        LevelVisualTheme theme = level.getVisualTheme();

        if (!theme.hasBattlefieldTint()) {
            return null;
        }

        Rectangle tint = new Rectangle(width, height);
        tint.setFill(Color.web(theme.getBattlefieldTintColor(), theme.getBattlefieldTintOpacity()));
        tint.setMouseTransparent(true);
        return tint;
    }

    private Pane createAtmosphereLayer(Level level, double width, double height) {
        LevelVisualTheme theme = level.getVisualTheme();

        if (!theme.hasAtmosphereMist()) {
            return null;
        }

        Pane atmosphereLayer = new Pane();
        atmosphereLayer.setPrefSize(width, height);
        atmosphereLayer.setMouseTransparent(true);

        Ellipse upperMist = createMistEllipse(
                theme,
                width * 0.34,
                height * 0.20,
                width * 0.34,
                height * 0.10
        );
        Ellipse lowerMist = createMistEllipse(
                theme,
                width * 0.72,
                height * 0.63,
                width * 0.28,
                height * 0.09
        );
        Ellipse centerMist = createMistEllipse(
                theme,
                width * 0.53,
                height * 0.40,
                width * 0.22,
                height * 0.07
        );

        atmosphereLayer.getChildren().addAll(upperMist, lowerMist, centerMist);
        return atmosphereLayer;
    }

    private void showScreen(Pane contentLayer, ImageView globalBg, String backgroundPath, Node screen) {
        configureBackground(globalBg, backgroundPath, contentLayer.getPrefWidth(), contentLayer.getPrefHeight());
        contentLayer.getChildren().setAll(screen);
    }

    private void configureBackground(ImageView backgroundView, String backgroundPath, double width, double height) {
        backgroundView.setImage(ImageAssets.load(backgroundPath));
        backgroundView.setFitWidth(width);
        backgroundView.setFitHeight(height);
        backgroundView.setPreserveRatio(false);
        backgroundView.setMouseTransparent(true);
    }

    private void applyLevelTheme(
            LevelVisualTheme theme,
            ImageView sunStorageIcon,
            ImageView waterStorageIcon,
            HBox sunBox,
            HBox waterBox,
            Label sunLabel,
            Label waterLabel,
            HBox plantPanel,
            VBox waveBox,
            ProgressBar waveProgressBar,
            Label flag1,
            Label flag2,
            Label flag3
    ) {
        sunBox.setStyle(theme.getResourceBoxStyle());
        waterBox.setStyle(theme.getResourceBoxStyle());
        sunLabel.setStyle(theme.getSunLabelStyle());
        waterLabel.setStyle(theme.getWaterLabelStyle());
        sunStorageIcon.setEffect(createResourceCounterEffect(theme));
        waterStorageIcon.setEffect(createResourceCounterEffect(theme));
        sunLabel.setEffect(createResourceCounterEffect(theme));
        waterLabel.setEffect(createResourceCounterEffect(theme));
        plantPanel.setStyle(theme.getPlantPanelStyle());
        waveBox.setStyle(theme.getWaveBoxStyle());
        waveProgressBar.setStyle(theme.getWaveProgressBarStyle());
        updateFlagStyles(theme, 0, flag1, flag2, flag3);
    }

    private DropShadow createResourceCounterEffect(LevelVisualTheme theme) {
        if (!theme.hasResourceEffect()) {
            return null;
        }

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(theme.getResourceEffectColor(), theme.getResourceEffectOpacity()));
        shadow.setRadius(theme.getResourceEffectRadius());
        shadow.setSpread(0.22);
        return shadow;
    }

    private Ellipse createMistEllipse(LevelVisualTheme theme, double centerX, double centerY, double radiusX, double radiusY) {
        Ellipse mist = new Ellipse(centerX, centerY, radiusX, radiusY);
        mist.setFill(Color.web(theme.getAtmosphereMistColor(), theme.getAtmosphereMistOpacity()));
        mist.setEffect(new GaussianBlur(theme.getAtmosphereMistBlurRadius()));
        mist.setMouseTransparent(true);
        return mist;
    }

    private void updateFlagStyles(LevelVisualTheme theme, int currentWave, Label flag1, Label flag2, Label flag3) {
        flag1.setStyle(currentWave >= 1 ? theme.getActiveFlagStyle() : theme.getInactiveFlagStyle());
        flag2.setStyle(currentWave >= 2 ? theme.getActiveFlagStyle() : theme.getInactiveFlagStyle());
        flag3.setStyle(currentWave >= 3 ? theme.getActiveFlagStyle() : theme.getInactiveFlagStyle());
    }

    private void resetGameplayUi(
            Button restartButton,
            VBox gameOverOverlay,
            Button pauseButton,
            Button resumeButton,
            Button mainMenuButton,
            VBox winOverlay
    ) {
        restartButton.setVisible(true);
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setMouseTransparent(true);
        pauseButton.setVisible(true);
        resumeButton.setVisible(false);
        mainMenuButton.setVisible(true);
        winOverlay.setVisible(false);
    }

    private void resetPlantSelection(
            GameBoard board,
            PlantCard peaShooterCard,
            PlantCard wallPlantCard,
            PlantCard sunflowerCard,
            PlantCard waterPlantCard,
            PlantCard bombPlantCard,
            PlantCard cageCard
    ) {
        board.setSelectedPlantType(PlantType.PEA_SHOOTER);
        syncSelectionState(board, peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
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
            PlantCard bombPlantCard,
            PlantCard cageCard
    ) {
        peaShooterCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.PEA_SHOOTER);
                syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
            }
        });

        wallPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.WALL_PLANT);
                syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
            }
        });

        sunflowerCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.SUNFLOWER);
                syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
            }
        });

        waterPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.WATER_PLANT);
                syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
            }
        });

        bombPlantCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(PlantType.BOMB_PLANT);
                syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
            }
        });

        cageCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].activateCageMode();
                syncSelectionState(boardRef[0], peaShooterCard, wallPlantCard, sunflowerCard, waterPlantCard, bombPlantCard, cageCard);
            }
        });
    }

    private void syncSelectionState(
            GameBoard board,
            PlantCard peaShooterCard,
            PlantCard wallPlantCard,
            PlantCard sunflowerCard,
            PlantCard waterPlantCard,
            PlantCard bombPlantCard,
            PlantCard cageCard
    ) {
        boolean cageSelected = board != null && board.isCageModeActive();
        PlantType selectedPlantType = board != null ? board.getSelectedPlantType() : null;

        peaShooterCard.setSelected(!cageSelected && selectedPlantType == PlantType.PEA_SHOOTER);
        wallPlantCard.setSelected(!cageSelected && selectedPlantType == PlantType.WALL_PLANT);
        sunflowerCard.setSelected(!cageSelected && selectedPlantType == PlantType.SUNFLOWER);
        waterPlantCard.setSelected(!cageSelected && selectedPlantType == PlantType.WATER_PLANT);
        bombPlantCard.setSelected(!cageSelected && selectedPlantType == PlantType.BOMB_PLANT);
        cageCard.setSelected(cageSelected);
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
