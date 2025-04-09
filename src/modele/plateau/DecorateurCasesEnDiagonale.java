package modele.plateau;

import java.util.ArrayList;

public class DecorateurCasesEnDiagonale extends DecorateurCasesAccessibles {

    public DecorateurCasesEnDiagonale(DecorateurCasesAccessibles _baseDecorateur) {
        super(_baseDecorateur);
    }

    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> casesDiagonale = new ArrayList<>();
        // Logique pour récupérer les cases accessibles en diagonale
        // Ajouter les cases accessibles en diagonale
        return casesDiagonale;
    }
}

