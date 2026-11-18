import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameApp extends Application {

    // Window and layout sizes (symbolic constants instead of magic numbers).
    private static final double INITIAL_WINDOW_WIDTH = 1400;
    private static final double INITIAL_WINDOW_HEIGHT = 900;
    private static final double BATTLEFIELD_HEIGHT = 700;
    private static final double BOARD_OFFSET_X = 40;
    private static final double BOARD_OFFSET_Y = -15;
    private static final double WAVE_BOX_X = 1200;
    private static final double WAVE_BOX_Y = 640;
    private static final double WIN_OVERLAY_X = 600;
    private static final double WIN_OVERLAY_Y = 220;
    private static final double LAYOUT_SPACING = 10;

    @Override
    public void start(Stage stage) {
        try {
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

        // The whole HUD (counters, plant cards, buttons, overlays) lives in its own class.
        GameHud hud = new GameHud(menuTheme, DESIGN_WIDTH, DESIGN_HEIGHT);

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
            launchGame(activeLevelTypeRef[0], activeDifficultyRef[0], boardRef,
                    globalBg, contentLayer, hud, DESIGN_WIDTH, DESIGN_HEIGHT);
        });

        menu.getNightModeButton().setOnAction(e -> {
            activeLevelTypeRef[0] = LevelType.NIGHT;
            activeDifficultyRef[0] = settingsPage.getSelectedDifficulty();
            launchGame(activeLevelTypeRef[0], activeDifficultyRef[0], boardRef,
                    globalBg, contentLayer, hud, DESIGN_WIDTH, DESIGN_HEIGHT);
        });

        menu.getSettingsButton().setOnAction(e -> {
            showScreen(contentLayer, globalBg, menuBackgroundPath, settingsPage);
        });

        settingsPage.getBackButton().setOnAction(e -> {
            showScreen(contentLayer, globalBg, menuBackgroundPath, menu);
        });

        hud.getRestartButton().setOnAction(e -> {
            launchGame(activeLevelTypeRef[0], activeDifficultyRef[0], boardRef,
                    globalBg, contentLayer, hud, DESIGN_WIDTH, DESIGN_HEIGHT);
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
                    hud.update(boardRef[0]);
                }
            }
        };
        timer.start();

        // Button handlers are set ONCE here (they used to be re-assigned on every frame).
        hud.getPauseButton().setOnAction(e -> {
            if (boardRef[0] != null && boardRef[0].getGameState() == GameState.RUNNING) {
                boardRef[0].pauseGame();
                hud.showPausedControls();
            }
        });

        hud.getResumeButton().setOnAction(e -> {
            if (boardRef[0] != null && boardRef[0].getGameState() == GameState.PAUSED) {
                boardRef[0].resumeGame();
                hud.showRunningControls();
            }
        });

        // The three "back to menu" buttons (top bar, win screen, game-over screen) do the same thing.
        Runnable returnToMenu = () -> {
            disposeCurrentBoard(boardRef);
            hud.resetForMenu();
            showScreen(contentLayer, globalBg, menuBackgroundPath, menu);
        };
        hud.getMainMenuButton().setOnAction(e -> returnToMenu.run());
        hud.getWinMenuButton().setOnAction(e -> returnToMenu.run());
        hud.getGameOverMenuButton().setOnAction(e -> returnToMenu.run());

        hud.getWinRestartButton().setOnAction(e -> {
            if (hud.getRestartButton().getOnAction() != null) {
                hud.getRestartButton().fire();
            }
        });

        // Window size
        Scene scene = new Scene(root, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);

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
        } catch (IllegalArgumentException exception) {
            GameLogger.error("Game startup failed because a required configuration value was invalid.", exception);
            throw exception;
        } catch (RuntimeException exception) {
            GameLogger.error("Game startup failed unexpectedly.", exception);
            throw exception;
        }
    }

    /**
     * Builds the visible gameplay layout:
     * top bar + battlefield pane + overlays
     */
    private VBox buildGameLayout(GameBoard board, GameHud hud, double designWidth, double designHeight) {
        VBox waveBox = hud.getWaveBox();
        VBox winOverlay = hud.getWinOverlay();
        VBox gameOverOverlay = hud.getGameOverOverlay();

        GameBoardView boardView = new GameBoardView(board.getLevel(), board.getGraves());
        boardView.setCellClickHandler((row, col) -> {
            Plant plant = board.placePlantAt(row, col);

            if (plant != null) {
                boardView.addPlantToCell(row, col, plant);
            }
        });
        board.setPlantRemovedHandler(boardView::removePlantFromCell);
            
        Pane battlefieldPane = new Pane();
        battlefieldPane.setPrefSize(designWidth, BATTLEFIELD_HEIGHT);
        battlefieldPane.setStyle("-fx-background-color: transparent;");
        Rectangle battlefieldTint = createBattlefieldTint(board.getLevel(), designWidth, BATTLEFIELD_HEIGHT);
        Pane atmosphereLayer = createAtmosphereLayer(board.getLevel(), designWidth, BATTLEFIELD_HEIGHT);

        // The grid (view) and the board (game objects) sit exactly on top of each other.
        boardView.setLayoutX(BOARD_OFFSET_X);
        boardView.setLayoutY(BOARD_OFFSET_Y);

        board.setLayoutX(BOARD_OFFSET_X);
        board.setLayoutY(BOARD_OFFSET_Y);
        board.setPickOnBounds(false);

        if (battlefieldTint != null) {
            battlefieldPane.getChildren().add(battlefieldTint);
        }
        if (atmosphereLayer != null) {
            battlefieldPane.getChildren().add(atmosphereLayer);
        }
        battlefieldPane.getChildren().addAll(boardView, board, waveBox, winOverlay, gameOverOverlay);

        waveBox.setLayoutX(WAVE_BOX_X);
        waveBox.setLayoutY(WAVE_BOX_Y);

        winOverlay.setLayoutX(WIN_OVERLAY_X);
        winOverlay.setLayoutY(WIN_OVERLAY_Y);
        gameOverOverlay.relocate(0, 0);

        VBox layout = new VBox(hud.getTopBar(), battlefieldPane);
        layout.setSpacing(LAYOUT_SPACING);
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
            GameHud hud,
            double designWidth,
            double designHeight
    ) {
        // Stop the previous match before starting a new one (Restart).
        disposeCurrentBoard(boardRef);

        Level level = Levels.create(levelType);
        LevelVisualTheme visualTheme = level.getVisualTheme();
        GameBoard board = createGameBoard(level, difficulty);
        boardRef[0] = board;

        configureBackground(globalBg, visualTheme.getBackgroundPath(), designWidth, designHeight);
        hud.applyLevelTheme(visualTheme);
        hud.showResources(board.getSunPoints(), board.getWaterPoints());
        hud.resetGameplayUi();
        hud.resetPlantSelection(board);
        hud.setupPlantButtons(boardRef);

        VBox gameLayout = buildGameLayout(board, hud, designWidth, designHeight);
        contentLayer.getChildren().setAll(gameLayout);
    }

    private void disposeCurrentBoard(GameBoard[] boardRef) {
        if (boardRef[0] != null) {
            boardRef[0].dispose();
            boardRef[0] = null;
        }
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

    private Ellipse createMistEllipse(LevelVisualTheme theme, double centerX, double centerY, double radiusX, double radiusY) {
        Ellipse mist = new Ellipse(centerX, centerY, radiusX, radiusY);
        mist.setFill(Color.web(theme.getAtmosphereMistColor(), theme.getAtmosphereMistOpacity()));
        mist.setEffect(new GaussianBlur(theme.getAtmosphereMistBlurRadius()));
        mist.setMouseTransparent(true);
        return mist;
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

    // Dependency Inversion: GameApp (the client) decides which helper objects
    // the match uses and injects them into GameBoard through its constructor.
    // To change a helper (e.g. a short demo wave list), only this method changes.
    private static GameBoard createGameBoard(Level level, Difficulty difficulty) {
        Difficulty chosenDifficulty = difficulty != null ? difficulty : Difficulty.MEDIUM;
        DifficultySettings settings = chosenDifficulty.getSettings();

        ResourceManager resources = new ResourceManager(
                settings.getStartingSun(),
                settings.getStartingWater()
        );
        WaveManager waveManager = new WaveManager(
                WaveManager.createDefaultWaves(),
                settings.getSpawnIntervalMultiplier()
        );
        MatchTimers timers = new MatchTimers();
        CageAbility cage = new CageAbility();

        return new GameBoard(level, chosenDifficulty, resources, waveManager, timers, cage);
    }
}
