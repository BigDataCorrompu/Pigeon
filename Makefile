# Nom du dossier de sortie
OUT_DIR = out

# Fichier listant les sources Java
SOURCES = sources.txt

# Classe principale (modifie si le nom du package change)
MAIN_CLASS = fr.pigeon.Main

# Règle par défaut
all: compile

# Génère la liste des fichiers sources
$(SOURCES):
	find src -name "*.java" > $(SOURCES)

# Compile tous les fichiers Java
compile: $(SOURCES)
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) @$(SOURCES)

# Exécute le programme
run: compile
	java -cp $(OUT_DIR) $(MAIN_CLASS)

# Supprime les fichiers compilés et sources.txt
clean:
	rm -rf $(OUT_DIR) $(SOURCES)

# Supprime tout et recompile
rebuild: clean all
