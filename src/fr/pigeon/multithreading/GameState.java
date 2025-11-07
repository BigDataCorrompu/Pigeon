package fr.pigeon.multithreading;
import fr.pigeon.entity.Coordinate;
import fr.pigeon.entity.Meal;
import fr.pigeon.entity.Pigeon;
import fr.pigeon.utils.Constants;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/* * Represents the state of the game, including pigeons and food items.
 */

public class GameState {
    private final List<Pigeon> pigeons;
    private final List<Meal> meals;
    private final List<Meal> rottenMeals;
    private final float radiusPigeon = 5.0f; // Rayon d'interaction du pigeon
    private final float radiusMeal = 3.0f; // Rayon d'interaction de la nourriture

    // Pigeon paramètres de dispersion
    private float scareChance = 0.005f; // Probabilité initiale de dispersion
    private final float MIN_SCARE_CHANCE = 0.0025f;
    private final float MAX_SCARE_CHANCE = 0.015f;
    private final Random random = new Random();



    public GameState() {
        this.pigeons = new CopyOnWriteArrayList<>();
        this.meals = new CopyOnWriteArrayList<>();
        this.rottenMeals = new CopyOnWriteArrayList<>();
    }

    public boolean isCollided(Pigeon pigeon, Meal meal) {
        float distance = pigeon.getPosition().distanceTo(meal.getPosition());
        return distance < (radiusPigeon + radiusMeal);
    }


    public void updateFlyingPigeons() {
        // Gestion de si les pigeons doivent voler ou non
        if(meals.isEmpty()) {
            for (Pigeon pigeon : pigeons) {
                pigeon.stopPigeon();
            }
        } else {
            for (Pigeon pigeon : pigeons) {
                pigeon.startPigeon();
            }
        }
    }

    public void updatefreshnessMeals() {
        // Gestion de la fraîcheur de la nourriture
        // On ne doit pas supprimer d'éléments directement pendant l'itération.
        // Collecter d'abord les meals à retirer, puis effectuer la suppression
        // dans un bloc synchronisé pour garantir l'atomicité.
        java.util.List<Meal> toRemove = new java.util.ArrayList<>();
        for (Meal meal : meals) {
            meal.decreaseFreshness();
            if (!meal.isFresh()) {
                toRemove.add(meal);
            }
        }

        if (!toRemove.isEmpty()) {
            synchronized (this) {
                for (Meal dead : toRemove) {
                    // Eviter les suppressions redondantes
                    if (meals.remove(dead)) {
                        rottenMeals.add(dead);
                    }
                }
            }
        }
    }

    public void updatePositionsPigeons() {
        // Position des pigeons
        for (Pigeon pigeon : pigeons) {
            if (pigeon.isAfraid()) {
                pigeon.startPigeon();
                continue; // Ne pas mettre à jour la position si le pigeon est effrayé
            }
            if (meals.isEmpty()) {
                pigeon.stopPigeon();
            }
            else {
                pigeon.startPigeon();
                // Choisir la nourriture la plus fraîche (max freshnessTicks)
                Meal freshest = null;
                int maxFreshness = Integer.MIN_VALUE;
                for (Meal m : meals) {
                    if (m.getFreshnessTicks() > maxFreshness) {
                        maxFreshness = m.getFreshnessTicks();
                        freshest = m;
                    }
                }
                if (freshest != null) {
                    pigeon.setTarget(freshest.getPosition());
                }


            }
        }
    }

    public void updateCollisions() {
        // Gestion des collisions entre pigeons et nourriture
        for (Pigeon pigeon : pigeons) {
            if(pigeon.isAfraid()) {
                continue; // Ne pas vérifier les collisions si le pigeon est effrayé
            }
            for (Meal meal : meals) {
                if(meal.isFresh()) {
                    if (isCollided(pigeon, meal)) {
                        this.eatMeal(pigeon, meal);
                    }
                }
            }
        }
    }

    public void update() {
        updateFlyingPigeons();
        updatefreshnessMeals();
        updatePositionsPigeons();
        updateCollisions();
        updateScareChance();
        if (random.nextFloat() < scareChance) {
            dispersePigeons();
        }
    }

    public void eatMeal(Pigeon pigeon, Meal meal) {
        synchronized (this) {
            // Remove only if still present; only the thread that succeeds to remove
            // should be allowed to feed the pigeon.
            boolean removed = meals.remove(meal);
            if (removed) {
                pigeon.feed();
            }
        }
    }

    private void updateScareChance() {
        // Variation aléatoire de la probabilité de dispersion
        float variation = (random.nextFloat() * 0.1f) - 0.05f; // Entre -0.05 et 0.05
        scareChance += variation;
        scareChance = Math.max(MIN_SCARE_CHANCE, Math.min(MAX_SCARE_CHANCE, scareChance));
    }

    private void dispersePigeons() {
        for (Pigeon pigeon : pigeons) {
            pigeon.setAfraid();
            // Calcule une nouvelle position aléatoire dans les bornes de la fenêtre
            float newX = random.nextFloat() * Constants.WINDOW_WIDTH;
            float newY = random.nextFloat() * Constants.WINDOW_HEIGHT;
            pigeon.setTarget(new Coordinate(newX, newY));

            // Force le pigeon à voler même sans nourriture
            pigeon.startPigeon();
        }
    }

    /**
     * Méthode de test / utilitaire : force la dispersion des pigeons.
     * Utile pour les tests unitaires.
     */
    public void forceDisperse() {
        dispersePigeons();
    }

    public List<Meal> getMeals() {
        return this.meals;
    }
    public List<Pigeon> getPigeons() {
        return this.pigeons;
    }
    public List<Meal> getRottenMeals() {
        return this.rottenMeals;
    }

    public void addPigeon(Pigeon pigeon) {
        this.pigeons.add(pigeon);
    }

    public void addMeal(Meal nourriture) {
        this.meals.add(nourriture);
    }

    public void removePigeon(Pigeon pigeon) {
        this.pigeons.remove(pigeon);
    }

    public void removeMeal(Meal meal) {
        this.meals.remove(meal);
    }


    public void drawGamestate(Graphics2D g2) {
        // Dessiner les meals
        for (Meal m : meals) {
            g2.setColor(Color.GREEN);  // Repas frais en vert
            // Dessiner le cercle pour le repas
            int x = (int)(m.getPosition().getX() - radiusMeal);
            int y = (int)(m.getPosition().getY() - radiusMeal);
            g2.fillOval(x, y, (int)(radiusMeal * 2), (int)(radiusMeal * 2));
        }

        for (Meal m : rottenMeals) {
            g2.setColor(Color.RED);  // Repas frais en vert
            // Dessiner le cercle pour le repas
            int x = (int)(m.getPosition().getX() - radiusMeal);
            int y = (int)(m.getPosition().getY() - radiusMeal);
            g2.fillOval(x, y, (int)(radiusMeal * 2), (int)(radiusMeal * 2));
        }


        // Dessiner les pigeons
        g2.setColor(Color.BLUE);
        for (Pigeon p : pigeons) {
            int x = (int)(p.getPosition().getX() - radiusPigeon);
            int y = (int)(p.getPosition().getY() - radiusPigeon);
            g2.fillOval(x, y, (int)(radiusPigeon * 2), (int)(radiusPigeon * 2));
        }
    }

    @Override
    public String toString() {
        return "GameState{" +
                "pigeons=" + pigeons +
                ", meals=" + meals +
                ", rottenMeals=" + rottenMeals +
                '}';
    }

}

