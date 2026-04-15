import { useState, useEffect, useRef } from 'react';
import './App.css';

function App() {
  const [ws, setWs] = useState(null);
  const [gameState, setGameState] = useState('LOGIN'); // LOGIN, WAITING_NICK, LOBBY, PREGUNTA, RESULTADO, SCOREBOARD, FIN, ADMIN_FORM, ADMIN_DASHBOARD
  const [nick, setNick] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [esperaMsg, setEsperaMsg] = useState('');
  const [jugadoresConectados, setJugadoresConectados] = useState(0);
  const [connectionUrl, setConnectionUrl] = useState('');
  const [wsConnected, setWsConnected] = useState(false);
  
  // Datos de la pregunta actual
  const [preguntaActiva, setPreguntaActiva] = useState(null);
  const [respuestaElegida, setRespuestaElegida] = useState(null);
  const [tiempoDisponible, setTiempoDisponible] = useState(15);
  const [tiempoRestante, setTiempoRestante] = useState(0);
  
  // Resultados y Ranking
  const [resultadoActual, setResultadoActual] = useState(null);
  const [ranking, setRanking] = useState([]);
  const [ganadorFinal, setGanadorFinal] = useState(null);

  // Datos del Profesor (Admin)
  const [adminPreguntas, setAdminPreguntas] = useState([
    { texto: '¿Qué componente se considera el cerebro del ordenador?', a: 'Memoria RAM', b: 'Disco Duro', c: 'CPU (Procesador)', d: 'Tarjeta de Red', correcta: 'c' },
    { texto: '¿Cuál de estos es un Sistema Operativo?', a: 'Microsoft Word', b: 'Google Chrome', c: 'Windows 11', d: 'Photoshop', correcta: 'c' },
    { texto: '¿Qué significan las siglas WWW?', a: 'World Wide Web', b: 'World Web Window', c: 'Wide Wall Web', d: 'World Wide Wifi', correcta: 'a' },
    { texto: '¿Qué dispositivo se usa principalmente para introducir texto en un ordenador?', a: 'Ratón (Mouse)', b: 'Teclado', c: 'Monitor', d: 'Altavoz', correcta: 'b' },
    { texto: '¿Cuál es la unidad mínima de información en informática?', a: 'Byte', b: 'Mega', c: 'Bit', d: 'Pixel', correcta: 'c' }
  ]);

  // Refs para usar dentro de los callbacks del socket sin tener que re-crearlo
  const gameStateRef = useRef(gameState);
  useEffect(() => {
    gameStateRef.current = gameState;
  }, [gameState]);

  useEffect(() => {
    // Tomar la IP dinámicamente de la URL a la que se ha conectado el alumno
    const proxyIp = window.location.hostname;
    const frontendPort = window.location.port || '5173';
    setConnectionUrl(`http://${proxyIp}:${frontendPort}`);
    const socket = new WebSocket(`ws://${proxyIp}:5001`);
    
    socket.onopen = () => {
      console.log('Conectado al proxy WS');
      setWsConnected(true);
    };

    socket.onmessage = (event) => {
      const msg = event.data.trim();
      if (!msg) return;
      
      console.log("RX:", msg);
      const partes = msg.split('|');
      const cmd = partes[0];
      const currentGameState = gameStateRef.current;
      console.log("-> Comando:", cmd, "| EstadoActual:", currentGameState);

      switch (cmd) {
        case 'NICK_OK':
          console.log("-> Procesando NICK_OK");
          if (currentGameState === 'ADMIN_FORM') {
            console.log("-> Ignorando NICK_OK por ser ADMIN");
          } else {
            console.log("-> Cambiando estado a LOBBY");
            setGameState('LOBBY');
          }
          setErrorMsg('');
          break;
        case 'NICK_ERROR':
          console.log("-> Error de Nick:", partes[1]);
          setErrorMsg(partes[1] || 'Error de nick');
          // Volver a LOGIN si estábamos esperando validación
          if (currentGameState === 'WAITING_NICK') {
            setGameState('LOGIN');
          }
          break;
        // NICK_ERROR ya manejado arriba
        case 'ESPERA':
          if (partes.length > 1) {
              setEsperaMsg(partes[1]);
          }
          break;
        case 'JUGADORES':
          setJugadoresConectados(parseInt(partes[1]));
          break;
        case 'PREGUNTA':
          // eslint-disable-next-line no-case-declarations
          const p = {
            num: partes[1],
            texto: partes[2],
            opciones: [
              { id: 'a', texto: partes[3] },
              { id: 'b', texto: partes[4] },
              { id: 'c', texto: partes[5] },
              { id: 'd', texto: partes[6] }
            ],
            tiempo: parseInt(partes[7]) || 15
          };
          setPreguntaActiva(p);
          setRespuestaElegida(null);
          setTiempoDisponible(p.tiempo);
          setTiempoRestante(p.tiempo);
          if(currentGameState !== 'ADMIN_DASHBOARD') setGameState('PREGUNTA');
          break;
        case 'RESULTADO':
          setResultadoActual({ estado: partes[1], correcta: partes[2], puntos: partes[3] });
          if(currentGameState !== 'ADMIN_DASHBOARD') setGameState('RESULTADO');
          break;
        case 'RANKING':
          // eslint-disable-next-line no-case-declarations
          const parsedRanking = partes[1].split(',').map(r => {
             const [pos, nom, pts, racha] = r.split(':');
             return { pos, nom, pts, racha: parseInt(racha) || 0 };
          });
          setRanking(parsedRanking);
          if(currentGameState !== 'ADMIN_DASHBOARD') setGameState('SCOREBOARD');
          break;
        case 'FIN':
          setGanadorFinal({ nick: partes[1], puntos: partes[2] });
          setGameState('FIN');
          break;
        default:
          break;
      }
    };

    setWs(socket);
    return () => socket.close();
  }, []); // Dependencias vacías para no reconectar

  useEffect(() => {
    let timer;
    if (gameState === 'PREGUNTA' && tiempoRestante > 0) {
      timer = setInterval(() => setTiempoRestante(t => t - 1), 1000);
    }
    return () => clearInterval(timer);
  }, [gameState, tiempoRestante]);

  const enviarNick = (e) => {
    e.preventDefault();
    if (nick.trim() && ws) {
      setErrorMsg('');
      setGameState('WAITING_NICK');
      ws.send(`NICK|${nick.trim()}`);
    }
  };

  const enviarRespuesta = (idRespuesta) => {
    if (gameState === 'PREGUNTA' && !respuestaElegida) {
      setRespuestaElegida(idRespuesta);
      ws.send(`RESPUESTA|${idRespuesta}`);
    }
  };

  // ADMIN LOGIC
  const handleAdminPreguntaChange = (idx, field, value) => {
    const pNuevas = [...adminPreguntas];
    pNuevas[idx][field] = value;
    setAdminPreguntas(pNuevas);
  };

  const lanzarPartida = () => {
    // Validar que no esten vacias
    for(let p of adminPreguntas) {
        if(!p.texto || !p.a || !p.b || !p.c || !p.d) {
            alert("Rellena todas las preguntas y opciones.");
            return;
        }
    }
    // Conectar como Admin
    ws.send("NICK|ADMIN");
    
    // Preparar el chorizo de ADMIN_START
    let cmd = "ADMIN_START";
    adminPreguntas.forEach(p => {
       cmd += `|${p.texto}|${p.a}|${p.b}|${p.c}|${p.d}|${p.correcta}`;
    });
    
    setTimeout(() => {
        ws.send(cmd);
        setGameState('ADMIN_DASHBOARD');
    }, 500);
  };

  // VISTAS
  if (gameState === 'LOGIN') {
    return (
      <div className="container center-all">
        <div className="card glass">
          <h1 className="title">Trivia Master <span className="emoji">🎮</span></h1>
          
          {/* Indicador de conexión al servidor */}
          <div className={`connection-status ${wsConnected ? 'connected' : 'disconnected'}`}>
            <span className="status-dot"></span>
            {wsConnected ? 'Conectado al servidor' : 'Conectando al servidor...'}
          </div>

          <form onSubmit={enviarNick} className="login-form">
            <input 
              type="text" placeholder="Tu nombre de jugador" 
              value={nick} onChange={e => setNick(e.target.value)} 
              className="input-text" required
              disabled={!wsConnected}
            />
            <button type="submit" className="btn btn-primary" disabled={!wsConnected}>
              {wsConnected ? 'Entrar a la Sala' : 'Esperando conexión...'}
            </button>
          </form>
          {errorMsg && <div className="alert error bounce">{errorMsg}</div>}
          <div style={{marginTop: "2rem", textAlign: "center", borderTop: "1px solid rgba(255,255,255,0.1)", paddingTop: "1rem"}}>
              <button onClick={() => setGameState('ADMIN_FORM')} className="btn btn-secundary" style={{background: 'rgba(255,255,255,0.1)', color: 'white', padding: '0.5rem 1rem', fontSize: '0.9rem', borderRadius: '8px'}}>🎓 Entorno del Profesor</button>
          </div>
        </div>
      </div>
    );
  }

  if (gameState === 'WAITING_NICK') {
    return (
      <div className="container center-all">
        <div className="card glass text-center">
          <h2 className="title" style={{fontSize: '2rem'}}>Entrando a la sala...</h2>
          <div className="loader"></div>
          <p className="subtitle">Validando tu nombre <strong style={{color: '#a855f7'}}>{nick}</strong></p>
          <div className="waiting-dots">
            <span></span><span></span><span></span>
          </div>
          <button 
            onClick={() => { setGameState('LOGIN'); setErrorMsg(''); }} 
            className="btn" 
            style={{marginTop: '2rem', background: 'rgba(255,255,255,0.1)', color: '#94a3b8', fontSize: '0.9rem'}}
          >
            ← Volver
          </button>
        </div>
      </div>
    );
  }

  if (gameState === 'ADMIN_FORM') {
    return (
      <div className="container">
        <div className="card glass" style={{maxWidth: '800px', padding: '2rem'}}>
          <h2 className="title" style={{fontSize: '2rem'}}>Admin: Configurar Preguntas</h2>
          <div className="admin-preguntas-scroll">
            {adminPreguntas.map((p, i) => (
              <div key={i} className="admin-p-box" style={{background: 'rgba(0,0,0,0.3)', padding: '1rem', borderRadius: '12px', marginBottom: '1.5rem'}}>
                <h3>Pregunta {i+1}</h3>
                <input type="text" placeholder="Enunciado de la pregunta" className="input-text" style={{width: '100%', marginBottom: '1rem'}} value={p.texto} onChange={e => handleAdminPreguntaChange(i, 'texto', e.target.value)} />
                <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', marginBottom: '1rem'}}>
                   <input type="text" placeholder="Opción A" className="input-text" value={p.a} onChange={e => handleAdminPreguntaChange(i, 'a', e.target.value)} />
                   <input type="text" placeholder="Opción B" className="input-text" value={p.b} onChange={e => handleAdminPreguntaChange(i, 'b', e.target.value)} />
                   <input type="text" placeholder="Opción C" className="input-text" value={p.c} onChange={e => handleAdminPreguntaChange(i, 'c', e.target.value)} />
                   <input type="text" placeholder="Opción D" className="input-text" value={p.d} onChange={e => handleAdminPreguntaChange(i, 'd', e.target.value)} />
                </div>
                <div>
                   <label style={{marginRight: '1rem', color: '#cbd5e1', fontWeight: 'bold'}}>Respuesta Correcta:</label>
                   <select className="input-text" style={{padding: '0.5rem'}} value={p.correcta} onChange={e => handleAdminPreguntaChange(i, 'correcta', e.target.value)}>
                      <option value="a">Opción A</option><option value="b">Opción B</option>
                      <option value="c">Opción C</option><option value="d">Opción D</option>
                   </select>
                </div>
              </div>
            ))}
          </div>
          <button onClick={lanzarPartida} className="btn btn-primary" style={{width: '100%', marginTop: '1rem', background: '#10b981'}}>🚀 Arrancar Servidor y Partida</button>
        </div>
      </div>
    );
  }

  if (gameState === 'ADMIN_DASHBOARD') {
      return (
        <div className="container center-all">
          <div className="card glass text-center" style={{maxWidth: '600px'}}>
            <h2 className="title" style={{fontSize: '2rem'}}>Panel de Control Activo</h2>
            <div className="loader" style={{borderColor: 'rgba(16,185,129,0.2)', borderTopColor: '#10b981'}}></div>
            <p className="subtitle">La partida se está ejecutando en los dispositivos de los alumnos.</p>
            
            {/* Caja con la URL de conexión para los alumnos */}
            <div className="connection-info-box">
              <p className="connection-info-label">📡 Los alumnos deben abrir esta dirección en su navegador:</p>
              <div className="connection-url-display">
                <code>{connectionUrl}</code>
                <button 
                  className="btn-copy" 
                  onClick={() => navigator.clipboard.writeText(connectionUrl)}
                  title="Copiar URL"
                >
                  📋
                </button>
              </div>
              <p className="connection-info-hint">Asegúrate de que todos los dispositivos estén en la misma red WiFi</p>
            </div>

            <div className="stats-box" style={{background: 'rgba(16,185,129,0.2)', color: '#6ee7b7', marginTop: '1rem', width: '100%'}}>
               🏆 Revisa la pantalla de un alumno para ver los resultados o el pódium al finalizar.
            </div>
            
            <div className="ranking-section fade-in" style={{marginTop: '2rem'}}>
                <h3 style={{marginBottom: '1rem', color: '#e2e8f0'}}>Ranking en Vivo</h3>
                <div className="ranking-list slim" style={{textAlign: 'left'}}>
                  {ranking.length === 0 ? <p style={{color: '#94a3b8', textAlign: 'center'}}>Esperando puntuaciones...</p> : null}
                  {ranking.map((r, i) => (
                    <div key={i} className={`ranking-item`}>
                       <div className="r-left">
                          <span className="pos">#{r.pos}</span>
                          <span className="nom">{r.nom} 
                            {r.racha >= 2 && <span className="racha-badge pulse">🔥 x{r.racha}</span>}
                          </span>
                       </div>
                       <div className="r-right"><span className="pts">{r.pts} pts</span></div>
                    </div>
                  ))}
                </div>
            </div>
          </div>
        </div>
      );
  }

  if (gameState === 'LOBBY') {
    return (
      <div className="container center-all">
        <div className="card glass text-center">
          <h2>Sala de Espera</h2>
          <div className="loader"></div>
          <p className="subtitle">{esperaMsg || "Esperando a que empiece la partida..."}</p>
          <div className="stats-box">
             <span className="emoji">👥</span> {jugadoresConectados} Usuarios conectados
          </div>
        </div>
      </div>
    );
  }

  if (gameState === 'PREGUNTA') {
    const progresoPorcentaje = (tiempoRestante / tiempoDisponible) * 100;
    
    return (
      <div className="container">
        <div className="card glass shadow-lg">
          <div className="header-flex">
             <h3>Pregunta {preguntaActiva?.num}/5</h3>
             <div className={`tiempo ${tiempoRestante <= 5 ? 'danger pulse' : ''}`}>
               ⏳ {tiempoRestante}s
             </div>
          </div>
          
          <div className="progress-bar-bg">
            <div className="progress-bar" style={{width: `${progresoPorcentaje}%`}}></div>
          </div>

          <h1 className="pregunta-texto">{preguntaActiva?.texto}</h1>
          
          <div className="grid-opciones">
            {preguntaActiva?.opciones.map((op) => (
              <button 
                key={op.id}
                onClick={() => enviarRespuesta(op.id)}
                disabled={respuestaElegida !== null}
                className={`btn btn-opcion ${respuestaElegida === op.id ? 'seleccionada p-click' : ''}`}
              >
                <span className="op-letter">{op.id.toUpperCase()}</span> {op.texto}
              </button>
            ))}
          </div>
          
          {respuestaElegida && (
            <div className="waiting-msg fade-in">
              Esperando a que termine el tiempo...
            </div>
          )}
        </div>
      </div>
    );
  }

  if (gameState === 'RESULTADO' || gameState === 'SCOREBOARD') {
    // Si ya estamos viendo el Scoreboard, mostramos también el resultado arriba
    return (
      <div className="container">
        <div className="card glass result-card fade-in-up">
           {resultadoActual?.estado === 'SI' && (
              <div className="resultado-header success bounce">
                 <h1>¡Correcto! 🎉</h1>
                 <p>Ganaste puntos.</p>
              </div>
           )}
           {resultadoActual?.estado === 'NO' && (
              <div className="resultado-header fail shake">
                 <h1>Incorrecto 😞</h1>
                 <p>La respuesta era: {resultadoActual?.correcta.toUpperCase()}</p>
              </div>
           )}
           {resultadoActual?.estado === 'TIEMPO' && (
              <div className="resultado-header timeout">
                 <h1>Tiempo Agotado ⌛</h1>
                 <p>La respuesta era: {resultadoActual?.correcta.toUpperCase()}</p>
              </div>
           )}

           <div className="score-badge">Tienes {resultadoActual?.puntos} puntos</div>

           {gameState === 'SCOREBOARD' && (
             <div className="ranking-section fade-in">
                <h2>Tabla de Posiciones</h2>
                <div className="ranking-list">
                  {ranking.filter(r => r.nom !== 'ADMIN').map((r, i) => (
                    <div key={i} className={`ranking-item ${r.nom === nick ? 'me' : ''}`}>
                       <div className="r-left">
                          <span className="pos">#{r.pos}</span>
                          <span className="nom">{r.nom} 
                            {r.nom === nick ? ' (Tú)' : ''}
                            {r.racha >= 2 && <span className="racha-badge pulse">🔥 x{r.racha}</span>}
                          </span>
                       </div>
                       <div className="r-right">
                          <span className="pts">{r.pts} pts</span>
                       </div>
                    </div>
                  ))}
                </div>
             </div>
           )}
           <p className="info-txt">Preparando siguiente ronda...</p>
        </div>
      </div>
    );
  }

  if (gameState === 'FIN') {
    return (
      <div className="container center-all">
        <div className="card glass trophy-card zoom-in">
          <h1>🏆 ¡Juego Terminado! 🏆</h1>
          <div className="trophy-icon bounce">🎖️</div>
          <h2>Ganador: <span>{ganadorFinal?.nick}</span></h2>
          <p>Con un total de <b>{ganadorFinal?.puntos}</b> puntos.</p>
          
          <div className="ranking-list slim mb-top fade-in">
            {ranking.filter(r => r.nom !== 'ADMIN').slice(0, 3).map((r, i) => (
              <div key={i} className={`ranking-item ${r.nom === nick ? 'me' : ''}`}>
                  <span className="pos">
                    {r.pos === "1" ? '🥇' : r.pos === "2" ? '🥈' : '🥉'}
                  </span>
                  <span className="nom">{r.nom}</span>
                  <span className="pts">{r.pts} pts</span>
              </div>
            ))}
          </div>

          <div className="final-msg space-t">{esperaMsg}</div>
        </div>
      </div>
    );
  }

  return null;
}

export default App;
