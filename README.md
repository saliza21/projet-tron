# 🏍️ Tron AI

Un jeu de **Tron** multijoueur avec IA en Java/JavaFX, où des motos laissent des traces et les joueurs doivent survivre le plus longtemps possible sans se crasher. Chaque équipe peut utiliser un algorithme d'IA différent, choisi au démarrage.

---

## 📋 Description

Inspiré du jeu Tron, chaque joueur se déplace sur une grille et laisse une trace derrière lui. Un joueur est éliminé s'il sort de la grille ou entre en collision avec une trace. Le dernier joueur (ou la dernière équipe) encore en vie gagne. Le jeu tourne en **mode automatique** (IA joue toute seule) ou en **mode manuel** (touches directionnelles).

---

## ✨ Fonctionnalités

- 🤖 **3 algorithmes d'IA** sélectionnables par équipe au démarrage via une boîte de dialogue
- ⚡ **Mode auto** : les IA jouent automatiquement à raison d'un coup par seconde (`auto=1` dans config)
- ⌨️ **Mode manuel** : contrôle au clavier (touches directionnelles + `ENTER` pour forcer un coup auto)
- 👥 **Multi-équipes et multi-joueurs** : nombre de joueurs et d'équipes configurable
- 🎨 **Couleurs distinctes par équipe et par joueur** : 4 palettes (rouge, bleu, vert, jaune), chaque joueur a sa propre teinte
- 📊 **Panneau d'état** en temps réel : joueur courant, statut (Alive/Dead) et algorithme de chaque équipe
- ♟️ **Copie et undo du jeu** : les algorithmes travaillent sur une copie de l'état pour explorer sans modifier la partie réelle
- 🗺️ **Placement automatique des joueurs** : positions de départ calculées selon le nombre d'équipes (opposées pour 2 équipes, réparties en cercle sinon)
- 🏁 **Détection de fin de partie** : pop-up indiquant l'équipe gagnante, fermeture automatique de l'application

---

## 🧠 Algorithmes d'IA

Chaque équipe choisit son algorithme au lancement (profondeur max = 5 pour tous). La fonction d'évaluation est basée sur la **zone contrôlée** par le joueur rapportée à la surface totale de la grille.

| Algorithme | Stratégie |
|------------|-----------|
| **MaxN** | Chaque joueur maximise son propre score — adapté au multi-joueurs sans équipes |
| **Paranoid** | Le joueur courant maximise son score, tous les autres le minimisent — stratégie défensive |
| **SOS** (Social Objective Search) | Combine intérêt personnel, score des coéquipiers et pénalité pour les adversaires — adapté au jeu en équipe |

---

## 🎯 Heuristiques

L'heuristique est utilisée pour suggérer les meilleurs mouvements lors de l'évaluation. Celle active au démarrage est `DangerousnessHeuristic`.

| Heuristique | Critères d'évaluation |
|-------------|----------------------|
| **ControlAreaHeuristic** | Zone contrôlée + options de mouvement disponibles − pénalité si proche des bords |
| **DangerousnessHeuristic** | Zone contrôlée + options de mouvement − pénalité si proche d'un adversaire (distance ≤ 2) − pénalité si proche des bords − pénalité si la case est déjà tracée |

---

## 🏗️ Architecture

Le projet suit le patron **MVC**, avec **Factory** pour les algorithmes et **Strategy** pour les heuristiques.

```
src/main/java/com/tronai/
├── Main.java                            # Point d'entrée — lance ViewApplication (JavaFX)
├── config/
│   └── ConfigLoader.java               # Charge config.properties (grille, joueurs, mode auto)
├── algo/
│   ├── IAI.java                         # Interface commune des algos (findBestMove)
│   ├── AlgoFactory.java                 # Factory : instancie MaxN / Paranoid / SOS selon le nom
│   ├── MaxN.java                        # Algo MaxN (profondeur 5)
│   ├── Paranoid.java                    # Algo Paranoid (profondeur 5)
│   └── SOS.java                         # Algo SOS — gestion des coéquipiers et adversaires
├── heuristic/
│   ├── Heuristic.java                   # Interface (evaluatePosition + suggestMoves)
│   ├── ControlAreaHeuristic.java        # Heuristique basée sur la zone contrôlée
│   └── DangerousnessHeuristic.java      # Heuristique de dangerosité (active au démarrage)
├── model/
│   ├── Game.java                        # Moteur de jeu : grille, mouvements, copy(), undoMove(), isGameOver()
│   ├── Player.java                      # Joueur : id, position, équipe, état alive/dead
│   └── Team.java                        # Équipe : liste de joueurs + algorithme assigné
├── controller/
│   └── GameController.java             # Contrôleur : boucle AnimationTimer (1 coup/sec), move(), autoMove()
├── view/
│   ├── ViewApplication.java            # Application JavaFX : init, dialogue de sélection d'algo, clavier
│   └── GameView.java                   # Vue : GridPane + panneau d'état (couleurs par équipe/joueur)
└── util/
    ├── AlgorithmType.java               # Enum : MAXN, PARANOID, SOS
    ├── Direction.java                   # Enum : UP, DOWN, LEFT, RIGHT (avec dx/dy)
    ├── Cell.java                        # Case de la grille : isEmpty, isPlayer, owner
    ├── Position.java                    # Coordonnées (x, y)
    └── Move.java                        # Mouvement (direction + dx/dy)
```

---

## ⚙️ Configuration

Tout est centralisé dans `src/main/resources/config.properties` :

| Propriété | Valeur par défaut | Description |
|-----------|:-----------------:|-------------|
| `grid.width` | `8` | Largeur de la grille |
| `grid.height` | `8` | Hauteur de la grille |
| `cell.size` | `30` | Taille d'une cellule en pixels |
| `number.of.players` | `2` | Nombre total de joueurs |
| `number.of.teams` | `1` | Nombre d'équipes |
| `auto` | `1` | `1` = mode auto (IA joue seule) / `0` = mode manuel |

---

## 🎮 Utilisation

### Au démarrage

Une boîte de dialogue apparaît pour **chaque équipe** afin de choisir son algorithme (`MAXN`, `PARANOID` ou `SOS`).

### Contrôles clavier (mode `auto=0`)

| Touche | Action |
|--------|--------|
| `↑ ↓ ← →` | Déplacer le joueur courant |
| `ENTER` | Forcer un coup automatique de l'IA |

### Mode auto (`auto=1`)

L'IA de chaque équipe joue automatiquement, un coup par seconde. La partie s'arrête et l'application se ferme après affichage de l'équipe gagnante.

---

## 🚀 Lancer le projet

### Prérequis

- **Java 17**
- **JavaFX 17.0.6** (via Maven ou manuellement dans `lib/javafx-sdk-17.0.14/lib`)
- **Maven**

### Avec Maven

```bash
mvn clean javafx:run
```

### Compilation et exécution manuelle

```bash
# Compiler
javac --module-path "lib/javafx-sdk-17.0.14/lib" \
      --add-modules javafx.controls,javafx.fxml \
      -d out \
      src/main/java/com/tronai/**/*.java \
      src/main/java/com/tronai/Main.java

# Lancer
java --module-path "lib/javafx-sdk-17.0.14/lib" \
     --add-modules javafx.controls,javafx.fxml \
     -cp out com.tronai.Main
```

---

## 🧪 Tests

Des tests unitaires JUnit 5 sont présents dans `src/test/java/com/tronai/model/GameTest.java`.

```bash
mvn test
```

---
