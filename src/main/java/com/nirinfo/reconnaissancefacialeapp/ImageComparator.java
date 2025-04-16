package com.nirinfo.reconnaissancefacialeapp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageComparator {

    /**
     * Compare deux images et vérifie si leur similarité dépasse un seuil donné,
     * avec une tolérance sur les couleurs des pixels.
     *
     * @param capturedImage Image capturée sous forme de tableau de bytes.
     * @param databaseImage Image enregistrée dans la base de données sous forme de tableau de bytes.
     * @param threshold Seuil de similarité en pourcentage (0-100).
     * @return true si la similarité est supérieure ou égale au seuil, sinon false.
     */
    public boolean compareImagesWithThreshold(byte[] capturedImage, byte[] databaseImage, double threshold) {
        if (capturedImage == null || databaseImage == null) {
            return false; // Images absentes
        }

        try {
            BufferedImage img1 = ImageIO.read(new ByteArrayInputStream(capturedImage));
            BufferedImage img2 = ImageIO.read(new ByteArrayInputStream(databaseImage));

            if (img1 == null || img2 == null) {
                return false; // Images invalides
            }

            if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
                return false; // Dimensions différentes
            }

            int totalPixels = img1.getWidth() * img1.getHeight();
            int matchingPixels = 0;
            int tolerance = 30; // Valeur de tolérance (modifiable)

            for (int y = 0; y < img1.getHeight(); y++) {
                for (int x = 0; x < img1.getWidth(); x++) {
                    int rgb1 = img1.getRGB(x, y);
                    int rgb2 = img2.getRGB(x, y);
                    if (pixelsAreSimilar(rgb1, rgb2, tolerance)) {
                        matchingPixels++;
                    }
                }
            }

            double similarity = (matchingPixels / (double) totalPixels) * 100;
            return similarity >= threshold;

        } catch (IOException e) {
            System.err.println("Erreur lors de la comparaison des images : " + e.getMessage());
        }

        return false;
    }

    /**
     * Vérifie si deux couleurs RGB sont similaires en tenant compte d'une tolérance.
     */
    private boolean pixelsAreSimilar(int rgb1, int rgb2, int tolerance) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;

        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;

        return Math.abs(r1 - r2) <= tolerance &&
               Math.abs(g1 - g2) <= tolerance &&
               Math.abs(b1 - b2) <= tolerance;
    }
}
