package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Couleur;
import modele.plateau.Plateau;

public class Joueur {
    private Jeu jeu;
    private ArrayList<Piece> pieces;
    private Roi roi;
    private boolean estTourActuel;
    private boolean isBlanc;
    private Couleur couleur;



    public Joueur(Jeu _jeu, boolean isBlanc) {
        this.jeu = _jeu;
        this.isBlanc = isBlanc;
        this.couleur = this.isBlanc ? Couleur.BLANC : Couleur.NOIR;
        this.estTourActuel = this.isBlanc;
        this.pieces = new ArrayList<>();
    }

    public boolean estBlanc() {
        return isBlanc;
    }

    public Joueur getAdversaire() {
        return estBlanc() ? jeu.getJ2() : jeu.getJ1();
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public ArrayList<Coup> getCoupsValides() {
        ArrayList<Coup> coupsValides = new ArrayList<>();
    
        for (Piece piece : pieces) {
            ArrayList<Case> coupsPossibles = piece.calculerDeplacementsPossibles();
            
            for (Case caseCible : coupsPossibles) {
                Coup coup = new Coup(piece.getPosition(), caseCible);  // Create a Coup object for each move
                if (isCoupValide(coup)) {
                    coupsValides.add(coup);
                }
            }
        }
    
        return coupsValides;
    }

    private boolean isCoupValide(Coup coup) {
        Plateau plateau = jeu.getPlateau();
        return plateau.validerCoup(coup, this);
    }

    public Coup getCoup() {
        synchronized (this) {
            while (jeu.coupRecu == null || !estTourActuel) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Coup coup = jeu.coupRecu;
        jeu.coupRecu = null; // Réinitialiser le coup pour la prochaine attente
        return coup;
    }
    
    public Roi getRoi() {
        return roi;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public void setRoi(Roi roi) {
        this.roi = roi;
    }

    public boolean estTourActuel() {
        return estTourActuel;
    }

    public void setTourActuel(boolean estTourActuel) {
        this.estTourActuel = estTourActuel;
    }

    public void initialiserPieces(Plateau plateau, boolean estBlanc) {
        pieces.clear();
        
        int lignePions = estBlanc ? 6 : 1;
        int lignePieces = estBlanc ? 7 : 0;
    
        // Ajouter les pions
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            Piece pion = new Pion(plateau.getCases()[x][lignePions], this);
            pieces.add(pion);
        }
    
        // Ajouter les pièces majeures
        pieces.add(new Tour(plateau.getCases()[0][lignePieces], this));
        pieces.add(new Tour(plateau.getCases()[7][lignePieces], this));
        pieces.add(new Cavalier(plateau.getCases()[1][lignePieces], this));
        pieces.add(new Cavalier(plateau.getCases()[6][lignePieces], this));
        pieces.add(new Fou(plateau.getCases()[2][lignePieces], this));
        pieces.add(new Fou(plateau.getCases()[5][lignePieces], this));
        pieces.add(new Reine(plateau.getCases()[3][lignePieces], this));
        
        // Ajouter le Roi
        roi = new Roi(plateau.getCases()[4][lignePieces], this);
        pieces.add(roi);
    }
    
}
