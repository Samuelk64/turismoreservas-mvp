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

### Reglas de negocio verificadas

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

## Instrucciones de ejecución

### Prerrequisitos

Asegúrate de tener instalado:

- [Java 21](https://adoptium.net/)
- [Node.js 18+](https://nodejs.org/)
- [Git](https://git-scm.com/) (opcional)
- IDE recomendado: IntelliJ IDEA
---

### 1. Ejecutar el backend

**Opción A — Desde el IDE **

1. Abre el proyecto `turismoreservas` en IntelliJ IDEA
2. Espera que Maven descargue las dependencias automáticamente
3. Ubica la clase `TurismoreservasApplication.java`
4. Haz clic en el botón ▶️ o presiona `Shift + F10`
   **Opción B — Desde la terminal**

```bash
cd turismoreservas
./mvnw spring-boot:run       
mvnw.cmd spring-boot:run     
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

> ⚠ El backend debe estar corriendo antes de abrir el frontend, de lo contrario las llamadas a la API fallarán.
 
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

## Información relevante

**Asignatura:** Ingenieria de Software I
**Proyecto:** MVP — Sistema de Gestión de Reservas de Turismo Rural  
**Stack:** Spring Boot + React  
**Alcance:** 2 pantallas funcionales (CRUD de Experiencias + Transacción de Reserva)
**Autores:** Samuel Calle, Joseph Escobar, Juan David Castañeda