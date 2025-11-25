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
        // Si no se especifica departamento, listar todos
        $stmt = $conn->query("SELECT id_sensor, codigo_sensor, tipo, estado, fecha_alta, fecha_baja, id_departamento FROM sensores ORDER BY id_sensor DESC");
    } else {
        // Filtrar por departamento
        $stmt = $conn->prepare("SELECT id_sensor, codigo_sensor, tipo, estado, fecha_alta, fecha_baja, id_departamento FROM sensores WHERE id_departamento = :id_departamento ORDER BY id_sensor DESC");
        $stmt->execute([':id_departamento' => $id_departamento]);
    }
    
    $sensores = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode($sensores);
    
} catch(PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>
