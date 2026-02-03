package modele.plateau;

import java.util.ArrayList;
import modele.jeu.Piece;

public class DecorateurCasesEnDiagonale extends DecorateurCasesAccessibles {

    // Constructeur qui reçoit le décorateur de base et l'instance de la pièce
    public DecorateurCasesEnDiagonale(DecorateurCasesAccessibles base, Piece piece) {
        super(base, piece);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> casesDiagonale = new ArrayList<>();
        Plateau plateau = piece.getPlateau();
        Case position = piece.getPosition();

        int[] directions = {-1, 1};

        for (int dx : directions) {
            for (int dy : directions) {
                int x = position.getX() + dx;
                int y = position.getY() + dy;

                while (x >= 0 && x < Plateau.SIZE_X && y >= 0 && y < Plateau.SIZE_Y) {
                    Case c = plateau.getCase(x, y);
                    if (c.estVide()) {
                        casesDiagonale.add(c);
                    } else {
                        // Vérification de sécurité si la case contient une pièce (évite potentiellement une NullPointerException)
                        if (c.getPiece() != null && c.getPiece().getJoueur() != piece.getJoueur()) {
                            casesDiagonale.add(c); // case occupée par ennemi
                        }
                        break; // bloqué par une pièce
                    }
                    x += dx;
                    y += dy;
                }
            }
        }
        return casesDiagonale;
    }
}
