public class ParasiteZombie extends Zombie {

    public ParasiteZombie(int row) {
        super(row);

        setZombieImage("/assets/parasite.png");
    }

    @Override
    public void act(GameBoard board) {
        move();
    }

    @Override
    public boolean onPlantContact(Plant plant) {
        // Parasites infect instead of eating, and walk past the plant.
        if (!plant.isInfected()) {
            plant.infect();
            GameLogger.info("Plant infected by parasite zombie!");
        }
        return false;
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Parasite zombies apply their special effect during plant collisions.
    }
}
