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
        $stmt = $conn->query("SELECT id_usuario, nombre, email, estado, otros_datos, id_departamento FROM usuarios ORDER BY id_usuario DESC");
    } else {
        // Filtrar por departamento
        $stmt = $conn->prepare("SELECT id_usuario, nombre, email, estado, otros_datos, id_departamento FROM usuarios WHERE id_departamento = :id_departamento ORDER BY id_usuario DESC");
        $stmt->execute([':id_departamento' => $id_departamento]);
    }
    
    $usuarios = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Extraer el rol del campo otros_datos y agregarlo como campo separado
    foreach ($usuarios as &$usuario) {
        $rol = 'OPERADOR'; // valor por defecto
        if (preg_match('/Rol:\s*(\w+)/', $usuario['otros_datos'], $matches)) {
            $rol = $matches[1];
        }
        $usuario['rol'] = $rol;
    }
    
    echo json_encode($usuarios);
    
} catch(PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>
