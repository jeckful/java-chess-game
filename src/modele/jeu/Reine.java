package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.DecorateurCasesAccessibles;
import modele.plateau.DecorateurCasesDeBase;
import modele.plateau.DecorateurCasesEnLigne;
import modele.plateau.DecorateurCasesEnDiagonale;

public class Reine extends Piece {
    
    public Reine(Case _c, Joueur _joueur) {
        super(_c, _joueur, "Reine");
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        // Crée le décorateur de base avec l'instance de la pièce
        DecorateurCasesAccessibles deplacementsBase = new DecorateurCasesDeBase(this);
        
        // Crée la chaîne des déplacements en ligne droite
        DecorateurCasesAccessibles deplacementsLigne = new DecorateurCasesEnLigne(deplacementsBase, this);
        // Crée la chaîne des déplacements en diagonale
        DecorateurCasesAccessibles deplacementsDiagonale = new DecorateurCasesEnDiagonale(deplacementsBase, this);

        // Combine les deux résultats
        ArrayList<Case> deplacements = deplacementsLigne.getCasesAccessibles();
        deplacements.addAll(deplacementsDiagonale.getCasesAccessibles());

        return deplacements;
    }
}
