package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;

public class Cavalier extends Piece {
    public Cavalier(Case _c, Joueur _joueur) {
        super(_c, _joueur);
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        // Implement the knight's movement logic (L-shape)
        return deplacements;
    }
}
