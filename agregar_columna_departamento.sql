-- Agregar columna id_departamento a la tabla usuarios
ALTER TABLE usuarios 
ADD COLUMN id_departamento INT(11) DEFAULT 1 AFTER otros_datos;

-- Agregar columna id_departamento a la tabla sensores
ALTER TABLE sensores 
ADD COLUMN id_departamento INT(11) DEFAULT 1 AFTER fecha_baja;

-- Agregar columna id_departamento a la tabla eventos_acceso
ALTER TABLE eventos_acceso 
ADD COLUMN id_departamento INT(11) DEFAULT 1 AFTER resultado;

-- Verificar los cambios
DESCRIBE usuarios;
DESCRIBE sensores;
DESCRIBE eventos_acceso;

-- Actualizar todos los registros existentes para que pertenezcan al departamento 1
UPDATE usuarios SET id_departamento = 1 WHERE id_departamento IS NULL;
UPDATE sensores SET id_departamento = 1 WHERE id_departamento IS NULL;
UPDATE eventos_acceso SET id_departamento = 1 WHERE id_departamento IS NULL;
