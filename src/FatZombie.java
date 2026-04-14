public class FatZombie extends Zombie {

    public FatZombie(int row) {
        super(row);

        setHealth(300); // more health (normal ~100)
        setSpeed(0.3);  // slower than normal zombie

        setZombieImage("file:src/assets/fat_zombie.png");
    }
}