Readme · MD
Copy

#  Sistema de Gestión de Reservas — Turismo Rural

MVP  para la gestión de reservas de experiencias de turismo rural. El sistema permite administrar experiencias turísticas y realizar reservas a través de dos pantallas funcionales.
 
---

##  Contexto del proyecto

El sistema fue diseñado a partir de un diagrama de dominio que modela las entidades principales del negocio: clientes, operadores rurales, experiencias, itinerarios y reservas. La arquitectura sigue el patrón por capas (Controller → Service → Model) sin persistencia en base de datos — los datos viven en memoria durante la ejecución de la aplicación.

### Entidades principales

| Entidad | Descripción |
|---|---|
| `Persona` | Clase abstracta base para Cliente, OperadorRural y Administrador |
| `Cliente` | Usuario que realiza reservas de experiencias |
| `OperadorRural` | Proveedor que ofrece experiencias turísticas |
| `Experiencia` | Actividad turística con horarios predefinidos y capacidad máxima |
| `Itinerario` | Agrupación de una o varias experiencias para un cliente |
| `Reserva` | Transacción principal que vincula un cliente con experiencias seleccionadas |

### Reglas de negocio implementadas

- **RN-05:** El sistema no permite crear una reserva si existe conflicto de horario entre las experiencias seleccionadas
- Un itinerario debe tener al menos una experiencia (`1..*`)
- Una reserva puede agrupar una o varias experiencias (`1..*`)
- La capacidad máxima de cada experiencia es validada al momento de la reserva
---

##  Tecnologías utilizadas

### Backend
| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.x |
| Spring Web | — |
| Spring Validation | — |
| Lombok | — |
| SpringDoc OpenAPI (Swagger) | 2.8.8 |
| Maven | — |

### Frontend
| Tecnología | Versión |
|---|---|
| React | 18+ |
| Vite | — |
| Material UI (MUI) | v6 |
| React Router | v6 |
| Axios | — |
 
---

## Estructura del proyecto

```
turismo-rural/
 ├── turismoreservas/                  ← Proyecto Spring Boot
 │    ├── src/main/java/com/turismoreservas/
 │    │    ├── controllers/            ← Endpoints REST
 │    │    │    ├── ClienteController.java
 │    │    │    ├── ExperienciaController.java
 │    │    │    ├── ItinerarioController.java
 │    │    │    ├── OperadorController.java
 │    │    │    └── ReservaController.java
 │    │    ├── service/                ← Lógica de negocio + datos en memoria
 │    │    │    ├── ClienteService.java
 │    │    │    ├── ExperienciaService.java
 │    │    │    ├── ItinerarioService.java
 │    │    │    ├── OperadorService.java
 │    │    │    └── ReservaService.java
 │    │    ├── domain/
 │    │    │    ├── entity/            ← Clases del dominio
 │    │    │    ├── dto/               ← Objetos de transferencia de datos
 │    │    │    └── enums/             ← MetodoPago · EstadoReserva
 │    │    ├── exception/              ← Manejo global de errores
 │    │    └── config/                 ← CORS · OpenAPI
 │    └── src/main/resources/
 │         └── application.yml
 │
 └── turismo-rural-frontend/           ← Proyecto React + Vite
      └── src/
           ├── api/                    ← Llamadas HTTP al backend
           │    ├── axios.js
           │    ├── clientes.js
           │    ├── experiencias.js
           │    └── reservas.js
           ├── components/
           │    └── Navbar.jsx
           ├── pages/
           │    ├── ExperienciasPage.jsx   ← Pantalla CRUD
           │    └── ReservasPage.jsx       ← Pantalla transacción
           ├── utils/
           │    └── imagenes.js
           ├── App.jsx
           └── main.jsx
```
 
---

## Instrucciones de ejecución

### Prerrequisitos

Asegúrate de tener instalado:

- [Java 21](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- [Git](https://git-scm.com/) (opcional)
- IDE recomendado: IntelliJ IDEA para el backend, VS Code para el frontend
---

### 1. Ejecutar el backend

**Opción A — Desde el IDE (recomendada)**

1. Abre el proyecto `turismoreservas` en IntelliJ IDEA
2. Espera que Maven descargue las dependencias automáticamente
3. Ubica la clase `TurismoreservasApplication.java`
4. Haz clic en el botón ▶️ o presiona `Shift + F10`
   **Opción B — Desde la terminal**

```bash
cd turismoreservas
./mvnw spring-boot:run        # Mac / Linux
mvnw.cmd spring-boot:run      # Windows
```

El backend quedará disponible en:
```
http://localhost:8080
```

**Verificar que el backend está corriendo:**

Abre en el navegador:
```
http://localhost:8080/swagger-ui.html
```
Deberías ver la documentación interactiva de los 5 grupos de endpoints.
 
---

### 2. Ejecutar el frontend

Abre una **nueva terminal** y ejecuta:

```bash
cd turismo-rural-frontend
npm install       # solo la primera vez
npm run dev
```

El frontend quedará disponible en:
```
http://localhost:5173
```

> ⚠️ El backend debe estar corriendo antes de abrir el frontend, de lo contrario las llamadas a la API fallarán.
 
---

##  Pantallas del sistema

### Pantalla 1 — Gestión de Experiencias (CRUD)
Ruta: `/experiencias`

Permite:
- Ver todas las experiencias con imagen según su tipo, precio, ubicación, duración, capacidad y horarios disponibles
- Crear nuevas experiencias con validación de campos
- Editar experiencias existentes incluyendo sus horarios
- Eliminar experiencias con confirmación
### Pantalla 2 — Reservas (Transacción principal)
Ruta: `/reservas`

Permite:
- Crear una reserva en 3 pasos guiados (Stepper):
    1. Seleccionar cliente
    2. Elegir experiencias y seleccionar un horario por cada una
    3. Ingresar fecha, cantidad de personas, método de pago y observaciones
- Ver resumen del total estimado antes de confirmar
- Ver todas las reservas registradas con sus horarios confirmados
- Cancelar reservas activas
---

## Endpoints disponibles

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/experiencias` | Listar todas las experiencias |
| GET | `/api/experiencias/{id}` | Obtener experiencia por ID |
| POST | `/api/experiencias` | Crear nueva experiencia |
| PUT | `/api/experiencias/{id}` | Actualizar experiencia |
| DELETE | `/api/experiencias/{id}` | Eliminar experiencia |
| GET | `/api/reservas` | Listar todas las reservas |
| GET | `/api/reservas/{id}` | Obtener reserva por ID |
| POST | `/api/reservas` | Crear nueva reserva |
| PUT | `/api/reservas/{id}/cancelar` | Cancelar reserva |
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/operadores` | Listar operadores rurales |
| PUT | `/api/operadores/{id}/habilitar` | Habilitar operador |
| PUT | `/api/operadores/{id}/deshabilitar` | Deshabilitar operador |
| GET | `/api/itinerarios` | Listar itinerarios |
| POST | `/api/itinerarios` | Crear itinerario |
| PUT | `/api/itinerarios/{id}/agregar-experiencia/{expId}` | Agregar experiencia a itinerario |
| DELETE | `/api/itinerarios/{id}/eliminar-experiencia/{expId}` | Eliminar experiencia de itinerario |
 
---

## Datos precargados en memoria

El sistema inicia con los siguientes datos de ejemplo:

**Clientes:** Ana Garcia · Carlos Ramirez · Maria Lopez · Juan Torres

**Experiencias:**
| Nombre | Tipo | Precio | Horarios |
|---|---|---|---|
| Senderismo El Roble | Senderismo | $75.000 COP | 07:00 · 10:00 · 13:00 |
| Avistamiento de Aves | Naturaleza | $55.000 COP | 05:30 · 08:00 |
| Taller de Quesos Artesanales | Gastronomia | $45.000 COP | 09:00 · 14:00 · 16:00 |
| Recorrido en Jeep Willy | Cultural | $35.000 COP | 08:00 · 11:00 · 15:00 |

**Operadores:** Pedro Arbelaez · Lucia Cardona · Andres Salazar

**Itinerarios:** Fin de semana en el Quindio · Experiencia cafetera completa

>  Los datos se reinician cada vez que se reinicia el backend al no tener persistencia en base de datos.
 
---

## Información académica

**Asignatura:** Desarrollo de Software  
**Proyecto:** MVP — Sistema de Gestión de Reservas de Turismo Rural  
**Stack:** Spring Boot + React  
**Alcance:** 2 pantallas funcionales (CRUD de Experiencias + Transacción de Reserva)