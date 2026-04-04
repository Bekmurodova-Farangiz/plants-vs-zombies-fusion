public class PeaShooter extends Plant {

    public PeaShooter(int row, int col) {
        super(row, col);

        setShootingInterval(1.0);
        setCooldown(1.5);

        getView().setFill(javafx.scene.paint.Color.GREEN);
    }
}
