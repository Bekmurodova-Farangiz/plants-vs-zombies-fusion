public class LevelVisualTheme {

    private final String backgroundPath;
    private final String battlefieldTintColor;
    private final double battlefieldTintOpacity;
    private final String resourceBoxStyle;
    private final String resourceEffectColor;
    private final double resourceEffectOpacity;
    private final double resourceEffectRadius;
    private final String plantPanelStyle;
    private final String waveBoxStyle;
    private final String waveProgressBarStyle;
    private final String sunLabelStyle;
    private final String waterLabelStyle;
    private final String activeFlagStyle;
    private final String inactiveFlagStyle;
    private final String graveEffectColor;
    private final double graveEffectOpacity;
    private final double graveGlowLevel;
    private final double graveShadowRadius;
    private final String atmosphereMistColor;
    private final double atmosphereMistOpacity;
    private final double atmosphereMistBlurRadius;

    public LevelVisualTheme(
            String backgroundPath,
            String battlefieldTintColor,
            double battlefieldTintOpacity,
            String resourceBoxStyle,
            String resourceEffectColor,
            double resourceEffectOpacity,
            double resourceEffectRadius,
            String plantPanelStyle,
            String waveBoxStyle,
            String waveProgressBarStyle,
            String sunLabelStyle,
            String waterLabelStyle,
            String activeFlagStyle,
            String inactiveFlagStyle,
            String graveEffectColor,
            double graveEffectOpacity,
            double graveGlowLevel,
            double graveShadowRadius,
            String atmosphereMistColor,
            double atmosphereMistOpacity,
            double atmosphereMistBlurRadius
    ) {
        this.backgroundPath = backgroundPath;
        this.battlefieldTintColor = battlefieldTintColor;
        this.battlefieldTintOpacity = battlefieldTintOpacity;
        this.resourceBoxStyle = resourceBoxStyle;
        this.resourceEffectColor = resourceEffectColor;
        this.resourceEffectOpacity = resourceEffectOpacity;
        this.resourceEffectRadius = resourceEffectRadius;
        this.plantPanelStyle = plantPanelStyle;
        this.waveBoxStyle = waveBoxStyle;
        this.waveProgressBarStyle = waveProgressBarStyle;
        this.sunLabelStyle = sunLabelStyle;
        this.waterLabelStyle = waterLabelStyle;
        this.activeFlagStyle = activeFlagStyle;
        this.inactiveFlagStyle = inactiveFlagStyle;
        this.graveEffectColor = graveEffectColor;
        this.graveEffectOpacity = graveEffectOpacity;
        this.graveGlowLevel = graveGlowLevel;
        this.graveShadowRadius = graveShadowRadius;
        this.atmosphereMistColor = atmosphereMistColor;
        this.atmosphereMistOpacity = atmosphereMistOpacity;
        this.atmosphereMistBlurRadius = atmosphereMistBlurRadius;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public String getBattlefieldTintColor() {
        return battlefieldTintColor;
    }

    public double getBattlefieldTintOpacity() {
        return battlefieldTintOpacity;
    }

    public boolean hasBattlefieldTint() {
        return battlefieldTintOpacity > 0;
    }

    public String getResourceBoxStyle() {
        return resourceBoxStyle;
    }

    public String getResourceEffectColor() {
        return resourceEffectColor;
    }

    public double getResourceEffectOpacity() {
        return resourceEffectOpacity;
    }

    public double getResourceEffectRadius() {
        return resourceEffectRadius;
    }

    public boolean hasResourceEffect() {
        return resourceEffectRadius > 0;
    }

    public String getPlantPanelStyle() {
        return plantPanelStyle;
    }

    public String getWaveBoxStyle() {
        return waveBoxStyle;
    }

    public String getWaveProgressBarStyle() {
        return waveProgressBarStyle;
    }

    public String getSunLabelStyle() {
        return sunLabelStyle;
    }

    public String getWaterLabelStyle() {
        return waterLabelStyle;
    }

    public String getActiveFlagStyle() {
        return activeFlagStyle;
    }

    public String getInactiveFlagStyle() {
        return inactiveFlagStyle;
    }

    public String getGraveEffectColor() {
        return graveEffectColor;
    }

    public double getGraveEffectOpacity() {
        return graveEffectOpacity;
    }

    public double getGraveGlowLevel() {
        return graveGlowLevel;
    }

    public double getGraveShadowRadius() {
        return graveShadowRadius;
    }

    public boolean hasGravePolish() {
        return graveGlowLevel > 0 || graveShadowRadius > 0;
    }

    public String getAtmosphereMistColor() {
        return atmosphereMistColor;
    }

    public double getAtmosphereMistOpacity() {
        return atmosphereMistOpacity;
    }

    public double getAtmosphereMistBlurRadius() {
        return atmosphereMistBlurRadius;
    }

    public boolean hasAtmosphereMist() {
        return atmosphereMistOpacity > 0 && atmosphereMistBlurRadius > 0;
    }
}
