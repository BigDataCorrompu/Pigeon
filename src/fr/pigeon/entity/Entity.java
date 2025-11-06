package fr.pigeon.entity;

public class Entity {
    protected AtomEntity position;
    protected float radius;

    public Entity(Coordinate position) {
        this.position = new AtomEntity(position);
    }

    public Coordinate getPosition() {
        return this.position.getCoordinate();
    }
    
    public void setPosition(Coordinate position) {
        this.position.setCoordinate(position);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "position=" + position +
                '}';
    }

}