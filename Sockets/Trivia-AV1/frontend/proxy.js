import { WebSocketServer } from 'ws';
import net from 'net';

const WS_PORT = 5001;
const JAVA_HOST = '127.0.0.1';
const JAVA_PORT = 5000;

console.log(`[Proxy] Iniciando servidor intermediario de WebSockets en el puerto ${WS_PORT}...`);
const wss = new WebSocketServer({ host: '0.0.0.0', port: WS_PORT });

wss.on('connection', (ws) => {
    console.log('[Proxy] Nuevo cliente web conectado (React). Conectando al backend Java...');
    
    // Crear socket TCP hacia Java
    const tcpSocket = new net.Socket();
    
    tcpSocket.connect(JAVA_PORT, JAVA_HOST, () => {
        console.log('[Proxy] Conectado exitosamente al servidor Java.');
    });

    // Lo que llega de Java (TCP) lo mandamos a React (WS)
    tcpSocket.on('data', (data) => {
        const text = data.toString('utf-8');
        // El servidor Java puede enviar varios mensajes juntos en el mismo paquete TCP.
        // Separamos por salto de línea y enviamos un WebSocket individual por cada uno.
        const lineas = text.split('\n');
        for (let linea of lineas) {
            linea = linea.trim();
            if (linea.length > 0) {
                ws.send(linea);
            }
        }
    });

    tcpSocket.on('close', () => {
        console.log('[Proxy] Conexion con Java cerrada.');
        if (ws.readyState === ws.OPEN) ws.close();
    });

    tcpSocket.on('error', (err) => {
        console.error('[Proxy] Error de socket TCP:', err.message);
        if (ws.readyState === ws.OPEN) ws.close();
    });

    // Lo que llega de React (WS) lo mandamos a Java (TCP)
    ws.on('message', (message) => {
        // En Java estamos leyendo con in.readLine(), por lo que necesitamos un \n al final
        let text = message.toString();
        if (!text.endsWith('\n')) {
            text += '\n';
        }
        tcpSocket.write(text);
    });

    ws.on('close', () => {
        console.log('[Proxy] Cliente web desconectado.');
        tcpSocket.destroy();
    });
});

console.log(`[Proxy] Listo. El Frontend de React debe conectarse a ws://localhost:${WS_PORT}`);
