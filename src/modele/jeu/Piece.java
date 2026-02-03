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
    private boolean aBouge = false;

    public void setABouge(boolean aBouge) {
        this.aBouge = aBouge;
    }
    

    public Piece(Case position, Joueur joueur, String nom) {
        this.joueur = joueur;
        this.position = position;
        this.nom=nom;
        this.aBouge=false;
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
        ArrayList<Case> deplacementsPossibles = calculerDeplacementsPossibles();
        if (nouvelleCase != null && deplacementsPossibles.contains(nouvelleCase)) {
            if (position != null) {
                position.quitterLaCase();
            }
            nouvelleCase.setPiece(this);
            position = nouvelleCase;
            this.aBouge=true;
        }
    }

    public boolean getABouge() {
        return this.aBouge;
    }

    public boolean estCoupValide(Coup coup, Plateau plateau) {
        return true; // Par défaut, à surcharger
    }
    

    public boolean appartientA(Joueur joueur) {
        return this.joueur == joueur;
    }

    public Couleur getCouleur() {
        return joueur.getCouleur();  // Retourne la couleur du joueur
    }

    public abstract ArrayList<Case> calculerDeplacementsPossibles();

    public Plateau getPlateau() {
        if (plateau == null && position != null) {
            plateau = position.getPlateau();
        }
        return plateau;
    }
}
