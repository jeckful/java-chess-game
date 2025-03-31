/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.jeu.Coup;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.jeu.Roi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class Plateau extends Observable {

    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;

    private HashMap<Case, Point> map = new HashMap<Case, Point>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées

    // Add references to players
    private Joueur j1;
    private Joueur j2;

    public Plateau(Joueur j1, Joueur j2) {
        this.j1 = j1;
        this.j2 = j2;
        initPlateauVide();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    private void initPlateauVide() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this, x, y);
                map.put(grilleCases[x][y], new Point(x, y));
                System.out.println("Created case at: " + x + ", " + y);
            }
        }
    }

    public void placerPieces() {
        // Initialisation des pièces du joueur 1 (par exemple)
        Roi roiJoueur1 = new Roi(this);
        Case caseRoiJoueur1 = grilleCases[4][7];
        roiJoueur1.allerSurCase(caseRoiJoueur1);
        System.out.println("Placed " + roiJoueur1 + " on case " + caseRoiJoueur1);
    
        // Initialisation des pièces du joueur 2 (par exemple)
        Roi roiJoueur2 = new Roi(this);
        Case caseRoiJoueur2 = grilleCases[4][0];
        roiJoueur2.allerSurCase(caseRoiJoueur2);
        System.out.println("Placed " + roiJoueur2 + " on case " + caseRoiJoueur2);
    
        setChanged();
        notifyObservers();
    }

    public void arriverCase(Case c, Piece p) {
        c.p = p;
    }

    public void deplacerPiece(Case c1, Case c2) {
        if (c1.p != null) {
            c1.p.allerSurCase(c2);
        }
        setChanged();
        notifyObservers();
    }

    public Case caseDansDirection(Case c, Direction direction) {
        // Récupérer la position de la case
        Point pos = map.get(c);
        int x = pos.x;
        int y = pos.y;

        // Calculer la nouvelle position en fonction de la direction
        switch (direction) {
            case haut:
                y--; // Déplacement vers le haut
                break;
            case bas:
                y++; // Déplacement vers le bas
                break;
            case gauche:
                x--; // Déplacement vers la gauche
                break;
            case droite:
                x++; // Déplacement vers la droite
                break;
            default:
                return null; // Si la direction est invalide, retourner null
        }

        // Vérifier si la position est valide
        if (contenuDansGrille(new Point(x, y))) {
            return grilleCases[x][y];
        }
        return null; // Si la case n'est pas dans les limites du plateau
    }

    /** Indique si p est contenu dans la grille */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    
    private Case caseALaPosition(Point p) {
        Case retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleCases[p.x][p.y];
        }
        return retour;
    }

    public boolean validerCoup(Coup coup, Joueur joueur) {
        Piece piece = coup.getDep().getPiece();  // Use the getter for dep
        
        // Vérifier que le mouvement est valide pour cette pièce
        if (piece.calculerDeplacementsPossibles().contains(coup.getArr())) {  // Use the getter for arr
            // Appliquer le coup
            deplacerPiece(coup.getDep(), coup.getArr());
            
            // Vérifier si le coup met le joueur en échec
            if (estEnEchec(joueur)) {
                // Annuler le coup si cela met le joueur en échec
                deplacerPiece(coup.getArr(), coup.getDep());
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean estEnEchec(Joueur joueur) {
        Roi roi = joueur.getRoi();
        
        // Get the opponent's pieces by passing both j1 and j2 to the method
        for (Piece piece : getToutesLesPiecesAdverses(joueur)) {
            if (piece.peutAttaquer(roi.getCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean estEnEchecAprèsCoup(Coup coup, Joueur joueur) {
        // Étape 1 : Appliquer temporairement le coup
        Piece piece = coup.getDep().getPiece();  // Use the getter for dep
        Case caseArrivee = coup.getArr();  // Use the getter for arr
        
        // Déplacer la pièce vers la case d'arrivée
        piece.allerSurCase(caseArrivee);
        
        // Étape 2 : Vérifier si le roi du joueur est en échec
        boolean roiEnEchec = false;
        for (Piece p : getToutesLesPiecesAdverses(joueur)) {
            if (p.peutAttaquerRoi()) { // Méthode qui vérifie si une pièce peut attaquer le roi
                roiEnEchec = true;
                break;
            }
        }
    
        // Étape 3 : Annuler le coup simulé (remettre la pièce dans sa position d'origine)
        piece.allerSurCase(coup.getDep());  // Use the getter for dep
        
        return roiEnEchec;
    }

    // Update the getToutesLesPiecesAdverses method to take j1 and j2 as parameters
    public ArrayList<Piece> getToutesLesPiecesAdverses(Joueur joueur) {
        ArrayList<Piece> piecesAdverses = new ArrayList<>();
        
        // Add pieces of the opposing player
        if (joueur == j1) {
            // Add all pieces of player 2 (j2)
            piecesAdverses.addAll(j2.getPieces());  // Assuming j2 has a `getPieces()` method
        } else {
            // Add all pieces of player 1 (j1)
            piecesAdverses.addAll(j1.getPieces());  // Assuming j1 has a `getPieces()` method
        }
        
        return piecesAdverses;
    }
}
