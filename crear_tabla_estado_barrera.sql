-- Modificar tabla barrera existente para agregar campo procesado
ALTER TABLE barrera 
ADD COLUMN IF NOT EXISTS procesado TINYINT DEFAULT 0,
ADD COLUMN IF NOT EXISTS id_usuario INT NULL,
ADD COLUMN IF NOT EXISTS ultima_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Asegurarse de que exista registro para departamento 1
INSERT INTO barrera (id_departamento, estado, procesado) 
VALUES (1, 'CERRADA', 1)
ON DUPLICATE KEY UPDATE procesado = 1;

-- Si no hay registros, inicializar
UPDATE barrera SET procesado = 1 WHERE procesado IS NULL;
