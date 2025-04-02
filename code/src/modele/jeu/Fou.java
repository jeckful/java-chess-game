package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;

public class Fou extends Piece {
    public String nom = "Fou";
    public Fou(Case _c, Joueur _joueur) {
        super(_c, _joueur);
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        // Implement the bishop's movement logic (diagonals)
        return deplacements;
    }
}
