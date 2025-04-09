package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Jeu extends Thread {
    private Plateau plateau;
    private Joueur j1;
    private Joueur j2;
    protected Coup coupRecu;
    private Joueur joueurActuel;
    private final Object verrouPlateau = new Object();

    public Jeu() {
        // Initialisation des joueurs
        j1 = new Joueur(this, true, "Joueur 1");
        j2 = new Joueur(this, false, "Joueur 2");
        joueurActuel = j1;

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
        coupRecu = c;

        synchronized (this) {
            notify();
        }
        System.out.println("hello");
    }

    public void appliquerCoup(Coup coup) {
        // Vérification des erreurs
        if (coup == null) {
            System.out.println("Coup invalide: coup null");
            return;
        }
        if (coup.getDep() == null || coup.getArr() == null) {
            System.out.println("Coup invalide: case de départ ou d'arrivée null");
            return;
        }
        Piece piece = coup.getDep().getPiece();
        if (piece == null) {
            System.out.println("Coup invalide: aucune pièce sur la case de départ (" + coup.getDep().getX() + ", " + coup.getDep().getY() + ")");
            return;
        }
        Joueur joueur = piece.getJoueur();
        if (joueur == null) {
            System.out.println("Coup invalide: le joueur est null");
            return;
        }

        // Affichage du coup
        System.out.println("Tentative de déplacement de " + piece.getNom() + " par " + joueur.getNom() +
                           " de (" + coup.getDep().getX() + "," + coup.getDep().getY() + ") vers (" +
                           coup.getArr().getX() + "," + coup.getArr().getY() + ")");

        // Vérification de la validité du coup
        boolean coupValide = plateau.validerCoup(coup, joueur);
        if (coupValide) {
            System.out.println("Coup validé.");
            synchronized (verrouPlateau) {  // Synchronisation uniquement sur le plateau
                plateau.deplacerPiece(coup.getDep(), coup.getArr());
                plateau.notifyChange();
            }
        } else {
            System.out.println("Coup invalide selon les règles du jeu.");
        }

        // Notification après coup (en dehors de la synchronisation)
        plateau.notifyChange();
    }
    
    
    
    
    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {
        while (true) {
            synchronized (this) {
                // Attendre que ce soit le tour de j1
                while (joueurActuel != j1) {
                    try {
                        wait(); // J2 attend que j1 ait fini
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
        
                System.out.println("Tour du joueur 1.");
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
        
                joueurActuel = j2; // Changement de tour
                notifyAll(); // Notifie j2 que c'est son tour
            }
        
            synchronized (this) {
                // Attendre que ce soit le tour de j2
                while (joueurActuel != j2) {
                    try {
                        wait(); // j1 attend que j2 ait fini
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
        
                System.out.println("Tour du joueur 2.");
                // Tour du joueur 2
                Coup c = j2.getCoup();
                if (c != null) {
                    appliquerCoup(c);
                    if (estEchecEtMat(j1)) {
                        System.out.println("Échec et mat ! Joueur 2 gagne.");
                        break;
                    }
                } else {
                    System.out.println("Le joueur 2 n'a pas effectué un coup valide.");
                }
        
                joueurActuel = j1; // Changement de tour
                notifyAll(); // Notifie j1 que c'est son tour
            }
            plateau.notifyChange();
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