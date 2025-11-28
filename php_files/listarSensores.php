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
        // Si no se especifica departamento, listar todos con información del usuario
        $stmt = $conn->query("SELECT s.id_sensor, s.codigo_sensor, s.tipo, s.estado, s.fecha_alta, s.fecha_baja, 
                              s.id_departamento, s.id_usuario, u.nombre as nombre_usuario 
                              FROM sensores s 
                              LEFT JOIN usuarios u ON s.id_usuario = u.id_usuario 
                              ORDER BY s.id_sensor DESC");
    } else {
        // Filtrar por departamento con información del usuario
        $stmt = $conn->prepare("SELECT s.id_sensor, s.codigo_sensor, s.tipo, s.estado, s.fecha_alta, s.fecha_baja, 
                                s.id_departamento, s.id_usuario, u.nombre as nombre_usuario 
                                FROM sensores s 
                                LEFT JOIN usuarios u ON s.id_usuario = u.id_usuario 
                                WHERE s.id_departamento = :id_departamento 
                                ORDER BY s.id_sensor DESC");
        $stmt->execute([':id_departamento' => $id_departamento]);
    }
    
    $sensores = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode($sensores);
    
} catch(PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>
