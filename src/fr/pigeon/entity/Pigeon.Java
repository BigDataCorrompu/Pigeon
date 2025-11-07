package fr.pigeon.entity;

import fr.pigeon.utils.Constants;

public class Pigeon extends Entity implements Runnable {
    // Pour que le thread n'utilise pas une variable dans le cache non modifiée
    private volatile boolean isRunning = true; // Le thread est il en cours d'exécution ?
    private volatile boolean isFlying; // LE pigeon est-il en vol ?
    private volatile boolean isAfraid;
    // Référence sur le thread qui exécute ce pigeon (permet arrêt propre)
    private volatile Thread threadRef;
    private static final float SPEED = 1.0f; // Vitesse de déplacement du pigeon
    private int mealsEaten;
    private Coordinate target;

    // Gestion de la peur
    private static final long FEAR_DURATION = 5000;      // Durée de la peur
    private static final long FEAR_COOLDOWN = 4000;      // Délai minimum entre deux peurs
    private long fearStartTime = 0;                      // Quand la peur a commencé
    private long lastScareTime = 0;

    public Pigeon(Coordinate position) {
        super(position);
        this.isFlying = false;
    }

    // Logique de déplacement vers la cible
    public void moveTowardTarget(Coordinate targetPos) {
        Coordinate myPos = this.getPosition();

        // Calcul de la direction
        float dx = targetPos.getX() - myPos.getX();
        float dy = targetPos.getY() - myPos.getY();
        float angle = (float) Math.atan2(dy, dx);

        // Déplacement
        float newX = myPos.getX() + SPEED * (float) Math.cos(angle);
        float newY = myPos.getY() + SPEED * (float) Math.sin(angle);

    // clamp au bord de la fenêtre en utilisant les constantes globales
    newX = Math.max(0, Math.min(newX, Constants.WINDOW_WIDTH));
    newY = Math.max(0, Math.min(newY, Constants.WINDOW_HEIGHT));

        this.position.setCoordinate(new Coordinate(newX, newY));
    }

    public void setTarget(Coordinate target) {
        this.target = target;
    }

    /** Retourne la cible actuelle du pigeon (peut être null). */
    public Coordinate getTarget() {
        return this.target;
    }

    /** Indique si le pigeon est en train de voler (true) ou au repos (false). */
    public boolean isFlying() {
        return this.isFlying;
    }

    /** Retourne le nombre de repas consommés par ce pigeon. */
    public int getMealsEaten() {
        return this.mealsEaten;
    }

    public void startThread() {
        // Ne pas créer plusieurs threads pour le même pigeon
        if (threadRef != null && threadRef.isAlive()) return;
        threadRef = new Thread(this, "Pigeon-Thread");
        threadRef.start();
    }

    /**
     * Arrête proprement le thread du pigeon.
     * Met isRunning à false et interrompt le thread si nécessaire.
     */
    public void stopThread() {
        this.isRunning = false;
        if (threadRef != null) {
            threadRef.interrupt();
        }
    }

    public void startPigeon() {
        this.isFlying = true;
    }

    public void stopPigeon() {
        this.isFlying = false;
    }

    public void feed() {
        mealsEaten++;
    }

    public boolean isAfraid() {
        return isAfraid;
    }

    public boolean calmPigeon() {
        this.isAfraid = false;
        return this.isAfraid;
    }

    public void setAfraid() {
        long now = System.currentTimeMillis();

        // Vérifie si le pigeon peut être effrayé à nouveau
        if (now - lastScareTime < FEAR_COOLDOWN) {
            return; // Ignore si trop tôt
        }

        this.isAfraid = true;
        this.fearStartTime = now;
        this.lastScareTime = now;
    }

    private void checkAndUpdateFearState() {
        if (isAfraid && System.currentTimeMillis() - fearStartTime >= FEAR_DURATION) {
            calmPigeon();
            this.isFlying = true;
            this.target = null;
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                checkAndUpdateFearState(); // Vérifier l'état de peur
                if (isFlying && target != null) {
                    // Logique du pigeon pendant qu'il vole
                    this.moveTowardTarget(this.target);
                }
                // Dormir à chaque itération pour éviter le busy-wait (réduire l'utilisation CPU)
                Thread.sleep(16); // ~60 fps
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Pigeon{" +
                "position=" + getPosition() +
                ", isFlying=" + isFlying +
                ", mealsEaten=" + mealsEaten +
                ", target=" + target +
                '}';
    }

}
