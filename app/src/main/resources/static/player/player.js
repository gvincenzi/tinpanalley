let ws, selectedColorId = 1, currentLang = 'it';
let myPlayerId = sessionStorage.getItem('tpa_player_id');
let currentRoomCode = new URLSearchParams(location.search).get('room') || sessionStorage.getItem('tpa_room_code') || '';
let draftOptions = [], selectedDraft = [], auctionBids = {}, lastPhase = null;
let hasNominatedAward = false, hasVotedAward = false, isAutoReconnecting = false;

function init() {
  if (currentRoomCode) document.getElementById('roomInput').value = currentRoomCode.toUpperCase();
  renderColorPicker([]);
  connectWebSocket();
}

function connectWebSocket() {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
  ws = new WebSocket(`${protocol}//${location.host}/ws`);
  ws.onopen = () => {
    if (myPlayerId && currentRoomCode) {
      isAutoReconnecting = true;
      ws.send(JSON.stringify({ type: 'JOIN_ROOM', payload: { roomCode: currentRoomCode, playerId: myPlayerId } }));
    }
  };
  ws.onmessage = (e) => {
    const msg = JSON.parse(e.data);
    if (msg.type === 'JOINED_ROOM') {
      isAutoReconnecting = false;
      myPlayerId = msg.payload.player.id;
      currentRoomCode = msg.payload.room.code;
      sessionStorage.setItem('tpa_player_id', myPlayerId);
      sessionStorage.setItem('tpa_room_code', currentRoomCode);
      document.getElementById('joinView').style.display = 'none';
      document.getElementById('gameControllerView').style.display = 'flex';
      syncRoomState(msg.payload.room);
    } else if (msg.type === 'DRAFT_OPTIONS') {
      draftOptions = msg.payload.options || [];
      selectedDraft = [];
      renderDraft();
    } else if (msg.type === 'BIDS_ACCEPTED') {
      document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-cyan); text-align:center;">Offerte inviate! Attendi la chiusura dell'asta.</p>`;
    } else if (msg.type === 'ANSWER_ACCEPTED') {
      document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-cyan); text-align:center;">Risposta registrata! Attendi i risultati.</p>`;
    } else if (msg.type === 'BET_ACCEPTED') {
      document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-gold); text-align:center;">${msg.payload.bet ? 'Scommessa da 200 BT piazzata!' : 'Ascolto gratuito confermato.'} Attendi la musica...</p>`;
    } else if (msg.type === 'AWARD_NOMINATED') {
      hasNominatedAward = true;
      document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-cyan); text-align:center;">✅ Brano candidato! Attendi gli altri manager...</p>`;
    } else if (msg.type === 'AWARD_VOTED') {
      hasVotedAward = true;
      document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-gold); text-align:center;">✅ Voto segreto registrato! Attendi la proclamazione...</p>`;
    } else if (msg.type === 'ROOM_STATE') {
      syncRoomState(msg.payload);
    } else if (msg.type === 'ERROR') {
      if (isAutoReconnecting) {
        isAutoReconnecting = false;
        myPlayerId = null;
        sessionStorage.removeItem('tpa_player_id');
      } else {
        showError(msg.payload.message);
      }
    }
  };
}

function sendPlayer(type, payload = {}) {
  ws.send(JSON.stringify({ type, payload: { roomCode: currentRoomCode, ...payload } }));
}

function joinRoom() {
  isAutoReconnecting = false;
  sendPlayer('JOIN_ROOM', {
    roomCode: document.getElementById('roomInput').value.trim().toUpperCase(),
    playerId: null,
    name: document.getElementById('nameInput').value.trim(),
    colorId: selectedColorId
  });
}

function syncRoomState(room) {
  if (room.language && room.language !== currentLang) setPlayerLang(room.language);
  if (!myPlayerId) { renderColorPicker(room.takenColors || []); return; }

  const me = (room.players || []).find(p => p.id === myPlayerId);
  if (!me) return;

  const id = getIdentity(me.colorId);
  const badge = document.getElementById('myBadge');
  badge.style.color = id.color;
  badge.innerHTML = `${renderIconSvg(me.colorId, 32)}<div><strong>${me.name}</strong> ${me.freelance ? '⚡ Freelance' : ''}</div>`;
  document.getElementById('myBeats').textContent = me.beats;
  document.getElementById('myPr').textContent = me.reputation;

  const phaseChanged = room.phase !== lastPhase;
  lastPhase = room.phase;

  if (room.phase === 'LOBBY') {
    document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--text-secondary); text-align:center;">Sei dentro! In attesa che l'Host avvii la partita...</p>`;
  } else if (room.phase === 'DRAFT') {
    if (me.draftCompleted) document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-cyan); text-align:center;">Roster pronto! Attendi gli altri...</p>`;
  } else if (room.phase === 'AUCTION' && phaseChanged) {
    auctionBids = {};
    renderAuction(room, me);
  } else if (room.phase === 'BOARD_ACTIONS') {
    renderBoardActions(room, me);
  } else if (room.phase === 'LIVE_SHOW') {
    renderLiveStage(room, me);
  } else if (room.phase === 'AWARDS') {
    if (phaseChanged) { hasNominatedAward = false; hasVotedAward = false; }
    renderAwardsStage(room, me);
  } else if (room.phase === 'FINISHED') {
    document.getElementById('dynamicStage').innerHTML = `<p style="color:var(--brand-gold); text-align:center; font-size:1.2rem;">🏆 Partita conclusa! Guarda la classifica sullo schermo centrale.</p>`;
  }
}

function renderDraft() {
  const stage = document.getElementById('dynamicStage');
  stage.innerHTML = `<h3>Scegli 2 Artisti</h3>` + draftOptions.map(a => `
    <button class="btn" style="background:${selectedDraft.includes(a.id) ? 'var(--brand-cyan)' : 'var(--bg-elevated)'}; color:${selectedDraft.includes(a.id) ? '#000' : '#fff'};"
      onclick="toggleDraft('${a.id}')">${a.artist}</button>
  `).join('') + `<button class="btn btn-primary" ${selectedDraft.length !== 2 ? 'disabled' : ''} onclick="submitDraft()">Conferma Roster</button>`;
}

function toggleDraft(id) {
  if (selectedDraft.includes(id)) selectedDraft = selectedDraft.filter(x => x !== id);
  else if (selectedDraft.length < 2) selectedDraft.push(id);
  renderDraft();
}

function submitDraft() { sendPlayer('SUBMIT_DRAFT', { trackIds: selectedDraft }); }

function renderAuction(room, me) {
  const stage = document.getElementById('dynamicStage');
  const total = Object.values(auctionBids).reduce((a, b) => a + b, 0);
  stage.innerHTML = `<h3>Asta Segreta (Tot: ${total} / ${me.beats} BT)</h3>` + (room.auctionItems || []).map(a => `
    <div style="display:flex; justify-content:space-between; align-items:center; background:var(--bg-elevated); padding:10px; border-radius:8px;">
      <span>${a.artist}</span>
      <div style="display:flex; gap:8px; align-items:center;">
        <button class="btn" style="padding:6px 12px;" onclick="changeBid('${a.id}', -100, ${me.beats})">-</button>
        <strong>${auctionBids[a.id] || 0}</strong>
        <button class="btn" style="padding:6px 12px;" onclick="changeBid('${a.id}', 100, ${me.beats})">+</button>
      </div>
    </div>
  `).join('') + `<button class="btn btn-primary" onclick="sendPlayer('SUBMIT_BIDS', { bids: auctionBids })">Invia Offerte</button>`;
  window._currentRoom = room; window._currentMe = me;
}

function changeBid(id, delta, maxBeats) {
  const current = auctionBids[id] || 0;
  const next = Math.max(0, current + delta);
  const otherSum = Object.entries(auctionBids).filter(([k]) => k !== id).reduce((s, [, v]) => s + v, 0);
  if (otherSum + next <= maxBeats) {
    auctionBids[id] = next;
    renderAuction(window._currentRoom, window._currentMe);
  }
}

function renderBoardActions(room, me) {
  const others = (room.players || []).filter(p => p.id !== me.id);
  const rosterHtml = (me.roster || []).map(a =>
    `<button class="btn btn-primary" onclick="sendPlayer('START_LIVE', { trackId: '${a.id}' })">🎤 Live: ${a.artist}</button>`
  ).join('');
  const royaltyHtml = others.map(o =>
    `<button class="btn" style="background:var(--bg-elevated); color:var(--text-primary);" onclick="sendPlayer('BOARD_ACTION', { action:'PAY_ROYALTY', targetPlayerId:'${o.id}' })">Royalty 300 BT → ${o.name}</button>`
  ).join('');

  document.getElementById('dynamicStage').innerHTML = `
    <h3>Azioni Tabellone</h3>
    <button class="btn" style="background:var(--bg-elevated); color:var(--brand-gold);" onclick="sendPlayer('BOARD_ACTION', { action:'BUY_CLUB' })">Acquista Club (-800 BT)</button>
    <button class="btn" style="background:var(--bg-elevated); color:var(--brand-cyan);" onclick="sendPlayer('BOARD_ACTION', { action:'SPONSOR' })">Incassa Sponsor (+300 BT)</button>
    ${royaltyHtml}
    <hr style="border-color:var(--border-subtle)">
    <h3>Organizza Live Show</h3>
    ${rosterHtml}
  `;
}

function renderLiveStage(room, me) {
  const stage = document.getElementById('dynamicStage');
  if (room.lastLiveSummary) {
    stage.innerHTML = `<p style="color:var(--brand-gold); text-align:center;">Risultati pubblicati sullo schermo centrale!</p>`;
    return;
  }
  if (me.id === room.currentOrganizerId) {
    stage.innerHTML = `<p style="color:var(--brand-cyan); text-align:center;">Sei l'organizzatore di questo Live! Incrocia le dita per il punteggio Trendsetter.</p>`;
    return;
  }
  if (!room.liveAudioPlaying) {
    stage.innerHTML = `
      <h3>Scommessa sulla Hit?</h3>
      <button class="btn" style="background:var(--bg-elevated); color:#fff;" onclick="sendPlayer('PLACE_BET', { bet:false })">Ascolto Gratis (0 BT)</button>
      <button class="btn btn-primary" onclick="sendPlayer('PLACE_BET', { bet:true })">Scommetti 200 BT (PR ×2)</button>
    `;
  } else {
    stage.innerHTML = `
      <h3>Indovina il brano!</h3>
      <input id="ansArtist" class="input-field" placeholder="Artista">
      <input id="ansTitle" class="input-field" placeholder="Titolo canzone">
	  <input id="ansDecade" type="number" inputmode="numeric" min="1900" max="2030" class="input-field" placeholder="Anno o decennio (es. 1980)">
      <button class="btn btn-primary" onclick="sendPlayer('SUBMIT_LIVE_ANSWER', { artist: ansArtist.value, title: ansTitle.value, decade: ansDecade.value })">Invia Risposta</button>
    `;
  }
}

function renderAwardsStage(room, me) {
  const stage = document.getElementById('dynamicStage');
  if (room.awardsSubPhase === 'NOMINATE') {
    if (hasNominatedAward) {
      stage.innerHTML = `<p style="color:var(--brand-cyan); text-align:center;">✅ Brano candidato! Attendi gli altri manager...</p>`;
      return;
    }
    stage.innerHTML = `<h3>Candidatura Festival</h3><p style="font-size:0.85rem; color:var(--text-secondary)">Scegli 1 artista dal tuo roster:</p>` +
      (me.roster || []).map(a => `<button class="btn btn-primary" onclick="sendPlayer('NOMINATE_AWARD', { trackId: '${a.id}' })">🏆 ${a.artist}</button>`).join('');
  } else if (room.awardsSubPhase === 'PLAYBACK') {
    if (room.lastAwardSummary) {
      stage.innerHTML = `<p style="color:var(--brand-gold); text-align:center;">Brano rivelato sullo schermo centrale!</p>`;
    } else if (me.id === room.awardCurrentOwnerId) {
      stage.innerHTML = `<p style="color:var(--brand-gold); text-align:center;">È il tuo brano in gara! Non puoi rispondere.</p>`;
    } else if (!room.awardAudioPlaying) {
      stage.innerHTML = `<p style="color:var(--text-secondary); text-align:center;">In attesa che parta il prossimo brano del Festival...</p>`;
    } else {
      stage.innerHTML = `
        <h3>Gran Blind Test (Punti ×2!)</h3>
        <input id="awArtist" class="input-field" placeholder="Artista (+4 PR)">
        <input id="awTitle" class="input-field" placeholder="Titolo (+6 PR)">
        <button class="btn btn-primary" onclick="sendPlayer('SUBMIT_AWARD_ANSWER', { artist: awArtist.value, title: awTitle.value })">Invia Risposta</button>
      `;
    }
  } else if (room.awardsSubPhase === 'VOTE') {
    if (hasVotedAward) {
      stage.innerHTML = `<p style="color:var(--brand-gold); text-align:center;">✅ Voto segreto registrato! Attendi la proclamazione...</p>`;
      return;
    }
    const others = (room.awardCandidates || []).filter(c => c.ownerId !== me.id);
    stage.innerHTML = `<h3>Voto della Critica (+10 PR)</h3>` + others.map(c =>
      `<button class="btn" style="background:var(--bg-elevated); color:var(--text-primary);" onclick="sendPlayer('VOTE_AWARD', { ownerId: '${c.ownerId}' })">⭐ ${c.artist} — ${c.title}</button>`
    ).join('');
  }
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
    btn.innerHTML = `${renderIconSvg(p.id, 26)}<span>${currentLang === 'fr' ? p.nameFr : p.nameIt}</span>`;
    btn.onclick = () => { selectedColorId = p.id; renderColorPicker(takenColors); };
    grid.appendChild(btn);
  });
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
  const isJoined = document.getElementById('gameControllerView').style.display !== 'none';
  const el = document.getElementById(isJoined ? 'playerError' : 'errorMsg');
  if (!el) return;
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => el.style.display = 'none', 4000);
}

init();
