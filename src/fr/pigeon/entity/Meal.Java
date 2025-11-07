package fr.pigeon.entity;


public class Meal extends Entity {
    private int freshnessTicks;

    public Meal(Coordinate position) {
        super(position);
        this.freshnessTicks = 100;
    }


    public void decreaseFreshness() {
        if (freshnessTicks > 0) {
            freshnessTicks--;
        }
    }

    public boolean isFresh() {
        return freshnessTicks > 0;
    }

    /**
     * Retourne le nombre de ticks restants (fraîcheur) de la nourriture.
     * Utilisé pour déterminer la nourriture la plus fraîche.
     */
    public int getFreshnessTicks() {
        return freshnessTicks;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "position=" + getPosition() +
                ", freshnessTicks=" + freshnessTicks +
                '}';
    }

}
