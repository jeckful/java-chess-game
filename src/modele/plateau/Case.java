package modele.plateau;

import modele.jeu.Piece;

public class Case {
    private Plateau plateau;
    private int x;
    private int y;
    protected Piece p;

    public Case(Plateau plateau, int x, int y) {
        this.plateau = plateau;
        this.x = x;
        this.y = y;
        this.p = null;
    }

    public Piece getPiece() {
        return p;
    }

    public void setPiece(Piece p) {
        this.p = p;
        if (p != null) {
            p.setPosition(this);
        }
    }

    public void quitterLaCase() {
        if (this.p != null) {
            this.p.setPosition(null); // Assurer que la pièce n'a plus de position
            this.p = null;
        }
    }

    // Méthode pour restaurer une pièce dans cette case
    public void restaurerPiece(Piece piece) {
        this.p = piece;
        if (piece != null) {
            piece.setPosition(this);
        }
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean estVide() {
        return p == null;
    }

    public Plateau getPlateau() {
        return plateau;
    }
    
}
