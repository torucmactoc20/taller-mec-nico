# Taller Mecánico – Proyecto integrador (Programación 2)

Aplicación web de gestión para un taller mecánico: permite administrar clientes, sus vehículos y las órdenes de trabajo que se abren para cada vehículo.

## Integrantes

- _Completar: nombre y apellido de cada integrante_

## Descripción del problema

Un taller necesita llevar el registro de sus clientes, de los vehículos de cada cliente y del historial de trabajos realizados. La aplicación reemplaza las planillas y cuadernos: ordena la información, evita duplicados (por ejemplo, dos vehículos con la misma patente) y controla el avance de cada reparación.

## Modelo de dominio

```
Cliente 1 ──── * Vehiculo 1 ──── * OrdenDeTrabajo
```

| Entidad | Atributos principales |
|---|---|
| `Cliente` | id, nombre, apellido, teléfono, lista de vehículos |
| `Vehiculo` | id, patente (única), marca, modelo, año, cliente, lista de órdenes |
| `OrdenDeTrabajo` | id, descripción, fecha de ingreso, estado (`ABIERTA`, `EN_REPARACION`, `FINALIZADA`), costo, vehículo |

Las relaciones uno a muchos son bidireccionales: la clave foránea está en `VEHICULO.CLIENTE_ID` y en `ORDENDETRABAJO.VEHICULO_ID`.

## Funcionalidades

- **Clientes**: registrar, listar, modificar y eliminar. Búsqueda por nombre o apellido.
- **Vehículos**: registrar (eligiendo el cliente dueño), listar, modificar y eliminar. Búsqueda por patente.
- **Órdenes de trabajo**: abrir, listar, consultar el detalle y filtrar por estado.
- **Operaciones de negocio** sobre las órdenes:
  - Abrir una orden para un vehículo.
  - Iniciar la reparación (`ABIERTA` → `EN_REPARACION`).
  - Finalizar la orden registrando el costo final (`EN_REPARACION` → `FINALIZADA`).
- Panel de inicio con el resumen de clientes, vehículos y órdenes por estado.
- Navegación entre pantallas desde el menú superior y desde los detalles (cliente → vehículos → órdenes).

### Validaciones y reglas de negocio

- Campos obligatorios y longitudes máximas en todos los formularios.
- Teléfono con formato válido.
- Patente con formato argentino válido (`AB123CD` o `ABC123`), normalizada a mayúsculas y **única**.
- Año del vehículo entre 1950 y el año siguiente al actual.
- Un vehículo **no puede tener más de una orden en curso** (abierta o en reparación).
- Solo se puede iniciar la reparación de una orden abierta, y solo se puede finalizar una orden en reparación.
- El costo final debe ser mayor a cero.
- **Bajas**: no se puede eliminar un cliente que tiene vehículos ni un vehículo que tiene órdenes (se impide la baja para no dejar datos inconsistentes).
- Los mensajes de confirmación y de error se muestran en pantalla.

## Arquitectura

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Dominio | `com.programacion2.dominio` | Entidades persistentes (JDO) y el enum `EstadoOrden` |
| Repositorio | `com.programacion2.repositorio` | Único acceso a la base con DataNucleus JDO (transacciones, consultas JDOQL) |
| Servicio | `com.programacion2.servicio` | Reglas de negocio y validaciones |
| Controlador | `com.programacion2.controlador` | Atienden las solicitudes del navegador (Spring MVC) y envían datos a las vistas con `Model` |
| Vista | `src/main/resources/templates` | Plantillas Thymeleaf con HTML, CSS y Bootstrap |
| Configuración | `com.programacion2.config` | `PersistenceManagerFactory` único como bean de Spring |

Las vistas nunca acceden a los repositorios: solo reciben datos del controlador.

### Recorrido de una operación (abrir una orden)

1. El navegador envía `POST /ordenes` desde `ordenes/form.html`.
2. `OrdenController.abrir` recibe los datos (`OrdenForm`) y llama al servicio.
3. `OrdenServicio.abrir` valida los datos y la regla "una sola orden en curso por vehículo".
4. `OrdenRepositorio.guardarParaVehiculo` abre una transacción JDO, vincula la orden con el vehículo y hace `makePersistent`.
5. DataNucleus ejecuta el `INSERT` en SQL Server y el controlador redirige al listado con un mensaje de confirmación.

### Uso de cada tecnología

- **Maven**: gestiona dependencias y el build; el plugin de DataNucleus realiza el *enhancement* de las clases en la fase `process-classes`.
- **Spring / Spring Boot**: inyección de dependencias (`@Repository`, `@Service`, `@Controller`) y servidor web embebido.
- **DataNucleus JDO**: persistencia de las entidades en SQL Server.
- **MVC + Thymeleaf**: controladores, `Model` y plantillas.
- **Bootstrap**: estilos de formularios, tablas y navegación (se carga desde CDN).

## Requisitos

- JDK 17 o superior
- Maven 3.9 o superior
- SQL Server (2019 o superior, o Express) y SQL Server Management Studio (SSMS), con acceso en `localhost:1433`
- Conexión a internet (para descargar dependencias y cargar Bootstrap desde CDN)

## Preparar la base de datos (SQL Server)

1. Abrir **SQL Server Management Studio**, conectarse como administrador, abrir `scripts/01-crear-base-de-datos.sql` en una nueva consulta y ejecutarlo (F5). Crea la base `TallerMecanico` y el usuario `taller`.
   Las tablas (`CLIENTE`, `VEHICULO`, `ORDENDETRABAJO`) las crea DataNucleus solas al iniciar la aplicación.

2. Verificar que el servidor acepte la conexión de la aplicación:
   - En las propiedades del servidor (SSMS → clic derecho en el servidor → Propiedades → Seguridad), la autenticación debe ser **modo mixto** (Windows y SQL Server). Después reiniciar el servicio de SQL Server.
   - En **SQL Server Configuration Manager** → Protocolos de SQL Server → **TCP/IP** debe estar *Habilitado* (puerto 1433). Reiniciar el servicio.

3. La conexión se configura en `src/main/resources/application.properties` y puede sobrescribirse con variables de entorno, sin tocar el código:

   | Variable | Valor por defecto |
   |---|---|
   | `DB_URL` | `jdbc:sqlserver://localhost:1433;databaseName=TallerMecanico;encrypt=true;trustServerCertificate=true` |
   | `DB_USER` | `taller` |
   | `DB_PASSWORD` | la contraseña del usuario creado por el script |

   Si usan SQL Server Express con instancia con nombre, la URL es `jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=TallerMecanico;encrypt=true;trustServerCertificate=true`.
   Si cambian usuario o contraseña, definan las variables de entorno en lugar de modificar y subir el archivo.

4. (Opcional) Una vez iniciada la aplicación por primera vez, se puede ejecutar `scripts/02-datos-ejemplo.sql` en SSMS para cargar datos de ejemplo.

## Ejecutar la aplicación

```bash
mvn spring-boot:run
```

Luego abrir <http://localhost:8080>.

Para generar el jar y ejecutarlo:

```bash
mvn clean package
java -jar target/taller-mecanico-1.0-SNAPSHOT.jar
```

### Datos de ejemplo (opcional)

Para cargar algunos clientes, vehículos y órdenes la primera vez (solo si la base no tiene clientes):

```bash
# Linux / macOS
CARGAR_DATOS_EJEMPLO=true mvn spring-boot:run

# Windows (PowerShell)
$env:CARGAR_DATOS_EJEMPLO="true"; mvn spring-boot:run
```

Los datos se mantienen en la base al cerrar y volver a ejecutar la aplicación.
