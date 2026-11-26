let ws, roomCode = null, currentLang = 'it', qrInstance = null, timerInterval = null;
let lastAudioUrl = null;
let availablePlaylists = [];

async function loadPlaylists() {
  try {
    const res = await fetch('/api/playlists');
    availablePlaylists = await res.json();
    renderPlaylistSelect();
  } catch (e) {
    console.error('Errore caricamento playlist:', e);
  }
}

function renderPlaylistSelect() {
  const select = document.getElementById('genreSelect');
  if (!select || availablePlaylists.length === 0) return;
  const previous = select.value;
  select.innerHTML = availablePlaylists.map(p => {
    const label = currentLang === 'fr' ? p.labelFr : p.labelIt;
    return `<option value="${p.key}">${escapeHtml(label)}</option>`;
  }).join('');
  if (previous) select.value = previous;
}

function connectHost() {
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
  ws = new WebSocket(`${protocol}//${location.host}/ws`);
  ws.onopen = () => ws.send(JSON.stringify({ type: 'CREATE_ROOM', payload: { language: currentLang } }));
  ws.onmessage = (e) => {
    const msg = JSON.parse(e.data);
    if (msg.type === 'ROOM_CREATED' || msg.type === 'ROOM_STATE') updateRoomUI(msg.payload);
    else if (msg.type === 'FINAL_STANDINGS') renderFinalStandings(msg.payload);
    else if (msg.type === 'ERROR') showError(msg.payload.message);
  };
}

function sendHost(type, payload = {}) {
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({ type, payload: { roomCode, ...payload } }));
  }
}

function updateRoomUI(state) {
  roomCode = state.code;
  currentLang = state.language || 'it';
  applyLanguageUI();

  document.getElementById('roomCode').textContent = roomCode;
  const playerUrl = `${location.origin}/player?room=${roomCode}`;
  document.getElementById('joinUrl').textContent = playerUrl;

  if (!qrInstance) {
    qrInstance = new QRCode(document.getElementById('qrcode'), {
      text: playerUrl, width: 200, height: 200, colorDark: "#120b24", colorLight: "#ffffff"
    });
  }

  const players = state.players || [];
  document.getElementById('playerCount').textContent = players.length;
  document.getElementById('startBtn').disabled = players.length < 3;

  renderPlayerCards('playersList', players);
  renderPlayerCards('liveStandings', players);

  if (state.phase === 'LOBBY') {
    document.getElementById('lobbyView').style.display = 'grid';
    document.getElementById('gameView').style.display = 'none';
    return;
  }

  document.getElementById('lobbyView').style.display = 'none';
  document.getElementById('gameView').style.display = 'flex';
  document.getElementById('roundBadge').textContent = `ROUND ${state.currentRound} / ${state.totalRounds}`;
  renderActivePhase(state);
}

function renderPlayerCards(containerId, players) {
  const list = document.getElementById(containerId);
  list.innerHTML = '';
  players.forEach(p => {
    const id = getIdentity(p.colorId);
    const card = document.createElement('div');
    card.className = `player-card ${p.connected ? '' : 'offline'}`;
    card.style.color = id.color;
    card.innerHTML = `
      ${renderIconSvg(p.colorId, 34)}
      <div class="player-info">
        <span class="player-name">${escapeHtml(p.name)} ${p.freelance ? '⚡' : ''}</span>
        <span class="player-meta">${p.beats} BT • <strong>${p.reputation} PR</strong> • Club: ${p.clubsOwned}</span>
      </div>`;
    list.appendChild(card);
  });
}

function renderActivePhase(state) {
  const title = document.getElementById('phaseTitle');
  const content = document.getElementById('phaseContent');
  const audioEl = document.getElementById('liveAudio');
  clearInterval(timerInterval);

  if (state.phase === 'DRAFT') {
    title.textContent = 'DRAFT INIZIALE';
    content.innerHTML = `<p style="color:var(--text-secondary)">Ogni giocatore sta scegliendo 2 artisti sul proprio smartphone...</p>`;
  } else if (state.phase === 'AUCTION') {
    title.textContent = 'FASE 1 • MERCATO & SCOUTING (ASTA SEGRETA)';
    const itemsHtml = (state.auctionItems || []).map(a =>
      `<div style="background:var(--bg-elevated); padding:14px 20px; border-radius:10px; border:1px solid var(--brand-gold); font-family:var(--font-display); font-size:1.2rem;">${escapeHtml(a.artist)}</div>`
    ).join('');
    content.innerHTML = `
      <div id="countdown" style="font-size:2.4rem; color:var(--brand-gold); font-family:var(--font-display); margin-bottom:16px;"></div>
      <div style="display:flex; justify-content:center; gap:14px; flex-wrap:wrap; margin-bottom:20px;">${itemsHtml}</div>
      <button class="btn btn-primary" onclick="sendHost('RESOLVE_AUCTION')">Chiudi Asta e Assegna</button>
    `;
    startCountdown(state.phaseEndsAt);
  } else if (state.phase === 'BOARD_ACTIONS') {
    audioEl.pause();
    lastAudioUrl = null;
    title.textContent = 'FASE 2 & 3 • TABELLONE & LIVE SHOW';
    content.innerHTML = `
      <p style="color:var(--text-secondary); margin-bottom:20px;">Giocate i vostri 3 PA sulla mappa fisica e lanciate i Live Show dagli smartphone.</p>
      <button class="btn btn-primary" onclick="sendHost('NEXT_ROUND')">Fine Round → Prossimo Step</button>
    `;
  } else if (state.phase === 'LIVE_SHOW') {
    const org = (state.players || []).find(p => p.id === state.currentOrganizerId);
    title.textContent = `LIVE SHOW • Organizza ${org ? escapeHtml(org.name) : ''}`;

    if (state.lastLiveSummary) {
      audioEl.pause();
      const s = state.lastLiveSummary;
      const rows = (s.results || []).map(r =>
        `<div style="padding:8px; border-bottom:1px solid var(--border-subtle);">${escapeHtml(r.name)}: ${r.okArtist ? '🎤' : ''} ${r.okTitle ? '🎵' : ''} +${r.prGained} PR ${r.btWon ? `(+${r.btWon} BT)` : ''}</div>`
      ).join('');
      content.innerHTML = `
        <h3 style="color:var(--brand-gold); font-size:1.5rem;">${escapeHtml(s.artist)} — "${escapeHtml(s.title)}"</h3>
        <p style="color:var(--brand-cyan); margin:10px 0;">Esito: <strong>${s.tier}</strong> (+${s.orgBt} BT, +${s.orgPr} PR all'organizzatore)</p>
        <div style="max-width:460px; margin:16px auto; text-align:left;">${rows}</div>
        <button class="btn btn-primary" onclick="sendHost('BACK_TO_BOARD')">Torna al Tabellone</button>
      `;
    } else if (state.liveAudioPlaying) {
      if (state.liveAudioUrl && state.liveAudioUrl !== lastAudioUrl) {
        lastAudioUrl = state.liveAudioUrl;
        audioEl.src = state.liveAudioUrl;
        audioEl.play().catch(() => {});
      }
      content.innerHTML = `
        <div id="countdown" style="font-size:3rem; color:var(--brand-cyan); font-family:var(--font-display);"></div>
        <p style="color:var(--text-secondary); margin:16px 0;">🔊 Ascolto in corso! Inviate le risposte dai telefoni...</p>
        <button class="btn btn-primary" onclick="sendHost('RESOLVE_LIVE')">Rivela Brano &amp; Punteggi</button>
      `;
      startCountdown(state.phaseEndsAt);
    } else {
      content.innerHTML = `
        <p style="color:var(--text-secondary); margin-bottom:20px;">Gli avversari stanno decidendo se piazzare la Scommessa (200 BT)...</p>
        <button class="btn btn-primary" onclick="sendHost('START_LIVE_AUDIO')">▶ Avvia Musica (30 sec)</button>
      `;
    }
  } else if (state.phase === 'AWARDS') {
    title.textContent = 'GRAN FINALE • NOMAD MUSIC AWARDS';
    if (state.awardsSubPhase === 'NOMINATE') {
      content.innerHTML = `<p style="color:var(--text-secondary)">Candidature in corso (${state.awardNominationsCount} / ${state.players.length})...</p>`;
    } else if (state.awardsSubPhase === 'PLAYBACK') {
      const num = state.awardPlaybackIndex + 1;
      if (state.lastAwardSummary) {
        audioEl.pause();
        const s = state.lastAwardSummary;
        const rows = (s.results || []).map(r =>
          `<div style="padding:8px; border-bottom:1px solid var(--border-subtle);">${escapeHtml(r.name)}: ${r.okArtist ? '🎤' : ''} ${r.okTitle ? '🎵' : ''} +${r.prGained} PR</div>`
        ).join('');
        const isLast = num >= state.awardPlaybackTotal;
        content.innerHTML = `
          <h3 style="color:var(--brand-gold); font-size:1.6rem;">${escapeHtml(s.artist)} — "${escapeHtml(s.title)}"</h3>
          <p style="color:var(--brand-cyan); margin:8px 0;">Brano presentato da: <strong>${escapeHtml(s.ownerName)}</strong></p>
          <div style="max-width:460px; margin:16px auto; text-align:left;">${rows}</div>
          <button class="btn btn-primary" onclick="sendHost('NEXT_AWARD_TRACK')">
            ${isLast ? 'Vai al Voto della Critica →' : 'Prossimo Brano →'}
          </button>
        `;
      } else if (state.awardAudioPlaying) {
        if (state.awardAudioUrl && state.awardAudioUrl !== lastAudioUrl) {
          lastAudioUrl = state.awardAudioUrl;
          audioEl.src = state.awardAudioUrl;
          audioEl.play().catch(() => {});
        }
        content.innerHTML = `
          <h3 style="color:var(--brand-gold)">Brano in gara #${num} di ${state.awardPlaybackTotal}</h3>
          <div id="countdown" style="font-size:3rem; color:var(--brand-cyan); font-family:var(--font-display);"></div>
          <button class="btn btn-primary" onclick="sendHost('RESOLVE_AWARD_TRACK')">Rivela Brano &amp; Punteggi</button>
        `;
        startCountdown(state.phaseEndsAt);
      } else {
        audioEl.pause();
        lastAudioUrl = null;
        content.innerHTML = `
          <h3 style="color:var(--brand-gold)">Brano in gara #${num} di ${state.awardPlaybackTotal}</h3>
          <p style="color:var(--text-secondary); margin:14px 0;">Punteggi raddoppiati (+4 Artista / +6 Titolo)!</p>
          <button class="btn btn-primary" onclick="sendHost('START_AWARD_AUDIO')">▶ Avvia Brano del Festival</button>
        `;
      }
    } else if (state.awardsSubPhase === 'VOTE') {
      audioEl.pause();
      content.innerHTML = `
        <h3 style="color:var(--brand-magenta)">VOTO DELLA CRITICA (+10 PR)</h3>
        <p style="color:var(--text-secondary); margin:14px 0;">Voti ricevuti: ${state.awardVotesCount} / ${state.players.length}</p>
        <button class="btn btn-primary" onclick="sendHost('FINISH_GAME')">🏆 Chiudi Voti e Proclama Vincitore</button>
      `;
    }
  } else if (state.phase === 'FINISHED') {
    audioEl.pause();
    renderFinalStandings({
      standings: state.finalStandings || [],
      awardWinners: state.awardWinners || []
    });
  }
}

function renderFinalStandings(payload) {
  const standings = payload.standings || [];
  const awardWinners = payload.awardWinners || [];

  document.getElementById('phaseTitle').textContent = '🏆 CLASSIFICA FINALE — KING OF TIN PAN ALLEY';

  let awardHtml = '';
  if (awardWinners.length > 0) {
    const songs = awardWinners.map(w =>
      `<div><strong>${escapeHtml(w.artist)} — "${escapeHtml(w.title)}"</strong><br><small style="color:var(--text-secondary)">Presentata da ${escapeHtml(w.ownerName)} (${w.votes} voti • +${w.prize} PR)</small></div>`
    ).join('<hr style="border-color:var(--border-subtle); margin:8px 0;">');
    awardHtml = `
      <div style="max-width:520px; margin:0 auto 24px; padding:16px; border-radius:12px; background:rgba(255,42,133,0.12); border:1.5px solid var(--brand-magenta);">
        <div style="font-family:var(--font-display); color:var(--brand-magenta); font-size:0.85rem; letter-spacing:0.1em; margin-bottom:6px;">🌟 NOMAD MUSIC AWARD — CANZONE VINCITRICE</div>
        ${songs}
      </div>
    `;
  }

  const html = standings.map((s, idx) =>
    `<div style="padding:12px; font-size:1.2rem; border-bottom:1px solid var(--border-subtle);">
      <strong>#${idx + 1} ${escapeHtml(s.name)}</strong> — <span style="color:var(--brand-gold)">${s.totalPr} PR</span>
      <small style="color:var(--text-muted)">(${s.beats} BT)</small>
    </div>`
  ).join('');
  document.getElementById('phaseContent').innerHTML = `${awardHtml}<div style="max-width:520px; margin:0 auto;">${html}</div>`;
}

function startCountdown(endsAt) {
  const update = () => {
    const el = document.getElementById('countdown');
    if (!el) return;
    const sec = Math.max(0, Math.ceil((endsAt - Date.now()) / 1000));
    el.textContent = `${sec}s`;
  };
  update();
  timerInterval = setInterval(update, 500);
}

function changeLanguage(lang) {
  currentLang = lang;
  applyLanguageUI();
  sendHost('SET_LANGUAGE', { language: lang });
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
  
  renderPlaylistSelect();
}

function startGame() {
  const genre = document.getElementById('genreSelect').value;
  sendHost('START_GAME', { genre });
}

function showError(text) {
  const el = document.getElementById('errorBanner');
  el.textContent = text;
  el.style.display = 'block';
  setTimeout(() => el.style.display = 'none', 4000);
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, m => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[m]));
}

connectHost();
loadPlaylists();
