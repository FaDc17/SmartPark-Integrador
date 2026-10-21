Diseña un prototipo interactivo de alta fidelidad para un SISTEMA WEB DE GESTIÓN DE ESTACIONAMIENTO llamado “SmartPark”.

IMPORTANTE:
- NO diseñar como aplicación móvil.
- Debe ser una página/sistema web pensado principalmente para escritorio.
- El diseño posteriormente será desarrollado de forma responsive/adaptable.
- Crear todas las pantallas necesarias y conectarlas mediante interacciones para poder navegar por el prototipo.
- Estilo moderno, profesional, limpio y sencillo.
- No sobrecargar la interfaz.
- Usar bordes ligeramente redondeados, sombras suaves, buena separación entre componentes e iconografía simple.
- Mantener consistencia visual entre todas las pantallas.

PALETA PRINCIPAL:
- Verde turquesa: #1BBC9B
- Azul grisáceo oscuro: #2D3E50
- Blanco: #FFFFFF
- Fondo gris claro: #F2F2F2

Usar #2D3E50 principalmente para navbar, títulos importantes y elementos oscuros.
Usar #1BBC9B como color de acción, botones principales, indicadores y elementos destacados.
Usar blanco y gris claro para fondos, tarjetas, formularios y tablas.
En la sección de estadísticas se pueden utilizar colores secundarios diferentes pero armónicos con esta paleta.

LOGO:
Crear un logo textual sencillo para “SmartPark”, relacionado visualmente con estacionamiento, vehículos o una letra P de parking. Debe combinar con los colores principales.

--------------------------------------------------
1. PANTALLA: INICIAR SESIÓN
--------------------------------------------------

Esta debe ser la primera pantalla del sistema.

En la parte superior colocar un navbar sencillo.
Como el usuario todavía no ha iniciado sesión, el navbar NO debe mostrar opciones de navegación.
Solamente debe aparecer el logo y nombre “SmartPark”.

En el centro de la pantalla crear una tarjeta/formulario de inicio de sesión.

Título:
“Iniciar sesión”

Campos:
- Correo
- Contraseña

Agregar iconos pequeños relacionados con cada campo si ayudan al diseño.

Botón principal:
“Iniciar sesión”

Botón secundario:
“Crear cuenta”

También puede aparecer el texto:
“¿No tienes una cuenta?”

INTERACCIONES:
- “Iniciar sesión” → Dashboard / Inicio.
- “Crear cuenta” → pantalla Crear cuenta.

--------------------------------------------------
2. PANTALLA: CREAR CUENTA
--------------------------------------------------

Mantener prácticamente el mismo estilo visual de Iniciar sesión.

Navbar únicamente con logo SmartPark.

Título:
“Crear cuenta”

Campos:
- Nombre
- Apellido
- Correo
- Contraseña

Botón principal:
“Completar registro”

Agregar una opción discreta:
“¿Ya tienes una cuenta? Iniciar sesión”

INTERACCIONES:
- “Completar registro” → Iniciar sesión.
- “Iniciar sesión” → pantalla Iniciar sesión.

--------------------------------------------------
3. DASHBOARD / PÁGINA PRINCIPAL
--------------------------------------------------

Después de iniciar sesión mostrar el Dashboard principal.

NAVBAR:
Logo “SmartPark” a la izquierda.

Opciones:
- Inicio
- Registrar
- Módulo
- Datos
- Salir

Resaltar visualmente la pestaña actualmente seleccionada.

En la página principal colocar un banner horizontal moderno que ocupe prácticamente todo el ancho disponible.

Puede incluir:
“Bienvenido a SmartPark”
“Gestión rápida y eficiente de tu estacionamiento”

Utilizar elementos gráficos relacionados con estacionamientos o vehículos, pero sin sobrecargar el diseño.

Debajo colocar dos tarjetas de indicadores:

TARJETA 1:
Título: “Ocupados”
Valor: 0

TARJETA 2:
Título: “Libres”
Valor: 100

Los números deben ser grandes y fáciles de identificar.

Agregar una sección:

“TARIFA DE ESTACIONAMIENTO”

Mostrar la información mediante una tabla o tarjeta informativa:

Tarifa por hora: S/ 6.00
Minuto adicional: S/ 0.10

Agregar una pequeña explicación:
“Una vez superado el tiempo pagado, se cobrarán S/ 0.10 por cada minuto adicional.”

Al final de la página colocar un footer sencillo:
“© 2026 SmartPark. Todos los derechos reservados.”

--------------------------------------------------
4. PANTALLA: REGISTRAR VEHÍCULO
--------------------------------------------------

Se accede mediante:
Navbar → Registrar

Usar un título más profesional:
“Registro de ingreso”

Subtítulo:
“Registra el ingreso de un vehículo al estacionamiento.”

Crear un formulario sencillo.

Campos:
- Nombre del cliente
- Placa del vehículo

Botón:
“Agregar”

Puede incluir un pequeño icono de automóvil.

Al presionar “Agregar”, simular visualmente una confirmación:
“Vehículo registrado correctamente.”

Mantener siempre visible el navbar.

--------------------------------------------------
5. PANTALLA: VEHÍCULOS EN ESTACIONAMIENTO
--------------------------------------------------

Se accede mediante:
Navbar → Módulo

Título:
“Vehículos actualmente estacionados”

Subtítulo:
“Consulta y administra los vehículos que se encuentran actualmente dentro del estacionamiento.”

En la parte superior agregar un buscador.

Placeholder:
“Buscar por cliente, placa o ID...”

Debajo crear una tabla.

COLUMNAS:
- ID
- Cliente
- Placa
- Tiempo
- Costo
- Acciones

Agregar varios registros ficticios para visualizar correctamente el diseño.

Ejemplo:
001 | Carlos Mendoza | ABC-123 | 01:25 h | S/ 8.50
002 | María Torres | XYZ-456 | 00:45 h | S/ 6.00
003 | José Ramos | BCD-789 | 02:10 h | S/ 13.00

En “Acciones” colocar:

Botón:
“Modificar”

Botón:
“Cobrar”

INTERACCIÓN:
- “Cobrar” → pantalla Procesar pago.
- “Modificar” puede abrir un pequeño modal para modificar Nombre y Placa.

--------------------------------------------------
6. PANTALLA: PROCESAR PAGO
--------------------------------------------------

Crear una pantalla profesional para realizar el cobro.

Título:
“Procesar pago”

Mostrar primero un resumen del estacionamiento mediante una tarjeta:

Cliente: Carlos Mendoza
Placa: ABC-123
Tiempo estacionado: 01:25 h
Total a pagar: S/ 8.50

Debajo colocar:

“Método de pago”

Dos botones/tarjetas seleccionables:

- Efectivo
- Yape

INTERACCIÓN EFECTIVO:

Cuando el usuario seleccione “Efectivo”, mostrar dinámicamente debajo:

Total:
S/ 8.50

Campo:
“Monto recibido”

Ejemplo:
S/ 10.00

Calcular y mostrar:
“Vuelto: S/ 1.50”

Si el monto ingresado es inferior al total, mostrar:
“Saldo pendiente: S/ X.XX”

Agregar botón:
“Procesar pago”

INTERACCIÓN YAPE:

Cuando el usuario seleccione “Yape”, mostrar debajo un código QR ficticio grande y centrado.

Texto:
“Escanea el código QR para realizar el pago.”

Mostrar:
“Total: S/ 8.50”

Agregar botón:
“Procesar pago”

Después de procesar cualquiera de los métodos, mostrar una confirmación visual:
“Pago realizado correctamente.”

La pantalla puede tener apariencia de comprobante/boleta digital, pero mantener el diseño general de SmartPark.

--------------------------------------------------
7. PANTALLA: DATOS Y ESTADÍSTICAS
--------------------------------------------------

Se accede mediante:
Navbar → Datos

Título:
“Datos y estadísticas”

Subtítulo:
“Resumen del rendimiento y actividad del estacionamiento.”

Diseñar un dashboard de analítica moderno.

Los datos serán ficticios por ahora.

Crear tarjetas superiores con KPIs:

- Ingresos del día: S/ 540.00
- Vehículos atendidos: 82
- Tiempo promedio: 1 h 32 min
- Ocupación promedio: 68%

Debajo agregar distintos gráficos.

GRÁFICO DE BARRAS:
“Ingresos por día”

Mostrar datos ficticios de lunes a domingo.

GRÁFICO CIRCULAR:
“Estado de estacionamientos”

Ejemplo:
- Ocupados: 68%
- Libres: 32%

OTRO GRÁFICO:
“Vehículos por hora”

Mostrar las horas con mayor cantidad de vehículos.

Agregar otra tarjeta o gráfico:
“Promedio de venta”

Los gráficos pueden utilizar colores adicionales como azul, amarillo, naranja y tonos verdes, siempre manteniendo armonía con:
#1BBC9B
#2D3E50

No utilizar demasiados colores.

--------------------------------------------------
8. SALIR
--------------------------------------------------

El botón “Salir” del navbar NO necesita una pantalla independiente.

INTERACCIÓN:
Navbar → Salir → Iniciar sesión

Debe cerrar visualmente la sesión y regresar inmediatamente a la primera pantalla.

--------------------------------------------------
NAVEGACIÓN DEL PROTOTIPO
--------------------------------------------------

Configurar el prototipo interactivo de manera que se pueda demostrar el flujo completo:

Iniciar sesión
→ Dashboard

Crear cuenta
→ Registro
→ Iniciar sesión

Dashboard
→ Registrar
→ Módulo
→ Datos

Módulo
→ Cobrar
→ Procesar pago

Procesar pago
→ seleccionar Efectivo
→ mostrar campos de efectivo

Procesar pago
→ seleccionar Yape
→ mostrar QR

Salir
→ Iniciar sesión

El navbar debe permanecer visualmente consistente en Dashboard, Registrar, Módulo, Procesar pago y Datos.

--------------------------------------------------
ESTILO GENERAL
--------------------------------------------------

Diseñar para escritorio aproximadamente en 1440 px de ancho.

Debe parecer un sistema administrativo web real que posteriormente pueda implementarse utilizando HTML, CSS, JavaScript y una base de datos.

Priorizar:
- facilidad de uso
- buena jerarquía visual
- formularios claros
- tablas legibles
- navegación sencilla
- dashboard moderno
- componentes reutilizables
- consistencia visual

Evitar:
- apariencia de aplicación móvil
- diseños excesivamente futuristas
- degradados exagerados
- exceso de animaciones
- demasiados colores
- elementos decorativos innecesarios

El resultado debe verse como un sistema de gestión de estacionamiento moderno, académico pero suficientemente profesional para ser presentado como prototipo de un proyecto de software.