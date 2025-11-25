<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $accion = $_GET['accion'] ?? '';
    $id_usuario = $_GET['id_usuario'] ?? '';
    $id_departamento = $_GET['id_departamento'] ?? '';
    
    if (empty($accion) || empty($id_usuario) || empty($id_departamento)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Faltan parámetros']);
        exit;
    }
    
    // Validar acción
    if (!in_array($accion, ['ABRIR', 'CERRAR'])) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Acción no válida']);
        exit;
    }
    
    // Registrar evento de control de barrera
    $tipo_evento = $accion == 'ABRIR' ? 'APERTURA' : 'CIERRE';
    
    $stmt = $conn->prepare("INSERT INTO eventos_acceso (id_sensor, id_usuario, tipo_evento, resultado, fecha_hora, id_departamento) 
                            VALUES (NULL, :id_usuario, :tipo_evento, 'PERMITIDO', NOW(), :id_departamento)");
    $stmt->execute([
        ':id_usuario' => $id_usuario,
        ':tipo_evento' => $tipo_evento,
        ':id_departamento' => $id_departamento
    ]);
    
    echo json_encode([
        'estado' => 1, 
        'mensaje' => 'Barrera ' . strtolower($accion) . 'da correctamente',
        'accion' => $accion
    ]);
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
