MODIFICA EL PROTOTIPO ACTUAL DE SMARTPARK.

IMPORTANTE:
NO crear una página nueva desde cero.
NO rediseñar la interfaz.
NO cambiar la paleta de colores, navbar, banner, estilo de tarjetas ni estructura general.
Mantener el diseño actual de SmartPark y corregir únicamente el funcionamiento, coherencia de datos y algunos textos indicados a continuación.

Mantener la paleta:
#1BBC9B
#2D3E50
#FFFFFF
#F2F2F2

==================================================
1. INICIAR SESIÓN
==================================================

Mantener visualmente la pantalla actual.

Por ahora NO implementar validación real de correo y contraseña.

El botón “Iniciar sesión” debe permitir acceder a la página principal incluso si los campos están vacíos.

Esto es intencional porque todavía estamos trabajando con un prototipo.

==================================================
2. CREAR CUENTA
==================================================

Mantener la pantalla actual con:

- Nombre
- Apellido
- Correo
- Contraseña
- Botón “Completar registro”

En esta pantalla sí deben completarse los campos para simular el registro.

Al presionar “Completar registro”:
- Mostrar confirmación de registro exitoso.
- Permitir regresar a Iniciar sesión.
- El usuario podrá posteriormente utilizar visualmente el correo y contraseña registrados.

No implementar todavía autenticación ni base de datos real.

==================================================
3. PÁGINA PRINCIPAL / DASHBOARD
==================================================

Mantener el banner actual y el diseño general.

Corregir la coherencia entre:

- Espacios ocupados
- Espacios libres
- Vehículos existentes en Módulo

Si en Módulo existen vehículos actualmente estacionados, el Dashboard NO puede mostrar 0 ocupados y 100 libres.

Ejemplo:
Si la capacidad es de 100 espacios y existen 5 vehículos en Módulo:

Ocupados: 5
Libres: 95

Cuando se agregue un vehículo desde Registrar:
Ocupados aumenta en 1.
Libres disminuye en 1.

Cuando un vehículo sea retirado después del cobro:
Ocupados disminuye en 1.
Libres aumenta en 1.

Los valores deben mantenerse visualmente relacionados con la información del Módulo.

==================================================
4. TARIFA
==================================================

En la página principal mantener la sección de tarifa del estacionamiento.

Mostrar:

Tarifa por hora:
S/ 6.00

Tarifa por minuto:
S/ 0.10

ELIMINAR completamente el mensaje:

“Una vez superado el tiempo pagado se cobrarán S/0.10 por cada minuto adicional”

Ya NO existe el concepto de “minuto adicional”.

La tarifa funciona directamente por tiempo utilizado.

La equivalencia es:

60 minutos = S/6.00
1 minuto = S/0.10

Por lo tanto:

15 minutos = S/1.50
30 minutos = S/3.00
45 minutos = S/4.50
60 minutos = S/6.00
75 minutos = S/7.50
90 minutos = S/9.00
120 minutos = S/12.00

FÓRMULA:

Costo = cantidad total de minutos × S/0.10

Aplicar esta misma lógica en TODO SmartPark.

IMPORTANTE:
Actualmente existe un registro de aproximadamente 45 minutos que muestra S/6.00. Esto es incorrecto.

45 minutos deben equivaler a:

45 × S/0.10 = S/4.50

Corregir cualquier registro ficticio que no respete esta relación.

==================================================
5. REGISTRAR – REGISTRO DE INGRESO
==================================================

Mantener el diseño actual.

Mantener únicamente los campos:

- Nombre del cliente
- Placa del vehículo

Mantener botón:

“Agregar”

Ahora el botón Agregar DEBE funcionar.

Ejemplo:

Nombre:
Brando Jesús

Placa:
ABC-134

Al presionar “Agregar”:

1. Validar que ambos campos tengan información.
2. Crear un nuevo registro.
3. Asignarle automáticamente un ID.
4. Generar una hora de ingreso ficticia/random coherente.
5. Agregar inmediatamente ese vehículo a la tabla de “Vehículos actualmente estacionados” del Módulo.
6. Mostrar mensaje:
“Vehículo registrado correctamente”.
7. Actualizar los contadores de ocupados y libres del Dashboard.

NO solicitar previamente cuánto tiempo permanecerá el vehículo.

El tiempo se calculará/simulará como tiempo de estancia.

==================================================
6. MÓDULO – VEHÍCULOS ACTUALMENTE ESTACIONADOS
==================================================

Mantener:
- Título actual.
- Descripción actual.
- Buscador.
- Diseño general de la tabla.

El buscador debe seguir funcionando y permitir localizar registros por cliente o placa.

Modificar las columnas de la tabla para que sean:

ID
Cliente
Placa
Hora de ingreso
Tiempo de estancia
Costo
Acciones

“HORA DE INGRESO”:
Mostrar una hora ficticia/random para cada vehículo.

Ejemplo:
08:35
10:20
13:45

“TIEMPO DE ESTANCIA”:
Representa cuánto tiempo lleva actualmente el vehículo estacionado.

Ejemplos:
25 min
45 min
1 h 15 min
1 h 30 min
2 h

Los datos pueden continuar siendo simulados porque se trata de un prototipo.

Sin embargo, TIEMPO y COSTO siempre deben ser matemáticamente coherentes.

Ejemplos:

25 min → S/2.50
45 min → S/4.50
1 h → S/6.00
1 h 15 min → S/7.50
1 h 30 min → S/9.00
2 h → S/12.00

==================================================
7. MODIFICAR VEHÍCULO
==================================================

Mantener el botón actual “Modificar”.

Actualmente el modal permite editar Nombre y Placa, pero aunque aparece:

“Datos actualizados correctamente”

la tabla no refleja realmente el cambio.

CORREGIR ESTE ERROR.

Ejemplo:

Registro actual:
Carlos Mendoza
ABC-123

El trabajador selecciona “Modificar”.

Cambia:
Carlos Mendoza

por:
Carlos Rodríguez

Presiona “Guardar”.

La tabla debe cambiar inmediatamente y mostrar:

Carlos Rodríguez

Lo mismo debe funcionar para la placa.

El mensaje:
“Datos actualizados correctamente”

solo debe mostrarse después de que la modificación se haya aplicado realmente en la tabla.

==================================================
8. COBRAR
==================================================

Mantener el botón “Cobrar” en Acciones.

Al presionarlo, abrir la pantalla actual de “Procesar pago”.

Mantener el resumen:

- Cliente
- Placa
- Tiempo estacionado
- Total a pagar

El total debe provenir del mismo registro seleccionado y respetar:

Total = minutos estacionado × S/0.10

Ejemplo:

Cliente: María Torres
Tiempo: 45 minutos

TOTAL:
S/4.50

NO S/6.00.

Mantener los tres métodos de pago actuales:

- Efectivo
- Yape
- Tarjeta

Mantener el funcionamiento actual de monto recibido y vuelto para efectivo.

Yape y Tarjeta siguen siendo simulaciones visuales.

==================================================
9. PROCESAR PAGO Y BOLETA
==================================================

Mantener el diseño actual de la boleta/comprobante.

Después de procesar el pago mostrar una vista previa de la boleta con los datos correspondientes:

- Código
- Cliente
- Placa
- Tiempo estacionado
- Total
- Método de pago
- Fecha/hora
- Estado PAGADO

Actualmente aparecen dos botones separados:

“Imprimir comprobante”
“Finalizar”

CAMBIAR este comportamiento.

Debe existir solamente un botón principal:

“Finalizar e imprimir”

Al presionarlo:

1. Simular la impresión de la boleta.
2. Mostrar la boleta/comprobante final.
3. Finalizar el proceso de pago.
4. Considerar que el vehículo está saliendo del estacionamiento.
5. Eliminar ese vehículo de “Vehículos actualmente estacionados”.
6. Actualizar espacios ocupados y libres.
7. Permitir regresar al Módulo.

No implementar impresión física real. Solo simular la acción dentro del prototipo.

==================================================
10. DATOS Y ESTADÍSTICAS
==================================================

Mantener el diseño actual y los gráficos existentes, pero hacer más explícito qué periodo representa cada indicador.

Todos los valores pueden continuar siendo DATOS DEMOSTRATIVOS/FICTICIOS.

Mostrar discretamente:
“Datos demostrativos”

INDICADOR 1:
PORCENTAJE DE ESPACIOS DISPONIBLES EN TIEMPO REAL

Representa los espacios libres respecto a la capacidad total actual.

Fórmula:

Espacios libres / Capacidad total × 100

Ejemplo:
Capacidad: 100
Libres: 32
Ocupados: 68

Disponibilidad: 32%

Debe quedar explícitamente indicado:
“Disponibilidad actual” o “En tiempo real”.

Este indicador NO es semanal.

Idealmente mantener un gráfico circular/donut mostrando libres y ocupados.

==================================================
INDICADOR 2:
TIEMPO PROMEDIO DE PERMANENCIA SEMANAL

Debe quedar explícitamente indicado que corresponde a la SEMANA.

Representa el promedio del tiempo que permanecieron estacionados los vehículos durante cada semana.

Ejemplo de datos ficticios:

Semana 1: 1 h 20 min
Semana 2: 1 h 35 min
Semana 3: 1 h 15 min
Semana 4: 1 h 40 min

Mostrar mediante gráfico de barras.

KPI principal:
1 h 40 min

Agregar texto:
“Promedio semanal de permanencia”.

==================================================
INDICADOR 3:
PORCENTAJE DE VEHÍCULOS ESTACIONADOS EN LA SEMANA /
USO DE CAPACIDAD SEMANAL

Debe quedar explícitamente indicado que corresponde a la SEMANA.

Representa la cantidad de vehículos atendidos durante la semana respecto a la capacidad teórica semanal.

Fórmula:

Vehículos estacionados durante la semana /
(Capacidad total × días de la semana)
× 100

Ejemplo ficticio:

Lunes: 65
Martes: 72
Miércoles: 80
Jueves: 76
Viernes: 92
Sábado: 95
Domingo: 70

Total semanal: 550 vehículos.

Si existen 100 espacios:

Capacidad semanal teórica:
100 × 7 = 700

Uso:
550 / 700 × 100 = 78.6%

Mostrar:
“78.6% de capacidad semanal utilizada”

Utilizar gráfico de barras por día.

==================================================
INDICADOR 4:
INGRESOS DEL MES

Cambiar “Ingresos totales anuales” por un indicador claramente MENSUAL.

Ejemplo:

“Ingresos del mes”
S/5,800

Mostrar también un gráfico que permita comparar los ingresos de diferentes meses.

Ejemplo ficticio:

Enero: S/4,200
Febrero: S/4,500
Marzo: S/4,800
Abril: S/5,100
Mayo: S/5,300
Junio: S/5,600
Julio: S/5,900
Agosto: S/6,100
Septiembre: S/5,800

Mantener estos datos como demostrativos.

El objetivo es poder visualizar la evolución mensual de los ingresos.

==================================================
11. COHERENCIA GENERAL
==================================================

Todo el prototipo debe mantener coherencia entre sus pantallas.

Ejemplo de flujo:

INICIAR SESIÓN
→ permite entrar provisionalmente sin validar credenciales.

REGISTRAR
→ Brando Jesús
→ ABC-134
→ Agregar

MÓDULO
→ aparece Brando Jesús / ABC-134
→ se genera hora de ingreso ficticia
→ muestra tiempo de estancia
→ calcula costo correctamente.

MODIFICAR
→ los cambios realmente aparecen en la tabla.

COBRAR
→ toma los datos de ESA MISMA FILA
→ calcula el total según S/0.10 por minuto
→ procesa pago
→ muestra boleta.

FINALIZAR E IMPRIMIR
→ finaliza el pago
→ simula impresión
→ vehículo sale
→ desaparece del Módulo
→ se actualizan espacios libres y ocupados.

NO agregar el sistema de tiempo contratado, tiempo excedido ni cobro adicional que se planteó anteriormente.

Volver al funcionamiento SIMPLE originalmente planteado:
el vehículo ingresa, permanece estacionado, el costo depende del tiempo total utilizado y se cobra al momento de salir.

NO REDISEÑAR SMARTPARK.
Solo corregir y conectar correctamente las funcionalidades indicadas.