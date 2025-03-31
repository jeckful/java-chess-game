/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.jeu.Piece;

public class Case {

    protected Piece p;        // La pièce présente sur cette case (peut être nulle si la case est vide)
    protected Plateau plateau; // Référence au plateau sur lequel cette case se trouve
    protected int x;          // Position de la case sur l'axe des X
    protected int y;          // Position de la case sur l'axe des Y

    // Constructeur : initialise la case avec le plateau et ses coordonnées
    public Case(Plateau _plateau, int x, int y) {
        plateau = _plateau;
        this.x = x;
        this.y = y;
    }
    public Case(Plateau _plateau) {
        plateau = _plateau;
    }

    // Getter pour la pièce de cette case
    public Piece getPiece() {
        return p;
    }

    // Setter pour placer une pièce sur cette case
    public void setPiece(Piece piece) {
        p = piece;
    }

    // Méthode pour quitter la case, mettant la pièce à null
    public void quitterLaCase() {
        p = null;
    }

    // Méthode pour savoir si la case est vide (pas de pièce)
    public boolean estVide() {
        return p == null;
    }

    // Méthode pour obtenir les coordonnées (x, y) de la case
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Méthode pour obtenir le plateau sur lequel cette case est située
    public Plateau getPlateau() {
        return plateau;
    }

    // Méthode pour déplacer la pièce d'une case à une autre
    public void deplacerPieceVers(Case destination) {
        if (this.p != null) {
            destination.setPiece(this.p);
            quitterLaCase();
        }
    }

    // Méthode pour vérifier si cette case est adjacente à une autre
    public boolean estAdjacente(Case other) {
        // Vérifie si les cases sont adjacentes horizontalement ou verticalement
        return Math.abs(this.x - other.x) <= 1 && Math.abs(this.y - other.y) <= 1;
    }

    /*
    public void setEntite(Piece _e) {

        p = _e;

    }*/
}
