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
        // Si no se especifica departamento, obtener el último evento global
        $stmt = $conn->query("SELECT tipo_evento FROM eventos_acceso 
                              WHERE tipo_evento IN ('APERTURA', 'CIERRE') 
                              ORDER BY fecha_hora DESC 
                              LIMIT 1");
    } else {
        // Filtrar por departamento
        $stmt = $conn->prepare("SELECT tipo_evento FROM eventos_acceso 
                                WHERE tipo_evento IN ('APERTURA', 'CIERRE') 
                                AND id_departamento = :id_departamento 
                                ORDER BY fecha_hora DESC 
                                LIMIT 1");
        $stmt->execute([':id_departamento' => $id_departamento]);
    }
    
    $evento = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($evento) {
        $estado = ($evento['tipo_evento'] == 'APERTURA') ? 'ABIERTA' : 'CERRADA';
    } else {
        $estado = 'CERRADA'; // Estado por defecto
    }
    
    echo json_encode(['estado' => $estado]);
    
} catch(PDOException $e) {
    echo json_encode(['error' => $e->getMessage(), 'estado' => 'DESCONOCIDO']);
}
?>
