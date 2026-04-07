public class FastZombie extends Zombie {

    public FastZombie(int row) {
        super(row);

        setZombieImage("file:src/assets/fast_zombie.png");
        setSpeed(2.5);
    }
}