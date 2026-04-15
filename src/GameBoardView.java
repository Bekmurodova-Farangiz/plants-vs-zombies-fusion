import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.function.BiConsumer;


public class GameBoardView extends GridPane {

    public static final int ROWS = BoardMetrics.ROWS;
    public static final int COLUMNS = BoardMetrics.COLUMNS;
    public static final int CELL_WIDTH = BoardMetrics.CELL_WIDTH;
    public static final int CELL_HEIGHT = BoardMetrics.CELL_HEIGHT;

    private StackPane[][] cells;
    private BiConsumer<Integer, Integer> cellClickHandler;

    public GameBoardView() {
        cells = new StackPane[ROWS][COLUMNS];
        setStyle("-fx-background-color: transparent;");
        createView();
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
        cells[row][col].getChildren().add(plant.getView());
    }
    public void removePlantFromCell(Plant plant) {
        cells[plant.getRow()][plant.getCol()].getChildren().remove(plant.getView());
    }
}
