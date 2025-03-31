/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.jeu;

import modele.plateau.*;

import java.util.ArrayList;

public class Roi extends Piece {

    public Roi(Plateau _plateau) {
        super(_plateau);  // Pass Plateau to Piece constructor

        // Decorate accessible cases for the King (in lines and diagonals)
        casesAccessibles = new DecorateurCasesEnLigne(new DecorateurCasesEnDiagonale(null));
    }

    @Override
    public ArrayList<Case> calculerDeplacementsPossibles() {
        ArrayList<Case> deplacements = new ArrayList<>();
        
        // Les déplacements du roi (une case dans chaque direction)
        Direction[] directions = Direction.values();
        for (Direction dir : directions) {
            // Calcul des cases accessibles en fonction de la direction
            Case cAccess = plateau.caseDansDirection(c, dir);
            if (cAccess != null) {
                deplacements.add(cAccess);
            }
        }
        return deplacements;
    }
}

