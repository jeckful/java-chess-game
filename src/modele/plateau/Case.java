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
        if (this.p != null && this.p != p) {
            this.p.setPosition(null);
        }

        this.p = p;

        if (p != null) {
            p.setPosition(this);
        }
    }

    public void quitterLaCase() {
         if (this.p != null) {
             this.setPiece(null);
         }
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Case other = (Case) obj;
        return this.x == other.x && this.y == other.y;
    }
    
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        char colonne = (char) ('a' + x); // 0 → 'a', 1 → 'b', etc.
        int ligne = 8 - y;               // 0 → 8, 1 → 7, ..., 7 → 1
        return "" + colonne + ligne;    // Exemple : "e4"
    }
        
        
}