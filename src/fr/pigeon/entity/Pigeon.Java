package fr.pigeon.entity;


public class Pigeon extends Entity implements Runnable {
    // Pour que le thread n'utilise pas une variable dans le cache non modifiée
    private volatile boolean isFlying; 
    private static final float SPEED = 1.0f; // Vitesse de déplacement du pigeon


    public Pigeon(Coordinate position) {
        super(position);
        this.isRunning = false;
    }


    // Logique de déplacement vers la cible
    private void moveTowardTarget(Meal target) {
        Coordinate targetPos = taget.getPosition();
        Coordinate myPos = this.getPosition();

        // Calcul de la direction
        float dx = targetPos.getX() - myPos.getX();
        float dy = targetPos.getY() - myPos.getY();
        angle = (float) Math.atan2(dy, dx);

        // Déplacement
        float newX = myPos.getX() + SPEED * Math.cos(angle);
        float newY = myPos.getY() + SPEED * Math.sin(angle);

        this.setPosition(new Coordinate(newX, newY));    
    }

    public void startPigeon() {
        this.isFlying = true;
        new Thread(this).start();
    }

    public void stopPigeon() {
        this.isFlying = false;
    }


    @Override
    public void run() {
        while (isFlying) {
            try {
                // Logique du pigeon pendant qu'il vole
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        }



}