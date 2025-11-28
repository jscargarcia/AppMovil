# Resumen: Asignación de Usuario a Sensores

## 📝 Cambios Realizados

Se implementó la funcionalidad para asignar un usuario específico a cada sensor al momento de crearlo.

---

## 🗄️ Cambios en Base de Datos

### Script SQL: `agregar_usuario_a_sensores.sql`

```sql
ALTER TABLE sensores 
ADD COLUMN IF NOT EXISTS id_usuario INT NULL,
ADD CONSTRAINT fk_sensores_usuario 
FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) 
ON DELETE SET NULL;
```

**Ejecutar en servidor:**
```bash
mysql -u root -p'Admin12345#' MCU-app < agregar_usuario_a_sensores.sql
```

---

## 🔧 Archivos PHP Modificados

### 1. `registrarSensor.php`
- ✅ Ahora recibe parámetro `id_usuario`
- ✅ Inserta el sensor con el usuario asignado
- ✅ Valida todos los campos requeridos

### 2. `listarSensores.php`
- ✅ Hace JOIN con tabla `usuarios`
- ✅ Devuelve campo `nombre_usuario` en el JSON
- ✅ Muestra "NULL" si no hay usuario asignado

---

## 📱 Archivos Android Modificados

### 1. **Layout: `activity_registrar_sensor.xml`**
- ✅ Agregado `Spinner` para seleccionar usuario
- ✅ Agregado `TextView` con título "Asignar a usuario:"

### 2. **Activity: `RegistrarSensor.kt`**
- ✅ Agregada función `cargarUsuarios()` que llama a `listarUsuarios.php`
- ✅ Carga solo usuarios ACTIVOS del departamento
- ✅ El usuario seleccionado se envía en el POST a `registrarSensor.php`
- ✅ Validación: No permite registrar sensor sin seleccionar usuario

### 3. **Data Class: `Sensor.kt`**
- ✅ Agregado campo opcional `nombre_usuario: String?`

### 4. **Layout: `item_sensor.xml`**
- ✅ Agregado `TextView` con id `txtUsuario` para mostrar el usuario asignado

### 5. **Adaptador: `SensorAdapter.kt`**
- ✅ Muestra el nombre del usuario asignado en cada item
- ✅ Si no hay usuario, muestra "Sin asignar"

### 6. **Activity: `GestionSensores.kt`**
- ✅ Actualizado para leer el campo `nombre_usuario` del JSON

### 7. **Activity: `HomeAdminActivity.kt`**
- ✅ Spinner de sensores muestra también el nombre del usuario
- ✅ Formato: "ID - CÓDIGO (ESTADO) - Usuario: NOMBRE"

---

## 🎯 Flujo de Uso

1. **Admin accede a "Gestión de Sensores"**
2. **Presiona "Agregar Nuevo Sensor"**
3. **Ingresa:**
   - Código del sensor (UID del RFID)
   - Tipo (LLAVERO o TARJETA)
   - **Selecciona usuario del Spinner** ⬅️ NUEVO
4. **Presiona "REGISTRAR SENSOR"**
5. **Sistema guarda sensor con usuario asignado**

---

## 📊 Vista de Sensores

Ahora los sensores se muestran con esta información:

```
Código: A1B2C3D4
Tipo: LLAVERO
Estado: ACTIVO
Usuario: Juan Pérez         ⬅️ NUEVO
[Botones de control]
```

---

## 🔍 Validaciones Implementadas

- ✅ Solo usuarios ACTIVOS aparecen en el Spinner
- ✅ Solo sensores del mismo departamento
- ✅ No permite crear sensor sin seleccionar usuario
- ✅ Si un usuario se elimina de la BD, el sensor queda con `id_usuario = NULL`

---

## 🚀 Para Aplicar los Cambios

### Paso 1: Base de Datos
```bash
ssh ec2-user@35.168.148.150
cd /var/www/html
mysql -u root -p'Admin12345#' MCU-app
```

```sql
ALTER TABLE sensores ADD COLUMN id_usuario INT NULL;
ALTER TABLE sensores ADD CONSTRAINT fk_sensores_usuario 
FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE SET NULL;
```

### Paso 2: Archivos PHP
Subir al servidor:
- `registrarSensor.php`
- `listarSensores.php`

### Paso 3: Recompilar App Android
Abrir proyecto en Android Studio y hacer Build > Rebuild Project

---

## ✅ Verificación

Para verificar que funciona:

1. **Probar API desde navegador:**
```
http://35.168.148.150/listarSensores.php?id_departamento=1
```

Debe devolver JSON con campo `nombre_usuario`:
```json
[
  {
    "id_sensor": "1",
    "codigo_sensor": "A1B2C3D4",
    "tipo": "LLAVERO",
    "estado": "ACTIVO",
    "id_usuario": "5",
    "nombre_usuario": "Juan Pérez"
  }
]
```

2. **En la app:** Crear un sensor nuevo y verificar que se muestre el usuario

---

## 📝 Notas Adicionales

- Los sensores antiguos (sin usuario asignado) mostrarán "Sin asignar"
- Se puede modificar para permitir cambiar el usuario después de crear el sensor
- El sistema sigue funcionando si un sensor no tiene usuario asignado
