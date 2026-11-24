# TIN PAN ALLEY: The NoMad Music Moguls

**Auteur :** Giuseppe Vincenzi  
**Joueurs :** 3–8 | **Âge :** 14+ | **Durée :** 45–60 minutes  
**Format :** Jeu de société hybride (Plateau physique + Application Web connectée au streaming audio)

---

## 1. THÈMATIQUE ET INTRODUCTION

Dans le quartier vibrant de **NoMad (Manhattan)**, là où est née l'industrie musicale moderne au tournant du XXe siècle le long de la légendaire *Tin Pan Alley*, une nouvelle génération d'imprésarios s'affronte pour imposer sa signature sur les scènes live de la ville. Pour triompher, il faudra faire preuve de flair financier lors des enchères de talents, de vision stratégique sur le territoire et d'une oreille affûtée lors des blind tests en club.

L'intégralité des opérations musicales, le chronométrage et l'arbitrage des scores sont confiés à **"The Algorhythm"**, une application Web qui agit comme DJ et arbitre impartial, connectée directement au compte streaming d'un des joueurs (Spotify Premium via Web Playback SDK ou Deezer via extraits officiels). Aucun participant n'est sacrifié dans le rôle passif de maître de jeu : tout le monde joue et concourt pour la victoire.

---

## 2. LA MONNAIE DU JEU : LES BEATS (BT)

La monnaie officielle du jeu est le **Beat**, abrégé **BT** (au pluriel : **Beats**).  
Les Beats représentent le budget opérationnel, les cachets promotionnels et la trésorerie des agences. Ils sont utilisés pour :
* participer aux enchères d'artistes ;
* acheter les licences des clubs ;
* payer les royalties aux propriétaires des salles ;
* placer des paris lors des soirées live.

---

## 3. MATÉRIEL DE JEU

### Matériel Physique :
* **1 Plateau de jeu (Quartier NoMad, Manhattan) :** Divisé en **5 Districts** interconnectés en boucle (*Broadway, Flatiron, 5th Avenue, Koreatown, Madison Square Park*). Chaque district abrite exactement **2 Clubs** (10 Clubs au total).
* **8 Pions Imprésario** en bois laqué.
* **16 Jetons "Contrôle de Club"** (2 par couleur de joueur).
* **Billets ou Jetons Beats (BT) :** Valeurs de 100, 500 et 1 000 BT.
* **8 Fiches "Bureau de Management" :** Aides de jeu individuelles.
* **1 Jeton Premier Joueur**.

### Module Numérique ("The Algorhythm") :
* Application Web accessible sans installation depuis n'importe quel smartphone via un simple QR-Code (chaque téléphone fait office de manette de jeu secrète).
* Sortie audio via l'écran central (tablette/ordinateur/smartphone) relié à une enceinte Bluetooth au centre de la table.
* Moteur de **Fuzzy Matching** : validation automatique des réponses même en cas de fautes de frappe, accents omis ou articles inversés.
* Gestion automatisée des comptes à rebours, des enchères simultanées et du classement en temps réel.

---

## 4. LE PLATEAU ET LES CLUBS

Les 5 districts forment un tracé en anneau fermé :

$$\text{Broadway} \longleftrightarrow \text{Flatiron} \longleftrightarrow \text{5th Avenue} \longleftrightarrow \text{Koreatown} \longleftrightarrow \text{Madison Square Park} \longleftrightarrow \text{Broadway}$$

Chaque district est exclusivement adjacent à celui qui le précède et à celui qui le suit sur la boucle. Plusieurs pions peuvent occuper simultanément le même district.

### Les 10 Clubs du Quartier :
* **Broadway :** *Amplifier*, *Side B*
* **Flatiron :** *Prism*, *Rooftop*
* **5th Avenue :** *Signal*, *The Pulse*
* **Koreatown :** *Night Shift*, *Frequency*
* **Madison Square Park :** *Green Room*, *Open Air*

---

## 5. MISE EN PLACE

1. **La Console Centrale :** Installez un appareil central (tablette ou ordinateur) relié à l'enceinte Bluetooth. L'hôte se connecte avec son compte streaming (Spotify Premium ou Deezer) et sélectionne le répertoire musical convenu pour la partie (catalogue général de hits, sélection thématique ou playlist sur-mesure).
2. **Connexion des Joueurs :** Chaque participant scanne le QR-Code avec son smartphone, renseigne son nom de scène et choisit la couleur de son pion.
3. **Dotation Initiale :** Chaque imprésario reçoit :
   * **3 000 BT** de la Banque physique ;
   * 1 Fiche Bureau de Management ;
   * 2 Jetons Contrôle de Club de sa couleur ;
   * 1 Pion Imprésario, placé dans le District de départ attribué aléatoirement par l'application.
4. **Conditions de Départ :** Tous les joueurs démarrent avec **0 Point de Réputation (PR)**. L'application désigne aléatoirement le Premier Joueur.
5. **Draft Initial des Artistes (Simultané) :**
   * L'application affiche sur le smartphone de chaque joueur 4 artistes distincts extraits du catalogue.
   * Chaque manager sélectionne secrètement **2 artistes** pour composer son Roster de départ. L'algorithme garantit qu'un artiste attribué ne sera jamais proposé à un autre joueur.

---

## 6. STRUCTURE D'UNE PARTIE

Afin de maintenir une durée calibrée entre **45 et 60 minutes**, le nombre de Rounds s'adapte automatiquement au nombre de participants :
* **De 3 à 4 Joueurs :** **3 Rounds** de saison + Grand Festival Final.
* **De 5 à 8 Joueurs :** **2 Rounds** de saison + Grand Festival Final.

Chaque Round est rigoureusement découpé en **3 Phases successives** :
* **Phase 1 : Marché & Repérage (Numérique, simultanée)**
* **Phase 2 : Actions & Gestion des Clubs (Physique, au tour par tour)**
* **Phase 3 : Les Soirées Live (Hybride, blind test collectif)**

À la fin de chaque Round, le jeton Premier Joueur passe au joueur suivant dans le sens horaire.

---

## 7. PHASE 1 : MARCHÉ & REPÉRAGE (Simultanée — 3 minutes)

Au début de chaque Round, les regards se tournent vers les smartphones pour recruter de nouveaux talents :
1. L'application dévoile sur l'écran central **4 nouveaux artistes** disponibles sur le marché.
2. Depuis son téléphone, chaque joueur participe à une **enchère secrète à l'aveugle**, en misant des Beats sur un ou plusieurs artistes (mises par tranches de 100 BT, minimum 100 BT). Le système bloque toute mise supérieure au solde réel du joueur ou toute combinaison d'offres excédant ses fonds disponibles.
3. À la fin du compte à rebours de **60 secondes**, les résultats sont résolus instantanément :
   * L'artiste est attribué au plus offrant, qui verse le montant à la Banque physique.
   * **En cas d'égalité :** L'artiste est remporté par le joueur possédant **le moins de Points de Réputation (PR)** au moment de l'enchère (mécanisme de rattrapage). En cas de nouvelle égalité, l'ordre du tour prévaut.
   * **Artistes non vendus (0 offre) :** Ils sont retirés définitivement de la partie.

---

## 8. PHASE 2 : ACTIONS & GESTION DES CLUBS (Physique)

En commençant par le Premier Joueur et dans le sens horaire, chaque manager dispose de **3 Points d'Action (PA)** à dépenser sur le plateau. Les actions peuvent être combinées librement :

* **Se Déplacer (1 PA par District) :** Déplacez votre pion vers un District adjacent sur la boucle.
* **Acquérir une Licence de Club (1 PA et 800 BT) :** Réglez **800 BT** à la Banque pour poser un jeton Contrôle de Club sur une salle libre du district où se trouve votre pion.
  * Chaque imprésario peut contrôler un **maximum de 2 Clubs** par partie.
  * Dès cet instant, tout rival organisant une soirée dans ce club devra vous verser **300 BT de royalty** avant le concert.
* **Toucher un Sponsor (1 PA) :** Recevez **300 BT** de la Banque (sécurité financière).

Les PA non dépensés durant le tour sont perdus.

---

## 9. PHASE 3 : LES SOIRÉES LIVE (Blind test collectif)

À tour de rôle, chaque imprésario peut activer sa **Licence Concert** du round (maximum **1 Soirée Live par joueur par Round** ; l'action est facultative, il est possible de passer).

L'organisateur choisit l'un des deux Clubs présents dans le district où se trouve son pion :
* Si le Club appartient à un rival, il lui verse immédiatement **300 BT de royalty**.
* Si le Club est libre ou lui appartient, aucune royalty n'est due.
* **Réutilisation des Salles :** Un même Club peut accueillir plusieurs soirées durant le même Round par des joueurs différents. Le propriétaire encaisse 300 BT pour chaque concert qui s'y tient.

L'organisateur sélectionne sur son smartphone l'artiste de son roster qu'il souhaite programmer. L'application prépare la lecture d'un titre de cet artiste.

---

### A. La Billetterie Municipale (Revenus de l'Organisateur)
L'organisateur ne dépend pas de la générosité de ses adversaires : il reçoit une rétribution institutionnelle de la Banque, indexée sur la formule du **Trendsetter**, basée sur le nombre d'adversaires à table ayant reconnu l'artiste programmé.  
*(Soit $N$ le nombre total d'adversaires présents, organisateur exclu).*

| Résultat de la Soirée | Adversaires trouvant l'Artiste | Revenus Organisateur (Banque) | Réputation Gagnée |
| :--- | :--- | :--- | :--- |
| **Flop Underground** | 0 adversaire | **0 BT** | **0 PR** |
| **Trendsetter d'Élite** | De 1 jusqu'à la moitié ($\lfloor N/2 \rfloor$) | **1 200 BT** | **+3 PR** |
| **Succès Mainstream** | De plus de la moitié à $N-1$ adversaires | **800 BT** | **+1 PR** |
| **Trop Commercial** | Tous les adversaires ($N$) | **400 BT** | **0 PR** |

---

### B. Le Pari sur le Hit (Les Adversaires)
Tous les autres joueurs participent systématiquement à l'écoute. Aucun joueur ne reste passif.  
Avant le lancement du morceau, chaque adversaire valide son choix sur son écran :

* **Accès Standard (0 BT) :** L'écoute est gratuite. Les bonnes réponses rapportent les PR habituels.
* **Pari sur le Hit (200 BT versés à la Banque) :**
  * **Pari Gagnant :** Si le joueur identifie **au moins** l'Artiste ou le Titre, il double les PR obtenus par sa réponse et empoche **500 BT** de la Banque (récupération de sa mise de 200 BT + 300 BT de bénéfice net).
  * **Pari Perdu :** Si le joueur échoue à trouver à la fois l'Artiste et le Titre, les 200 BT sont conservés par la Banque.

---

### C. Écoute & Attribution des Points
1. L'application diffuse **30 secondes** du morceau sur l'enceinte.
2. Tous les participants saisissent simultanément leurs réponses sur smartphone avant la fin du chronomètre.
3. Le correcteur automatique valide les saisies et met à jour les scores :
   * **Artiste correct :** **+2 PR** (deviennent **+4 PR** avec Pari gagnant).
   * **Titre correct :** **+3 PR** (deviennent **+6 PR** avec Pari gagnant).
   * *Les points d'Artiste et de Titre sont cumulables (jusqu'à 5 PR de base, ou 10 PR avec Pari).*
   * **Décennie de consolation (+1 PR) :** Attribuée **uniquement** aux joueurs ayant manqué à la fois l'Artiste et le Titre, mais ayant identifié la décennie exacte de sortie du titre (ex. années 80). Ce point ne peut jamais être doublé par un pari.

---

## 10. CLAUSE DE SAUVEGARDE : LE "SCOUT FREELANCE"

Dans *Tin Pan Alley*, aucun joueur n'est éliminé pour faillite :
1. **Activation :** Si un joueur ne possède pas les fonds suffisants pour honorer une dépense obligatoire ou une royalty due à un adversaire, il prend immédiatement le statut de **Scout Freelance**.
2. **Sauvetage d'Urgence :** L'application retire automatiquement de son roster l'artiste ayant la valeur marchande la plus élevée pour le remettre aux enchères au tour suivant. En échange, le joueur reçoit une aide d'urgence de **1 000 BT** de la Banque pour régler sa dette.  
   *(Si le joueur ne possède aucun artiste à céder, il reçoit les 1 000 BT mais écope d'une pénalité de **-5 PR** lors du décompte final).*
3. **Privilège Temporaire :** Pour le reste du Round en cours, le Freelance bénéficie d'un bonus de **+1 PR supplémentaire** pour chaque bonne réponse trouvée lors des concerts adverses.
4. **Rétablissement :** À l'issue du Round en cours, le statut de Freelance prend fin automatiquement.

---

## 11. GRAND FINAL : LES "NOMAD MUSIC AWARDS"

Une fois le dernier Round de saison terminé (à l'issue de la Phase 3), le plateau est gelé et le Festival s'ouvre :

1. **Sélection des Titres :** Chaque manager choisit en secret via l'application **1 chanson majeure** issue d'un des artistes de son roster final.
2. **Le Grand Blind Test :** L'application diffuse de manière anonyme et aléatoire tous les morceaux en compétition :
   * Les points d'Artiste et de Titre sont doublés pour tous les participants : **+4 PR pour l'Artiste** et **+6 PR pour le Titolo/Titre**.
   * Un joueur ne peut pas marquer de points sur le morceau qu'il a lui-même présenté.
3. **Prix de la Critique :** Après les écoutes, chaque joueur vote sur son smartphone pour le meilleur morceau du festival (hors sa propre sélection) :
   * Le titre réunissant le plus de suffrages remporte le trophée, octroyant un bonus décisif de **+10 PR** à son impresario.
   * *En cas d'égalité de votes, les 10 PR sont partagés équitablement (+5 PR pour chaque candidat ex aequo).*

---

## 12. DÉCOMPTE FINAL DES POINTS ET VICTOIRE

L'application centralise l'ensemble des données et proclame le palmarès final en additionnant :
* **Points de Réputation (PR)** cumulés durant les Soirées Live et au Festival ;
* **Valeur Immobilière :** **+1 PR** pour chaque Club sous contrôle sur le plateau ;
* **Monopole de District :** **+3 PR bonus** pour chaque District dont le manager contrôle **les deux** Clubs ;
* **Trésorerie Restante :** **+1 PR** par tranche complète de **500 BT** en caisse ;
* **Pénalités :** Déduction éventuelle de **-5 PR** pour recours au sauvetage sans artiste cédé.

### Règles de Départage :
En cas d'égalité aux points pour la première place, la victoire est tranchée selon les critères suivants :
1. Le plus grand nombre de PR obtenus lors de la Phase 3 (Soirées Live).
2. La plus importante réserve de Beats (BT) en caisse.
3. En cas d'égalité persistante, la victoire est partagée ex aequo.

The Algorhythm lance alors l'hymne triomphal : l'imprésario trônant au sommet du classement est officiellement couronné **King of Tin Pan Alley** !