package fr.pigeon.entity;


public class Meal extends Entity {
    private int idMeal;

    public Meal(Coordinate position) {
        this.position = new AtomEntity(position);
        this.idMeal = IdGenerator.getInstance().getNextId();
    }


    public int getIdMeal() {
        return idMeal;
    }

}