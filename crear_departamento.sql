-- Crear departamento principal
INSERT INTO departamentos (numero, torre, otros_datos) 
VALUES (
    '101',
    'Torre A',
    'Departamento principal del sistema'
);

-- Verificar la inserción
SELECT * FROM departamentos;

-- IMPORTANTE: Verificar si usuarios y sensores tienen columna id_departamento
DESCRIBE usuarios;
DESCRIBE sensores;
DESCRIBE eventos_acceso;
