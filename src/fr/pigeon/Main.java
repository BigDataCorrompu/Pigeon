package fr.pigeon;

import fr.pigeon.affichage.Display;
import fr.pigeon.multithreading.Simulation;
import javax.swing.*;



public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crée la simulation
            Simulation simulation = new Simulation();

            // Crée le panneau d'affichage
            Display display = new Display();
            display.setSimulation(simulation); // on lie la vue au modèle

            // Crée la fenêtre principale
            JFrame frame = new JFrame("Pigeon Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(display);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Démarre la simulation dans un thread séparé
            new Thread(simulation).start();
        });
    }
}
