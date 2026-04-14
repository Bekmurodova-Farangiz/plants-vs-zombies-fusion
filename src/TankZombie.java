public class TankZombie extends Zombie {

    public TankZombie(int row) {
        super(row);

        setHealth(250);
        setSpeed(1.6);
        setAttackDamage(3);
        setZombieImage("file:src/assets/tank_zombie.png");
    }
}