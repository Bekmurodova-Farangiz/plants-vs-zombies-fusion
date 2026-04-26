public class ParasiteZombie extends Zombie {

    public ParasiteZombie(int row) {
        super(row);

        setZombieImage("/assets/parasite.png");
    }

    @Override
    public void act(GameBoard board) {
        moveLeft();
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Parasite zombies apply their special effect during plant collisions.
    }
}
