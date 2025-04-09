package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.beans.PropertyChangeListener;

import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.plateau.Case;
import modele.plateau.Couleur;
import modele.jeu.Piece;
import modele.jeu.Roi;
import modele.jeu.Pion;
import modele.jeu.Reine;
import modele.jeu.Tour;
import modele.jeu.Cavalier;
import modele.jeu.Fou;
import modele.plateau.Plateau;

/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (clic position départ -> position arrivée pièce))
 *
 */
public class VueControleur extends JFrame {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 50; // nombre de pixel par case
    // icones affichées dans la grille
    private ImageIcon icoRoiBlanc, icoRoiNoir;
    private ImageIcon icoPionBlanc, icoPionNoir;
    private ImageIcon icoReineBlanc, icoReineNoir;
    private ImageIcon icoTourBlanc, icoTourNoir;
    private ImageIcon icoCavalierBlanc, icoCavalierNoir;
    private ImageIcon icoFouBlanc, icoFouNoir;

    private Case caseClic1; // mémorisation des cases cliquées
    private Case caseClic2;


    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)


    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addPropertyChangeListener(evt -> mettreAJourAffichage());

        mettreAJourAffichage();
    }


    private void chargerLesIcones() {
        icoRoiBlanc = chargerIcone("../Images/wK.png");
        icoRoiNoir = chargerIcone("../Images/bK.png");
        icoPionBlanc = chargerIcone("../Images/wP.png");
        icoPionNoir = chargerIcone("../Images/bP.png");
        icoReineBlanc = chargerIcone("../Images/wQ.png");
        icoReineNoir = chargerIcone("../Images/bQ.png");
        icoTourBlanc = chargerIcone("../Images/wR.png");
        icoTourNoir = chargerIcone("../Images/bR.png");
        icoCavalierBlanc = chargerIcone("../Images/wN.png");
        icoCavalierNoir = chargerIcone("../Images/bN.png");
        icoFouBlanc = chargerIcone("../Images/wB.png");
        icoFouNoir = chargerIcone("../Images/bB.png");
    }

    private ImageIcon chargerIcone(String urlIcone) {
        java.net.URL imgURL = getClass().getResource("/Images/" + urlIcone);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            // Redimensionner l'icône
            Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("Impossible de charger l'image: " + urlIcone);
            return null;
        }
    }
    
    

    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase, sizeY * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                final int xx = x; // permet de compiler la classe anonyme ci-dessous
                final int yy = y;
                // écouteur de clics
                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JLabel caseCliquee = (JLabel) e.getSource();
                        int x = -1, y = -1;
                        
                        // Recherche de la case cliquée
                        for (int i = 0; i < sizeX; i++) {
                            for (int j = 0; j < sizeY; j++) {
                                if (tabJLabel[i][j] == caseCliquee) {
                                    x = i;
                                    y = j;
                                }
                            }
                        }
                    
                        if (x == -1 || y == -1) {
                            System.out.println("Erreur : case non trouvée.");
                            return;
                        }
                    
                        System.out.println("Case cliquée : (" + x + ", " + y + ")");
                    
                        Case caseSelectionnee = plateau.getCases()[x][y];
                    
                        if (caseClic1 == null) {
                            caseClic1 = caseSelectionnee;
                            System.out.println("Première case sélectionnée.");
                        } else {
                            caseClic2 = caseSelectionnee;
                            System.out.println("Deuxième case sélectionnée.");
                        
                            Coup coup = new Coup(caseClic1, caseClic2);
                            jeu.envoyerCoup(coup);  // ✅ c’est la seule vraie modif importante
                        
                            mettreAJourAffichage();
                        
                            caseClic1 = null;
                            caseClic2 = null;
                        }                        
                    }
                    
                });


                jlab.setOpaque(true);

                if ((y % 2 == 0 && x % 2 == 0) || (y % 2 != 0 && x % 2 != 0)) {
                    tabJLabel[x][y].setBackground(new Color(50, 50, 110));
                } else {
                    tabJLabel[x][y].setBackground(new Color(150, 150, 210));
                }

                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }

    
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                Case c = plateau.getCases()[x][y];
    
                if (c != null) {

                    Piece e = c.getPiece();
                    //System.out.println("Case (" + x + ", " + y + "): " + e);
    
                    if (e != null) {
                        // Check the color of the piece and assign the appropriate icon
                        if (e instanceof Roi) {
                            if (e.getCouleur() == Couleur.BLANC) {
                                tabJLabel[x][y].setIcon(icoRoiBlanc);
                            } else {
                                tabJLabel[x][y].setIcon(icoRoiNoir);
                            }
                        } else if (e instanceof Pion) {
                            if (e.getCouleur() == Couleur.BLANC) {
                                tabJLabel[x][y].setIcon(icoPionBlanc);
                            } else {
                                tabJLabel[x][y].setIcon(icoPionNoir);
                            }
                        } else if (e instanceof Reine) {
                            if (e.getCouleur() == Couleur.BLANC) {
                                tabJLabel[x][y].setIcon(icoReineBlanc);
                            } else {
                                tabJLabel[x][y].setIcon(icoReineNoir);
                            }
                        } else if (e instanceof Tour) {
                            if (e.getCouleur() == Couleur.BLANC) {
                                tabJLabel[x][y].setIcon(icoTourBlanc);
                            } else {
                                tabJLabel[x][y].setIcon(icoTourNoir);
                            }
                        } else if (e instanceof Cavalier) {
                            if (e.getCouleur() == Couleur.BLANC) {
                                tabJLabel[x][y].setIcon(icoCavalierBlanc);
                            } else {
                                tabJLabel[x][y].setIcon(icoCavalierNoir);
                            }
                        } else if (e instanceof Fou) {
                            if (e.getCouleur() == Couleur.BLANC) {
                                tabJLabel[x][y].setIcon(icoFouBlanc);
                            } else {
                                tabJLabel[x][y].setIcon(icoFouNoir);
                            }
                        }
                    } else {
                        tabJLabel[x][y].setIcon(null);
                    }
                }
            }
        }
    }
}
