package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Jeu extends Thread {
    private Plateau plateau;
    private Joueur j1;
    private Joueur j2;
    protected Coup coupRecu;

    public Jeu() {
        // Initializing players
        j1 = new Joueur(this);
        j2 = new Joueur(this);

        plateau = new Plateau(j1,j2);
        plateau.placerPieces();

        // Make sure to initialize Roi here for both players
        j1.setRoi(new Roi(plateau));
        j2.setRoi(new Roi(plateau));

        start();
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void placerPieces() {
        plateau.placerPieces();
    }

    public void envoyerCoup(Coup c) {
        coupRecu = c;
        synchronized (this) {
            notify();
        }
        System.out.println("Coup recu !");
    }

    public void appliquerCoup(Coup coup) {
        // Vérifier si le coup est valide
        if (plateau.validerCoup(coup, j1)) {
            // Appliquer le coup au plateau
            plateau.deplacerPiece(coup.getDep(), coup.getArr());
            System.out.println("Coup validé.");
        } else {
            System.out.println("Coup invalide.");
        }
    }

    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {
        while (true) {
            // Tour de j1
            Coup c = j1.getCoup();
            appliquerCoup(c);
            if (estEchecEtMat(j1)) {
                System.out.println("Échec et mat pour joueur 1");
                break; // Fin de la partie
            }

            // Tour de j2
            c = j2.getCoup();
            appliquerCoup(c);
            if (estEchecEtMat(j2)) {
                System.out.println("Échec et mat pour joueur 2");
                break; // Fin de la partie
            }
        }
    }

    public boolean estEchecEtMat(Joueur joueur) {
        // Vérifier si le joueur est en échec
        if (plateau.estEnEchec(joueur)) {
            for (Coup coup : joueur.getCoupsValides()) {
                // Correct usage of estEnEchecAprèsCoup with both Coup and Joueur
                if (!plateau.estEnEchecAprèsCoup(coup, joueur)) {
                    return false;  // Il existe un coup valide pour sortir de l'échec
                }
            }
            return true;  // Aucun coup valide, échec et mat
        }
        return false;
    }
    

    public ArrayList<Piece> getPiecesAdverses(Joueur joueur) {
        return plateau.getToutesLesPiecesAdverses(joueur);
    }
}