const PLAYER_IDENTITIES = [
  { id: 1, color: '#00e5ff', nameIt: 'Ciano',    nameFr: 'Cyan',     svg: '<path d="M3 18v-6a9 9 0 0 1 18 0v6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z" fill="none" stroke="currentColor" stroke-width="2"/>' },
  { id: 2, color: '#ff2a85', nameIt: 'Magenta',  nameFr: 'Magenta',  svg: '<circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="2"/><circle cx="12" cy="12" r="5.5" fill="none" stroke="currentColor" stroke-width="1.2" stroke-dasharray="6 3"/><circle cx="12" cy="12" r="2.2" fill="currentColor"/>' },
  { id: 3, color: '#ffb703', nameIt: 'Oro',      nameFr: 'Or',       svg: '<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>' },
  { id: 4, color: '#52ff00', nameIt: 'Verde',    nameFr: 'Vert',     svg: '<rect x="9" y="2" width="6" height="11" rx="3" fill="none" stroke="currentColor" stroke-width="2"/><path d="M5 10v1a7 7 0 0 0 14 0v-1M12 18v4M8 22h8" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' },
  { id: 5, color: '#b565ff', nameIt: 'Viola',    nameFr: 'Violet',   svg: '<rect x="2" y="5" width="20" height="14" rx="2" fill="none" stroke="currentColor" stroke-width="2"/><line x1="2" y1="10" x2="22" y2="10" stroke="currentColor" stroke-width="1.5"/><rect x="5.8" y="10" width="2.4" height="5" fill="currentColor"/><rect x="10.8" y="10" width="2.4" height="5" fill="currentColor"/><rect x="15.8" y="10" width="2.4" height="5" fill="currentColor"/>' },
  { id: 6, color: '#ff4d4d', nameIt: 'Corallo',  nameFr: 'Corail',   svg: '<path d="M9 18V5l11-2v13" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><circle cx="6" cy="18" r="3" fill="none" stroke="currentColor" stroke-width="2"/><circle cx="17" cy="16" r="3" fill="none" stroke="currentColor" stroke-width="2"/>' },
  { id: 7, color: '#72efdd', nameIt: 'Ghiaccio', nameFr: 'Glace',    svg: '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>' },
  { id: 8, color: '#ff7b00', nameIt: 'Arancio',  nameFr: 'Orange',   svg: '<path d="M3 18h18L19 7l-4.5 5L12 5 9.5 12 5 7z" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/><line x1="3" y1="21" x2="21" y2="21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' }
];

function getIdentity(colorId) {
  return PLAYER_IDENTITIES.find(p => p.id === colorId) || PLAYER_IDENTITIES[0];
}

function renderIconSvg(colorId, sizePx = 28) {
  const id = getIdentity(colorId);
  return `<svg width="${sizePx}" height="${sizePx}" viewBox="0 0 24 24" style="color: ${id.color}">${id.svg}</svg>`;
}

const APP_TEXT = {
  it: {
    hostTitle: "THE ALGORHYTHM // Console Centrale",
    scanToJoin: "Inquadra con lo smartphone per entrare",
    roomCodeLabel: "CODICE STANZA",
    playersConnected: "Impresari in attesa",
    minPlayersHint: "Servono almeno 3 giocatori (max 8) per avviare la partita.",
    startGame: "Inizia Partita",
    draftPhase: "Fase: Draft Iniziale in corso...",
    offline: "Disconnesso",
    joinTitle: "Entra in Partita",
    roomCodePlaceholder: "Codice Stanza (es. NMAD)",
    namePlaceholder: "Il tuo nome da Impresario",
    pickColor: "Scegli la tua etichetta (Colore & Simbolo)",
    joinBtn: "Entra nel Club",
    waitingHost: "Sei dentro! In attesa che l'Host avvii la partita...",
    draftPlayerHint: "Partita avviata! (Tappa 2: qui apparirà il Draft degli artisti)."
  },
  fr: {
    hostTitle: "THE ALGORHYTHM // Console Centrale",
    scanToJoin: "Scannez avec votre smartphone pour rejoindre",
    roomCodeLabel: "CODE SALLE",
    playersConnected: "Imprésarios en attente",
    minPlayersHint: "Minimum 3 joueurs (max 8) pour démarrer la partie.",
    startGame: "Démarrer la Partie",
    draftPhase: "Phase : Draft Initial en cours...",
    offline: "Déconnecté",
    joinTitle: "Rejoindre la Partie",
    roomCodePlaceholder: "Code Salle (ex. NMAD)",
    namePlaceholder: "Votre nom d'Imprésario",
    pickColor: "Choisissez votre label (Couleur & Symbole)",
    joinBtn: "Entrer dans le Club",
    waitingHost: "Vous êtes connecté ! En attente du lancement par l'Hôte...",
    draftPlayerHint: "Partie lancée ! (Étape 2 : le Draft des artistes apparaîtra ici)."
  }
};
