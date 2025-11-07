# Nom du dossier de sortie
OUT_DIR = out

# Fichier listant les sources Java
SOURCES = sources.txt

# Classe principale (modifie si le nom du package change)
MAIN_CLASS = fr.pigeon.Main

# Règle par défaut
all: compile

# Génère la liste des fichiers sources (insensible à la casse pour inclure .Java / .java)
$(SOURCES):
	find src -iname "*.java" > $(SOURCES)

# Compile tous les fichiers Java
compile: $(SOURCES)
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) @$(SOURCES)
	# Si pigeon.png est absent mais pigeon.svg présent, essayer la conversion SVG->PNG
	if [ -f src/fr/pigeon/resources/pigeon.svg ] && [ ! -f src/fr/pigeon/resources/pigeon.png ]; then \
		echo "pigeon.png introuvable — tentative de conversion pigeon.svg -> pigeon.png"; \
		if [ -f .venv/bin/python ]; then \
			.venv/bin/python tools/convert_svg.py --width 32 --height 32 || echo "Conversion SVG->PNG (venv) échouée"; \
		elif command -v python3 >/dev/null 2>&1; then \
			python3 tools/convert_svg.py --width 32 --height 32 || echo "Conversion SVG->PNG (python3) échouée"; \
		else \
			echo "Aucun python3 ni .venv trouvé — saut de la conversion SVG->PNG"; \
		fi; \
	fi
	# Copier les ressources (images, svg, etc.) dans la sortie pour que Class.getResourceAsStream(...) les trouve
	if [ -d src/fr/pigeon/resources ]; then \
		mkdir -p $(OUT_DIR)/fr/pigeon/resources; \
		cp -R src/fr/pigeon/resources/* $(OUT_DIR)/fr/pigeon/resources/ 2>/dev/null || true; \
	fi

# Exécute le programme
run: compile
	java -cp $(OUT_DIR) $(MAIN_CLASS)

# Supprime les fichiers compilés et sources.txt
clean:
	rm -rf $(OUT_DIR) $(SOURCES)

# Supprime tout et recompile
rebuild: clean all
