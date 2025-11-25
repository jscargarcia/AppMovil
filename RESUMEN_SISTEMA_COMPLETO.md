# Sistema IoT Control de Acceso - Resumen Completo

## 📁 Archivos PHP (Carpeta: php_files/)

### 1. conexion.php
- Configuración de conexión PDO a MySQL
- Host: localhost, DB: iot_acceso, User: root

### 2. login.php
- Autentica usuarios por email/password
- Devuelve: id_usuario, nombre, email, rol, id_departamento
- Extrae rol desde campo otros_datos
- Soporta contraseñas hasheadas y texto plano

### 3. registrarUsuario.php
- Registra nuevos usuarios
- Parámetros: nombre, email, password, telefono, rol, estado, id_departamento
- Hashea contraseña con bcrypt
- Valida email único

### 4. listarUsuarios.php
- Lista usuarios filtrados por id_departamento
- Devuelve: id_usuario, nombre, email, estado, otros_datos, id_departamento

### 5. cambiarEstadoUsuario.php
- Actualiza estado de usuario (ACTIVO/INACTIVO/BLOQUEADO)
- Parámetros: id_usuario, estado

### 6. registrarSensor.php
- Registra nuevos sensores (LLAVERO/TARJETA)
- Parámetros: codigo, tipo, estado, id_departamento
- Valida código único

### 7. listarSensores.php
- Lista sensores filtrados por id_departamento
- Devuelve: id_sensor, codigo_sensor, tipo, estado, fecha_alta, fecha_baja, id_departamento

### 8. actualizarEstadoSensor.php
- Actualiza estado de sensor (ACTIVO/INACTIVO/PERDIDO/BLOQUEADO)
- Parámetros: id_sensor, estado

### 9. listarEventos.php
- Lista eventos filtrados por id_departamento
- Límite: 100 eventos más recientes
- Devuelve: id_evento, id_sensor, id_usuario, tipo_evento, resultado, fecha_hora, id_departamento

### 10. barrera.php
- Controla apertura/cierre de barrera
- Registra eventos de tipo APERTURA/CIERRE
- Parámetros: accion, id_usuario, id_departamento

### 11. estadoBarrera.php
- Consulta estado actual de barrera
- Filtra por id_departamento
- Devuelve: estado (ABIERTA/CERRADA)

---

## 📱 Archivos Kotlin (Android)

### Archivos Actualizados:
1. ✅ MainActivity.kt - Login con id_departamento
2. ✅ HomeAdminActivity.kt - Panel admin completo
3. ✅ HomeOperadorActivity.kt - Panel operador
4. ✅ EstadoBarrera.kt - Monitoreo en tiempo real
5. ✅ HistorialAccesos.kt - Listado de eventos
6. ✅ RegistrarSensor.kt - Registro de sensores
7. ✅ GestionSensores.kt - Gestión completa
8. ✅ GestionUsuarios.kt - Gestión de usuarios
9. ✅ SensorAdapter.kt - Adaptador con 4 estados

### Características Implementadas:
- ✅ Gestión de departamentos
- ✅ Control de barrera (ABRIR/CERRAR)
- ✅ Estados de sensores: ACTIVO, INACTIVO, PERDIDO, BLOQUEADO
- ✅ Roles: ADMIN, OPERADOR
- ✅ Historial de eventos con filtros
- ✅ Validación de permisos por rol
- ✅ Actualización automática de estado de barrera (cada 5 segundos)
- ✅ Timeout extendido a 30 segundos
- ✅ Manejo de errores con mensajes detallados

---

## 🗄️ Base de Datos MySQL

### Tablas:
1. **departamentos**
   - id_departamento (PK, AUTO_INCREMENT)
   - numero VARCHAR(20)
   - torre VARCHAR(50)
   - otros_datos TEXT

2. **usuarios**
   - id_usuario (PK, AUTO_INCREMENT)
   - nombre VARCHAR(100)
   - email VARCHAR(150) UNIQUE
   - password_hash VARCHAR(255)
   - estado ENUM('ACTIVO', 'INACTIVO', 'BLOQUEADO')
   - otros_datos TEXT (contiene rol y teléfono)
   - id_departamento INT(11)

3. **sensores**
   - id_sensor (PK, AUTO_INCREMENT)
   - codigo_sensor VARCHAR(100)
   - tipo ENUM('LLAVERO', 'TARJETA')
   - estado ENUM('ACTIVO', 'INACTIVO', 'PERDIDO', 'BLOQUEADO')
   - fecha_alta DATETIME
   - fecha_baja DATETIME
   - id_departamento INT(11)

4. **eventos_acceso**
   - id_evento (PK, AUTO_INCREMENT)
   - id_sensor INT(11)
   - id_usuario INT(11)
   - tipo_evento ENUM('ACCESO_VALIDO', 'ACCESO_RECHAZADO', 'APERTURA', 'CIERRE')
   - resultado ENUM('PERMITIDO', 'DENEGADO')
   - fecha_hora DATETIME
   - id_departamento INT(11)

---

## 📋 Scripts SQL

### 1. crear_departamento.sql
- Crea departamento principal (ID=1, Torre A, número 101)

### 2. agregar_columna_departamento.sql
- Agrega columna id_departamento a usuarios, sensores, eventos_acceso
- Valor por defecto: 1

### 3. insertar_admin.sql
- Crea usuario administrador
- Email: dilannavid@gmail.com
- Password: Admin123#
- Departamento: 1

---

## 🌐 Configuración

### Servidor:
- IP: 35.168.148.150
- Todas las URLs en app apuntan a: http://35.168.148.150/

### Base de Datos Local:
- Host: localhost
- Usuario: root
- Contraseña: (vacía)
- Base de datos: iot_acceso

---

## ✅ Checklist de Implementación

1. [ ] Ejecutar crear_departamento.sql
2. [ ] Ejecutar agregar_columna_departamento.sql
3. [ ] Ejecutar insertar_admin.sql
4. [ ] Subir todos los archivos PHP a servidor
5. [ ] Compilar aplicación Android
6. [ ] Probar login con admin
7. [ ] Registrar sensores de prueba
8. [ ] Probar control de barrera
9. [ ] Verificar historial de eventos

---

## 🔐 Credenciales

**Administrador:**
- Email: dilannavid@gmail.com
- Password: Admin123#
- Rol: ADMIN
- Departamento: 1

---

Generado: 25 de noviembre de 2025
