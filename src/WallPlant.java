public class WallPlant extends Plant {

    public WallPlant(int row, int col) {
        super(row, col);

        setPlantImage("file:src/assets/wallplant.png");

        setShootingInterval(0);
        setHealth(250);
        setCooldown(3.0);
        setWaterCost(40);

        
    }
}