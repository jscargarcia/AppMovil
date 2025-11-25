C-- Insertar administrador Dilan Cortes
-- Primero, verificar que exista el departamento con id=1
-- Si no existe, créalo primero

-- Insertar el administrador
INSERT INTO usuarios (nombre, email, password_hash, estado, otros_datos, id_departamento) 
VALUES (
    'dilan cortes',
    'dilannavid@gmail.com',
    '$2y$10$YixHF5KkJNO6Mxh4E8VSOuqbY3Vr0P/gj5V5qKqO6c0KbGe8h3XGa',
    'ACTIVO',
    'Rol: ADMIN, Teléfono: +56 9 31047253',
    1
);

-- Verificar la inserción
SELECT * FROM usuarios WHERE email = 'dilannavid@gmail.com';
