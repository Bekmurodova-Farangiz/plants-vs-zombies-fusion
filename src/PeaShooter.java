public class PeaShooter extends Plant {

    public PeaShooter(int row, int col) {
        super(row, col);
        
        setPlantImage("file:src/assets/peashooter.png");

        setShootingInterval(1.0);
        setCooldown(1.5);

    }
}
