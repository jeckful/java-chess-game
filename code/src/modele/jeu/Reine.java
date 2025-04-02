package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;

public class Reine extends Piece {
    public Reine(Case _c, Joueur _joueur) {
        super(_c, _joueur);
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        // Implement the queen's movement logic (rook + bishop)
        return deplacements;
    }
}
