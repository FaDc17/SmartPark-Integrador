MODIFICA el prototipo actual de SmartPark realizando únicamente los siguientes cambios. La página actual está casi perfecta, así que NO rediseñes ni cambies significativamente la estructura, distribución, navbar, colores, tamaños, componentes ni estilo visual existente. Conserva todas las pantallas, funciones e interacciones actuales.

==================================================
1. MODIFICAR “DATOS Y ESTADÍSTICAS”
==================================================

Reemplaza los gráficos actuales por gráficos con DATOS FICTICIOS, ya que todavía no existe conexión con la base de datos. Deben representar exactamente estos 4 indicadores:

1. PORCENTAJE DE ESPACIOS DISPONIBLES

Crear gráfico circular/donut.

Capacidad total ficticia: 100 espacios
Libres: 32 (32%)
Ocupados: 68 (68%)

Mostrar en el centro:
“32% disponible”

Representa:
Espacios libres / Capacidad total × 100

Usar verde turquesa #1BBC9B para disponibles y un color contrastante armónico para ocupados.

2. TIEMPO PROMEDIO DE PERMANENCIA SEMANAL

Crear gráfico de barras comparando el promedio de permanencia por semana:

Semana 1: 1 h 20 min
Semana 2: 1 h 35 min
Semana 3: 1 h 15 min
Semana 4: 1 h 40 min

Destacar como KPI:
“1 h 40 min”

Representa:
Suma de tiempos de permanencia / Vehículos que finalizaron su estancia.

3. PORCENTAJE DE VEHÍCULOS ESTACIONADOS EN LA SEMANA

Crear gráfico de barras con vehículos atendidos por día:

Lunes: 65
Martes: 72
Miércoles: 80
Jueves: 76
Viernes: 92
Sábado: 95
Domingo: 70

Total semanal: 550 vehículos.

Capacidad ficticia: 100 espacios.
Capacidad teórica semanal: 700.

Mostrar:
“78.6% de capacidad semanal utilizada”

Representa:
Vehículos estacionados durante la semana / (Capacidad total × 7 días) × 100.

4. PORCENTAJE DE INGRESOS POR MES

Crear gráfico de barras o líneas con ingresos ficticios:

Ene S/4200
Feb S/4500
Mar S/4800
Abr S/5100
May S/5300
Jun S/5600
Jul S/5900
Ago S/6100
Sep S/5800
Oct S/6200
Nov S/6500
Dic S/7000

Al pasar el cursor o seleccionar un mes, mostrar el monto y su porcentaje respecto al total anual.

Representa:
Ingresos del mes / Ingresos totales del año × 100.

IMPORTANTE PARA ESTADÍSTICAS:
- Mostrar discretamente “Datos demostrativos” para indicar que todavía son ficticios.
- Mantener identidad SmartPark: #1BBC9B, #2D3E50, #FFFFFF y #F2F2F2.
- Se permiten colores secundarios armónicos en los gráficos.
- Cada gráfico debe tener título, valores, leyenda y tooltip.
- Organizar los 4 indicadores en tarjetas profesionales.
- Mantener diseño responsive.
- NO modificar otras secciones por estos cambios.

==================================================
2. AGREGAR TARJETA COMO MÉTODO DE PAGO
==================================================

En la página actual de Procesar pago ya existen Efectivo y Yape. NO rehagas esta página. Mantén su apariencia y solamente agrega una tercera opción:

- Efectivo
- Yape
- Tarjeta de crédito/débito

Los tres métodos deben utilizar el mismo estilo visual.

EFECTIVO:
Mantener el funcionamiento actual:
- Total a pagar
- Campo “Monto recibido”
- Si paga menos, mostrar “Saldo pendiente”
- Si paga más, mostrar “Vuelto”
- Botón “Procesar pago”

YAPE:
Mantener:
- QR ficticio
- Total a pagar
- Texto para escanear QR
- Botón “Procesar pago”

TARJETA:
Al seleccionarla, mostrar debajo:
- Total a pagar
- “Pago con tarjeta”
- Indicador visual “Esperando pago...”
- Botón “Confirmar pago”

Representar visualmente el uso de un POS/terminal de pago de manera sencilla.

NO solicitar ni almacenar números reales de tarjetas, CVV u otros datos bancarios. Es únicamente una simulación visual del prototipo.

==================================================
3. TICKET / COMPROBANTE DESPUÉS DEL PAGO
==================================================

Para CUALQUIERA de los tres métodos:

Efectivo / Yape / Tarjeta

Cuando el usuario presione “Procesar pago” o “Confirmar pago”, mostrar visualmente cómo se vería el ticket/comprobante final.

Usar la imagen referencial proporcionada como referencia visual si está disponible, pero adaptar el comprobante al estilo actual de SmartPark.

NO cambiar considerablemente la página actual.

El ticket debe ser pequeño, limpio y profesional, similar a un comprobante real de estacionamiento.

Debe contener:

SMARTPARK
Comprobante de estacionamiento

Código único: SP-2026-000001
Cliente: Carlos Mendoza
Placa: ABC-123
Tiempo estacionado: 01 h 25 min
Total: S/ 8.50
Método de pago: Efectivo / Yape / Tarjeta
Fecha: fecha del pago
Hora: hora del pago
Estado: PAGADO

“Gracias por utilizar SmartPark”

El código debe ser único para cada pago simulado, por ejemplo:
SP-2026-000001
SP-2026-000002
SP-2026-000003

Los datos del ticket deben cambiar según el cliente seleccionado desde la tabla de vehículos.

El método de pago debe mostrar automáticamente la opción que el usuario utilizó.

Agregar botones:
- “Imprimir comprobante”
- “Finalizar”

“Imprimir comprobante” debe representar visualmente la futura impresión del ticket.

“Finalizar” debe cerrar el proceso, regresar al Módulo y considerar al vehículo como retirado/pagado.

NO crear tres diseños diferentes de ticket. Utilizar el mismo componente y cambiar dinámicamente el método de pago y los datos.

==================================================
IMPORTANTE
==================================================

Conserva el prototipo actual de SmartPark. NO reconstruyas el proyecto desde cero.

NO modificar innecesariamente:
- Login
- Crear cuenta
- Inicio
- Registrar
- Módulo
- Navbar
- Footer
- Diseño general

Solamente:
1. Reemplazar los gráficos de Datos por los 4 indicadores especificados.
2. Agregar Tarjeta como tercer método de pago.
3. Agregar el ticket/comprobante visual después de procesar cualquier pago.

Mantener todas las interacciones que ya funcionan.

Mantener los colores principales:
#1BBC9B verde turquesa
#2D3E50 azul grisáceo oscuro
#FFFFFF blanco
#F2F2F2 gris claro

El resultado debe sentirse como una actualización del prototipo actual, NO como un nuevo diseño.