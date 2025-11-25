<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $id_usuario = $_GET['id_usuario'] ?? '';
    
    if (empty($id_usuario)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Falta id_usuario']);
        exit;
    }
    
    $stmt = $conn->prepare("SELECT nombre, email, otros_datos FROM usuarios WHERE id_usuario = :id_usuario");
    $stmt->execute([':id_usuario' => $id_usuario]);
    
    if ($stmt->rowCount() > 0) {
        $usuario = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Extraer el rol de otros_datos
        $rol = 'OPERADOR'; // valor por defecto
        if (preg_match('/Rol:\s*(\w+)/', $usuario['otros_datos'], $matches)) {
            $rol = $matches[1];
        }
        
        echo json_encode([
            'estado' => 1,
            'nombre' => $usuario['nombre'],
            'email' => $usuario['email'],
            'rol' => $rol
        ]);
    } else {
        echo json_encode(['estado' => 0, 'mensaje' => 'Usuario no encontrado']);
    }
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
