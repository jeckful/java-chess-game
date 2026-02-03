package modele.jeu;

import java.util.ArrayList;
import modele.plateau.Case;
import modele.plateau.Plateau;
import java.util.List; // Nécessaire pour listPiecesToString helper


public class Jeu extends Thread {
    private Plateau plateau;
    private Joueur j1;
    private Joueur j2;
    private volatile Coup coupEnAttente = null; // Volatile pour visibilité entre threads
    private final Object verrouCoup = new Object(); // Objet pour wait/notify sur le coup
    private final Object verrouPlateau = new Object(); // Objet pour synchroniser les modifications du plateau et l'état du jeu (joueurActuel)
    private Joueur joueurActuel;
    private Coup dernierCoup;
    private boolean partieTerminee = false;




    public Jeu() {
        // Initialisation des joueurs
        j1 = new Joueur(this, true, "Joueur Blanc");
        j2 = new Joueur(this, false, "Joueur Noir");
        joueurActuel = j1; // Le joueur blanc commence

        // Le Plateau se crée et s'initialise avec les joueurs, ce qui place les pièces.
        // Le constructeur de Plateau appelle Plateau.placerPieces().
        plateau = new Plateau(j1, j2);

        //configTestPat();

        start(); // Démarre le thread de jeu (appelle run -> jouerPartie)
    }

    public Joueur getJoueurActuel() {
        return joueurActuel;
    }
    
    public Joueur getJ1() {
        return j1;
    }

    public Joueur getJ2() {
        return j2;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    // Cette méthode n'est normalement pas appelée depuis Jeu si le Plateau s'initialise dans le constructeur
    // public void placerPieces() {
    //     plateau.placerPieces();
    // }

    // Appelé par la VueControleur pour soumettre un coup (s'exécute sur l'EDT)
    public void soumettreCoup(Coup c) {
        synchronized (verrouCoup) {
            coupEnAttente = c; // Stocker le coup soumis
            verrouCoup.notify(); // Réveiller le thread jeu qui attend dans jouerPartie
        }
        // Log du coup soumis - s'assure que coup, dep et piece sont non null avant d'accéder à leurs méthodes pour le log
        String coupLog = "null/invalide";
        if (c != null && c.getDep() != null) {
            Piece piecePourLog = c.getDep().getPiece();
            String pieceNomPourLog = (piecePourLog != null && piecePourLog.getNom() != null) ? piecePourLog.getNom() : "Pièce inconnue";
             String depStrPourLog = (c.getDep() != null) ? "(" + c.getDep().getX() + "," + c.getDep().getY() + ")" : "null dep"; // Added null check for dep case
             String arrStrPourLog = (c.getArr() != null) ? "(" + c.getArr().getX() + "," + c.getArr().getY() + ")" : "null arr"; // Added null check for arr case
            coupLog = pieceNomPourLog + " de " + depStrPourLog + " vers " + arrStrPourLog;
        }
        System.out.println("Coup soumis par la Vue : " + coupLog); // Log submission
    }

    // Méthode interne pour attendre et récupérer le coup (s'exécute sur le thread Jeu)
    private Coup attendreEtRecupererCoup() throws InterruptedException {
        Coup coupARetourner = null;
        synchronized (verrouCoup) {
            // Attendre TANT QUE aucun coup n'a été soumis ET que le thread n'est pas interrompu
            while (coupEnAttente == null && !Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " (" + (joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu") + ") attend un coup..."); // Log d'attente (added null check for joueurActuel)
                verrouCoup.wait(); // Libère le verrouCoup et attend une notification
            }
            // Un coup est arrivé ou le thread a été interrompu
            if (!Thread.currentThread().isInterrupted()) {
                 coupARetourner = coupEnAttente;
                 coupEnAttente = null; // "Consommer" le coup pour la prochaine attente
                 System.out.println(Thread.currentThread().getName() + " a récupéré le coup."); // Log retrieval
            } else {
                System.out.println(Thread.currentThread().getName() + " interrompu pendant l'attente."); // Log interruption
            }
        }
        return coupARetourner;
    }


    // Applique le coup si valide. Retourne true si le coup a été appliqué physiquement, false sinon.
    // Cette méthode vérifie si le coup soumis correspond à un coup légal calculé pour le joueur actuel.
    // (S'exécute sur le thread Jeu)
    public boolean appliquerCoup(Coup coup) {
        // --- Validations Initiales Rapides ---
        if (coup == null || coup.getDep() == null || coup.getArr() == null) {
            System.out.println("Coup invalide : coup ou cases null."); // Log invalid coup
            return false;
        }
        Case caseDepart = coup.getDep(); // Obtenir la case de départ
        Piece piece = caseDepart.getPiece(); // Obtenir la pièce DE LA CASE DE DÉPART *actuellement*

        if (piece == null) {
            System.out.println("Coup invalide : aucune pièce sur la case de départ (" + caseDepart.getX() + "," + caseDepart.getY() + ")."); // Log invalid coup
            return false; // Le coup est invalide si pas de pièce au départ
        }
        Joueur joueur = piece.getJoueur();
        if (joueur == null || joueur != joueurActuel) {
            System.out.println("Coup invalide : ce n'est pas le tour de " + (joueur != null ? joueur.getNom() : "Joueur inconnu") + " ou pièce non valide."); // Log invalid coup (added null check for joueur)
            return false; // Le coup est invalide si mauvaise pièce ou mauvais joueur
        }

        System.out.println("Tentative : " + piece.getNom() + " (" + piece.getCouleur() + ") de (" + caseDepart.getX() + "," + caseDepart.getY() + ") vers (" +
                           coup.getArr().getX() + "," + coup.getArr().getY() + ")"); // Log attempt


        // --- Validation de la légalité complète du coup ---
        // Cette section suppose que Joueur.getCoupsValides() retourne la liste des coups LÉGAUX
        // (filtrés des auto-échecs par simulation).
        // Le calcul des coups légaux est potentiellement coûteux !
        ArrayList<Coup> coupsLegaux = joueurActuel.getCoupsValides(); // Calculate legal moves
        boolean isCoupLegal = false;
        // Comparer le coup soumis avec la liste des coups légaux calculés
        for(Coup legalCoup : coupsLegaux) {
             // On compare les références des objets Case, qui devraient être les mêmes instances du plateau
            if (legalCoup.getDep() == coup.getDep() && legalCoup.getArr() == coup.getArr()) {
                isCoupLegal = true;
                break;
            }
        }

        if (!isCoupLegal) {
             System.out.println("Validation échouée : Le coup soumis (" + caseDepart.getX() + "," + caseDepart.getY() + ")->(" + coup.getArr().getX() + "," + coup.getArr().getY() + ") n'est pas dans la liste des coups légaux possibles pour " + joueurActuel.getNom() + "."); // Log invalid coup
             // Optionnel : Afficher les coups légaux possibles pour aider au débogage si la liste est petite
             // if (coupsLegaux.size() < 30) { // Limite pour éviter les très longs logs
             //    System.out.println("Coups légaux calculés (" + coupsLegaux.size() + ") :");
             //    for(Coup lc : coupsLegaux) {
             //        // Assurez-vous que getPiece() et getPosition() ne sont pas null ici
             //        String pieceNom = (lc.getPiece() != null && lc.getPiece().getNom() != null) ? lc.getPiece().getNom() : "Pièce inconnue";
             //        String depStr = (lc.getDep() != null) ? "(" + lc.getDep().getX() + "," + lc.getDep().getY() + ")" : "null";
             //        String arrStr = (lc.getArr() != null) ? "(" + lc.getArr().getX() + "," + lc.getArr().getY() + ")" : "null";
             //        System.out.println("  - " + pieceNom + " de " + depStr + " vers " + arrStr);
             //    }
             // }
             return false; // The move is invalid if it's not in the list of legal moves
        }
        System.out.println("Validation coup légal : OK."); // Log successful validation


        // --- If all validations pass, apply the move ---
        System.out.println("Toutes validations OK. Application du coup."); // Log application start
        boolean succes = false; // Initialize succes to false, expecting deplacerPieceUniquement to confirm success
        // Synchronize plate modifications and game state (joueurActuel)
        synchronized (verrouPlateau) {
            try {
                 // Call the physical move method and get its result (should return boolean)
                 succes = plateau.deplacerPieceUniquement(coup.getDep(), coup.getArr(), joueurActuel);
                 dernierCoup = coup;


                 if (succes) {
                     // Notify the view only if the physical move succeeded
                     plateau.notifyChange();
                 } else {
                     // deplacerPieceUniquement detected an internal error (e.g., unexpected null piece)
                     System.err.println("L'application physique du coup a échoué dans deplacerPieceUniquement."); // Log physical failure
                 }

            } catch (Exception e) { // Catch other unexpected exceptions during the move
                System.err.println("ERREUR imprévue pendant l'application du coup : " + e.getMessage()); // Log unexpected error
                e.printStackTrace(); // Print stack trace
                succes = false;
            }
        } // End synchronized(verrouPlateau)

        if (succes) {
            System.out.println("Coup appliqué avec succès."); // Log successful application
        } else {
            // This message will be displayed if 'succes' remained false (internal failure or exception)
            System.out.println("Echec de l'application du coup."); // Log failed application
        }

        return succes; // Return the actual result of applying the move
    }


    // Method executed by the game thread at startup
    public void run() {
        jouerPartie(); // Launches the main game loop
    }

    // Main game loop, executed in its own thread
    public void jouerPartie() {
        System.out.println("Thread Jeu (" + Thread.currentThread().getName() + ") démarré. Début de la partie."); // Log game start

        // The loop continues as long as the game is not over and the thread is not interrupted
        while (!partieTerminee && !Thread.currentThread().isInterrupted()) {
            System.out.println("\n--- Tour de " + (joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu") + " (" + (joueurActuel != null && joueurActuel.getCouleur() != null ? joueurActuel.getCouleur() : "Couleur inconnue") + ") ---"); // Log turn start (added null checks)

 
            System.out.println("DEBUG: État du plateau avant calcul des coups légaux pour " + (joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu")); // Log debug state start


            System.out.println("--------------------------------------------------------------------"); // Log debug state end


            // Vérifier les conditions de fin de partie AU DÉBUT du tour du joueur actuel
            // Cela couvre les cas où le joueur n'a aucun coup légal (pat ou mat s'il est en échec)
            // Cette vérification doit être faite avant d'attendre un coup de ce joueur.
            ArrayList<Coup> coupsLegauxJoueurActuel = joueurActuel.getCoupsValides(); // Calculates legal moves (potentially expensive)

            System.out.println("DEBUG: Nombre de coups légaux calculés pour " + (joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu") + " : " + coupsLegauxJoueurActuel.size()); // Log number of legal moves (added null check)


            if (coupsLegauxJoueurActuel.isEmpty()) {
                if (plateau != null && plateau.estEnEchec(joueurActuel)) {
                    String msg = "ÉCHEC ET MAT ! " + ((joueurActuel == j1) ? j2 : j1).getNom() + " gagne !";
                    System.out.println(msg);
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, msg, "Fin de partie", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    });
                    partieTerminee = true;
                } else {
                    String msg = "PAT ! Match nul.";
                    System.out.println(msg);
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, msg, "Fin de partie", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    });
                    partieTerminee = true;
                }

                 // Notify the view of the end of the game and potentially the result
                 if(plateau != null) plateau.notifyChange(); // Force a final view refresh
                 // Exit the game loop
                 break; // Exit the game loop immediately
            } else {
                 // The current player has legal moves. Game continues.
                 if (plateau != null && plateau.estEnEchec(joueurActuel)) { // Check if plateau is not null
                     // Notify the player they are in check (view should also react to estEnEchec)
                     System.out.println((joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu") + " est en échec !"); // Announce check (added null check)
                 }
            }


            // Wait for and retrieve the move from the current player
            Coup coupJoueur = null;
            try {
                coupJoueur = attendreEtRecupererCoup(); // Waits here for a move submitted by the View
            } catch (InterruptedException e) {
                System.out.println("Thread Jeu interrompu pendant l'attente d'un coup."); // Log interruption
                Thread.currentThread().interrupt(); // Redefine the interruption status
                partieTerminee = true; // Exit the loop
                break; // Exit the try/catch immediately
            }


            // Check if the thread was interrupted while waiting or if coup is null (due to interruption)
            if (Thread.currentThread().isInterrupted() || coupJoueur == null) {
                 System.out.println("Sortie de la boucle de jeu suite interruption ou coup null après attente."); // Log exit
                 partieTerminee = true; // Ensure exit if not already done
                 break; // Exit the loop
            }


            // Attempt to apply the submitted move. appliquerCoup checks legality and executes physically.
            boolean coupApplique = appliquerCoup(coupJoueur);

            if (coupApplique) {
                // The move was applied physically successfully. Change player.
                synchronized (verrouPlateau) { // Synchronize player change
                    joueurActuel = (joueurActuel == j1) ? j2 : j1;
                    System.out.println("Tour changé. C'est au tour de " + (joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu") + "."); // Log player change (added null check)
                }
                // The loop will restart, and the first check of the next turn will determine
                // if the new player is in check, stalemate, or checkmate.
            } else {
                // The move was not applied (because invalid or physical failure in deplacerPieceUniquement).
                // The turn does NOT change, the same player must play again.
                System.out.println("Coup invalide ou application physique échouée. Le tour de " + (joueurActuel != null ? joueurActuel.getNom() : "Joueur inconnu") + " continue."); // Log invalid/failed move (added null check)
                // The loop will simply restart and wait for a new move from the same player.
            }

        } // End while (!partieTerminee && !Thread.currentThread().isInterrupted())

        System.out.println("Boucle de jeu terminée."); // Log loop end
        if(partieTerminee) {
             System.out.println("La partie est terminée."); // Log game over state
             // Here, you could add other end-of-game actions
        } else {
             System.out.println("La partie a été interrompue."); // Log interrupted state
        }
         // Ensure the thread terminates cleanly here.
    }


     // This method is used in jouerPartie to check for checkmate at the start of the turn.
     // It assumes that Joueur.getCoupsValides() returns only legal moves.
     public boolean estEchecEtMat(Joueur joueur) {
         // To be checkmate, the player must be in check
         if (plateau == null || !plateau.estEnEchec(joueur)) { // Check for null plateau
             return false;
         }

         // And must have NO legal moves to get out of check
         // (assumes getCoupsValides in Joueur.java returns only legal moves, using estEnEchecAprèsCoup)
         ArrayList<Coup> coupsLegaux = joueur.getCoupsValides(); // Potentially expensive
         return coupsLegaux.isEmpty(); // If the list of legal moves is empty, it's checkmate
     }

    public Coup getDernierCoup() {
        return dernierCoup;
    }

    public boolean estPartieTerminee() {
        return partieTerminee;
    }
    
    public void configTestPat() {
        // On vide le plateau
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                plateau.getCase(x, y).setPiece(null);
            }
        }
    
        // On vide les listes de pièces
        j1.getPieces().clear();
        j2.getPieces().clear();
    
        // On place les 3 pièces restantes
        // Roi noir en h8
        Roi roiNoir = new Roi(plateau.getCase(7, 0), j2);
        plateau.getCase(7, 0).setPiece(roiNoir);
        j2.setRoi(roiNoir);
        j2.getPieces().add(roiNoir);
    
        // Roi blanc en f7
        Roi roiBlanc = new Roi(plateau.getCase(5, 1), j1);
        plateau.getCase(5, 1).setPiece(roiBlanc);
        j1.setRoi(roiBlanc);
        j1.getPieces().add(roiBlanc);
    
        // Dame blanche en g6
        Reine dame = new Reine(plateau.getCase(5, 2), j1);
        plateau.getCase(5, 2).setPiece(dame);
        j1.getPieces().add(dame);
    }
}