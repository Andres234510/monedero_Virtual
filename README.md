# Monedero Virtual con Sistema de Puntos

Proyecto Final de Estructuras de Datos. Aplicación de escritorio (JavaFX) que simula un monedero virtual con depósitos, retiros, transferencias, transacciones programadas, multimonedero, notificaciones, reportes y un sistema de puntos con rangos y beneficios.

Todas las estructuras de datos (listas, pilas, colas, cola de prioridad, árboles, grafos y tablas hash) están implementadas por el equipo, sin usar las colecciones de java.util.

## Requisitos

- JDK 17 o superior
- Maven (incluido en IntelliJ IDEA)
- JavaFX 21 (Maven lo descarga solo)

## Cómo ejecutar

*Desde IntelliJ:* abrir el proyecto, esperar a que Maven termine de sincronizar y ejecutar la clase co.edu.uniquindio.monedero.Launcher con el botón verde. No ejecutar App directamente: da el error "JavaFX runtime components are missing".

*Desde la terminal:*


mvn javafx:run


*Pruebas unitarias:*


mvn test


## Usuarios de prueba

Al iniciar se cargan datos de prueba (util/DatosPrueba.java). Todo vive en memoria: al cerrar el programa solo vuelven estos datos.

| Perfil | Usuario | Clave | Qué se puede probar |
|---|---|---|---|
| Administrador | admin | admin | Clientes, rangos, procesos y reportes globales |
| Cliente Demo | 1001 | 1234 | Historial, reversión de transacciones, puntos y rango |
| Ana Gómez | 1002 | 1234 | Cliente que recibe una transferencia |
| Luis Pérez | 1003 | 1234 | 60.000 puntos para probar canjes |

Los clientes que crea el administrador usan un PIN de 4 dígitos.

## Funcionalidades

*Cliente*
- Inicio: saldo, puntos disponibles y rango.
- Transacciones: depósito, retiro y transferencia (con puntos calculados en vivo).
- Historial: filtro por tipo y reversión de la última transacción o de todas.
- Puntos y canje: progreso al siguiente rango y canje de beneficios.
- Programadas: depósitos y transferencias recurrentes (única, diaria, semanal, mensual).
- Notificaciones: navegación circular y marca de leídas.
- Monederos: crear monederos y mover dinero entre ellos.
- Reportes: resumen, extracto, gasto por categoría y patrones de gasto.

*Administrador*
- Clientes: crear y listar.
- Rangos: ranking de clientes por puntos.
- Procesos: procesar programadas vencidas y verificar integridad.
- Reportes: ranking, transferencias entre clientes e impacto de puntos.

## Sistema de puntos

| Operación | Puntos por cada 100 unidades |
|---|---|
| Depósito | 1 |
| Retiro | 2 |
| Transferencia | 3 |

| Rango | Puntos acumulados | Bono de puntos | Descuento en comisión de transferencia |
|---|---|---|---|
| Bronce | 0 a 500 | 0% | 0% |
| Plata | 501 a 1000 | 5% | 10% |
| Oro | 1001 a 5000 | 10% | 20% |
| Platino | más de 5000 | 20% | 30% |

Canjes: 100 puntos (10% menos de comisión en transferencias), 500 puntos (un mes sin cargos por retiros) y 1000 puntos (bono de 50 unidades de saldo). Canjear no baja el rango, porque este se calcula con los puntos acumulados.

Los puntos se recalculan al revertir una transacción.

## Estructuras de datos y dónde se usan

| Estructura | Uso |
|---|---|
| Lista enlazada | Historial de transacciones, monederos de la cuenta |
| Pila | Transacciones reversibles |
| Cola | Recorrido en anchura del grafo |
| Cola de prioridad (heap) | Transacciones programadas por fecha |
| Árbol AVL | Ranking y rangos de clientes por puntos |
| Árbol binario de búsqueda | Puntos acumulados por cliente |
| Grafo dirigido | Categorías de gasto, transferencias entre clientes, flujo entre monederos |
| Tabla hash | Clientes por documento y por cuenta, índices del programador |
| Lista circular | Notificaciones del cliente |

## Estructura del proyecto


src/main/java/co/edu/uniquindio/monedero/
├── App.java, Launcher.java      # arranque JavaFX
├── estructuras/                 # estructuras de datos propias
├── modelo/                      # Cliente, Cuenta, Monedero, transacciones, puntos
├── servicio/                    # lógica de negocio (Servicios es el punto de acceso)
├── algoritmos/                  # puntos, rangos, ordenamiento, verificación recursiva
├── repositorio/                 # almacén en memoria de clientes
├── controlador/                 # controladores JavaFX (cliente/ y admin/)
├── excepciones/
└── util/                        # Constantes, Sesion, DatosPrueba

src/main/resources/
├── vistas/                      # FXML (login, cliente/, admin/)
└── estilos/estilos.css


## Equipo

- Andrés Felipe Gutiérrez Vera
- Sofía Cabezas Mejía
