package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;

public class Tour extends Piece {
    public String nom = "Tour";
    public Tour(Case _c, Joueur _joueur) {
        super(_c, _joueur);
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        // Implement the movement logic for a rook (straight lines)
        return deplacements;
    }
}
