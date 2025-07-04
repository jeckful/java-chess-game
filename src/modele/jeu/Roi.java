package modele.jeu;

import modele.plateau.Case;
import modele.plateau.DecorateurCasesAccessibles;
import modele.plateau.DecorateurCasesDeBase;
import modele.plateau.DecorateurCasesEnLigne;
import modele.plateau.DecorateurCasesEnDiagonale;

import java.util.ArrayList;

public class Roi extends Piece {
    private DecorateurCasesAccessibles casesAccessibles;

    public Roi(Case _c, Joueur _joueur) {
        super(_c, _joueur, "Roi");
        DecorateurCasesAccessibles base = new DecorateurCasesDeBase(this);
        DecorateurCasesAccessibles diag = new DecorateurCasesEnDiagonale(base, this);
        casesAccessibles = new DecorateurCasesEnLigne(diag, this);
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        ArrayList<Case> cases = casesAccessibles.getCasesAccessibles();

        for (Case caseCible : cases) {
            if (caseCible != null &&
                (caseCible.getPiece() == null || caseCible.getPiece().getJoueur() != this.joueur) &&
                Math.abs(caseCible.getX() - this.getPosition().getX()) <= 1 &&
                Math.abs(caseCible.getY() - this.getPosition().getY()) <= 1) {
                deplacements.add(caseCible);
            }
        }
        
        
        return deplacements;
    }

    public ArrayList<Case> calculerRoqueSiAutorise() {
        ArrayList<Case> roques = new ArrayList<>();
        if (!this.getABouge() && !plateau.estEnEchec(joueur)) {
            int y = this.getPosition().getY();
    
            // Petit roque (E1 → G1 ou E8 → G8)
            Case f = plateau.getCase(5, y);
            Case g = plateau.getCase(6, y);
            Case h = plateau.getCase(7, y);
    
            if (f != null && g != null && h != null &&
                f.estVide() && g.estVide() &&
                h.getPiece() instanceof Tour &&
                !((Tour) h.getPiece()).getABouge()) {
    
                Coup testF = new Coup(this.getPosition(), f);
                Coup testG = new Coup(this.getPosition(), g);
    
                if (!plateau.estEnEchecAprèsCoup(testF, joueur) &&
                    !plateau.estEnEchecAprèsCoup(testG, joueur)) {
                    roques.add(g);
                }
            }
    
            // Grand roque (E1 → C1 ou E8 → C8)
            Case d = plateau.getCase(3, y);
            Case c = plateau.getCase(2, y);
            Case b = plateau.getCase(1, y);
            Case a = plateau.getCase(0, y);
    
            if (d != null && c != null && b != null && a != null &&
                d.estVide() && c.estVide() && b.estVide() &&
                a.getPiece() instanceof Tour &&
                !((Tour) a.getPiece()).getABouge()) {
    
                Coup testD = new Coup(this.getPosition(), d);
                Coup testC = new Coup(this.getPosition(), c);
    
                if (!plateau.estEnEchecAprèsCoup(testD, joueur) &&
                    !plateau.estEnEchecAprèsCoup(testC, joueur)) {
                    roques.add(c);
                }
            }
        }
        return roques;
    }
    
    
}
