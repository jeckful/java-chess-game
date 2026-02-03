package modele.jeu;

import modele.plateau.Case;
import modele.plateau.DecorateurCasesEnLigne;
import modele.plateau.DecorateurCasesAccessibles;
import modele.plateau.DecorateurCasesDeBase;
import modele.plateau.Plateau;
import java.util.ArrayList;

public class Tour extends Piece {
    public Tour(Case _c, Joueur _joueur) {
        super(_c, _joueur, "Tour");
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        DecorateurCasesAccessibles deplacementsBase = new DecorateurCasesDeBase(this);
        DecorateurCasesAccessibles deplacementsLigne = new DecorateurCasesEnLigne(deplacementsBase, this);
        return deplacementsLigne.getCasesAccessibles();
    }
}
