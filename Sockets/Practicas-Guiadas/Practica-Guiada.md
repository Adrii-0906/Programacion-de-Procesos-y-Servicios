---
theme: default
background: '#282a36'
colorSchema: dark
title: Cliente-Servidor en Java (Sockets)
---

<div class="flex flex-col items-center justify-center pt-20 text-center">
  <div class="bg-gradient-to-r from-blue-400 to-purple-500 text-transparent bg-clip-text text-6xl font-extrabold mb-4 pb-2">
    Java Sockets
  </div>
  <h2 class="text-3xl text-gray-300 font-light tracking-wide mb-10">Arquitectura Cliente-Servidor</h2>
  <div class="bg-white/10 px-6 py-3 rounded-full border border-white/20 shadow-lg text-blue-200 text-lg">
    Módulo: <b>PSP</b> <span class="opacity-50 mx-2">|</span> Curso: <b>2º DAM</b>
  </div>
</div>

---

# Idea clave

- Dos programas independientes en ejecución.
- Cliente: inicia siempre la conexión.
- Servidor: espera pacientemente las conexiones entrantes.
- Objetivo principal: establecer comunicación a través de la red.

---

# Esquema

Cliente → Servidor → Cliente

El cliente envía una petición directa a la IP y puerto del servidor. El servidor acepta la conexión y en ese momento establecen un canal bidireccional estable para intercambiar datos.

---

# Servidor básico (SIN hilos)

- Utiliza `ServerSocket` para reservar un puerto del sistema.
- El método `accept()` bloquea por completo la ejecución.
- Solo puede atender a un único cliente antes de terminar.

```java
ServerSocket server = new ServerSocket(5000);
Socket cliente = server.accept(); // Se bloquea esperando

// Atiende al cliente de forma secuencial
cliente.close();
```

---

# Cliente básico

- Utiliza `Socket(host, puerto)` para apuntar al servidor.
- Establece la conexión de forma transparente y automática.
- Cierra su extremo del socket al finalizar la comunicación.

```java
Socket socket = new Socket("127.0.0.1", 5000);

// Flujo de comunicación básica
socket.close();
```

---

# Comunicación

- Se envían datos usando `PrintWriter`.
- Se reciben datos leyendo con `BufferedReader`.
- El método `readLine()` bloquea la ejecución hasta recibir texto real.

```java
PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

salida.println("Mensaje enviado por la red");
String respuesta = entrada.readLine();
```

---

# 1. Establecimiento de Conexión

<div class="grid grid-cols-2 gap-6 mt-8">
  <div class="bg-blue-900/30 p-6 rounded-xl shadow-2xl border-l-4 border-blue-500 relative">
    <div class="text-blue-400 font-bold mb-4 text-xl">🖥️ Servidor</div>
    <div class="font-mono text-sm space-y-3">
      <div class="bg-black/40 p-3 rounded shadow-inner">1. <code>new ServerSocket(5000)</code></div>
      <div class="bg-black/40 p-3 rounded shadow-inner opacity-80 border border-blue-500/30">2. <code>accept()</code> <span class="italic text-gray-400 block text-xs mt-1">Bloqueado esperando conexión...</span></div>
    </div>
  </div>
  <div class="bg-green-900/30 p-6 rounded-xl shadow-2xl border-l-4 border-green-500 opacity-90 transition-all hover:opacity-100">
    <div class="text-green-400 font-bold mb-4 text-xl">💻 Cliente</div>
    <div class="font-mono text-sm mt-16">
      <div class="bg-black/40 p-3 rounded shadow-inner border border-green-500/30">
        3. <code>new Socket(IP, 5000)</code> <span class="italic text-gray-400 block text-xs mt-1">Acercándose a la puerta...</span>
      </div>
    </div>
  </div>
</div>

<div class="text-center mt-8 p-3 bg-blue-500/10 border border-blue-500/30 rounded-lg text-blue-200 animate-pulse text-lg font-semibold tracking-wide">
  🔄 ¡El canal de comunicación se ha establecido!
</div>

---

# 2. Intercambio de Datos

<div class="flex flex-row justify-between gap-4 mt-6 items-stretch">
  <div class="flex-1 bg-blue-900/30 p-6 rounded-xl shadow-2xl border-l-4 border-blue-500 max-h-full">
    <div class="text-blue-400 font-bold mb-3 text-xl">🖥️ Servidor</div>
    <div class="font-mono text-sm space-y-4">
      <div class="bg-black/40 p-3 rounded shadow-inner border border-blue-500/20">
        4. <code>entrada.readLine()</code>
      </div>
      <div class="bg-black/40 p-3 rounded shadow-inner border border-blue-500/20">
        5. <code>salida.println("OK")</code>
      </div>
    </div>
  </div>

  <div class="flex flex-col gap-6 justify-center items-center text-4xl w-16">
    <div class="animate-pulse">⬅️</div>
    <div class="animate-pulse delay-75">➡️</div>
  </div>

  <div class="flex-1 bg-green-900/30 p-6 rounded-xl shadow-2xl border-l-4 border-green-500 max-h-full">
    <div class="text-green-400 font-bold mb-3 text-xl">💻 Cliente</div>
    <div class="font-mono text-sm space-y-4">
      <div class="bg-black/40 p-3 rounded shadow-inner border border-green-500/20">
        4. <code>salida.println("Datos")</code>
      </div>
      <div class="bg-black/40 p-3 rounded shadow-inner border border-green-500/20">
        5. <code>entrada.readLine()</code>
      </div>
    </div>
  </div>
</div>

---

# 3. Cierre de Sesión

<div class="grid grid-cols-2 gap-6 mt-12">
  <div class="bg-gray-800/80 p-6 rounded-xl shadow-lg border-l-4 border-red-500/70">
    <div class="text-gray-400 font-bold mb-4">🖥️ Servidor</div>
    <div class="font-mono text-sm">
      <div class="bg-red-900/30 p-3 rounded text-red-300">
        6. <code>socket.close()</code>
      </div>
    </div>
  </div>
  <div class="bg-gray-800/80 p-6 rounded-xl shadow-lg border-l-4 border-red-500/70">
    <div class="text-gray-400 font-bold mb-4">💻 Cliente</div>
    <div class="font-mono text-sm">
      <div class="bg-red-900/30 p-3 rounded text-red-300">
        6. <code>socket.close()</code>
      </div>
    </div>
  </div>
</div>

<div class="text-center mt-12 py-4 px-6 bg-red-900/20 border border-red-500/20 rounded-xl shadow text-red-200 text-lg">
  ❌ Fin de la comunicación bidireccional y liberación de los puertos lógicos en ambos ordenadores.
</div>

---

# Problema real

- ¿Qué pasa si intentan conectar varios clientes al mismo tiempo?
- El servidor se queda bloqueado atendiendo en exclusiva al primero.
- El resto de clientes quedan atrapados en espera indefinida.

Con un único flujo de ejecución secuencial, es arquitectónicamente imposible atender a múltiples usuarios físicos a la vez.

---

# Introducción a hilos

- La clase `Thread` permite ejecución de código en paralelo.
- El método `start()` lanza el hilo al aire en segundo plano.
- El método `run()` alberga toda la lógica que se ejecutará.

```java
class MiHilo extends Thread {
    public void run() {
        System.out.println("Ejecutando proceso en paralelo");
    }
}

MiHilo hilo = new MiHilo();
hilo.start();
```

---

# Idea multicliente

- El hilo principal del servidor acepta conexiones en un bucle continuo.
- Cada cliente que entra se asigna y delega en un hilo independiente.
- Tras derivar al cliente, el servidor vuelve a escuchar inmediatamente.

Esquema de concurrencia:

1. Entra nueva conexión.
2. Se instancia el hilo secundario para ese usuario.
3. El servidor se libera.

---

# Paso 1: Crear `ClientHandler.java`

Extraemos la lógica de comunicación que teníamos en el `Servidor` básico y la encapsulamos dentro del método `run()` de un nuevo `Thread`.

<div class="bg-gray-900/50 rounded-lg p-1 mt-2 shadow-xl border border-gray-700 overflow-y-auto max-h-[320px] custom-scrollbar">

```java {all|12-23}
package PracticaGuiada;
import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket cliente;
    
    public ClientHandler(Socket socket) { 
        this.cliente = socket; // Recibe el socket de la conexión
    }
    
    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true);
            
            String mensaje = entrada.readLine();
            System.out.println("Hilo atendiendo: Mensaje recibido: " + mensaje);
            salida.println("Mensaje recibido, gracias");
            
            cliente.close(); // Cerramos solo el socket del cliente
        } catch (Exception e) { e.printStackTrace(); }
    }
}
```

</div>

---

# Paso 2: Adaptar el `Servidor.java`

Sustituimos el código secuencial del `main` por un **bucle infinito** que simplemente acepte clientes y los ponga a trabajar en segundo plano usando nuestra nueva clase.

<div class="bg-gray-900/50 rounded-lg p-1 mt-4 shadow-xl border border-gray-700">

```java {all|11-16}
package PracticaGuiada;
import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Servidor concurrente escuchando en el puerto 5000...");
            
            while (true) {
                Socket cliente = server.accept();
                System.out.println("¡Nuevo cliente conectado desde " + cliente.getInetAddress() + "!");
                
                // Iniciamos el hilo sin bloquear el servidor
                new ClientHandler(cliente).start(); 
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
```

</div>

<div class="text-center mt-4 p-2 bg-yellow-900/20 text-yellow-300 text-sm rounded border border-yellow-500/30">
  ⚠️ Ya no ejecutamos `server.close()` porque ahora el servidor debe funcionar ininterrumpidamente para siempre.
</div>

---

# Flujo de Ejecución Concurrente

<div class="flex items-center justify-center gap-4 mt-12 mb-4">
  <div class="flex flex-col items-center">
    <div class="bg-blue-900/30 border-2 border-blue-500 rounded-xl p-4 shadow-lg shadow-blue-500/20 text-center w-40 relative">
      <div class="text-4xl mb-2">🖥️</div>
      <div class="font-bold text-blue-300">Servidor</div>
      <div class="mt-2 text-[10px] bg-blue-950 px-1 py-1 rounded text-blue-200 uppercase tracking-widest border border-blue-500/50">
        while(true)<br/>accept()
      </div>
    </div>
  </div>

  <div class="flex flex-col gap-8 justify-center text-gray-500 text-2xl mx-1">
    <div class="animate-pulse text-indigo-400">⚡</div>
    <div class="animate-pulse delay-100 text-indigo-400">⚡</div>
  </div>

  <div class="flex flex-col gap-4">
    <div class="bg-indigo-900/40 border border-indigo-500 rounded-lg p-3 text-center shadow min-w-[190px]">
      <div class="text-sm font-bold text-indigo-300">⚙️ Hilo Dedicado</div>
      <div class="text-xs text-indigo-200 opacity-80">(ClientHandler 1)</div>
    </div>
    <div class="bg-indigo-900/40 border border-indigo-500 rounded-lg p-3 text-center shadow min-w-[190px]">
      <div class="text-sm font-bold text-indigo-300">⚙️ Hilo Dedicado</div>
      <div class="text-xs text-indigo-200 opacity-80">(ClientHandler 2)</div>
    </div>
  </div>

  <div class="flex flex-col gap-6 justify-center mx-2 text-2xl">
    <div class="flex items-center"><span class="text-green-500 animate-pulse delay-75">⬅️➡️</span></div>
    <div class="flex items-center"><span class="text-yellow-500 animate-pulse delay-150">⬅️➡️</span></div>
  </div>

  <div class="flex flex-col gap-4">
    <div class="bg-green-900/30 border-l-4 border-green-500 rounded p-4 shadow text-center w-36 border border-green-500/20">
      <div class="text-2xl mb-1">💻</div>
      <div class="text-sm font-bold text-green-300">Cliente 1</div>
    </div>
    <div class="bg-yellow-900/30 border-l-4 border-yellow-500 rounded p-4 shadow text-center w-36 border border-yellow-500/20">
      <div class="text-2xl mb-1">💻</div>
      <div class="text-sm font-bold text-yellow-300">Cliente 2</div>
    </div>
  </div>
</div>

<div class="mt-12 p-5 bg-red-900/20 border-l-4 border-red-500 rounded-xl shadow-lg border-y border-r border-red-500/20 flex gap-4 items-center transition-all hover:bg-red-900/30">
  <div class="text-4xl px-2 animate-bounce">⚠️</div>
  <div class="text-red-200 text-sm leading-relaxed">
    <span class="text-red-400 font-extrabold block mb-1">LIMITACIÓN CLAVE: LOS CLIENTES NO SE HABLAN</span>
    Cada cliente físico es atendido de forma individual por su propio hilo. En este patrón, <b>los diferentes clientes no saben que existen otros clientes</b> y no pueden mandarse mensajes directamente. Para crear un sistema de Chat, el servidor principal debería almacenar a todos los usuarios en una lista global y redistribuir los mensajes masivamente (Broadcast).
  </div>
</div>

---

# Resumen Final

- **Límites de un hilo único:** Sin concurrencia, el servidor `accept()` colapsa y no puede gestionar más de un inicio de sesión.
- **División de tareas:** Con concurrencia de sockets, el hilo principal solo actúa de "telefonista" aceptando la llamada, y deriva la redacción operativa a hilos en paralelo.
- **Escalabilidad:** Esta separación de procesos es el patrón estructural en el que se basa cualquier servidor competitivo.
