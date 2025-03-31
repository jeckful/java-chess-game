package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Joueur {
    private Jeu jeu;
    private ArrayList<Piece> pieces;
    private Roi roi;

    public Joueur(Jeu _jeu) {
        jeu = _jeu;
        pieces = new ArrayList<>();
        roi = new Roi(jeu.getPlateau()); // Ensure Roi is initialized with the Plateau
        pieces.add(roi);  // Add the king to the player's pieces
    }

    public ArrayList<Coup> getCoupsValides() {
        ArrayList<Coup> coupsValides = new ArrayList<>();
    
        for (Piece piece : pieces) {
            ArrayList<Case> coupsPossibles = piece.calculerDeplacementsPossibles();
            
            for (Case caseCible : coupsPossibles) {
                Coup coup = new Coup(piece.getCase(), caseCible);  // Create a Coup object for each move
                if (isCoupValide(piece, caseCible)) {
                    coupsValides.add(coup);
                }
            }
        }
    
        return coupsValides;
    }

    private boolean isCoupValide(Piece piece, Case caseCible) {
        if (caseCible.getPiece() == null || !caseCible.getPiece().getClass().equals(piece.getClass())) {
            return !estRoiEnEchec(piece, caseCible);  // Ensure the king is not in check after the move
        }
        return false;
    }

    private boolean estRoiEnEchec(Piece piece, Case caseCible) {
        Plateau plateau = jeu.getPlateau();
        Case caseOrigine = piece.getCase();

        piece.allerSurCase(caseCible);

        boolean roiEnEchec = false;
        for (Piece p : pieces) {
            if (p instanceof Roi) {
                roiEnEchec = isRoiInCheck((Roi) p);
                break;
            }
        }

        piece.allerSurCase(caseOrigine);

        return roiEnEchec;
    }

    private boolean isRoiInCheck(Roi roi) {
        for (Piece p : jeu.getPlateau().getToutesLesPiecesAdverses(this)) {
            if (p.peutAttaquer(roi.getCase())) {
                return true;
            }
        }
        return false;
    }

    public Coup getCoup() {
        synchronized (jeu) {
            try {
                jeu.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return jeu.coupRecu;
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
}
