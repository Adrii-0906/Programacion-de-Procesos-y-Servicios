# API REST de Gestión de Productos

Proyecto Spring Boot para gestión de productos con seguridad y validación.

## Tecnologías
- Spring Boot 3.x
- Java 21
- H2 Database (En memoria)
- Spring Security (HTTP Basic)
- Bean Validation (Jakarta Validation)

## Ejecución
1. Compilar y descargar dependencias:
   ```bash
   mvn clean install
   ```
2. Ejecutar:
   ```bash
   mvn spring-boot:run
   ```

El servidor iniciará en `http://localhost:8081`.
H2 Console: `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:dualdb`, User: `sa`, Password: (vacío)).

## Endpoints

| Método | URL | Acceso | Descripción |
|--------|-----|--------|-------------|
| GET | `/api/productos` | 🔓 Público | Listar todos |
| GET | `/api/productos/{id}` | 🔓 Público | Obtener por ID |
| POST | `/api/productos` | 🔒 Autenticado | Crear producto |
| PUT | `/api/productos/{id}` | 🔒 Autenticado | Actualizar |
| DELETE | `/api/productos/{id}` | 🔒 Autenticado | Eliminar |

---

## Seguridad (RA5)

### Decisiones de diseño

- **HTTP Basic**: Se eligió por ser el esquema más sencillo y adecuado para una API de demostración/educativa. Las credenciales se envían en cada petición codificadas en Base64 mediante la cabecera `Authorization`.
- **CSRF desactivado**: Al ser una API REST sin estado (stateless) y sin formularios HTML, CSRF no es necesario.
- **Usuarios en memoria**: Se definen dos usuarios simulados (`InMemoryUserDetailsManager`) sin necesidad de base de datos adicional para autenticación.
- **Contraseñas cifradas con BCrypt**: Aunque los usuarios estén en memoria, las contraseñas se almacenan cifradas, siguiendo buenas prácticas.

### Usuarios configurados

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `user` | `password123` | USER |
| `admin` | `admin123` | ADMIN |

### Rutas protegidas

- **GET `/api/**`** → Acceso público (sin autenticación)
- **POST, PUT, DELETE `/api/**`** → Requieren autenticación HTTP Basic
- **`/h2-console/**`** → Acceso público (para desarrollo)

---

## Validación de Datos (RA5)

Los datos de entrada se validan automáticamente mediante Bean Validation. Si los datos no cumplen las reglas, la API devuelve un HTTP 400 con un JSON detallando los errores.

### Reglas de validación

| Campo | Reglas |
|-------|--------|
| `nombre` | Obligatorio, entre 2 y 100 caracteres |
| `precio` | Obligatorio, mínimo 0 |
| `categoria` | Opcional, máximo 50 caracteres |
| `stock` | Obligatorio, mínimo 0 |

---

## Ejemplos cURL

### Listar productos (público, sin autenticación)
```bash
curl http://localhost:8081/api/productos
```

### Crear producto (requiere autenticación)
```bash
curl -u user:password123 -X POST http://localhost:8081/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Tablet", "precio": 300.50, "categoria": "Electrónica", "stock": 20}'
```

### Crear producto SIN autenticación (devuelve 401 Unauthorized)
```bash
curl -X POST http://localhost:8081/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Tablet", "precio": 300.50, "stock": 20}'
```

### Enviar JSON inválido (devuelve 400 con errores de validación)
```bash
curl -u user:password123 -X POST http://localhost:8081/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "", "precio": -1, "stock": -5}'
```

Respuesta esperada (HTTP 400):
```json
{
  "nombre": "El nombre es obligatorio",
  "precio": "El precio debe ser positivo",
  "stock": "El stock no puede ser negativo"
}
```

### Actualizar producto (con admin)
```bash
curl -u admin:admin123 -X PUT http://localhost:8081/api/productos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Tablet Pro", "precio": 450.00, "categoria": "Electrónica", "stock": 15}'
```

### Eliminar producto
```bash
curl -u admin:admin123 -X DELETE http://localhost:8081/api/productos/1
```
