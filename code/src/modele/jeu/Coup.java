package modele.jeu;

import modele.plateau.Case;

public class Coup {
    private Case dep;
    private Case arr;

    public Coup(Case dep, Case arr) {
        this.dep = dep;
        this.arr = arr;
    }
    
    public Case getDep() {
        return dep;
    }
    
    public Case getArr() {
        return arr;
    }

    public Piece getPiece() {
        return dep.getPiece();
    }
}

