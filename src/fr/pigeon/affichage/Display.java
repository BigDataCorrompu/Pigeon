package fr.pigeon.affichage;
import fr.pigeon.entity.Coordinate;
import fr.pigeon.multithreading.Simulation;
import fr.pigeon.entity.Pigeon;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Display extends JPanel {
    // Display code goes here
    private int x = 0;
    private BufferedImage pigeonImage;
    // simple animation phase (used when no sprite frames available)
    private float wingPhase = 0f;
    private Simulation simulation;
    private final int heightWindow = 600;
    private final int widthWindow = 800;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Display() {
        setPreferredSize(new Dimension(widthWindow, heightWindow));
        setBackground(Color.WHITE);
    setFocusable(true); // Permet de recevoir le focus
    // Ne pas forcer le focus ici de façon bloquante; on demandera le focus lors du clic

        // Charger le sprite depuis le classpath (resources)
        try (InputStream in = Display.class.getResourceAsStream("/fr/pigeon/resources/pigeon.png")) {
            if (in != null) {
                pigeonImage = ImageIO.read(in);
            }
        } catch (IOException ignored) {}

        // Timer pour l'animation générale (déplacement du sprite décoratif et phases)
        new Timer(16, e -> {
            x += 2;
            if (x > getWidth()) x = -100;
            wingPhase += 0.2f;
            if (wingPhase > Float.MAX_VALUE - 1) wingPhase = 0f;
            repaint();
        }).start();

        // Mouse listener corrigé
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (simulation != null) {
                    Coordinate clickPosition = new Coordinate(e.getX(), e.getY());
                    simulation.addMeal(clickPosition);
                    repaint(); // Redessiner après ajout
                }
            }
        });

        // Key bindings (WHEN_IN_FOCUSED_WINDOW) : plus robuste que KeyListener
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "spawnPigeon");
        getActionMap().put("spawnPigeon", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (simulation != null) {
                    simulation.spawnRandomPigeon(widthWindow, heightWindow);
                    repaint();
                }
            }
        });

        // Ensure the panel requests focus when clicked so focus-based interactions work
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }   

    private void drawLegend(Graphics2D g2) {
        int startX = 20;
        int startY = 20;
        int lineHeight = 20;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        g2.drawString("Clic droit : ajouter de la nourriture à la position du clic", startX, startY);
        g2.drawString("Espace : Apparition aléatoire d'un pigeon", startX, startY + lineHeight);

        // Pigeon bleu
        g2.setColor(Color.BLUE);
        g2.fillOval(startX + 10, startY + lineHeight * 3 - 10, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString("Pigeon", startX + 30, startY + lineHeight * 3);

        // Nourriture verte
        g2.setColor(Color.GREEN);
        g2.fillOval(startX + 10, startY + lineHeight * 4 - 10, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString("Nourriture fraîche", startX + 30, startY + lineHeight * 4);

        // Nourriture rouge
        g2.setColor(Color.RED);
        g2.fillOval(startX + 10, startY + lineHeight * 5 - 10, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString("Nourriture pourrie", startX + 30, startY + lineHeight * 5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // fond dégradé
        g2.setPaint(new GradientPaint(0, 0, Color.DARK_GRAY, 0, getHeight(), Color.GRAY));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Dessiner les meals et autres éléments via GameState
        if (simulation != null) {
            simulation.drawEntities(g2);
        }

        // Dessiner les pigeons en sprites tournés vers leur cible (ou fallback)
        if (simulation != null) {
            for (fr.pigeon.entity.Pigeon p : simulation.getGameState().getPigeons()) {
                float px = p.getPosition().getX();
                float py = p.getPosition().getY();
                Coordinate target = p.getTarget();
                double angle = 0.0;
                if (target != null) {
                    double dx = target.getX() - px;
                    double dy = target.getY() - py;
                    angle = Math.atan2(dy, dx);
                }

                int drawX = (int) px;
                int drawY = (int) py;

                if (pigeonImage != null) {
                    int iw = pigeonImage.getWidth();
                    int ih = pigeonImage.getHeight();
                    AffineTransform at = new AffineTransform();
                    at.translate(drawX - iw/2.0, drawY - ih/2.0);
                    at.rotate(angle, iw/2.0, ih/2.0);
                    g2.drawImage(pigeonImage, at, this);
                } else {
                    // Fallback: draw a rotated triangle to indicate heading
                    g2.setColor(Color.BLUE);
                    AffineTransform old = g2.getTransform();
                    g2.translate(drawX, drawY);
                    g2.rotate(angle);
                    int[] xs = {-8, 8, -8};
                    int[] ys = {-6, 0, 6};
                    g2.fillPolygon(xs, ys, 3);
                    g2.setTransform(old);
                }
            }
        }

        // Décaler le dessin de légende
        drawLegend(g2);

        g2.dispose();
    }

    

}