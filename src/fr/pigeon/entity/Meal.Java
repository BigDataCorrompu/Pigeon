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

    @Override
    public String toString() {
        return "Meal{" +
                "position=" + getPosition() +
                ", freshnessTicks=" + freshnessTicks +
                '}';
    }

}