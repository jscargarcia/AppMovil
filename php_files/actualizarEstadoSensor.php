<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $id_sensor = $_GET['id_sensor'] ?? '';
    $estado = $_GET['estado'] ?? '';
    
    if (empty($id_sensor) || empty($estado)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Faltan parámetros']);
        exit;
    }
    
    // Validar estados permitidos
    $estadosPermitidos = ['ACTIVO', 'INACTIVO', 'PERDIDO', 'BLOQUEADO'];
    if (!in_array($estado, $estadosPermitidos)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Estado no válido']);
        exit;
    }
    
    // Actualizar estado del sensor
    $stmt = $conn->prepare("UPDATE sensores SET estado = :estado WHERE id_sensor = :id_sensor");
    $stmt->execute([
        ':estado' => $estado,
        ':id_sensor' => $id_sensor
    ]);
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(['estado' => 1, 'mensaje' => 'Sensor actualizado correctamente']);
    } else {
        echo json_encode(['estado' => 0, 'mensaje' => 'No se actualizó ningún sensor']);
    }
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
