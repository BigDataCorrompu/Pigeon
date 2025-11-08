
# Pigeon — Simulation de pigeons
Manon Le Vallois
Roland Oucherif
Sophie Rumeau

Ce dépôt contient une petite simulation Java (Swing) de pigeons cherchant de la nourriture.

Résumé rapide
- Langage : Java (code source dans `src/fr/pigeon/...`)
- Build : Makefile + `javac` (sortie dans `out/`)
- UI : Swing (`Display.java`)
- Tests rapides : `src/fr/pigeon/tests/TestSuite.java` (exécutable via `java -cp out fr.pigeon.tests.TestSuite`)

But de ce README
- expliquer comment préparer un environnement de travail (optionnel : venv Python pour outils),
- comment compiler et exécuter la simulation,
- comment activer le rendu SVG pour les sprites (optionnel),
- structure du projet et fonctionnalités principales.

=== Prérequis ===
- Java 17+ (JDK) ou une JDK compatible installée et disponible dans le PATH.
- make (macOS/Linux). Sous Windows, utilisez WSL, Git Bash, ou adaptez les commandes PowerShell/CMD indiquées ci‑dessous.
- Optionnel mais recommandé pour convertir le SVG en PNG localement sans dépendances Java : Python 3 et pip.

---

Installation (recommandée : avec un environnement Python virtuel — pour outils seulement)

1) Cloner le dépôt :

```bash
git clone <repo-url>
cd Pigeon
```

2) (Optionnel) Créer et activer un environnement Python virtuel (`.venv`) — commandes par système

- macOS / Linux (bash / zsh):

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

- Windows (PowerShell):

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

- Windows (cmd.exe):

```cmd
python -m venv .venv
.\.venv\Scripts\activate.bat
pip install -r requirements.txt
```

Remarques:
- Le venv est uniquement utilisé par le script `tools/convert_svg.py` (outil de conversion SVG→PNG). Le code Java n’a pas de dépendance Python.
- Si vous préférez ne pas utiliser de venv, installez `CairoSVG` ou utilisez `rsvg-convert` fourni par `librsvg` (par ex. via Homebrew / apt).

3) Convertir le SVG en PNG (optionnel — utile si vous ne voulez pas installer Batik)

Le projet contient `src/fr/pigeon/resources/pigeon.svg`. Le Makefile tente automatiquement la conversion pendant `make compile` si `pigeon.png` est absent (il appelle `tools/convert_svg.py` en privilégiant `.venv/bin/python`).

Vous pouvez lancer manuellement la conversion:

```bash
# avec le venv activé
python tools/convert_svg.py --width 32 --height 32

# ou si vous utilisez rsvg-convert (par ex. macOS via brew install librsvg):
rsvg-convert -w 32 -h 32 src/fr/pigeon/resources/pigeon.svg -o src/fr/pigeon/resources/pigeon.png
```

Après la conversion vous devriez avoir `src/fr/pigeon/resources/pigeon.png`.

4) Compiler et exécuter

```bash
make compile
make run
```

Le Makefile copie automatiquement le contenu de `src/fr/pigeon/resources/` dans `out/fr/pigeon/resources/` lors de la compilation, pour que les images soient accessibles via la classe runtime.

=== Optionnel : rasterisation SVG à l'exécution avec Apache Batik ===
Si vous préférez garder uniquement le SVG et laisser le programme rasteriser à la volée, installez Maven (ou téléchargez manuellement les jars Batik) puis :

```bash
# Exemple macOS (Homebrew)
brew install maven
./tools/install-batik.sh
make run
```

Le script `tools/install-batik.sh` tente de télécharger les jars Batik dans `lib/`. Si `lib/` existe, le Makefile inclura `lib/*` sur le classpath.

=== Structure du projet ===

- `src/fr/pigeon/` : code source Java
  - `affichage/Display.java` : composant Swing qui dessine l’état du jeu et les sprites
  - `entity/` : `Pigeon`, `Meal`, `Coordinate`, `Entity`, `AtomEntity`
  - `multithreading/` : `GameState`, `Simulation` (boucle de simulation)
  - `resources/` : images et SVG (`pigeon.svg`)
  - `tests/TestSuite.java` : petit harness de tests
- `Makefile` : tâches `compile`, `run`, `clean`, `rebuild`
- `requirements.txt` + `tools/convert_svg.py` : outils Python pour convertir le SVG en PNG si besoin
- `tools/install-batik.sh` : script optionnel pour télécharger Batik via Maven

=== Fonctionnalités principales ===
- Pigeons cherchent la nourriture la plus fraîche (réglage A)
- Arrêt propre des threads pigeons (B)
- Suppression sûre des repas hors itération (C)
- Remplacement des dimensions codées en dur par `Constants` (D)
- Support d’un sprite SVG/PNG pour les pigeons (affichage amélioré), ou fallback si image manquante
- Bouton `Restart` dans l’UI pour redémarrer la simulation
- Tests unitaires basiques (TestSuite)

=== Conseils / Dépannage ===
- Si l’image ne s’affiche pas :
  - Vérifiez que `src/fr/pigeon/resources/pigeon.png` existe. Le Makefile copie les fichiers dans `out/` lors de la compilation.
  - Si vous utilisez la rasterisation Batik, assurez-vous que `lib/` contient les jars et que `make run` les inclut (Makefile gère ça automatiquement si `lib/` existe).
- Sur Windows, préférez PowerShell (avec `Activate.ps1`) ou WSL/Git Bash si vous rencontrez des problèmes liés au shell.

=== Tests ===
- Compiler tout (`make compile`) puis exécuter :

```bash
java -cp out fr.pigeon.tests.TestSuite
```

Les tests vérifient des comportements clés (désignation du repas le plus frais, suppression concurrente, dispersion, etc.).

=== CI / Automation notes ===
- Exemple minimal pour CI (Ubuntu / macOS runner):

```bash
# installer python3 si nécessaire
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
make compile
```

- Si vous préférez que la conversion SVG→PNG soit obligatoire en CI (fail build si conversion échoue), dites-le moi et j'ajusterai le `Makefile` pour rendre la conversion stricte.

=== Contributors & Licence ===
- Conservez les crédits originaux pour les assets (le SVG provient d’OpenClipart / wildchief) — voir `src/fr/pigeon/resources/pigeon.svg` metadata.

---
