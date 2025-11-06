package fr.pigeon.utils;

import java.awt.Color;

public final class Constants {
    // Empêcher l'instanciation
    private Constants() {}

    // Dimensions de la fenêtre
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    
    // Paramètres des entités
    public static final float PIGEON_RADIUS = 5.0f;
    public static final float MEAL_RADIUS = 3.0f;
    public static final float PIGEON_SPEED = 1.0f;
    public static final int INITIAL_MEAL_FRESHNESS = 100;
    
    // Paramètres de peur/dispersion
    public static final float INITIAL_SCARE_CHANCE = 0.1f;
    public static final float MIN_SCARE_CHANCE = 0.05f;
    public static final float MAX_SCARE_CHANCE = 0.3f;
    public static final float SCARE_VARIATION = 0.05f;
    
    // Délais et intervalles
    public static final long SIMULATION_STEP = 100;  // millisecondes
    public static final long PIGEON_SLEEP_TIME = 16; // ~60 FPS
    
    // Couleurs
    public static final Color FRESH_MEAL_COLOR = Color.GREEN;
    public static final Color ROTTEN_MEAL_COLOR = Color.RED;
    public static final Color PIGEON_COLOR = Color.BLUE;
    
    // Marges pour spawn
    public static final float SPAWN_MARGIN = 50.0f;
}