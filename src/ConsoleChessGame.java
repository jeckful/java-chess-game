import modele.jeu.*;
import modele.plateau.*;
import java.util.Scanner;

public class ConsoleChessGame {
    public static void main(String[] args) {
        Jeu jeu = new Jeu();
        Plateau plateau = jeu.getPlateau();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Joueur joueur = jeu.getJoueurActuel();
            afficherPlateau(plateau);

            System.out.println("\nTour de " + joueur.getNom());
            System.out.print("Entrez un coup (ex: e2 e4) ou \"q\" pour quitter : ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("q")) break;

            String[] parties = input.split(" ");
            if (parties.length != 2 || !estCoordValide(parties[0]) || !estCoordValide(parties[1])) {
                System.out.println("Format invalide. Réessayez.");
                continue;
            }

            Case dep = caseDepuisNotation(plateau, parties[0]);
            Case arr = caseDepuisNotation(plateau, parties[1]);

            Coup coup = new Coup(dep, arr);
            jeu.soumettreCoup(coup);

            afficherPlateau(plateau);

            if (jeu.getPlateau().estEnEchec(joueur.getAdversaire())) {
                System.out.println("⚠ Échec contre " + joueur.getAdversaire().getNom());
            }

            if (jeu.estPartieTerminee()) {
                System.out.println("Partie terminée !");
                break;
            }
        }

        scanner.close();
    }

    static void afficherPlateau(Plateau plateau) {
        System.out.println("\n   a b c d e f g h");
        for (int y = 0; y < 8; y++) {
            System.out.print((8 - y) + "  ");
            for (int x = 0; x < 8; x++) {
                Piece p = plateau.getCase(x, y).getPiece();
                if (p == null) System.out.print(". ");
                else System.out.print(symbole(p) + " ");
            }
            System.out.println(" " + (8 - y));
        }
        System.out.println("   a b c d e f g h");
    }

    static String symbole(Piece p) {
        String nom = p.getNom().toLowerCase();
        boolean blanc = p.getCouleur() == Couleur.BLANC;
    
        if (nom.contains("roi"))      return blanc ? "♔" : "♚";
        if (nom.contains("reine"))    return blanc ? "♕" : "♛";
        if (nom.contains("tour"))     return blanc ? "♖" : "♜";
        if (nom.contains("fou"))      return blanc ? "♗" : "♝";
        if (nom.contains("cavalier")) return blanc ? "♘" : "♞";
        if (nom.contains("pion"))     return blanc ? "♙" : "♟";
        return "?";
    }
    

    static boolean estCoordValide(String s) {
        return s.length() == 2 && s.charAt(0) >= 'a' && s.charAt(0) <= 'h' &&
               s.charAt(1) >= '1' && s.charAt(1) <= '8';
    }

    static Case caseDepuisNotation(Plateau p, String notation) {
        int x = notation.charAt(0) - 'a';
        int y = 8 - Character.getNumericValue(notation.charAt(1));
        return p.getCase(x, y);
    }
}
