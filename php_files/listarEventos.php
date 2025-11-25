<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $id_departamento = $_GET['id_departamento'] ?? '';
    
    if (empty($id_departamento)) {
        // Si no se especifica departamento, listar todos los eventos
        $stmt = $conn->query("SELECT id_evento, id_sensor, id_usuario, tipo_evento, resultado, fecha_hora, id_departamento 
                              FROM eventos_acceso 
                              ORDER BY fecha_hora DESC 
                              LIMIT 100");
    } else {
        // Filtrar por departamento
        $stmt = $conn->prepare("SELECT id_evento, id_sensor, id_usuario, tipo_evento, resultado, fecha_hora, id_departamento 
                                FROM eventos_acceso 
                                WHERE id_departamento = :id_departamento 
                                ORDER BY fecha_hora DESC 
                                LIMIT 100");
        $stmt->execute([':id_departamento' => $id_departamento]);
    }
    
    $eventos = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode($eventos);
    
} catch(PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>
