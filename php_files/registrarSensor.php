<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $codigo = $_GET['codigo'] ?? '';
    $tipo = $_GET['tipo'] ?? '';
    $estado = $_GET['estado'] ?? 'ACTIVO';
    $id_departamento = $_GET['id_departamento'] ?? '';
    
    if (empty($codigo) || empty($tipo) || empty($id_departamento)) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Faltan parámetros']);
        exit;
    }
    
    // Validar que el tipo sea LLAVERO o TARJETA
    if (!in_array($tipo, ['LLAVERO', 'TARJETA'])) {
        echo json_encode(['estado' => 0, 'mensaje' => 'Tipo no válido. Use LLAVERO o TARJETA']);
        exit;
    }
    
    // Verificar si el código ya existe
    $stmt = $conn->prepare("SELECT id_sensor FROM sensores WHERE codigo_sensor = :codigo");
    $stmt->execute([':codigo' => $codigo]);
    
    if ($stmt->rowCount() > 0) {
        echo json_encode(['estado' => 0, 'mensaje' => 'El código del sensor ya existe']);
        exit;
    }
    
    // Insertar nuevo sensor
    $stmt = $conn->prepare("INSERT INTO sensores (codigo_sensor, tipo, estado, fecha_alta, id_departamento) 
                            VALUES (:codigo, :tipo, :estado, NOW(), :id_departamento)");
    $stmt->execute([
        ':codigo' => $codigo,
        ':tipo' => $tipo,
        ':estado' => $estado,
        ':id_departamento' => $id_departamento
    ]);
    
    echo json_encode([
        'estado' => 1, 
        'mensaje' => 'Sensor registrado correctamente',
        'id_sensor' => $conn->lastInsertId()
    ]);
    
} catch(PDOException $e) {
    echo json_encode(['estado' => 0, 'mensaje' => 'Error: ' . $e->getMessage()]);
}
?>
