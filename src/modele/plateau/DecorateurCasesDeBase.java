package modele.plateau;

import modele.jeu.Piece;
import java.util.ArrayList;

public class DecorateurCasesDeBase extends DecorateurCasesAccessibles {

    // Constructeur pour le décorateur de base
    public DecorateurCasesDeBase(Piece piece) {
        super(null, piece);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        return new ArrayList<>();
    }
}
