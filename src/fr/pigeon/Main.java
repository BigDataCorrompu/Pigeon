package fr.pigeon;

import fr.pigeon.affichage.Display;
import fr.pigeon.multithreading.Simulation;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crée la simulation
            Simulation simulation = new Simulation();
            AtomicReference<Simulation> simRef = new AtomicReference<>(simulation);

            // Crée le panneau d'affichage
            Display display = new Display();
            display.setSimulation(simulation); // on lie la vue au modèle

            // Panneau racine pour contenir l'affichage et les contrôles
            JPanel root = new JPanel(new BorderLayout());
            root.add(display, BorderLayout.CENTER);

            // Barre de contrôle avec bouton Restart
            JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton restartBtn = new JButton("Restart");
            controls.add(restartBtn);
            root.add(controls, BorderLayout.SOUTH);

            // Crée la fenêtre principale
            JFrame frame = new JFrame("Pigeon Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(root);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Action du bouton Restart : arrête la simulation courante et en démarre une nouvelle
            restartBtn.addActionListener(ev -> {
                // Stopper proprement l'ancienne simulation
                try {
                    Simulation old = simRef.get();
                    if (old != null) old.stop();
                } catch (Exception ignored) {}

                // Créer et démarrer une nouvelle simulation
                Simulation newSim = new Simulation();
                simRef.set(newSim);
                display.setSimulation(newSim);
                new Thread(newSim).start();
            });

            // Démarre la simulation initiale dans un thread séparé
            new Thread(simulation).start();
        });
    }
}
