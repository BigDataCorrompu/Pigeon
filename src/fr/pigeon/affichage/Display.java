package fr.pigeon.affichage;
import fr.pigeon.entity.Coordinate;
import fr.pigeon.multithreading.Simulation;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Display extends JPanel {
    // Display code goes here
    private int x = 0;
    private BufferedImage pigeonImage;
    private Simulation simulation;
    private final int heightWindow = 600;
    private final int widthWindow = 800;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Display() {
        setPreferredSize(new Dimension(widthWindow, heightWindow));
        setBackground(Color.WHITE);
        setFocusable(true); // Ajout pour que le KeyListener fonctionne
        requestFocusInWindow(); // Demande le focus

        // Sprite arriere plan
        try {
            pigeonImage = ImageIO.read(new File("src/fr/resources/pigeon.png"));
        } catch (IOException ignored) {}

        // Timer pour l'animation du pigeon
        new Timer(16, e -> {
            x += 2;
            if (x > getWidth()) x = -100;
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

        // Key listener pour ajouter un pigeon avec une touche
        addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        simulation.spawnRandomPigeon(widthWindow, heightWindow);
                        repaint(); 
                    }
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

        // Si une simulation est liée, dessine ses entités
        if (simulation != null) {
            simulation.drawEntities(g2);
        }

        // Dessiner le sprite du pigeon si nécessaire
        if (pigeonImage != null) {
            g2.drawImage(pigeonImage, x, 100, this);
        }
        
        drawLegend(g2);

        g2.dispose();
    }

    

}