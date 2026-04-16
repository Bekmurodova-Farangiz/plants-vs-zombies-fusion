import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;


public class GameBoardView extends GridPane {

    public static final int ROWS = BoardMetrics.ROWS;
    public static final int COLUMNS = BoardMetrics.COLUMNS;
    public static final int CELL_WIDTH = BoardMetrics.CELL_WIDTH;
    public static final int CELL_HEIGHT = BoardMetrics.CELL_HEIGHT;

    private final Level level;
    private final List<Grave> graves;
    private StackPane[][] cells;
    private BiConsumer<Integer, Integer> cellClickHandler;

    public GameBoardView() {
        this(Levels.create(LevelType.DAY), Collections.emptyList());
    }

    public GameBoardView(Level level) {
        this(level, Collections.emptyList());
    }

    public GameBoardView(Level level, List<Grave> graves) {
        this.level = level;
        this.graves = Collections.unmodifiableList(new ArrayList<>(graves));
        cells = new StackPane[ROWS][COLUMNS];
        setStyle("-fx-background-color: transparent;");
        createView();
        renderGraves();
    }

    private void createView() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Rectangle cellBackground = new Rectangle(CELL_WIDTH, CELL_HEIGHT);
                cellBackground.setFill(Color.TRANSPARENT);
                cellBackground.setStroke(null);

                StackPane cell = new StackPane();
                cell.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
                cell.setMinSize(CELL_WIDTH, CELL_HEIGHT);
                cell.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
                cell.setAlignment(Pos.CENTER);
                StackPane.setAlignment(cellBackground, Pos.CENTER);
                cell.getChildren().add(cellBackground);

                final int currentRow = row;
                final int currentCol = col;

                cell.setOnMouseClicked(e -> {
                    if (cellClickHandler != null) {
                        cellClickHandler.accept(currentRow, currentCol);
                    }
                });

                cells[row][col] = cell;
                add(cell, col, row);
            }
        }
    }

    public StackPane getCell(int row, int col) {
        return cells[row][col];
    }
    public void setCellClickHandler(BiConsumer<Integer, Integer> handler) {
        this.cellClickHandler = handler;
    }
    public void addPlantToCell(int row, int col, Plant plant) {
        plant.getView().setMouseTransparent(true);
        plant.configureView();
        StackPane.setAlignment(plant.getView(), Pos.CENTER);

        if (!cells[row][col].getChildren().contains(plant.getView())) {
            cells[row][col].getChildren().add(plant.getView());
        }
    }
    public void removePlantFromCell(Plant plant) {
        cells[plant.getRow()][plant.getCol()].getChildren().remove(plant.getView());
    }

    private void renderGraves() {
        for (Grave grave : graves) {
            addGraveToCell(grave);
        }
    }

    private void addGraveToCell(Grave grave) {
        ImageView graveView = new ImageView(ImageAssets.load(grave.getImagePath()));
        graveView.setFitWidth(CELL_WIDTH * 0.7);
        graveView.setFitHeight(CELL_HEIGHT * 0.8);
        graveView.setPreserveRatio(true);
        graveView.setMouseTransparent(true);
        applyGravePolish(graveView);
        StackPane.setAlignment(graveView, Pos.CENTER);
        cells[grave.getRow()][grave.getCol()].getChildren().add(graveView);
    }

    private void applyGravePolish(ImageView graveView) {
        LevelVisualTheme theme = level.getVisualTheme();

        if (!theme.hasGravePolish()) {
            return;
        }

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(theme.getGraveEffectColor(), theme.getGraveEffectOpacity()));
        shadow.setRadius(theme.getGraveShadowRadius());
        shadow.setSpread(0.2);

        Glow glow = new Glow(theme.getGraveGlowLevel());
        glow.setInput(shadow);

        graveView.setEffect(glow);
    }
}
