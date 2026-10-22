import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Owns the match-level JavaFX timers: the 50 ms game loop, the zombie spawner,
 * the countdown before each wave and the (optional) passive sun generator.
 * GameBoard decides WHAT happens on each tick; this class only handles WHEN,
 * plus pausing, resuming and stopping them all together.
 * (Each zombie and plant still owns its own Timeline.)
 */
public final class MatchTimers {

    private static final Duration GAME_LOOP_TICK = Duration.millis(50);

    private Timeline gameLoop;
    private Timeline zombieSpawner;
    private Timeline waveStartDelay;
    private Timeline sunGenerator;

    public void startGameLoop(Runnable onTick) {
        stop(gameLoop);
        gameLoop = repeating(GAME_LOOP_TICK, onTick);
    }

    public void startSpawner(double intervalSeconds, Runnable onTick) {
        stopSpawner();
        zombieSpawner = repeating(Duration.seconds(intervalSeconds), onTick);
    }

    public void stopSpawner() {
        stop(zombieSpawner);
    }

    public void scheduleWaveStart(double delaySeconds, Runnable onStart) {
        stopWaveStartDelay();
        waveStartDelay = new Timeline(new KeyFrame(Duration.seconds(delaySeconds), e -> {
            waveStartDelay = null;
            onStart.run();
        }));
        waveStartDelay.setCycleCount(1);
        waveStartDelay.play();
    }

    public void stopWaveStartDelay() {
        stop(waveStartDelay);
        waveStartDelay = null;
    }

    public void startSunGenerator(double intervalSeconds, Runnable onTick) {
        stop(sunGenerator);
        sunGenerator = repeating(Duration.seconds(intervalSeconds), onTick);
    }

    public void pauseAll() {
        pause(gameLoop);
        pause(zombieSpawner);
        pause(waveStartDelay);
    }

    /** @param resumeSpawner false when the current wave has already finished spawning */
    public void resumeAll(boolean resumeSpawner) {
        play(gameLoop);

        if (resumeSpawner) {
            play(zombieSpawner);
        }

        play(waveStartDelay);
    }

    public void stopAll() {
        stop(gameLoop);
        stopSpawner();
        stopWaveStartDelay();
        stop(sunGenerator);
    }

    private static Timeline repeating(Duration interval, Runnable onTick) {
        Timeline timeline = new Timeline(new KeyFrame(interval, e -> onTick.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        return timeline;
    }

    private static void pause(Timeline timeline) {
        if (timeline != null) {
            timeline.pause();
        }
    }

    private static void play(Timeline timeline) {
        if (timeline != null) {
            timeline.play();
        }
    }

    private static void stop(Timeline timeline) {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
