package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Couleur;
import modele.plateau.Plateau;
import modele.jeu.*;


public class Joueur {
    private Jeu jeu;
    private ArrayList<Piece> pieces;
    private Roi roi;
    private boolean isBlanc;
    private Couleur couleur;
    private String nom;

    public Joueur(Jeu _jeu, boolean isBlanc, String nom) {
        this.jeu = _jeu;
        this.isBlanc = isBlanc;
        this.couleur = this.isBlanc ? Couleur.BLANC : Couleur.NOIR;
        this.pieces = new ArrayList<>();
        this.nom = nom;
    }

    public Jeu getJeu() {
        return jeu;
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

    public String getNom() {
        return nom;
    }

    public ArrayList<Coup> getCoupsValides() {
        ArrayList<Coup> coupsLegaux = new ArrayList<>();

        Plateau plateau = jeu != null ? jeu.getPlateau() : null;
        if (plateau == null) {
            
             return coupsLegaux;
        }
        ArrayList<Piece> piecesCopy = new ArrayList<>(pieces);

        for (Piece piece : piecesCopy) {
            if (piece != null && piece.getPosition() != null && piece.getJoueur() == this) {
                ArrayList<Case> coupsPossibles = piece.calculerDeplacementsPossibles();

                if (coupsPossibles != null) {
                    for (Case caseCible : coupsPossibles) {
                        if (caseCible != null) {
                             Coup coup = new Coup(piece.getPosition(), caseCible);
                             if (!plateau.estEnEchecAprèsCoup(coup, this)) {
                                 coupsLegaux.add(coup);
                             }
                        }
                    }
                }

                if (piece instanceof Roi) {
                    ArrayList<Case> roques = ((Roi) piece).calculerRoqueSiAutorise();
                    for (Case caseR : roques) {
                        Coup coup = new Coup(piece.getPosition(), caseR);
                        if (!plateau.estEnEchecAprèsCoup(coup, this)) {
                            coupsLegaux.add(coup);
                        }
                    }
                }

            } else {
            }
        }
        

        return coupsLegaux;
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

    public void initialiserPieces(Plateau plateau, boolean estBlanc) {
        pieces.clear();
        int lignePions = estBlanc ? 6 : 1;
        int lignePieces = estBlanc ? 7 : 0;
        for (int x = 0; x < Plateau.SIZE_X; x++) {
            Case casePion = plateau.getCase(x, lignePions);
            if (casePion != null) {
                 Pion pion = new Pion(casePion, this);
                 pieces.add(pion);
            }
        }
        if (plateau.getCase(0, lignePieces) != null) pieces.add(new Tour(plateau.getCase(0, lignePieces), this));
        if (plateau.getCase(7, lignePieces) != null) pieces.add(new Tour(plateau.getCase(7, lignePieces), this));
        if (plateau.getCase(1, lignePieces) != null) pieces.add(new Cavalier(plateau.getCase(1, lignePieces), this));
        if (plateau.getCase(6, lignePieces) != null) pieces.add(new Cavalier(plateau.getCase(6, lignePieces), this));
        if (plateau.getCase(2, lignePieces) != null) pieces.add(new Fou(plateau.getCase(2, lignePieces), this));
        if (plateau.getCase(5, lignePieces) != null) pieces.add(new Fou(plateau.getCase(5, lignePieces), this));
        if (plateau.getCase(3, lignePieces) != null) pieces.add(new Reine(plateau.getCase(3, lignePieces), this));

        Case caseRoi = plateau.getCase(4, lignePieces);
        if (caseRoi != null) {
             roi = new Roi(caseRoi, this);
             pieces.add(roi);
        }
    }
}