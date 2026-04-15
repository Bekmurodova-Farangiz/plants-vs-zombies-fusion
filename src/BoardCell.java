public class BoardCell {

    private final int row;
    private final int col;
    private Plant occupant;

    public BoardCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Plant getOccupant() {
        return occupant;
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    public void setOccupant(Plant occupant) {
        this.occupant = occupant;
    }

    public void clearOccupant() {
        occupant = null;
    }
}
