package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Plateau;

import java.util.ArrayList;

public class Cavalier extends Piece {
    public Cavalier(Case _c, Joueur _joueur) {
        super(_c, _joueur, "Cavalier");
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        Plateau plateau = this.getPlateau();  // Accès au plateau via la méthode getPlateau()
        Case position = this.getPosition();  // Accès à la position via la méthode getPosition()
        
        // Déplacements possibles en forme de "L" pour le Cavalier
        int[][] directions = {
            {2, 1}, {2, -1},  // Deux cases en avant (vertical) + une case à gauche/droite (horizontal)
            {-2, 1}, {-2, -1}, // Deux cases en arrière (vertical) + une case à gauche/droite (horizontal)
            {1, 2}, {1, -2},  // Une case en avant (vertical) + deux cases à gauche/droite (horizontal)
            {-1, 2}, {-1, -2}  // Une case en arrière (vertical) + deux cases à gauche/droite (horizontal)
        };

        // Vérifier toutes les directions possibles du cavalier
        for (int[] dir : directions) {
            int x = position.getX() + dir[0];
            int y = position.getY() + dir[1];

            // Vérifier si la case est dans les limites du plateau
            if (x >= 0 && x < Plateau.SIZE_X && y >= 0 && y < Plateau.SIZE_Y) {
                Case c = plateau.getCase(x, y);
                // Le Cavalier peut se déplacer sur une case vide ou capturer une pièce ennemie
                if (c.estVide() || c.getPiece().getJoueur() != this.getJoueur()) {
                    deplacements.add(c);
                }
            }
        }

        return deplacements;
    }
}
