# TIN PAN ALLEY: The NoMad Music Moguls

<p align="center">
  <img src="assets/box_cover.png" alt="Tin Pan Alley Box Cover" width="380"/>
  <img src="assets/board.png" alt="Tin Pan Alley Game Board" width="380"/>
</p>

<p align="center">
  <strong>Autore / Auteur:</strong> Giuseppe Vincenzi<br>
  <strong>Giocatori / Joueurs:</strong> 3–8 | <strong>Durata / Durée:</strong> 45–60 min | <strong>Età / Âge:</strong> 14+<br>
  <strong>Genere / Genre:</strong> Gioco da tavolo ibrido (Mappa + Web-App) / Jeu de société hybride (Plateau + Web-App)
</p>

<p align="center">
  <a href="https://inmediart.ovh/algorhythm/"><strong>▶ Avvia The Algorhythm (Web-App)</strong></a> •
  <a href="#italiano">🇮🇹 Italiano</a> •
  <a href="#français">🇫🇷 Français</a>
</p>

---

<a name="italiano"></a>
## 🇮🇹 Italiano

> *"Scopri il talento. Accendi i club. Conquista la scena."*

### Cos'è Tin Pan Alley?
Nel leggendario quartiere di **NoMad (Manhattan)**, dove a cavallo del Novecento nacque l'industria discografica lungo la celebre *Tin Pan Alley*, una nuova generazione di impresari musicali compete per il dominio dei palchi cittadini.

**Tin Pan Alley** è un gioco da tavolo moderno e ibrido che fonde **strategia manageriale da tavolo** (controllo dell'area, aste al buio, gestione della liquidità in **Beats - BT**) ed **esperienza party/trivia musicale dal vivo**.

Il gioco è orchestrato da **"The Algorhythm"**, una Web-App in **Java 21 / Spring Boot** che si interfaccia con le API di **Deezer** (playlist preconfigurate o playlist personalizzate incollate al volo dall'Host). L'IA funge da DJ neutrale, gestisce il cronometro e convalida le risposte dei blind test tramite correttore intelligente (*Fuzzy Matching*): **nessun giocatore deve sacrificarsi nel ruolo di moderatore, tutti giocano al 100% del tempo.**

### Caratteristiche Principali
* **Zero Tempi Morti (No Downtime):** Tutti i giocatori partecipano contemporaneamente a ogni brano musicale digitando le risposte sul proprio smartphone.
* **La Formula "Trendsetter":** Guadagni se proponi brani raffinati che solo una parte del tavolo sa riconoscere. Troppo facile? Guadagni zero. Troppo difficile? Locale deserto!
* **Economia Dinamica in Beats (BT):** Acquisisci licenze sui club di NoMad (come *Amplifier* o *Prism*), incassa royalty dai rivali e punta sulla *Scommessa sulla Hit*.
* **Nessuna Eliminazione:** Il sistema dello *Scout Freelance* permette a chi va al verde di restare in gara fino al festival finale dei **NoMad Music Awards**.
* **Kit Print & Play Completo:** Tutti i componenti fisici (pedine, gettoni da 25 mm, banconote, schede e tabellone 38×38 cm diviso in 4× A4) includono la modalità **Ink-Friendly** per la stampa casalinga.

📖 **[LEGGI IL REGOLAMENTO COMPLETO IN ITALIANO](rules/regolamento_IT.md)**  
🎧 **[APRI LA WEB-APP THE ALGORHYTHM](https://inmediart.ovh/algorhythm/)**

---

<a name="français"></a>
## 🇫🇷 Français

> *"Dénichez le talent. Enflammez les clubs. Conquérez la scène musicale."*

### Qu'est-ce que Tin Pan Alley ?
Dans le quartier emblématique de **NoMad à Manhattan**, berceau historique de l'industrie du disque le long de la légendaire *Tin Pan Alley*, une nouvelle génération de managers s'affronte pour régner sur les scènes live de la ville.

**Tin Pan Alley** est un jeu de société hybride et contemporain combinant **gestion stratégique sur plateau** (contrôle de zones, enchères secrètes, investissements en **Beats - BT**) et **blind tests musicaux en streaming réel**.

Toute la partie musicale est orchestrée par **"The Algorhythm"**, une application web en **Java 21 / Spring Boot** connectée à l'API **Deezer** (playlists thématiques ou playlist personnalisée collée à la volée par l'hôte). L'IA agit comme DJ impartial, chronométreur et arbitre grâce à un correcteur intelligent (*Fuzzy Matching*) : **aucun joueur n'est contraint de jouer le rôle passif de maître de jeu ou DJ ; tout le monde participe activement.**

### Points Forts
* **Zéro Temps Mort :** Tous les participants jouent simultanément sur chaque morceau via leur smartphone.
* **Le Dilemme du "Trendsetter" :** Pour maximiser vos revenus lors de vos soirées club, choisissez des artistes pointus mais accessibles. Un titre trop évident ou trop obscur ne vous rapportera rien !
* **Économie & Contrôle de Quartier :** Prenez le contrôle des clubs phares de NoMad (*Amplifier*, *Prism*...), encaissez les royalties de vos rivaux et pariez sur les hits en direct.
* **Aucune Élimination :** Le statut de *Scout Freelance* protège les joueurs en difficulté financière et garantit une course au score ouverte jusqu'au gala final des **NoMad Music Awards**.
* **Kit Print & Play Complet :** Tous les éléments physiques (pions, jetons 25 mm, billets, fiches et plateau 38×38 cm en 4× A4) disposent d'un mode **Ink-Friendly** pour l'impression maison.

📖 **[LIRE LE RÈGLEMENT COMPLET EN FRANÇAIS](rules/reglement_FR.md)**  
🎧 **[OUVRIR L'APP WEB THE ALGORHYTHM](https://inmediart.ovh/algorhythm/)**

---

## 🗂️ Struttura del Repository / Structure du Dépôt

```text
├── assets/
│   ├── box_cover.png           # Concept art della scatola / Visuel de la boîte
│   ├── board.png               # Tabellone Notturno / Plateau Nocturne
│   └── board_ink.png           # Tabellone Ink-Friendly / Plateau Ink-Friendly
├── print-and-play/             # Componenti stampabili A4 (IT / FR + Ink-Friendly)
│   ├── 01_pedine_e_gettoni.html
│   ├── 02_beats_banconote.html
│   ├── 03_schede_management.html
│   ├── 04_tabellone.html
│   ├── css/
│   └── js/
├── rules/                      # Regolamenti completi / Livrets de règles complets
│   ├── regolamento_IT.md
│   └── reglement_FR.md
├── app/                        # Backend Java 21 / Spring Boot + WebSocket (The Algorhythm)
├── index.html                  # Landing page bilingue (IT / FR)
├── landing.css
├── translations.js
└── README.md                   # Questo file / Ce fichier
```

---

## ⚖️ Licenza & Crediti / Licence & Crédits
* **Game Design & Concept:** Giuseppe Vincenzi
* Tutti i diritti riservati / Tous droits réservés.