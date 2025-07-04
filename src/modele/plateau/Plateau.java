package modele.plateau;

import modele.jeu.Coup;
import modele.jeu.Joueur;
import modele.jeu.Piece;
import modele.jeu.Pion;
import modele.jeu.Reine;
import modele.jeu.Roi;
import modele.jeu.Tour;

import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Plateau {
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;

    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y];

    private Joueur j1;
    private Joueur j2;


    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void notifyChange() {
        support.firePropertyChange("plateauUpdated", null, this);
    }

    public Plateau(Joueur j1, Joueur j2) {
        this.j1 = j1;
        this.j2 = j2;
        initPlateauVide();
        placerPieces();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    public Case getCase(int x, int y) {
        if (x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y) {
            return grilleCases[x][y];
        }
        return null;
    }

    private void initPlateauVide() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this, x, y);
            }
        }
    }

    public void placerPieces() {
        j1.initialiserPieces(this, true);
        j2.initialiserPieces(this, false);

        notifyChange();
    }

    public boolean deplacerPieceUniquement(Case dep, Case arr, Joueur joueur) {
        Piece piece = dep.getPiece();
        if (piece == null || !piece.appartientA(joueur)) {
            return false;
        }

        // Prise en passant (détectée avant de poser le pion)
        boolean priseEnPassant = false;
        Case casePionPris = null;

        if (piece instanceof Pion && dep.getX() != arr.getX() && arr.estVide()) {
            casePionPris = getCase(arr.getX(), dep.getY());
            if (casePionPris != null && casePionPris.getPiece() instanceof Pion) {
                priseEnPassant = true;
            }
        }

        // Suppression de la pièce adverse si normale
        Piece piecePrise = arr.getPiece();
        if (piecePrise != null) {
            Joueur adversaire = joueur.getAdversaire();
            if (adversaire != null && adversaire.getPieces() != null) {
                adversaire.getPieces().remove(piecePrise);
            }
            arr.setPiece(null);
        }

        // Déplacement de la pièce
        dep.setPiece(null);
        arr.setPiece(piece);
        piece.allerSurCase(arr);

        // Exécution réelle de la prise en passant
        if (priseEnPassant && casePionPris != null) {
            Piece pionPris = casePionPris.getPiece();
            if (pionPris instanceof Pion) {
                casePionPris.setPiece(null);
                Joueur adversaire = joueur.getAdversaire();
                if (adversaire != null && adversaire.getPieces() != null) {
                    adversaire.getPieces().remove(pionPris);
                }
            }
        }



        // Gestion spéciale du roque
        if (piece instanceof Roi) {
            int y = arr.getY();
            // Petit roque
            if (dep.getX() == 4 && arr.getX() == 6) {
                Case caseTour = getCase(7, y);
                Case caseDestination = getCase(5, y);
                if (caseTour != null && caseTour.getPiece() instanceof Tour) {
                    Piece tour = caseTour.getPiece();
                    caseTour.setPiece(null);
                    caseDestination.setPiece(tour);
                    tour.setPosition(caseDestination);   // mise à jour logique
                    tour.setABouge(true);                // empêche un 2ᵉ roque

                }
            }
            // Grand roque
            else if (dep.getX() == 4 && arr.getX() == 2) {
                Case caseTour = getCase(0, y);
                Case caseDestination = getCase(3, y);
                if (caseTour != null && caseTour.getPiece() instanceof Tour) {
                    Piece tour = caseTour.getPiece();
                    caseTour.setPiece(null);
                    caseDestination.setPiece(tour);
                    tour.setPosition(caseDestination);
                    tour.setABouge(true);

                }
            }
        }

        // === Promotion du pion ===
        if (piece instanceof Pion) {
            int ligneArrivee = arr.getY();
            if ((piece.getCouleur() == Couleur.BLANC && ligneArrivee == 0) ||
                (piece.getCouleur() == Couleur.NOIR && ligneArrivee == 7)) {
                
                // Remplacer le pion par une reine
                Piece reine = new Reine(arr, joueur);
                arr.setPiece(reine);
                joueur.getPieces().remove(piece);
                joueur.getPieces().add(reine);
            }
        }

        notifyChange(); // pour informer la Vue que le modèle a changé


        return true;
    }


    public ArrayList<Piece> getToutesLesPiecesAdverses(Joueur joueur) {
        Joueur adversaire = (joueur == j1) ? j2 : j1;
        return new ArrayList<>(adversaire.getPieces());
    }


    public boolean estEnEchec(Joueur joueur) {
        if (joueur == null) {
            return false;
        }
        Roi roi = joueur.getRoi();
        if (roi == null) {
            return false;
        }

        Case positionRoi = roi.getPosition();
        if (positionRoi == null) {
            return false;
        }

        for (Piece pieceAdverse : getToutesLesPiecesAdverses(joueur)) {
            if (pieceAdverse != null && pieceAdverse.getPosition() != null) {
                 ArrayList<Case> deplacementsPossiblesAdverses = pieceAdverse.calculerDeplacementsPossibles();
                 if (deplacementsPossiblesAdverses != null && deplacementsPossiblesAdverses.contains(positionRoi)) {
                     return true;
                 }
            }
        }
        return false;
    }

    public boolean estEnEchecAprèsCoup(Coup coup, Joueur joueur) {
        if (coup == null || coup.getDep() == null || coup.getArr() == null) {
             return false;
        }
        Case caseDepart = coup.getDep();
        Case caseArrivee = coup.getArr();
        Piece pieceDeplacee = caseDepart.getPiece();

        if (pieceDeplacee == null) {
             return false;
        }
        if (joueur == null) {
             return false;
        }
        if (pieceDeplacee.getJoueur() != joueur) {
             return false;
        }
         if (caseArrivee == null) {
             return false;
         }

        Piece savedPieceOnDepartCase = caseDepart.p;
        Piece savedPieceOnArriveeCase = caseArrivee.p;

        Case savedPosDeplacee = pieceDeplacee.getPosition();

        Joueur adversaire = joueur.getAdversaire();
        boolean pieceCaptureeRetiree = false;
        if (savedPieceOnArriveeCase != null && adversaire != null) {
             pieceCaptureeRetiree = adversaire.getPieces().remove(savedPieceOnArriveeCase);
        }

        caseArrivee.p = pieceDeplacee;
        caseDepart.p = null;

        pieceDeplacee.setPosition(caseArrivee);

        boolean enEchec = estEnEchec(joueur);

        caseDepart.p = savedPieceOnDepartCase;

        caseArrivee.p = savedPieceOnArriveeCase;

        if (pieceCaptureeRetiree && adversaire != null && savedPieceOnArriveeCase != null) {
             if (!adversaire.getPieces().contains(savedPieceOnArriveeCase)) {
                adversaire.getPieces().add(savedPieceOnArriveeCase);
             }
        }

        pieceDeplacee.setPosition(savedPosDeplacee);

        return enEchec;
    }


    public Joueur getJ1() { return j1; }
    public Joueur getJ2() { return j2; }

}