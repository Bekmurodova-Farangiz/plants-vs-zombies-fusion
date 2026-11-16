import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * The in-game HUD (heads-up display): resource counters, plant cards, wave progress,
 * pause / resume / menu buttons and the win / game-over overlays.
 *
 * Single Responsibility: this class only builds the HUD widgets and keeps them in sync
 * with the GameBoard. GameApp keeps the application lifecycle (window, screens, matches).
 * Composition: GameApp HAS a GameHud instead of passing ~30 widgets around as parameters.
 * Information hiding: all widgets are private; GameApp only gets what it needs.
 */
public final class GameHud {

    private static final String CAGE_CARD_ID = "CageAbility";
    private static final String CAGE_IMAGE_PATH = "/assets/cage.png";

    private final ImageView sunStorageIcon;
    private final Label sunLabel;
    private final HBox sunBox;
    private final ImageView waterStorageIcon;
    private final Label waterLabel;
    private final HBox waterBox;
    private final ProgressBar waveProgressBar;
    private final Label flag1;
    private final Label flag2;
    private final Label flag3;
    private final Button restartButton;
    private final Button gameOverMenuButton;
    private final VBox gameOverOverlay;
    private final Label winLabel;
    private final Button winRestartButton;
    private final Button winMenuButton;
    private final VBox winOverlay;
    private final Button pauseButton;
    private final Button resumeButton;
    private final Button mainMenuButton;
    private final PlantCard peaShooterCard;
    private final PlantCard wallPlantCard;
    private final PlantCard sunflowerCard;
    private final PlantCard waterPlantCard;
    private final PlantCard bombPlantCard;
    private final PlantCard cageCard;
    private final VBox waveBox;
    private final HBox plantPanel;
    private final HBox topBar;

    public GameHud(LevelVisualTheme menuTheme, double designWidth, double designHeight) {
        // Resource counters (sun, water)
        sunStorageIcon = new ImageView(ImageAssets.load("/assets/sun.png"));
        sunStorageIcon.setFitWidth(120);
        sunStorageIcon.setFitHeight(120);
        sunStorageIcon.setPreserveRatio(true);

        sunLabel = new Label("" + Difficulty.MEDIUM.getSettings().getStartingSun());
        sunLabel.setStyle(menuTheme.getSunLabelStyle());

        sunBox = new HBox(sunStorageIcon, sunLabel);
        sunBox.setSpacing(6);
        sunBox.setAlignment(Pos.CENTER_LEFT);
        sunBox.setStyle(menuTheme.getResourceBoxStyle());

        waterStorageIcon = new ImageView(ImageAssets.load("/assets/water.png"));
        waterStorageIcon.setFitWidth(120);
        waterStorageIcon.setFitHeight(130);
        waterStorageIcon.setPreserveRatio(true);

        waterLabel = new Label("" + Difficulty.MEDIUM.getSettings().getStartingWater());
        waterLabel.setStyle(menuTheme.getWaterLabelStyle());

        waterBox = new HBox(waterStorageIcon, waterLabel);
        waterBox.setSpacing(6);
        waterBox.setAlignment(Pos.CENTER_LEFT);
        waterBox.setStyle(menuTheme.getResourceBoxStyle());

        waveProgressBar = new ProgressBar(0);
        waveProgressBar.setPrefWidth(180);
        waveProgressBar.setStyle(menuTheme.getWaveProgressBarStyle());

        flag1 = new Label("⚑");
        flag2 = new Label("⚑");
        flag3 = new Label("⚑");

        flag1.setStyle(menuTheme.getInactiveFlagStyle());
        flag2.setStyle(menuTheme.getInactiveFlagStyle());
        flag3.setStyle(menuTheme.getInactiveFlagStyle());

        HBox flagBar = new HBox(flag1, flag2, flag3);
        flagBar.setSpacing(55);
        flagBar.setAlignment(Pos.CENTER_LEFT);

        ImageView gameOverImage = new ImageView(ImageAssets.load("/assets/Gameover.png"));
        gameOverImage.setFitWidth(620);
        gameOverImage.setPreserveRatio(true);

        restartButton = new Button("Restart");
        restartButton.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #f2d25c;" +
                "-fx-text-fill: #1f1605;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 14 40 14 40;"
        );

        gameOverMenuButton = new Button("Menu");
        gameOverMenuButton.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: rgba(255,255,255,0.92);" +
                "-fx-text-fill: #1d1d1d;" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 12 34 12 34;"
        );

        gameOverOverlay = new VBox(gameOverImage, restartButton, gameOverMenuButton);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setSpacing(24);
        gameOverOverlay.setPrefSize(designWidth, designHeight);
        gameOverOverlay.setStyle("-fx-background-color: transparent;");
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setMouseTransparent(true);
        //shifts everything upward visually
        gameOverOverlay.setTranslateY(-190);

        winLabel = new Label("YOU WON THE GAME!");
        winLabel.setStyle("-fx-font-size: 42px; -fx-text-fill: gold; -fx-font-weight: bold;");
        winLabel.setVisible(false);

        winRestartButton = new Button("Restart");
        winRestartButton.setStyle("-fx-font-size: 18px;");
        winRestartButton.setVisible(false);

        winMenuButton = createIconButton("/assets/menu_icon.png", 55);
        winMenuButton.setVisible(false);

        winOverlay = new VBox(winLabel, winRestartButton, winMenuButton);
        winOverlay.setSpacing(18);
        winOverlay.setAlignment(Pos.CENTER);
        winOverlay.setVisible(false);

        pauseButton = createIconButton("/assets/pause_icon.png", 100);

        resumeButton = createIconButton("/assets/resume_icon.png", 100);
        resumeButton.setVisible(false);

        mainMenuButton = createIconButton("/assets/menu_icon.png", 100);

        pauseButton.setFocusTraversable(false);
        resumeButton.setFocusTraversable(false);
        mainMenuButton.setFocusTraversable(false);
        restartButton.setFocusTraversable(false);
        gameOverMenuButton.setFocusTraversable(false);

        peaShooterCard = createPlantCard(PlantType.PEA_SHOOTER);
        wallPlantCard = createPlantCard(PlantType.WALL_PLANT);
        sunflowerCard = createPlantCard(PlantType.SUNFLOWER);
        waterPlantCard = createPlantCard(PlantType.WATER_PLANT);
        bombPlantCard = createPlantCard(PlantType.BOMB_PLANT);
        cageCard = new PlantCard(
                CAGE_CARD_ID,
                CAGE_IMAGE_PATH,
                CageAbility.SUN_COST,
                CageAbility.WATER_COST
        );

        // Default selected style
        peaShooterCard.setSelected(true);

        // Top bar shown during gameplay
        waveBox = new VBox(waveProgressBar, flagBar);
        waveBox.setSpacing(4);
        waveBox.setStyle(menuTheme.getWaveBoxStyle());
        waveBox.setPrefWidth(240);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane pauseResumeSlot = new StackPane(pauseButton, resumeButton);
        pauseResumeSlot.setPrefWidth(50);
        pauseResumeSlot.setPrefHeight(50);
        
        plantPanel = new HBox(
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

        topBar = new HBox(
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

    }

    // ---------- what GameApp needs to place on screen or attach actions to ----------

    public HBox getTopBar() {
        return topBar;
    }

    public VBox getWaveBox() {
        return waveBox;
    }

    public VBox getGameOverOverlay() {
        return gameOverOverlay;
    }

    public VBox getWinOverlay() {
        return winOverlay;
    }

    public Button getPauseButton() {
        return pauseButton;
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getMainMenuButton() {
        return mainMenuButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public Button getGameOverMenuButton() {
        return gameOverMenuButton;
    }

    public Button getWinRestartButton() {
        return winRestartButton;
    }

    public Button getWinMenuButton() {
        return winMenuButton;
    }

    // ---------- keeping the HUD in sync with the game ----------

    /** Called every frame: copies the board's current state into the HUD. */
    public void update(GameBoard board) {
        int currentWave = board.getCurrentWave();
        GameState gameState = board.getGameState();
        boolean isLost = gameState == GameState.LOST;
        boolean isWon = gameState == GameState.WON;
        boolean isPaused = gameState == GameState.PAUSED;
        LevelVisualTheme activeTheme = board.getLevel().getVisualTheme();

        updateFlagStyles(activeTheme, currentWave);
        showResources(board.getSunPoints(), board.getWaterPoints());
        peaShooterCard.setOnCooldown(board.getRemainingCooldownMillis(PlantType.PEA_SHOOTER) > 0);
        wallPlantCard.setOnCooldown(board.getRemainingCooldownMillis(PlantType.WALL_PLANT) > 0);
        sunflowerCard.setOnCooldown(board.getRemainingCooldownMillis(PlantType.SUNFLOWER) > 0);
        waterPlantCard.setOnCooldown(board.getRemainingCooldownMillis(PlantType.WATER_PLANT) > 0);
        bombPlantCard.setOnCooldown(board.getRemainingCooldownMillis(PlantType.BOMB_PLANT) > 0);
        cageCard.setOnCooldown(board.getRemainingCageCooldownMillis() > 0);
        syncSelectionState(board);
        waveProgressBar.setProgress(board.getWaveProgress());

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

    public void showResources(int sunPoints, int waterPoints) {
        sunLabel.setText("" + sunPoints);
        waterLabel.setText("" + waterPoints);
    }

    /** Pause button visible, resume hidden. */
    public void showRunningControls() {
        pauseButton.setVisible(true);
        resumeButton.setVisible(false);
    }

    /** Resume button visible, pause hidden. */
    public void showPausedControls() {
        pauseButton.setVisible(false);
        resumeButton.setVisible(true);
    }

    /** Hides the overlays and restores the buttons before going back to the menu. */
    public void resetForMenu() {
        showRunningControls();
        mainMenuButton.setVisible(true);
        winOverlay.setVisible(false);
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setMouseTransparent(true);
    }

    /** Puts every HUD element back in its starting state for a new match. */
    public void resetGameplayUi() {
        restartButton.setVisible(true);
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setMouseTransparent(true);
        showRunningControls();
        mainMenuButton.setVisible(true);
        winOverlay.setVisible(false);
    }

    public void applyLevelTheme(LevelVisualTheme theme) {
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
        updateFlagStyles(theme, 0);
    }

    public void resetPlantSelection(GameBoard board) {
        board.setSelectedPlantType(PlantType.PEA_SHOOTER);
        syncSelectionState(board);
    }

    /**
     * Connects the plant cards to the current board.
     * boardRef always points to the match in progress, so the cards keep working after Restart.
     */
    public void setupPlantButtons(GameBoard[] boardRef) {
        connectPlantCard(peaShooterCard, PlantType.PEA_SHOOTER, boardRef);
        connectPlantCard(wallPlantCard, PlantType.WALL_PLANT, boardRef);
        connectPlantCard(sunflowerCard, PlantType.SUNFLOWER, boardRef);
        connectPlantCard(waterPlantCard, PlantType.WATER_PLANT, boardRef);
        connectPlantCard(bombPlantCard, PlantType.BOMB_PLANT, boardRef);

        cageCard.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].activateCageMode();
                syncSelectionState(boardRef[0]);
            }
        });
    }

    // ---------- private helpers ----------

    private void connectPlantCard(PlantCard card, PlantType type, GameBoard[] boardRef) {
        card.setOnMouseClicked(e -> {
            if (boardRef[0] != null) {
                boardRef[0].setSelectedPlantType(type);
                syncSelectionState(boardRef[0]);
            }
        });
    }

    private void syncSelectionState(GameBoard board) {
        boolean cageSelected = board != null && board.isCageModeActive();
        PlantType selectedPlantType = board != null ? board.getSelectedPlantType() : null;

        peaShooterCard.setSelected(!cageSelected && selectedPlantType == PlantType.PEA_SHOOTER);
        wallPlantCard.setSelected(!cageSelected && selectedPlantType == PlantType.WALL_PLANT);
        sunflowerCard.setSelected(!cageSelected && selectedPlantType == PlantType.SUNFLOWER);
        waterPlantCard.setSelected(!cageSelected && selectedPlantType == PlantType.WATER_PLANT);
        bombPlantCard.setSelected(!cageSelected && selectedPlantType == PlantType.BOMB_PLANT);
        cageCard.setSelected(cageSelected);
    }

    private void updateFlagStyles(LevelVisualTheme theme, int currentWave) {
        flag1.setStyle(currentWave >= 1 ? theme.getActiveFlagStyle() : theme.getInactiveFlagStyle());
        flag2.setStyle(currentWave >= 2 ? theme.getActiveFlagStyle() : theme.getInactiveFlagStyle());
        flag3.setStyle(currentWave >= 3 ? theme.getActiveFlagStyle() : theme.getInactiveFlagStyle());
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

    // Divide and conquer: one helper builds every plant card (written once, reused for each PlantType).
    private static PlantCard createPlantCard(PlantType type) {
        return new PlantCard(
                type.getIdentifier(),
                type.getImagePath(),
                type.getSunCost(),
                type.getWaterCost()
        );
    }

    // Divide and conquer: one helper builds every round image button (pause, resume, menu).
    private static Button createIconButton(String imagePath, double size) {
        ImageView icon = new ImageView(ImageAssets.load(imagePath));
        icon.setFitWidth(size);
        icon.setFitHeight(size);

        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        return button;
    }
}
