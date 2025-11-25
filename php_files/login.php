<?php
header('Content-Type: application/json');

// Conexión a base de datos
$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';
    
    if (empty($email) || empty($password)) {
        echo json_encode(['estado' => '0', 'mensaje' => 'Faltan parámetros']);
        exit;
    }
    
    // Buscar usuario por email
    $stmt = $conn->prepare("SELECT id_usuario, nombre, email, password_hash, estado, otros_datos, id_departamento FROM usuarios WHERE email = :email");
    $stmt->execute([':email' => $email]);
    $usuario = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$usuario) {
        echo json_encode(['estado' => '0', 'mensaje' => 'Usuario no encontrado']);
        exit;
    }
    
    // Verificar estado del usuario
    if ($usuario['estado'] != 'ACTIVO') {
        echo json_encode(['estado' => '0', 'mensaje' => 'Usuario inactivo o bloqueado']);
        exit;
    }
    
    // Verificar contraseña (soporta hash y texto plano para compatibilidad)
    $passwordValida = false;
    if (password_verify($password, $usuario['password_hash'])) {
        $passwordValida = true;
    } elseif ($password === $usuario['password_hash']) {
        $passwordValida = true;
    }
    
    if (!$passwordValida) {
        echo json_encode(['estado' => '0', 'mensaje' => 'Contraseña incorrecta']);
        exit;
    }
    
    // Extraer rol desde otros_datos
    $rol = 'OPERADOR'; // Por defecto
    if (!empty($usuario['otros_datos'])) {
        if (stripos($usuario['otros_datos'], 'ADMIN') !== false) {
            $rol = 'ADMIN';
        }
    }
    
    // Login exitoso
    echo json_encode([
        'estado' => '1',
        'mensaje' => 'Login exitoso',
        'id_usuario' => $usuario['id_usuario'],
        'nombre' => $usuario['nombre'],
        'email' => $usuario['email'],
        'rol' => $rol,
        'id_departamento' => $usuario['id_departamento']
    ]);
    
} catch(PDOException $e) {
    echo json_encode(['estado' => '0', 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
