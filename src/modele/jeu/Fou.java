package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.DecorateurCasesAccessibles;
import modele.plateau.DecorateurCasesDeBase;
import modele.plateau.DecorateurCasesEnDiagonale;

public class Fou extends Piece {

    public Fou(Case _c, Joueur _joueur) {
        super(_c, _joueur,"Fou");
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        // Crée le décorateur de base en passant l'instance de la pièce (this)
        DecorateurCasesAccessibles deplacementsBase = new DecorateurCasesDeBase(this);
        
        // Enrobe le décorateur de base avec celui des déplacements en diagonale
        DecorateurCasesAccessibles deplacementsDiagonale = new DecorateurCasesEnDiagonale(deplacementsBase, this);
        
        // Retourne toutes les cases accessibles calculées par la chaîne
        return deplacementsDiagonale.getCasesAccessibles();
    }
}
