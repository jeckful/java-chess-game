package modele.plateau;

import modele.jeu.Piece;
import java.util.ArrayList;

public abstract class DecorateurCasesAccessibles {

    protected Plateau plateau;
    protected Piece piece;
    protected DecorateurCasesAccessibles base;

    // Constructeur pour le décorateur racine (aucune base décorateur)
    public DecorateurCasesAccessibles(Piece piece) {
        this(null, piece);
    }

    // Constructeur pour créer un décorateur qui enrobe une instance de décorateur existante
    public DecorateurCasesAccessibles(DecorateurCasesAccessibles base, Piece piece) {
        this.base = base;
        this.piece = piece;
        if (piece != null) {
            this.plateau = piece.getPlateau();
        } else {
            this.plateau = null; // ou lever une exception selon le contexte
        }
    }

    public ArrayList<Case> getCasesAccessibles() {
        ArrayList<Case> retour = getMesCasesAccessibles();
        if (base != null) {
            retour.addAll(base.getCasesAccessibles());
        }
        return retour;
    }

    /*
     * Si vous souhaitez garder la possibilité de réassigner la pièce plus tard,
     * vous pouvez conserver ce setter, sinon il est préférable d'éliminer cette méthode
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
        this.plateau = (piece != null) ? piece.getPlateau() : null;
    }

    public abstract ArrayList<Case> getMesCasesAccessibles();
}
