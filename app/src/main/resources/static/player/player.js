let ws;
let selectedColorId = 1;
let myPlayerId = sessionStorage.getItem('tpa_player_id');
let currentRoomCode = new URLSearchParams(location.search).get('room') || sessionStorage.getItem('tpa_room_code') || '';
let currentLang = 'it';

function init() {
  const roomInput = document.getElementById('roomInput');
  if (currentRoomCode) {
    roomInput.value = currentRoomCode.toUpperCase();
  }
  renderColorPicker([]);
  connectWebSocket();
}

function connectWebSocket() {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
  ws = new WebSocket(`${protocol}//${location.host}/ws`);

  ws.onopen = () => {
    // Riconnessione automatica se il giocatore aveva già effettuato l'accesso
    if (myPlayerId && currentRoomCode) {
      ws.send(JSON.stringify({
        type: 'JOIN_ROOM',
        payload: { roomCode: currentRoomCode, playerId: myPlayerId }
      }));
    }
  };

  ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    if (msg.type === 'JOINED_ROOM') {
      const { player, room } = msg.payload;
      myPlayerId = player.id;
      currentRoomCode = room.code;
      sessionStorage.setItem('tpa_player_id', myPlayerId);
      sessionStorage.setItem('tpa_room_code', currentRoomCode);
      showWaitingRoom(player, room);
    } else if (msg.type === 'ROOM_STATE') {
      syncRoomState(msg.payload);
    } else if (msg.type === 'ERROR') {
      showError(msg.payload.message);
    }
  };
}

function renderColorPicker(takenColors) {
  const grid = document.getElementById('colorGrid');
  grid.innerHTML = '';

  PLAYER_IDENTITIES.forEach(p => {
    const isTaken = takenColors.includes(p.id);
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = `color-option ${selectedColorId === p.id && !isTaken ? 'selected' : ''}`;
    btn.style.color = p.color;
    btn.disabled = isTaken;
    btn.innerHTML = `
      ${renderIconSvg(p.id, 26)}
      <span>${currentLang === 'fr' ? p.nameFr : p.nameIt}</span>
    `;
    btn.onclick = () => {
      selectedColorId = p.id;
      renderColorPicker(takenColors);
    };
    grid.appendChild(btn);
  });
}

function joinRoom() {
  const code = document.getElementById('roomInput').value.trim().toUpperCase();
  const name = document.getElementById('nameInput').value.trim();

  ws.send(JSON.stringify({
    type: 'JOIN_ROOM',
    payload: {
      roomCode: code,
      playerId: null,
      name: name,
      colorId: selectedColorId
    }
  }));
}

function showWaitingRoom(player, room) {
  document.getElementById('joinView').style.display = 'none';
  document.getElementById('waitView').style.display = 'flex';
  updatePlayerCard(player, room);
}

function syncRoomState(room) {
  if (room.language && room.language !== currentLang) {
    setPlayerLang(room.language);
  }
  if (!myPlayerId) {
    renderColorPicker(room.takenColors || []);
    return;
  }
  const me = (room.players || []).find(p => p.id === myPlayerId);
  if (me) {
    updatePlayerCard(me, room);
  }
}

function updatePlayerCard(player, room) {
  const id = getIdentity(player.colorId);
  const badge = document.getElementById('myBadge');
  badge.style.color = id.color;
  badge.innerHTML = `
    ${renderIconSvg(player.colorId, 34)}
    <div>
      <div style="font-family:var(--font-display); font-weight:700; font-size:1.15rem; color:var(--text-primary);">${player.name}</div>
      <div style="font-size:0.8rem; color:var(--text-secondary);">Room: ${room.code}</div>
    </div>
  `;
  document.getElementById('myBeats').textContent = player.beats;
  document.getElementById('myPr').textContent = player.reputation;

  const t = APP_TEXT[currentLang];
  document.getElementById('lblWaitMsg').textContent = room.phase === 'LOBBY' ? t.waitingHost : t.draftPlayerHint;
}

function setPlayerLang(lang) {
  currentLang = lang;
  const t = APP_TEXT[lang];
  document.getElementById('btn-it').classList.toggle('active', lang === 'it');
  document.getElementById('btn-fr').classList.toggle('active', lang === 'fr');
  document.getElementById('lblJoinTitle').textContent = t.joinTitle;
  document.getElementById('roomInput').placeholder = t.roomCodePlaceholder;
  document.getElementById('nameInput').placeholder = t.namePlaceholder;
  document.getElementById('lblPickColor').textContent = t.pickColor;
  document.getElementById('joinBtn').textContent = t.joinBtn;
  renderColorPicker([]);
}

function showError(msg) {
  const el = document.getElementById('errorMsg');
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => el.style.display = 'none', 4000);
}

init();
