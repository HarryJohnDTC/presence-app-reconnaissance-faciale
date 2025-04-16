package com.nirinfo.reconnaissancefacialeapp;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import org.opencv.core.MatOfByte;

public class Camera extends JFrame {
    private VideoCapture camera;
    private JLabel videoLabel;
    private boolean isRunning;

    public Camera() {
        super("Camera Viewer");

        // Initialisation de la caméra
        camera = new VideoCapture(0); // Caméra par défaut
        if (!camera.isOpened()) {
            JOptionPane.showMessageDialog(this, "Erreur : Impossible d'accéder à la caméra.", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Configuration de l'interface utilisateur
        videoLabel = new JLabel();
        JButton captureButton = new JButton("Prendre la photo");
        JButton quitButton = new JButton("Quitter");

        captureButton.addActionListener(e -> captureImage());
        quitButton.addActionListener(e -> quitCamera());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        buttonPanel.add(quitButton);

        setLayout(new BorderLayout());
        add(videoLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Lancer le flux vidéo
        isRunning = true;
        startCameraFeed();
    }

    private void startCameraFeed() {
        Thread videoThread = new Thread(() -> {
            Mat frame = new Mat();
            while (isRunning) {
                if (camera.read(frame)) {
                    // Convertir le Mat en BufferedImage
                    BufferedImage image = matToBufferedImage(frame);
                    if (image != null) {
                        SwingUtilities.invokeLater(() -> videoLabel.setIcon(new ImageIcon(image)));
                    }
                }
            }
            frame.release();
        });
        videoThread.start();
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();

        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    private void captureImage() {
        Mat frame = new Mat();
        if (camera.read(frame)) {
            String filename = "captured_image.jpg";
            Imgcodecs.imwrite(filename, frame);
            JOptionPane.showMessageDialog(this, "Image capturée et enregistrée sous : " + filename, "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erreur : Impossible de capturer l'image.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        frame.release();
    }

    void quitCamera() {
        isRunning = false;
        camera.release();
        dispose();
    }
    
    public byte[] captureImageAsBytes() {
    Mat frame = new Mat();
    if (camera.read(frame)) { // videoCapture est un objet VideoCapture
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return buffer.toArray();
    } else {
        System.out.println("Erreur : Impossible de capturer l'image.");
        return null;
    }
}

}
