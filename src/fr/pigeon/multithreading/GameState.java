package fr.pigeon.multithreading;
import fr.pigeon.entite.Pigeon;
import fr.pigeon.entite.Nourriture;
import java.util.ArrayList;
import java.util.List;

/* * Represents the state of the game, including pigeons and food items.
 */

public class GameState {
    private List<Pigeon> pigeons;
    private List<Meal> meals;


    public GameState() {
        this.pigeons = new ArrayList<>();
        this.meals = new ArrayList<>();
    }

    public void addPigeon(Pigeon pigeon) {
        this.pigeons.add(pigeon);
    }



    public void addNourriture(Meal nourriture) {
        this.meals.add(nourriture);
    }
}

