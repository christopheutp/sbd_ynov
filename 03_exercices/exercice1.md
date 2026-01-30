## Exercice – “Secure Vault : coffre-fort de rapports clients”

### Mise en situation

Développer un mini “coffre-fort” local pour stocker des **rapports clients** (texte) sur disque. Ces rapports 
contiennent des informations sensibles.

### Objectif

Implémenter les 3 axes de la triade CIA :

* **Confidentialité** : le rapport ne doit pas être lisible sur disque.
* **Intégrité** : toute modification du fichier doit être détectée.
* **Disponibilité** : la lecture doit rester possible même si le fichier principal est indisponible (supprimé/corrompu).

### Travail demandé (énoncé)

Écrire une application console Java qui :

1. **Enregistre** un rapport (`clientId`, `content`) dans un fichier sur disque.
2. **Chiffre** le contenu avant écriture (confidentialité).
3. **Signe** (ou authentifie) les données stockées pour détecter toute altération (intégrité).
4. Maintient **une copie de secours** et, lors de la lecture, tente :

    * lecture depuis le fichier principal,
    * sinon bascule automatiquement sur la copie de secours (disponibilité).
5. Fournit deux commandes (ou deux exécutions dans le `main`) :

    * `save clientId "..."` : sauvegarde sécurisée
    * `read clientId` : lecture sécurisée (avec bascule si besoin)

Contraintes :

* Pas de dépendances externes (JDK uniquement).
* Choix du format (binaire ou texte) tant qu’il est reproductible.
* La clé doit provenir d’un **secret fourni** (ex : passphrase en dur dans le code pour l’exercice, ou argument CLI).

