public class NormalZombie extends Zombie {

    public NormalZombie(int row) {
        super(row);
    }

    @Override
    public void act(GameBoard board) {
        moveLeft();
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Normal zombies have no special spawn ability.
    }
}
