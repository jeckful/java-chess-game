package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.DecorateurCasesAccessibles;
import modele.plateau.Plateau;
import modele.jeu.Roi;  // Assurez-vous que vous avez importé la classe Roi

/**
 * Entités amenées à bouger
 */
public abstract class Piece {

    protected Case c;
    protected Plateau plateau;
    protected DecorateurCasesAccessibles casesAccessibles;
    protected Roi roi;  // Ajout de la référence au Roi

    public Piece(Plateau _plateau) {
        plateau = _plateau;
        // Initialize roi from the Jeu context or through a setter later
        this.roi = null;  // Roi will be set later by Jeu or Joueur
    }

    public void quitterCase() {
        c.quitterLaCase();
    }

    public void allerSurCase(Case _c) {
        if (c != null) {
            quitterCase();
        }
        c = _c;
        plateau.arriverCase(c, this);
    }

    public Case getCase() {
        return c;
    }

    public abstract ArrayList<Case> calculerDeplacementsPossibles();

    public boolean peutAttaquer(Case c) {
        // À compléter dans chaque pièce spécifique
        return false;
    }

    public ArrayList<Piece> getToutesLesPiecesAdverses(Joueur joueur) {
        ArrayList<Piece> piecesAdverses = new ArrayList<>();
        // Add logic here to populate piecesAdverses
        return piecesAdverses;
    }
    

    public boolean peutAttaquerRoi() {
        // Vérifier si la pièce peut attaquer le roi sur la même ligne ou colonne
        if (roi == null) {
            return false; // Si roi est null, renvoyer false
        }
        return (this.getCase().getX() == roi.getCase().getX() || this.getCase().getY() == roi.getCase().getY());
    }

    // A method to set the Roi if needed later
    public void setRoi(Roi _roi) {
        this.roi = _roi;
    }
}
