public class WallPlant extends Plant {

    public WallPlant(int row, int col) {
        super(row, col);

        setPlantImage("/assets/wallplant.png");

        setShootingInterval(0);
        setHealth(250);
        setCooldown(3.0);
        setWaterCost(40);

        
    }

    @Override
    public void act(GameBoard board) {
        // Wall plants do not perform an active action.
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Wall plants have no placement-triggered special ability.
    }
}
