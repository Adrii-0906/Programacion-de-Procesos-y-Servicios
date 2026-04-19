# 🎮 Trivia Multijugador — Sockets TCP en Java

Juego de Trivia multijugador cliente-servidor desarrollado en Java utilizando **Sockets TCP** y **multihilo (Threads)**. Un servidor gestiona la partida y múltiples clientes se conectan para competir respondiendo preguntas de tipo test.

---

## 📋 Índice

- [Arquitectura del proyecto](#-arquitectura-del-proyecto)
- [Requisitos previos](#-requisitos-previos)
- [Estructura de ficheros](#-estructura-de-ficheros)
- [Compilación](#-compilación)
- [Ejecución](#-ejecución)
  - [1. Iniciar el Servidor](#1-iniciar-el-servidor)
  - [2. Conectar Clientes](#2-conectar-clientes)
  - [3. Iniciar la Partida](#3-iniciar-la-partida)
- [Flujo de juego](#-flujo-de-juego)
- [Protocolo de comunicación](#-protocolo-de-comunicación)
- [Descripción de clases](#-descripción-de-clases)

---

## 🏗 Arquitectura del proyecto

```
┌─────────────┐     TCP (puerto 5000)     ┌─────────────────┐
│  Cliente 1   │ ◄──────────────────────► │                 │
├─────────────┤                           │                 │
│  Cliente 2   │ ◄──────────────────────► │    Servidor     │
├─────────────┤                           │  (GameManager)  │
│  Cliente N   │ ◄──────────────────────► │                 │
│  (máx. 10)   │                           │                 │
└─────────────┘                           └─────────────────┘
```

- El **servidor** escucha en el **puerto 5000** y acepta hasta **10 jugadores** simultáneos.
- Cada cliente conectado genera un **hilo independiente** (`ClienteHandler`) que gestiona su comunicación.
- El `GameManager` coordina las rondas, los tiempos, la corrección de respuestas y el ranking.

---

## ✅ Requisitos previos

- **Java JDK 8** o superior instalado.
- Acceso a una terminal / consola de comandos.
- Conexión de red entre servidor y clientes (o `localhost` para pruebas locales).

---

## 📁 Estructura de ficheros

```
P1_2-Trivia/
├── src/
│   ├── Servidor.java          # Punto de entrada del servidor
│   ├── Cliente.java           # Punto de entrada del cliente
│   ├── ClienteHandler.java    # Hilo que gestiona cada cliente conectado
│   ├── GameManager.java       # Lógica del juego (preguntas, rondas, ranking)
│   ├── Pregunta.java          # Modelo de dato de una pregunta
│   └── ReceptorMensajes.java  # Hilo del cliente que escucha mensajes del servidor
└── readme.md
```

---

## 🔧 Compilación

Desde la raíz del proyecto, compilar todos los ficheros fuente:

```bash
javac -d out src/*.java
```

Esto generará los `.class` compilados dentro de la carpeta `out/`.

---

## 🚀 Ejecución

### 1. Iniciar el Servidor

```bash
java -cp out Servidor
```

Salida esperada:

```
Servidor arrancado y esperando conexiones...
Escribe START para iniciar la partida:
```

El servidor queda a la espera de conexiones de clientes.

---

### 2. Conectar Clientes

**En la misma máquina (localhost):**

```bash
java -cp out Cliente
```

**Desde otra máquina en la misma red (indicando la IP del servidor):**

```bash
java -cp out Cliente 192.168.1.XX
```

> Sustituir `192.168.1.XX` por la IP real de la máquina que ejecuta el servidor.

Al conectarse, el cliente pedirá un **nick** (nombre de jugador):

```
Conectado al servidor.
Introduce tu nick: _
```

Se pueden conectar **hasta 10 jugadores** simultáneamente. El servidor informa de cada nueva conexión:

```
Nuevo jugador conectado (1/10)
Nick recibido: Adrian
```

---

### 3. Iniciar la Partida

Cuando todos los jugadores estén conectados, escribir `START` en la consola del servidor:

```
START
```

> ⚠️ Debe haber al menos **1 jugador conectado** para poder iniciar.

---

## 🎯 Flujo de juego

1. El administrador arranca el servidor y espera a que se conecten los jugadores.
2. Cada jugador se conecta e introduce su nick.
3. El administrador escribe `START` para comenzar la partida.
4. Se envían **5 preguntas** de tipo test (A, B, C, D) una a una a todos los jugadores.
5. Los jugadores disponen de **15 segundos** para responder cada pregunta.
6. Para responder, el jugador escribe la **letra de la opción** (a, b, c o d) en su consola.
7. Tras cada pregunta se muestra un **ranking parcial** con las puntuaciones.
8. Al finalizar todas las preguntas se muestra el **ranking final** y se anuncia al **ganador**.

### Ejemplo de interacción del cliente:

```
=== LA PARTIDA HA COMENZADO ===

--- Pregunta 1 de 5 ---
¿Qué es el "Hardware" de un ordenador?
  A: a) Los programas y aplicaciones instaladas.
  B: b) El sistema operativo que hace funcionar el equipo.
  C: c) Las partes físicas y tangibles del ordenador (pantalla, teclado, placa, etc.).
  D: d) La conexión a internet.
Tienes 15 segundos para responder. Formato: RESPUESTA|letra

c
Respuesta correcta! +1 punto

=== RANKING PARCIAL ===
1º - Adrian: 1 puntos
2º - Luis: 0 puntos
```

---

## 📡 Protocolo de comunicación

| Dirección             | Mensaje                | Descripción                                |
|-----------------------|------------------------|--------------------------------------------|
| Cliente → Servidor    | `nick` (texto libre)   | Primer mensaje: el jugador envía su nick   |
| Cliente → Servidor    | `RESPUESTA\|letra`     | El jugador envía su respuesta (a, b, c, d) |
| Servidor → Cliente    | `INFO\|texto`          | Mensaje informativo (respuesta registrada) |
| Servidor → Cliente    | Texto libre            | Preguntas, ranking, resultados, etc.       |

---

## 📖 Descripción de clases

### `Servidor.java`
Punto de entrada del servidor. Abre un `ServerSocket` en el puerto **5000** y lanza un hilo para aceptar conexiones de clientes (hasta 10). El hilo principal espera el comando `START` por teclado para iniciar la partida a través del `GameManager`.

### `Cliente.java`
Punto de entrada del cliente. Se conecta al servidor por socket TCP. Envía el nick del jugador y lanza un hilo `ReceptorMensajes` para escuchar mensajes del servidor. El hilo principal lee las respuestas del teclado y las envía con el formato `RESPUESTA|letra`.

### `ClienteHandler.java`
Extiende `Thread`. Se crea uno por cada cliente conectado. Gestiona la comunicación bidireccional con un jugador concreto: recibe su nick, escucha sus respuestas, y permite enviarle mensajes. Controla que solo se registre **una respuesta por ronda** y únicamente mientras la ronda esté abierta.

### `GameManager.java`
Controla toda la lógica de la partida:
- Almacena las **5 preguntas** predefinidas.
- Itera las rondas con un temporizador de **15 segundos** por pregunta.
- Corrige las respuestas de todos los jugadores.
- Genera y envía el **ranking parcial** tras cada pregunta y el **ranking final** al terminar.
- Anuncia al **ganador** (jugador con más puntos).

### `Pregunta.java`
Modelo de datos que representa una pregunta con su enunciado, cuatro opciones (A, B, C, D) y la letra de la respuesta correcta. Incluye getters y setters.

### `ReceptorMensajes.java`
Extiende `Thread`. Se ejecuta en el lado del cliente para escuchar constantemente los mensajes que llegan del servidor e imprimirlos por consola. Permite que el cliente reciba preguntas y resultados mientras sigue pudiendo escribir respuestas.
