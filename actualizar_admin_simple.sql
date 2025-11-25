-- SOLUCIÓN SIMPLE: Actualizar usuario existente con contraseña en texto plano
-- El login.php acepta contraseñas en texto plano para compatibilidad

UPDATE usuarios 
SET password_hash = 'Admin123#',
    otros_datos = 'Rol: ADMIN, Teléfono: +56 9 31047253',
    id_departamento = 1,
    estado = 'ACTIVO'
WHERE email = 'dilannavid@gmail.com';

-- Verificar el usuario
SELECT id_usuario, nombre, email, password_hash, estado, otros_datos, id_departamento 
FROM usuarios 
WHERE email = 'dilannavid@gmail.com';
