package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Couleur;
import modele.plateau.Plateau;

public class Pion extends Piece {
    private boolean premierDeplacement = true;

    public Pion(Case _c, Joueur _joueur) {
        super(_c, _joueur, "Pion");
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        if (this.position == null) {
            System.out.println("Erreur : la pièce n'a pas de position.");
            return new ArrayList<>(); 
        }
        int direction = (joueur.estBlanc()) ? -1 : 1;

        Plateau plateau = this.getPosition().getPlateau();

        // On avance
        Case avant = plateau.getCase(this.getPosition().getX(), this.getPosition().getY() + direction);
        if (avant != null && avant.estVide()) {
            deplacements.add(avant);
            
            // Premier mouvement : On peut avancer de deux cases.
            Case deuxCasesAvant = plateau.getCase(this.getPosition().getX(), this.getPosition().getY() + 2 * direction);
            if (premierDeplacement && deuxCasesAvant != null && deuxCasesAvant.estVide()) {
                deplacements.add(deuxCasesAvant);
            }
        }

        // Capture
        Case captureGauche = plateau.getCase(this.getPosition().getX() - 1, this.getPosition().getY() + direction);
        Case captureDroite = plateau.getCase(this.getPosition().getX() + 1, this.getPosition().getY() + direction);

        if (captureGauche != null && !captureGauche.estVide() && captureGauche.getPiece().appartientA(joueur.getAdversaire())) {
            deplacements.add(captureGauche);
        }
        if (captureDroite != null && !captureDroite.estVide() && captureDroite.getPiece().appartientA(joueur.getAdversaire())) {
            deplacements.add(captureDroite);
        }

        // Prise en passant
        int yActuel = this.getPosition().getY();
        int yAttaque = yActuel + direction;

        Jeu jeu = joueur != null ? joueur.getJeu() : null;
        if (jeu != null && jeu.getDernierCoup() != null) {
            Coup dernier = jeu.getDernierCoup();
            Piece cible = dernier.getArr() != null ? dernier.getArr().getPiece() : null;

            if (cible instanceof Pion && cible.getCouleur() != this.getCouleur()) {
                int xDernier = dernier.getArr().getX();
                int yDernier = dernier.getArr().getY();
                int xMoi = this.getPosition().getX();

                if (Math.abs(xDernier - xMoi) == 1 && yDernier == yActuel &&
                    Math.abs(dernier.getDep().getY() - yDernier) == 2) {

                    Case caseCapture = plateau.getCase(xDernier, yAttaque);
                    if (caseCapture != null && caseCapture.estVide()) {
                        deplacements.add(caseCapture);
                    }
                }
            }
        }

        return deplacements;
    }


    @Override
    public void allerSurCase(Case _c) {
        super.allerSurCase(_c);
        premierDeplacement = false;
    }
}
