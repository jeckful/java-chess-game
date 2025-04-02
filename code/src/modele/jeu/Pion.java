package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Plateau;

public class Pion extends Piece {
    private boolean premierDeplacement = true;

    public Pion(Case _c, Joueur _joueur) {
        super(_c, _joueur);
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        int direction = (joueur.estBlanc()) ? -1 : 1; // White moves up (-1), Black moves down (+1)

        Plateau plateau = this.getPosition().getPlateau();

        // Forward movement (1 step)
        Case avant = plateau.getCase(this.getPosition().getX(), this.getPosition().getY() + direction);
        if (avant != null && avant.estVide()) {
            deplacements.add(avant);
            
            // First move: Can move two steps forward
            Case deuxCasesAvant = plateau.getCase(this.getPosition().getX(), this.getPosition().getY() + 2 * direction);
            if (premierDeplacement && deuxCasesAvant != null && deuxCasesAvant.estVide()) {
                deplacements.add(deuxCasesAvant);
            }
        }

        // Capture moves (diagonal)
        Case captureGauche = plateau.getCase(this.getPosition().getX() - 1, this.getPosition().getY() + direction);
        Case captureDroite = plateau.getCase(this.getPosition().getX() + 1, this.getPosition().getY() + direction);

        if (captureGauche != null && !captureGauche.estVide() && captureGauche.getPiece().appartientA(joueur.getAdversaire())) {
            deplacements.add(captureGauche);
        }
        if (captureDroite != null && !captureDroite.estVide() && captureDroite.getPiece().appartientA(joueur.getAdversaire())) {
            deplacements.add(captureDroite);
        }

        return deplacements;
    }

    @Override
    public void allerSurCase(Case _c) {
        super.allerSurCase(_c);
        premierDeplacement = false;
    }
}
