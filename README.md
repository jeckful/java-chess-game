# ♟️ Projet Échecs - LIFAPOO (L3 Informatique)

Développement d'une application Java pour jouer aux échecs, en suivant le pattern MVC et en utilisant Swing pour l'interface graphique.

---

## 📁 Structure du projet

```
LIFAPOO/
├── src/               # Code source Java
├── bin/               # Fichiers compilés (.class), généré par `make`
├── Images/            # Images des pièces d'échecs
├── Makefile           # Compilation automatisée
├── .gitignore
└── README.md
```

---

## Compilation & Exécution

### Prérequis

- Java JDK 11+ installé
- Make installé (Linux/WSL/Unix-like)
- (Optionnel) VSCode avec l'extension Java

### Commandes

```bash
make        # Compile le projet et copie les images
make run    # Compile puis lance le programme
make clean  # Supprime les fichiers compilés
```

---

## Infos techniques

- Langage : Java
- GUI : Swing
- Pattern : MVC (Modèle-Vue-Contrôleur)
- Organisation : Packages `modele`, `plateau`, `VueControleur`

---

## Auteurs

- Yanis LAASSIBI
- Tristan LEPARE
