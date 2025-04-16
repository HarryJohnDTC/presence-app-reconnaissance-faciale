package com.nirinfo.reconnaissancefacialeapp;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.util.Optional;

public class CameraAppSwing extends JFrame {

    private JLabel videoLabel; // Affichage du flux vidéo
    private JButton captureButton; // Bouton pour capturer une image
    private VideoCapture camera;
    private Mat frame;
    private boolean capturing;
    private DatabaseHelper dbHelper; // Instance de DatabaseHelper

    public CameraAppSwing() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Charger OpenCV

        setTitle("Application de Caméra");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Initialiser les composants de l'interface
        videoLabel = new JLabel();
        captureButton = new JButton("Prendre une photo");

        // Ajouter des composants au JFrame
        setLayout(new BorderLayout());
        add(videoLabel, BorderLayout.CENTER);
        add(captureButton, BorderLayout.SOUTH);

        // Initialiser la caméra et démarrer le flux vidéo
        camera = new VideoCapture(0);
        frame = new Mat();
        capturing = true;

        dbHelper = new DatabaseHelper(); // Initialiser DatabaseHelper

        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Erreur : Impossible d'accéder à la caméra.");
            System.exit(1);
        }

        // Démarrer le flux vidéo dans un thread séparé
        new Thread(this::startVideoStream).start();

        // Action du bouton capture
        captureButton.addActionListener(e -> captureAndSavePhoto());
        
        
        JButton quitButton = new JButton("Quitter");
quitButton.addActionListener(e -> {
    // Libérer la caméra
    if (camera != null && camera.isOpened()) {
        camera.release();
    }
    // Fermer la fenêtre
    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(quitButton);
    if (frame != null) {
         
        frame.dispose(); // Ferme uniquement la fenêtre
    }
});


// Ajoutez ce bouton à la fenêtre
add(quitButton, BorderLayout.NORTH);

// Ajoutez un gestionnaire de fermeture de fenêtre
addWindowListener(new java.awt.event.WindowAdapter() {
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        camera.release();
        System.exit(0); // Fermer proprement l'application
    }
});

    }

    private void startVideoStream() {
        while (capturing) {
            if (camera.read(frame)) {
                // Convertir le Mat en BufferedImage pour l'affichage dans Swing
                BufferedImage image = matToBufferedImage(frame);
                if (image != null) {
                    ImageIcon icon = new ImageIcon(image);
                    videoLabel.setIcon(icon);
                }
            }
        }
        camera.release(); // Libérer la caméra après l'arrêt
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        // Convertir le Mat en BufferedImage (pour Swing)
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, buffer);
            byte[] byteArray = buffer.toArray();
            return ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

private void captureAndSavePhoto() {
    if (frame.empty()) {
        JOptionPane.showMessageDialog(this, "Erreur : Aucune image capturée.");
        return;
    }

    // Conversion de l'image en tableau d'octets
    Optional<byte[]> photoBytes = convertToBytes(frame);
    if (photoBytes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Erreur : Impossible de convertir l'image.");
        return;
    }

    // Demander le nom de l'utilisateur
    String userName = JOptionPane.showInputDialog(this, "Entrez le nom de l'utilisateur :");
    if (userName == null || userName.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Erreur : Nom d'utilisateur invalide.");
        return;
    }

    // Demander l'email de l'utilisateur
    String userEmail = JOptionPane.showInputDialog(this, "Entrez l'email de l'utilisateur :");
    if (userEmail == null || userEmail.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Erreur : Email invalide.");
        return;
    }

    // Enregistrement dans la base de données
    boolean success = dbHelper.insertUserWithPhotoAndEmail(userName, userEmail, photoBytes.get());
    if (success) {
        JOptionPane.showMessageDialog(this, "Photo sauvegardée avec succès pour l'utilisateur : " + userName);
    } else {
        JOptionPane.showMessageDialog(this, "Erreur : Échec de l'enregistrement de la photo.");
    }
}


    private Optional<byte[]> convertToBytes(Mat image) {
        try {
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, matOfByte);
            return Optional.of(matOfByte.toArray());
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion de l'image : " + e.getMessage());
            return Optional.empty();
        }
    }

}
