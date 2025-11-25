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
    $estado = $_GET['estado'] ?? '';
    
    if (empty($id_usuario) || empty($estado)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Faltan parámetros']);
        exit;
    }
    
    // Validar estados permitidos
    $estadosPermitidos = ['ACTIVO', 'INACTIVO', 'BLOQUEADO'];
    if (!in_array($estado, $estadosPermitidos)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Estado no válido']);
        exit;
    }
    
    // Actualizar estado del usuario
    $stmt = $conn->prepare("UPDATE usuarios SET estado = :estado WHERE id_usuario = :id_usuario");
    $stmt->execute([
        ':estado' => $estado,
        ':id_usuario' => $id_usuario
    ]);
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(['estado' => 1, 'mensaje' => 'Usuario actualizado correctamente']);
    } else {
        echo json_encode(['estado' => 0, 'mensaje' => 'No se actualizó ningún usuario']);
    }
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
