public enum Difficulty {
    EASY(new DifficultySettings(0.85, 0.85, 1.20, 300, 125)),
    MEDIUM(new DifficultySettings(1.0, 1.0, 1.0, 250, 100)),
    HARD(new DifficultySettings(1.20, 1.20, 0.85, 200, 75));

    private final DifficultySettings settings;

    Difficulty(DifficultySettings settings) {
        this.settings = settings;
    }

    public DifficultySettings getSettings() {
        return settings;
    }
}
