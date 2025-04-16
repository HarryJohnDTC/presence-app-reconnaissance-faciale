package com.nirinfo.reconnaissancefacialeapp;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import javax.swing.SwingUtilities;

public class ReconnaissanceFacialeApp {

    
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Accueil accueil = new Accueil();
                accueil.setVisible(true);
            }
        });
    }



    // Méthode pour comparer les images en utilisant leurs histogrammes
    public static double compareImages(Mat img1, Mat img2) {
        // Convertir les images en échelle de gris avant de calculer les histogrammes
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2GRAY);

        // Calcul des histogrammes
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
        Imgproc.calcHist(Collections.singletonList(img1), new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), new MatOfFloat(0, 256));
        Imgproc.calcHist(Collections.singletonList(img2), new MatOfInt(0), new Mat(), hist2, new MatOfInt(256), new MatOfFloat(0, 256));

        // Normaliser les histogrammes
        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);

        // Comparaison des histogrammes (méthode de corrélation)
        return Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_CORREL);
    }
}
