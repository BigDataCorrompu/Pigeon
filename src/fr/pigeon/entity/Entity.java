package fr.pigeon.entity;

public class Entity {
    private AtomEntity position;

    public Entity(Coordinate position) {
        this.position = new AtomEntity(position);
    }

    public AtomEntity getPosition() {
        return position;
    }
    
    public void setPosition(AtomEntity position) {
        this.position = position;
    }

}