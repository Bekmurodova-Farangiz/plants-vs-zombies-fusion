public class FastZombie extends Zombie {

    public FastZombie(int row) {
        super(row);

        setZombieImage("/assets/fast_zombie.png");
        setSpeed(2.5);
    }

    @Override
    public void act(GameBoard board) {
        move();
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Fast zombies rely on their higher speed rather than an active ability.
    }
}
