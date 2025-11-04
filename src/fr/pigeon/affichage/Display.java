package fr.pigeon.affichage;
import fr.pigeon.multithreading.Simulation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;

public class Display extends JPanel {
    // Display code goes here
    private int x = 0;
    private BufferedImage pigeonImage;
    private Simulation simulation;

    public Display() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // Sprite arriere plan
        try {
            pigeonImage = ImageIO.read(new File("src/resources/pigeon.png"));
        } catch (IOException ignored) {}

        // Timer pour l'animation du pigeon
        new Timer(16, e -> {
            x += 2;
            if (x > getWidth()) x = -100;
            repaint();
        }).start();


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            // antialiasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // fond dégradé
            g2.setPaint(new GradientPaint(0, 0, Color.DARK_GRAY, 0, getHeight(), Color.GRAY));
            g2.fillRect(0, 0, getWidth(), getHeight());

            

        
    }
}