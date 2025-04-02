package modele.jeu;

import modele.plateau.*;

import java.util.ArrayList;

public class Roi extends Piece {
    private DecorateurCasesAccessibles casesAccessibles;

    public Roi(Case _c, Joueur _joueur) {
        super(_c, _joueur);  // Call the Piece constructor with the case and joueur
        casesAccessibles = new DecorateurCasesEnLigne(new DecorateurCasesEnDiagonale(null));
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        
        // Utiliser la méthode getCasesAccessibles() pour récupérer les cases accessibles
        ArrayList<Case> cases = casesAccessibles.getCasesAccessibles();
        
        for (Case caseCible : cases) {
            // Vous pouvez ajouter la logique pour valider si une case est réellement accessible pour le roi
            // Par exemple : ne pas aller sur une case occupée par une pièce du même joueur
            if (caseCible != null && (caseCible.getPiece() == null || caseCible.getPiece().getJoueur() != this.joueur)) {
                deplacements.add(caseCible);
            }
        }
        
        return deplacements;
    }
}
