-- Agregar columna id_usuario a la tabla sensores si no existe
ALTER TABLE sensores 
ADD COLUMN IF NOT EXISTS id_usuario INT NULL,
ADD CONSTRAINT fk_sensores_usuario 
FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) 
ON DELETE SET NULL;

-- Opcional: Ver la estructura actualizada
-- DESCRIBE sensores;
