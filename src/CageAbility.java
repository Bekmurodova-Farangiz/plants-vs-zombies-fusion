/**
 * The cage power-up: its cost, its 8-second cooldown and whether the
 * player has it "armed" (selected and waiting for a click on a zombie).
 * GameBoard checks resources and game state; this class owns the cage's own rules.
 */
public final class CageAbility {

    public static final int SUN_COST = 50;
    public static final int WATER_COST = 50;
    private static final double TRAP_DURATION_SECONDS = 10.0;
    private static final long COOLDOWN_MILLIS = 8_000L;

    private boolean active;
    private long cooldownEndMillis;

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isReady() {
        return getRemainingCooldownMillis() == 0;
    }

    public long getRemainingCooldownMillis() {
        return Math.max(0, cooldownEndMillis - System.currentTimeMillis());
    }

    /** Traps the zombie, disarms the cage and starts its cooldown. */
    public void trap(Zombie zombie) {
        zombie.trapForSeconds(TRAP_DURATION_SECONDS);
        active = false;
        cooldownEndMillis = System.currentTimeMillis() + COOLDOWN_MILLIS;
    }
}
