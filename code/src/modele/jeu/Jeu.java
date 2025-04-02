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
        // Initialisation des joueurs
        j1 = new Joueur(this, true);
        j2 = new Joueur(this, false);

        // Création du plateau
        plateau = new Plateau(j1, j2);
        plateau.placerPieces();

        start();
    }

    public Joueur getJ1() {
        return j1;
    }

    public Joueur getJ2() {
        return j2;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void placerPieces() {
        plateau.placerPieces();
    }

    public void envoyerCoup(Coup c) {
        synchronized (c.getPiece().getJoueur()) {
            coupRecu = c;
            c.getPiece().getJoueur().notify();
        }
        appliquerCoup(c);
        System.out.println("Coup reçu et appliqué !");
    }
    

    public void appliquerCoup(Coup coup) {
        Joueur joueur = coup.getPiece().getJoueur();
        if (plateau.validerCoup(coup, joueur)) {
            plateau.deplacerPiece(coup.getDep(), coup.getArr());
            System.out.println("Coup validé.");
            plateau.notifyChange();
            changerTour();
            // Notifier l'autre joueur que c'est son tour
            synchronized (joueur) {
                joueur.notify();  // Notifier le joueur après avoir effectué un coup
            }
        } else {
            System.out.println("Coup invalide.");
        }
    }
    public void changerTour() {
        // Ensure players are notified after their turns
        synchronized (j1) {
            j1.notify();
        }
        synchronized (j2) {
            j2.notify();
        }
    }

    
    public void run() {
        jouerPartie();
    }
    public void jouerPartie() {
        while (true) {
            // Tour du joueur 1
            Coup c = j1.getCoup();
            if (c != null) {
                appliquerCoup(c);
                if (estEchecEtMat(j2)) {
                    System.out.println("Échec et mat ! Joueur 1 gagne.");
                    break;
                }
            } else {
                System.out.println("Le joueur 1 n'a pas effectué un coup valide.");
            }
    
            // Tour du joueur 2
            c = j2.getCoup();
            if (c != null) {
                appliquerCoup(c);
                if (estEchecEtMat(j1)) {
                    System.out.println("Échec et mat ! Joueur 2 gagne.");
                    break;
                }
            } else {
                System.out.println("Le joueur 2 n'a pas effectué un coup valide.");
            }
        }
    }    

    public boolean estEchecEtMat(Joueur joueur) {
        if (plateau.estEnEchec(joueur)) {
            for (Coup coup : joueur.getCoupsValides()) {
                if (!plateau.estEnEchecAprèsCoup(coup, joueur)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public ArrayList<Piece> getPiecesAdverses(Joueur joueur) {
        return plateau.getToutesLesPiecesAdverses(joueur);
    }
}