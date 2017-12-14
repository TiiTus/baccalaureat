#!/bin/bash

#Affichage du menu
echo "*****************************************************************************"
echo "***** Bienvenue dans l'application de gestion des notes du baccalauréat *****"
echo "*****************************************************************************"
echo "Que voulez-vous faire ?"
echo "1. Insertions de données dans la base"
echo "2. Extraire les notes d'une série pour la première session"
echo "3. Extraire les notes d'une série pour la seconde session"
echo "4. Extraire les moyennes pour les notes de le première session"
echo "5. Extraire les moyennes pour les notes de le seconde session"
echo "6. Extraire les notes remplaçables pour la seconde session"
echo "7. Supprimer un candidat de la base de données"
echo "*****************************************************************************"

#Lecture du choix entré au clavier
read -p "Entrez le numéro de votre choix : " choice

#switch pour lancer les différents scripts selon le choix
case $choice in
  1)
    echo "1. Insérer des candidats"
    echo "2. Insérer des profils"
    echo "3. Insérer des épreuves"
    echo "4. Insérer des matières"
    echo "5. Insérer des épreuves composées"
    echo "6. Insérer des épreuves remplaçables"
    echo "7. Insérer des précisions sur les épreuves"
    echo "8. Insérer des données de détermination entre une épreuve et un profil"
    echo "9. Insérer des résultats"
    read -p "Entrez le numéro de votre choix : " choice2
    case $choice2 in
      1)
        sh ./candidat.sh
        ;;
      2)
        sh ./profil.sh
        ;;
      3)
        sh ./epreuve.sh
        ;;
      4)
        sh ./matiere.sh
        ;;
      5)
        sh ./composer.sh
        ;;
      6)
        sh ./remplacer.sh
        ;;
      7)
        sh ./preciser.sh
        ;;
      8)
        sh ./determiner.sh
        ;;
      9)
        sh ./resultat.sh
        ;;
      *)
        echo "Le choix entré ne correspond à aucune fonctionnalité"
    esac
      ;;
  2)
    sh ./notesS1Extract.sh
    ;;
  3)
    sh ./notesS2Extract.sh
    ;;
  4)
    echo "Fonctonnalité non développée"
    ;;
  5)
    echo "Fonctonnalité non développée"
    ;;
  6)
    sh ./notesRemplacables.sh
    ;;
  7)
    sh suppCandidat.sh
    ;;
  *)
    echo "Le choix entré ne correspond à aucune fonctionnalité"
esac
