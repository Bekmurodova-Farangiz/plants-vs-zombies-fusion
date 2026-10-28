/**
 * Single place responsible for writing game messages to the console.
 * Game classes only decide WHAT to report; this class decides HOW it is printed
 * (Single Responsibility Principle). To log to a file later, only this class changes.
 */
public final class GameLogger {

    private GameLogger() {
        // utility class: no instances
    }

    /** Normal game event (spawns, placements, wave progress). */
    public static void info(String message) {
        System.out.println(message);
    }

    /** Problem that the game can recover from. */
    public static void error(String message) {
        System.err.println(message);
    }

    /** Problem caused by an exception: prints the message and the stack trace. */
    public static void error(String message, Throwable exception) {
        System.err.println(message);
        exception.printStackTrace();
    }
}
