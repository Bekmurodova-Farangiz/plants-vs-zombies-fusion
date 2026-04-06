public class WallPlant extends Plant {

    public WallPlant(int row, int col) {
        super(row, col);

        setShootingInterval(0);
        setHealth(250);
        setCooldown(3.0);

        
    }
}