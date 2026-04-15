# Trivia Multijugador - Cliente/Servidor en Java

Aplicación de trivia en tiempo real para varios jugadores conectados en red local, desarrollada con sockets TCP en Java.

## Descripción

Un servidor gestiona una partida de trivia donde varios jugadores (hasta 10) compiten respondiendo preguntas tipo test. Cada partida consta de 5 preguntas con 4 opciones de respuesta cada una. Los jugadores reciben las preguntas simultáneamente y deben responder dentro del tiempo establecido.

## Requisitos

- Java JDK 8 o superior
- Conexión en red local (para jugar entre distintos equipos)

## Compilación

```bash
cd src
javac *.java
```

## Ejecución

### 1. Iniciar el servidor

En un terminal, ejecuta:

```bash
cd src
java Servidor
```

El servidor se iniciará en el **puerto 5000** y quedará esperando conexiones.

### 2. Conectar clientes

En otros terminales (pueden ser de distintos equipos en la misma red), ejecuta:

```bash
cd src
java Cliente
```

Cada cliente deberá:
1. Introducir la IP del servidor (o `localhost` si es en el mismo equipo)
2. Elegir un nombre de usuario (nick) único

### 3. Iniciar la partida

Cuando todos los jugadores estén conectados, el administrador del servidor escribe:

```
START
```

## Reglas del juego

- Máximo **10 jugadores** simultáneos
- **5 preguntas** tipo test con 4 opciones: a, b, c, d
- **30 segundos** por pregunta
- Respuesta correcta: **+1 punto**
- Respuesta incorrecta o fuera de tiempo: **0 puntos**
- Tras cada pregunta se muestra el ranking
- Al finalizar se muestra el ranking final y el ganador

## Protocolo de comunicación

La comunicación entre cliente y servidor se realiza mediante mensajes de texto con el carácter `|` como delimitador:

| Mensaje | Dirección | Descripción |
|---|---|---|
| `NICK\|nombre` | Cliente → Servidor | Envío del nick |
| `NICK_OK` | Servidor → Cliente | Nick aceptado |
| `NICK_ERROR\|motivo` | Servidor → Cliente | Nick rechazado |
| `RESPUESTA\|letra` | Cliente → Servidor | Respuesta (a/b/c/d) |
| `PREGUNTA\|num\|texto\|opA\|opB\|opC\|opD\|tiempo` | Servidor → Cliente | Pregunta |
| `RESULTADO\|acierto\|correcta\|puntos` | Servidor → Cliente | Resultado |
| `RANKING\|datos` | Servidor → Cliente | Ranking actualizado |
| `FIN\|ganador\|puntos` | Servidor → Cliente | Fin de partida |

## Estructura del proyecto

```
Trivia-AV1/
├── src/
│   ├── Servidor.java        # Servidor principal y lógica del juego
│   ├── ClienteHandler.java  # Hilo para gestionar cada jugador
│   ├── Cliente.java          # Aplicación cliente
│   └── Pregunta.java         # Modelo de datos de pregunta
└── README.md                 # Este archivo
```

## Tecnologías

- **Sockets TCP** (`ServerSocket`, `Socket`)
- **Hilos** (`Thread`)
- **Streams de E/S** (`BufferedReader`, `PrintWriter`)
