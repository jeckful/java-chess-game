package modele.plateau;

import modele.jeu.Coup;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.jeu.Roi;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Plateau {
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;

    private HashMap<Case, Point> map = new HashMap<>();
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y];

    private Joueur j1;
    private Joueur j2;
    private Joueur joueurActuel;

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void notifyChange() {
        support.firePropertyChange("plateauUpdated", null, this);
    }

    public Plateau(Joueur j1, Joueur j2) {
        this.j1 = j1;
        this.j2 = j2;
        initPlateauVide();
        placerPieces();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    public Case getCase(int x, int y) {
        if (x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y) {
            return grilleCases[x][y];
        }
        return null;
    }

    private void initPlateauVide() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this, x, y);
                map.put(grilleCases[x][y], new Point(x, y));
            }
        }
    }

    public void placerPieces() {
        j1.initialiserPieces(this, true);
        j2.initialiserPieces(this, false);
        notifyChange();
    }

    public void deplacerPiece(Case dep, Case arr) {
        if (dep == null || arr == null) {
            System.out.println("Erreur : case de départ ou d'arrivée null");
            return;
        }
        Piece piece = dep.getPiece();
        if (piece == null) {
            System.out.println("Erreur : aucune pièce à déplacer depuis (" + dep.getX() + ", " + dep.getY() + ")");
            return;
        }
        System.out.println("Déplacement de " + piece.getNom() + " de (" + dep.getX() + "," + dep.getY() + ") vers (" + +arr.getX() + "," + arr.getY() + ")");
        arr.setPiece(piece);
        dep.setPiece(null);
    }
    

    public boolean validerCoup(Coup coup, Joueur joueur) {
        if (coup == null) {
            System.out.println("Validation échouée : coup null");
            return false;
        }
        if (coup.getDep() == null || coup.getArr() == null) {
            System.out.println("Validation échouée : case de départ ou d'arrivée null");
            return false;
        }
        if (coup.getDep().getPiece() == null) {
            System.out.println("Validation échouée : pas de pièce sur la case de départ (" + coup.getDep().getX() + ", " + coup.getDep().getY() + ")");
            return false;
        }
        if (!coup.getDep().getPiece().getJoueur().equals(joueur)) {
            System.out.println("Validation échouée : ce n'est pas le tour de ce joueur");
            return false;
        }
        return true; // À modifier selon les règles du jeu
    }
    
    
    public ArrayList<Piece> getToutesLesPiecesAdverses(Joueur joueur) {
        Joueur adversaire = (joueur == j1) ? j2 : j1;
        return adversaire.getPieces();
    }
    

    public boolean estEnEchec(Joueur joueur) {
        Roi roi = joueur.getRoi();
        if (roi == null) {
            return false; // Impossible de vérifier l'échec sans le roi
        }
    
        Case positionRoi = roi.getPosition();
        if (positionRoi == null) {
            return false;
        }
    
        // Vérifier si une pièce adverse peut attaquer le roi
        for (Piece piece : getToutesLesPiecesAdverses(joueur)) {
            if (piece.calculerDeplacementsPossibles().contains(positionRoi)) {
                return true; // Le roi est menacé
            }
        }
        return false;
    }

    public boolean estEnEchecAprèsCoup(Coup coup, Joueur joueur) {
        // Simuler le coup sans appliquer définitivement
        Case caseDepart = coup.getDep();
        Case caseArrivee = coup.getArr();
        
        // Sauvegarder la pièce déplacée et son ancienne position
        Piece pieceDeplacee = caseDepart.getPiece();
        Case anciennePosition = pieceDeplacee.getPosition();
        
        // Déplacer la pièce
        caseDepart.quitterLaCase();
        pieceDeplacee.allerSurCase(caseArrivee);
        
        // Vérifier si le joueur est en échec après le coup
        boolean enEchec = estEnEchec(joueur);
        
        // Annuler le coup (restaurer la position initiale de la pièce)
        anciennePosition.restaurerPiece(pieceDeplacee);  // Utiliser restaurerPiece ici
        caseArrivee.quitterLaCase();
        
        return enEchec;
    }
    
    public Joueur getJoueurActuel() {
        return joueurActuel;
    }
    
    

}