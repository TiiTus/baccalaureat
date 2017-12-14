#!/bin/sh
# ----------------------------------------
# Script de démarrage de l'application : <nom-appli>
# Author : Julien HANS
# Organization : Université de Lorraine
# date : oct 2017
# ---------------------------------------
# exemple:
# > -i ../samples/<nom-fichier> -p <mot-de-passe> -r <uri-bdd> -u <utilisateur-bdd>

# -----------------------------------------
# A modifier
# -----------------------------------------
# --->1) répertoire d'installation
PROJ_HOME="`dirname $0`/.."

# --->2) répertoire où se trouvent les *.jar
LIB_DIR="$PROJ_HOME/lib"

# --->3) répertoire où se trouvent les *.class
CLASSES_DIR="$PROJ_HOME/build/classes"


# --->4) nom de la classe à démarrer
CLASS_NAME=fr.ul.projet.CandidatDelete

# -->5) paramètres de la commande
PARAMS="-i 516043657 -p password -r jdbc:mysql://127.0.0.1:3306/bac?useSSL=false -u root"

# -----------------------------------------
# A ne pas modifier
# -----------------------------------------

# ---->Classpath automatique
PROJ_CP="$PROJ_HOME/build/classes"
for x in `ls "$LIB_DIR"`
do
PROJ_CP="$PROJ_CP:$LIB_DIR/$x"
done

# ---->java command
COMMAND="java -classpath \"$PROJ_CP\" $CLASS_NAME $PARAMS $@"
#echo $COMMAND
eval $COMMAND

# ----------------------------------------
#                  END
# ----------------------------------------
