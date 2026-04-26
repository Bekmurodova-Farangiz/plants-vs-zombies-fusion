public class TankZombie extends Zombie {

    public TankZombie(int row) {
        super(row);

        setHealth(250);
        setSpeed(1.6);
        setAttackDamage(3);
        setZombieImage("/assets/tank_zombie.png");
    }

    @Override
    public void act(GameBoard board) {
        move();
    }

    @Override
    public void specialAbility(GameBoard board) {
        // Tank zombies express their role through higher damage and resilience.
    }
}
