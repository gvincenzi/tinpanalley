const TRANSLATIONS = {
  it: {
    pageTitle: "TIN PAN ALLEY: The NoMad Music Moguls — Gioco da Tavolo Ibrido",
    nav: {
      game: "Il Gioco",
      board: "Tabellone",
      pnp: "Print & Play",
      rules: "Regolamento",
      app: "The Algorhythm"
    },
    hero: {
      badge: "Prototipo in Sviluppo • v1.0",
      tagline: '"Scopri il talento. Accendi i club. Conquista la scena."',
      desc: 'Un gioco da tavolo ibrido ambientato nella scena musicale contemporanea di Manhattan. Gestisci la tua agenzia, investi in <strong>Beats (BT)</strong>, controlla i club storici di NoMad e sfida gli avversari a colpi di blind test orchestrati da <strong>The Algorhythm</strong>.',
      specPlayers: "<strong>3–8</strong> Giocatori",
      specDuration: "<strong>45–60</strong> Minuti",
      specAge: "<strong>14+</strong> Età",
      specFormat: "<strong>Tavolo + Web-App</strong>",
      ctaPnp: "Scarica il Print &amp; Play",
      ctaRules: "Leggi il Regolamento"
    },
    features: {
      title: "Gestione strategica incontra <span>l'ascolto dal vivo</span>",
      subtitle: "Nessun giocatore deve sacrificarsi nel ruolo di moderatore o DJ. Tutti competono dall'inizio alla fine.",
      c1Tag: "01 • Zero Downtime",
      c1Title: "DJ Digitale &amp; Blind Test",
      c1Desc: "Tutti ascoltano i brani simultaneamente e inviano le risposte dal proprio smartphone. Il motore di <em>Fuzzy Matching</em> convalida artisti e titoli anche in caso di piccoli errori di battitura.",
      c2Tag: "02 • Psicologia",
      c2Title: 'La Regola "Trendsetter"',
      c2Desc: "Quando organizzi una serata in un club, non scegliere hit troppo scontate né brani introvabili: incassi il massimo solo se indovinano da 1 fino alla metà degli avversari.",
      c3Tag: "03 • Anti-Eliminazione",
      c3Title: "Scout Freelance &amp; Awards",
      c3Desc: "Chi resta senza Beats non viene eliminato: attiva il salvagente <em>Scout Freelance</em> e continua a fare punti fino al gran finale dei <strong>NoMad Music Awards</strong>."
    },
    board: {
      title: "I 5 Distretti e i 10 Club di NoMad",
      desc: "I giocatori si spostano lungo un anello urbano per acquisire le licenze dei club più ambiti, incassare 300 BT di royalty dai concerti altrui e puntare al monopolio di quartiere.",
      cta: "Apri &amp; Stampa Tabellone (4× A4)"
    },
    downloads: {
      title: "Materiali <span>Print &amp; Play</span>",
      subtitle: 'Apri ogni file nel browser, scegli tra la modalità <strong>Notturna (Neon)</strong> o <strong>Ink-Friendly</strong> (risparmio inchiostro) e stampa su fogli A4 al 100% di scala.',
      d1Meta: "Foglio A4 • 1 Pagina",
      d1Title: "Pedine, Gettoni Club &amp; Headliner",
      d1Desc: "8 pedine esagonali Impresario, 16 gettoni circolari Controllo Club (Ø 25 mm) e il segnalino Primo Giocatore Headliner.",
      d2Meta: "Fogli A4 • 3 Pagine",
      d2Title: "Banconote Beats (BT)",
      d2Desc: "La valuta ufficiale del gioco impaginata per taglio rapido: tagli da 100 BT (Ciano), 500 BT (Magenta) e 1.000 BT (Oro).",
      d3Meta: "Foglio A4 • 2 Schede A5",
      d3Title: "Schede Ufficio Management",
      d3Desc: "Riferimento rapido al tavolo per ogni giocatore: azioni, costi, tabella Trendsetter, scommesse e alloggiamento gettoni.",
      d4Meta: "Fogli A4 • 4 Pagine",
      d4Title: "Tabellone di NoMad (38 × 38 cm)",
      d4Desc: "Mappa vettoriale divisa in 4 quadranti A4 con modalità Ink-Friendly e slot esatti da 25 mm per i gettoni.",
      btnPrint: "Apri &amp; Stampa (HTML / PDF)",
      rMeta: "Documentazione Ufficiale • IT / FR",
      rTitle: "Regolamento Completo",
      rDesc: "Consulta le regole aggiornate (v1.0) in Italiano o in Francese, complete di esempi, formule di punteggio e spareggi."
    },
    webapp: {
      title: "Gioca ora con <span>The Algorhythm</span>",
      desc: "Avvia la Console Centrale sul tablet o PC collegato alla cassa audio per generare il QR Code della partita, oppure entra direttamente da smartphone come giocatore.",
      ctaHost: "▶ Avvia Console Centrale (Host)",
      ctaPlayer: "📱 Entra come Giocatore",
      cta: "Scarica Cover Scatola (PNG)"
    },
    footer: {
      credit: "<strong>TIN PAN ALLEY: The NoMad Music Moguls</strong> — Ideato e progettato da <strong>Giuseppe Vincenzi</strong>",
      rights: "Prototipo in sviluppo • Open Source (MIT & CC BY-SA 4.0) • Uso privato non commerciale"
    },
    legal: {
      title: "Progetto Open Source &amp; <span>Note d'Uso</span>",
      subtitle: "Trasparenza su licenze, streaming audio e ambito di utilizzo.",
      c1Tag: "🎯 Intenti",
      c1Title: "Gratuito &amp; No-Profit",
      c1Desc: "<strong>Tin Pan Alley</strong> è un progetto hobbistico, gratuito e open source. Nessun componente è in vendita e non è prevista alcuna monetizzazione (niente pubblicità, paywall o sponsor).",
      c1Li1: "<strong>Codice:</strong> Licenza MIT",
      c1Li2: "<strong>Regole &amp; Print &amp; Play:</strong> CC BY-SA 4.0",
      c2Tag: "🎵 Streaming Audio",
      c2Title: "Deezer API &amp; Diritti",
      c2Desc: 'L\'app riproduce esclusivamente <strong>anteprime ufficiali di 30 secondi</strong> in streaming tramite <a href="https://developers.deezer.com/" target="_blank" rel="noopener" class="legal-link">Deezer Developer API</a> per finalità non commerciali.',
      c2Li1: "Nessun file audio viene scaricato, salvato o ridistribuito.",
      c2Li2: "Brani, titoli e copertine restano proprietà esclusiva dei rispettivi titolari (non inclusi nelle licenze open source).",
      c3Tag: "⚠️ Limiti d'Uso",
      c3Title: "Uso Privato &amp; Garanzie",
      c3Desc: "Pensato esclusivamente per l'<strong>uso privato tra amici</strong> in contesti domestici.",
      c3Li1: "<strong>Eventi pubblici:</strong> eventuali licenze (es. SIAE / SACEM) sono a carico dell'organizzatore.",
      c3Li2: "Progetto fornito «così com'è», senza garanzia di continuità delle API esterne.",
      githubBtn: "Codice su GitHub"
    }
  },

  fr: {
    pageTitle: "TIN PAN ALLEY: The NoMad Music Moguls — Jeu de Société Hybride",
    nav: {
      game: "Le Jeu",
      board: "Plateau",
      pnp: "Print & Play",
      rules: "Règlement",
      app: "The Algorhythm"
    },
    hero: {
      badge: "Prototype en Développement • v1.0",
      tagline: '"Dénichez le talent. Enflammez les clubs. Conquérez la scène musicale."',
      desc: 'Un jeu de société hybride plongé dans la scène musicale contemporaine de Manhattan. Gérez votre agence, investissez en <strong>Beats (BT)</strong>, prenez le contrôle des clubs emblématiques de NoMad et affrontez vos rivaux lors de blind tests orchestrés par <strong>The Algorhythm</strong>.',
      specPlayers: "<strong>3–8</strong> Joueurs",
      specDuration: "<strong>45–60</strong> Minutes",
      specAge: "<strong>14+</strong> Âge",
      specFormat: "<strong>Plateau + Web-App</strong>",
      ctaPnp: "Télécharger le Print &amp; Play",
      ctaRules: "Lire le Règlement"
    },
    features: {
      title: "La gestion stratégique rencontre <span>le live</span>",
      subtitle: "Aucun joueur n'est contraint de jouer le rôle passif de DJ ou modérateur. Tout le monde participe à 100 %.",
      c1Tag: "01 • Zéro Temps Mort",
      c1Title: "DJ Numérique &amp; Blind Test",
      c1Desc: "Tous les participants écoutent les morceaux simultanément et répondent depuis leur smartphone. Le moteur de <em>Fuzzy Matching</em> valide artistes et titres même avec de légères fautes de frappe.",
      c2Tag: "02 • Psychologie",
      c2Title: 'La Règle "Trendsetter"',
      c2Desc: "Lors de vos soirées club, évitez les hits trop évidents comme les titres introuvables : vous maximisez vos gains si entre 1 et la moitié de vos adversaires trouvent l'artiste.",
      c3Tag: "03 • Zéro Élimination",
      c3Title: "Scout Freelance &amp; Awards",
      c3Desc: "Un joueur à court de Beats n'est jamais éliminé : il active le statut de secours <em>Scout Freelance</em> et continue de marquer des points jusqu'au gala final des <strong>NoMad Music Awards</strong>."
    },
    board: {
      title: "Les 5 Districts et les 10 Clubs de NoMad",
      desc: "Déplacez votre pion sur la boucle urbaine pour acquérir les licences des salles les plus prisées, encaisser 300 BT de royalties sur les concerts adverses et viser le monopole de quartier.",
      cta: "Ouvrir &amp; Imprimer le Plateau (4× A4)"
    },
    downloads: {
      title: "Matériel <span>Print &amp; Play</span>",
      subtitle: 'Ouvrez chaque fichier dans votre navigateur, choisissez entre le mode <strong>Nocturne (Néon)</strong> ou <strong>Ink-Friendly</strong> (économie d\'encre) et imprimez en A4 à l\'échelle 100 %.',
      d1Meta: "Feuille A4 • 1 Page",
      d1Title: "Pions, Jetons Club &amp; Headliner",
      d1Desc: "8 pions hexagonaux Imprésario, 16 jetons circulaires Contrôle de Club (Ø 25 mm) et le jeton Premier Joueur Headliner.",
      d2Meta: "Feuilles A4 • 3 Pages",
      d2Title: "Billets Beats (BT)",
      d2Desc: "La monnaie officielle du jeu mise en page pour une découpe rapide : coupures de 100 BT (Cyan), 500 BT (Magenta) et 1 000 BT (Or).",
      d3Meta: "Feuille A4 • 2 Fiches A5",
      d3Title: "Fiches Bureau de Management",
      d3Desc: "Aide de jeu individuelle : actions, coûts, tableau Trendsetter, paris et emplacements dédiés aux jetons de club.",
      d4Meta: "Feuilles A4 • 4 Pages",
      d4Title: "Plateau de NoMad (38 × 38 cm)",
      d4Desc: "Carte vectorielle divisée en 4 quadrants A4 avec mode Ink-Friendly et emplacements exacts de 25 mm pour les jetons.",
      btnPrint: "Ouvrir &amp; Imprimer (HTML / PDF)",
      rMeta: "Documentation Officielle • IT / FR",
      rTitle: "Règlement Complet",
      rDesc: "Consultez les règles complètes (v1.0) en Français ou en Italien, incluant les exemples, les barèmes de points et les départages."
    },
    webapp: {
      title: "Jouez maintenant avec <span>The Algorhythm</span>",
      desc: "Lancez la Console Centrale sur la tablette ou le PC relié à l'enceinte audio pour générer le QR Code de la partie, ou rejoignez directement depuis votre smartphone.",
      ctaHost: "▶ Lancer la Console Centrale (Hôte)",
      ctaPlayer: "📱 Rejoindre en tant que Joueur",
      cta: "Télécharger la Jaquette (PNG)"
    },
    footer: {
      credit: "<strong>TIN PAN ALLEY: The NoMad Music Moguls</strong> — Imaginé et conçu par <strong>Giuseppe Vincenzi</strong>",
      rights: "Prototype en développement • Open Source (MIT & CC BY-SA 4.0) • Usage privé non commercial"
    },
    legal: {
      title: "Projet Open Source &amp; <span>Mentions d'Usage</span>",
      subtitle: "Transparence sur les licences, le streaming audio et le cadre d'utilisation.",
      c1Tag: "🎯 Intentions",
      c1Title: "Gratuit &amp; Non Lucratif",
      c1Desc: "<strong>Tin Pan Alley</strong> est un projet amateur, gratuit et open source. Aucun élément n'est vendu et aucune monétisation n'est prévue (sans publicité, paywall ni sponsoring).",
      c1Li1: "<strong>Code source :</strong> Licence MIT",
      c1Li2: "<strong>Règles &amp; Print &amp; Play :</strong> CC BY-SA 4.0",
      c2Tag: "🎵 Streaming Audio",
      c2Title: "Deezer API &amp; Droits",
      c2Desc: 'L\'application diffuse uniquement des <strong>extraits officiels de 30 secondes</strong> en streaming via la <a href="https://developers.deezer.com/" target="_blank" rel="noopener" class="legal-link">Deezer Developer API</a> à des fins non commerciales.',
      c2Li1: "Aucun fichier audio n'est téléchargé, stocké ou redistribué.",
      c2Li2: "Morceaux, titres et pochettes restent la propriété exclusive de leurs titulaires de droits (exclus des licences open source).",
      c3Tag: "⚠️ Limites d'Usage",
      c3Title: "Usage Privé &amp; Garanties",
      c3Desc: "Conçu exclusivement pour un <strong>usage privé entre amis</strong> dans un cadre domestique.",
      c3Li1: "<strong>Événements publics :</strong> les licences requises (ex. SIAE / SACEM) relèvent de la responsabilité de l'organisateur.",
      c3Li2: "Projet fourni « en l'état », sans garantie de disponibilité continue des API externes.",
      githubBtn: "Code sur GitHub"
    }
  }
};