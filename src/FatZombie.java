public class FatZombie extends Zombie {

    public FatZombie(int row) {
        super(row);

        setHealth(300); // more health (normal ~100)
        setSpeed(0.3);  // slower than normal zombie

        setZombieImage("/assets/fat_zombie.png");
    }

    @Override
    public void act(GameBoard board) {
        move();
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Fat zombies express their role through durability rather than an active ability.
    }
}
