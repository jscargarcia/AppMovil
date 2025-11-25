<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $id_usuario = $_POST['id_usuario'] ?? '';
    $nombre = $_POST['nombre'] ?? '';
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';
    $rol = $_POST['rol'] ?? 'OPERADOR';
    
    if (empty($id_usuario) || empty($nombre) || empty($email)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Faltan parámetros obligatorios']);
        exit;
    }
    
    // Verificar si el email ya existe en otro usuario
    $stmt = $conn->prepare("SELECT id_usuario FROM usuarios WHERE email = :email AND id_usuario != :id_usuario");
    $stmt->execute([':email' => $email, ':id_usuario' => $id_usuario]);
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(['estado' => 0, 'mensaje' => 'El email ya está registrado en otro usuario']);
        exit;
    }
    
    // Construir la actualización
    $otros_datos = "Rol: $rol";
    
    if (!empty($password)) {
        // Si se proporciona contraseña, actualizarla también
        $password_hash = password_hash($password, PASSWORD_BCRYPT);
        $stmt = $conn->prepare("UPDATE usuarios SET nombre = :nombre, email = :email, password_hash = :password, otros_datos = :otros_datos WHERE id_usuario = :id_usuario");
        $stmt->execute([
            ':nombre' => $nombre,
            ':email' => $email,
            ':password' => $password_hash,
            ':otros_datos' => $otros_datos,
            ':id_usuario' => $id_usuario
        ]);
    } else {
        // Sin actualizar contraseña
        $stmt = $conn->prepare("UPDATE usuarios SET nombre = :nombre, email = :email, otros_datos = :otros_datos WHERE id_usuario = :id_usuario");
        $stmt->execute([
            ':nombre' => $nombre,
            ':email' => $email,
            ':otros_datos' => $otros_datos,
            ':id_usuario' => $id_usuario
        ]);
    }
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(['estado' => 1, 'mensaje' => 'Usuario actualizado correctamente']);
    } else {
        echo json_encode(['estado' => 0, 'mensaje' => 'No se realizaron cambios']);
    }
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
