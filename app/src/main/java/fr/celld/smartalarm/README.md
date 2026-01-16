# SmartAlarm

The but de ce projet est de créer une application d'alarme intelligente pour les appareils Android.
L'application permet aux utilisateurs de définir des alarmes personnalisées avec diverses options et fonctionnalités.

La principale fonctionnalité de SmartAlarm est la possibilité de configurer une alarme qui peut être désactivée si on est déjà réveillé, en utilisant des capteurs de mouvement ou d'autres critères.
Le but est d'améliorer l'expérience de réveil des utilisateurs en offrant une alarme plus adaptative et intelligente.

## Fonctionnalités principales
- Définition d'alarmes personnalisées avec des sons, des vibrations et des messages.
- Utilisation de capteurs pour détecter si l'utilisateur est déjà réveillé. (On aura l'option de choisir une méthode de détection)
- Interface utilisateur intuitive pour la gestion des alarmes.
- Les alarmes devront fonctionner même si l'application est fermée ou si l'appareil est en mode veille.
- Options de répétition d'alarmes pour les jours spécifiques de la semaine.
- Possibilité de définir des alarmes multiples.
- Notifications pour rappeler à l'utilisateur les alarmes à venir.
- Mode "snooze" pour retarder l'alarme.

## MVP
Le MVP (Minimum Viable Product) de SmartAlarm inclura les fonctionnalités suivantes :
- Création et gestion d'alarmes simples.
- Activation et désactivation des alarmes.
- Utilisation d'un capteur de mouvement pour détecter si l'utilisateur est réveillé.
- Interface utilisateur de base pour la configuration des alarmes.
- Alarme fonctionnelle même lorsque l'application est fermée.

## Technologies utilisées
- Langage de programmation : Kotlin
- Plateforme : Android
- IDE : Android Studio

## UI
L'interface utilisateur de SmartAlarm sera conçue pour être simple et intuitive.
Elle comprendra des écrans pour la liste des alarmes, la configuration des alarmes, et les paramètres de l'application.
Des icônes et des couleurs seront utilisées pour améliorer l'expérience utilisateur.

### Page principale
La page principale affichera la liste des alarmes définies par l'utilisateur. Chaque alarme sera représentée par une carte contenant les informations suivantes :
- Heure de l'alarme
- Jours de répétition
- Bouton pour activer/désactiver l'alarme
- Bouton pour modifier ou supprimer l'alarme

Un bouton flottant permettra à l'utilisateur d'ajouter une nouvelle alarme.

### Page de configuration de l'alarme
La page de configuration de l'alarme permettra à l'utilisateur de définir les paramètres suivants :
- Heure de l'alarme (sélecteur d'heure)
- Jours de répétition (cases à cocher pour chaque jour de la semaine)
- Sonnerie (sélecteur de sonnerie)
- Vibration (option pour activer/désactiver la vibration)
- Méthode de détection de réveil (options pour choisir le capteur à utiliser)
- Bouton pour enregistrer l'alarme
- Bouton pour annuler la configuration

### Page des paramètres
La page des paramètres permettra à l'utilisateur de configurer les options globales de l'application, telles que :
- Activer/désactiver les notifications
- Choisir le thème de l'application (clair/sombre)
- Accéder aux informations sur l'application et au support
- Bouton pour réinitialiser les paramètres par défaut