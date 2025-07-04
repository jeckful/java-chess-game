package modele.plateau;

import java.util.ArrayList;
import modele.jeu.Piece;

public class DecorateurCasesEnLigne extends DecorateurCasesAccessibles {
    
    // Constructeur qui prend un décorateur de base et la pièce courante.
    public DecorateurCasesEnLigne(DecorateurCasesAccessibles base, Piece piece) {
        super(base, piece);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> casesLigne = new ArrayList<>();
        Plateau plateau = piece.getPlateau();
        Case position = piece.getPosition();

        int[][] directions = {
            {1, 0},  // droite
            {-1, 0}, // gauche
            {0, 1},  // bas
            {0, -1}  // haut
        };

        for (int[] dir : directions) {
            int x = position.getX() + dir[0];
            int y = position.getY() + dir[1];

            while (x >= 0 && x < Plateau.SIZE_X && y >= 0 && y < Plateau.SIZE_Y) {
                Case c = plateau.getCase(x, y);
                if (c.estVide()) {
                    casesLigne.add(c);
                } else {
                    if (c.getPiece() != null && c.getPiece().getJoueur() != piece.getJoueur()) {
                        casesLigne.add(c); // case occupée par ennemi
                    }
                    break; // bloqué par une pièce
                }
                x += dir[0];
                y += dir[1];
            }
        }

        return casesLigne;
    }
}
