package modele.plateau;

import java.util.ArrayList;

public class DecorateurCasesEnLigne extends DecorateurCasesAccessibles {
    public DecorateurCasesEnLigne(DecorateurCasesAccessibles _baseDecorateur) {
        super(_baseDecorateur);
    }
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> casesLigne = new ArrayList<>();
        // Logique pour récupérer les cases accessibles en ligne
        // Dans chaque direction, tant qu'il n'y a pas de pièce
        // Ajouter les cases accessibles en ligne
        return casesLigne;
    }
}
