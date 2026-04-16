public class BoardCell {

    private final int row;
    private final int col;
    private Plant occupant;
    private Grave grave;

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

    public Grave getGrave() {
        return grave;
    }

    public boolean hasGrave() {
        return grave != null;
    }

    public void setGrave(Grave grave) {
        this.grave = grave;
    }

    public void clearOccupant() {
        occupant = null;
    }
}
