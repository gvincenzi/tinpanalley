let ws;
let roomCode = null;
let currentLang = 'it';
let qrInstance = null;

function connectHost() {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
  ws = new WebSocket(`${protocol}//${location.host}/ws`);

  ws.onopen = () => {
    ws.send(JSON.stringify({
      type: 'CREATE_ROOM',
      payload: { language: currentLang }
    }));
  };

  ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    if (msg.type === 'ROOM_CREATED' || msg.type === 'ROOM_STATE') {
      updateRoomUI(msg.payload);
    } else if (msg.type === 'ERROR') {
      showError(msg.payload.message);
    }
  };
}

function updateRoomUI(state) {
  roomCode = state.code;
  currentLang = state.language || 'it';
  applyLanguageUI();

  document.getElementById('roomCode').textContent = roomCode;

  // Costruisce URL per gli smartphone e genera il QR Code una sola volta
  const playerUrl = `${location.origin}/player/?room=${roomCode}`;
  document.getElementById('joinUrl').textContent = playerUrl;

  if (!qrInstance) {
    qrInstance = new QRCode(document.getElementById('qrcode'), {
      text: playerUrl,
      width: 210,
      height: 210,
      colorDark: "#120b24",
      colorLight: "#ffffff"
    });
  }

  // Aggiorna lista giocatori
  const list = document.getElementById('playersList');
  list.innerHTML = '';
  const players = state.players || [];
  document.getElementById('playerCount').textContent = players.length;

  players.forEach(p => {
    const id = getIdentity(p.colorId);
    const card = document.createElement('div');
    card.className = `player-card ${p.connected ? '' : 'offline'}`;
    card.style.color = id.color;
    card.innerHTML = `
      ${renderIconSvg(p.colorId, 34)}
      <div class="player-info">
        <span class="player-name">${escapeHtml(p.name)}</span>
        <span class="player-meta">${p.connected ? `${p.beats} BT • ${p.reputation} PR` : APP_TEXT[currentLang].offline}</span>
      </div>
    `;
    list.appendChild(card);
  });

  document.getElementById('startBtn').disabled = players.length < 3;

  if (state.phase !== 'LOBBY') {
    document.getElementById('lobbyView').style.display = 'none';
    document.getElementById('gameView').style.display = 'block';
  }
}

function changeLanguage(lang) {
  currentLang = lang;
  applyLanguageUI();
  if (ws && ws.readyState === WebSocket.OPEN && roomCode) {
    ws.send(JSON.stringify({
      type: 'SET_LANGUAGE',
      payload: { roomCode, language: lang }
    }));
  }
}

function applyLanguageUI() {
  const t = APP_TEXT[currentLang];
  document.getElementById('btn-it').classList.toggle('active', currentLang === 'it');
  document.getElementById('btn-fr').classList.toggle('active', currentLang === 'fr');
  document.getElementById('lblHostTitle').textContent = t.hostTitle;
  document.getElementById('lblScan').textContent = t.scanToJoin;
  document.getElementById('lblCode').textContent = t.roomCodeLabel;
  document.getElementById('lblPlayers').textContent = t.playersConnected;
  document.getElementById('lblHint').textContent = t.minPlayersHint;
  document.getElementById('startBtn').textContent = t.startGame;
  document.getElementById('lblDraft').textContent = t.draftPhase;
}

function startGame() {
  if (!roomCode) return;
  ws.send(JSON.stringify({
    type: 'START_GAME',
    payload: { roomCode }
  }));
}

function showError(text) {
  const el = document.getElementById('errorBanner');
  el.textContent = text;
  el.style.display = 'block';
  setTimeout(() => el.style.display = 'none', 4000);
}

function escapeHtml(str) {
  return str.replace(/[&<>"']/g, m => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[m]));
}

connectHost();
