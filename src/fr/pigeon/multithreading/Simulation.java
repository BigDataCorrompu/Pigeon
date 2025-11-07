package fr.pigeon.multithreading;
import fr.pigeon.entity.*;
import fr.pigeon.utils.Constants;
import java.awt.Graphics2D;


/* * Represents the simulation of the pigeon game. With the multithreading aspect.
 */

public class Simulation implements Runnable { 
    private GameState gameState;
    private volatile boolean isRunning;


    public Simulation() {
        this.gameState = new GameState();
        this.isRunning = true;
    }

    @Override
    public void run() {
        while(isRunning) {
            try {
                step();
                Thread.sleep(Constants.SIMULATION_STEP);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private synchronized void step() {
        // Met à jour l'état du jeu à chaque étape de la simulation
        gameState.update(); // Convertir en secondes
    }

    public void stop() {
        // Arrêter la boucle de la simulation
        this.isRunning = false;
        // Arrêter proprement tous les pigeons actifs
        if (gameState != null) {
            for (Pigeon p : gameState.getPigeons()) {
                try {
                    p.stopThread();
                } catch (Exception ignored) {
                    // Ne pas laisser une exception d'arrêt casser la fermeture
                }
            }
        }
    }


    public void drawEntities(Graphics2D g2) {
        gameState.drawGamestate(g2);
    }

    public void addMeal(Coordinate position) {
        gameState.addMeal(new Meal(position));
    }

    public void spawnRandomPigeon(int widthWindow, int heightWindow) {
        if (widthWindow <= 0 || heightWindow <= 0) {
            return; // Dimension invalide
        }
        float x = (float)(Math.random() * widthWindow); 
        float y = (float)(Math.random() * heightWindow); 
        Pigeon pigeon = new Pigeon(new Coordinate(x, y));
        pigeon.startThread();
        gameState.addPigeon(pigeon);
    }


    @Override
    public String toString() {
        return "Simulation{" +
                "gameState=" + gameState +
                ", isRunning=" + isRunning +
                '}';
    }
}