package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Couleur;
import modele.plateau.Plateau;

import java.util.ArrayList;

public abstract class Piece {
    protected Case position;
    protected Joueur joueur;
    protected Plateau plateau;
    public String nom;

    public Piece(Case position, Joueur joueur) {
        this.joueur = joueur;
        this.position = position;
        if (position != null) {
            position.setPiece(this);
        }
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public String getNom() {
        return nom;
    }

    public Case getPosition() {
        return position;
    }

    public void setPosition(Case position) {
        this.position = position;
    }

    public void allerSurCase(Case nouvelleCase) {
        if (nouvelleCase != null && (nouvelleCase.getPiece() == null || nouvelleCase.getPiece().getJoueur() != this.joueur)) {
            if (position != null) {
                position.quitterLaCase();
            }
            nouvelleCase.setPiece(this);
            position = nouvelleCase;
        }
    }

    public boolean appartientA(Joueur joueur) {
        return this.joueur == joueur;
    }

    public Couleur getCouleur() {
        return joueur.getCouleur();  // Retourne la couleur du joueur
    }

    public abstract ArrayList<Case> calculerDeplacementsPossibles();
}
