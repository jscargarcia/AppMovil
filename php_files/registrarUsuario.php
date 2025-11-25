<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $nombre = $_POST['nombre'] ?? $_GET['nombre'] ?? '';
    $email = $_POST['email'] ?? $_GET['email'] ?? '';
    $password = $_POST['password'] ?? $_GET['password'] ?? '';
    $telefono = $_POST['telefono'] ?? $_GET['telefono'] ?? '';
    $rol = $_POST['rol'] ?? $_GET['rol'] ?? 'OPERADOR';
    $estado = $_POST['estado'] ?? $_GET['estado'] ?? 'ACTIVO';
    $id_departamento = $_POST['id_departamento'] ?? $_GET['id_departamento'] ?? '1';
    
    if (empty($nombre) || empty($email) || empty($password)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Faltan parámetros obligatorios (nombre, email, password)']);
        exit;
    }
    
    // Validar formato de email
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Email no válido']);
        exit;
    }
    
    // Verificar si el email ya existe
    $stmt = $conn->prepare("SELECT id_usuario FROM usuarios WHERE email = :email");
    $stmt->execute([':email' => $email]);
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(['estado' => 0, 'mensaje' => 'El email ya está registrado']);
        exit;
    }
    
    // Hashear la contraseña
    $password_hash = password_hash($password, PASSWORD_BCRYPT);
    
    // Crear campo otros_datos con rol y teléfono
    $otros_datos = "Rol: " . strtoupper($rol);
    if (!empty($telefono)) {
        $otros_datos .= ", Teléfono: $telefono";
    }
    
    // Insertar nuevo usuario
    $stmt = $conn->prepare("INSERT INTO usuarios (nombre, email, password_hash, estado, otros_datos, id_departamento) 
                            VALUES (:nombre, :email, :password_hash, :estado, :otros_datos, :id_departamento)");
    $stmt->execute([
        ':nombre' => $nombre,
        ':email' => $email,
        ':password_hash' => $password_hash,
        ':estado' => $estado,
        ':otros_datos' => $otros_datos,
        ':id_departamento' => $id_departamento
    ]);
    
    echo json_encode([
        'estado' => 1, 
        'mensaje' => 'Usuario registrado correctamente',
        'id_usuario' => $conn->lastInsertId()
    ]);
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
