<?php
header('Content-Type: application/json');

$host = 'localhost';
$user = 'root';
$pass = 'Admin12345#';
$db = 'MCU-app';

try {
    $conn = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    $id_departamento = $_GET['id_departamento'] ?? 1;
    
    // Obtener el estado actual de la barrera
    $stmt = $conn->prepare("SELECT estado, procesado FROM estado_barrera 
                            WHERE id_departamento = :id_departamento");
    $stmt->execute([':id_departamento' => $id_departamento]);
    $resultado = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($resultado) {
        $estado = $resultado['estado'];
        $procesado = $resultado['procesado'];
        
        // Si no ha sido procesado, devolver el comando
        if ($procesado == 0) {
            $comando = ($estado == 'ABIERTA') ? 'ABRIR' : 'CERRAR';
            
            // Marcar como procesado
            $stmt = $conn->prepare("UPDATE estado_barrera 
                                    SET procesado = 1 
                                    WHERE id_departamento = :id_departamento");
            $stmt->execute([':id_departamento' => $id_departamento]);
            
            echo json_encode([
                'estado' => $estado,
                'comando' => $comando,
                'procesado' => 0
            ]);
        } else {
            // Ya fue procesado, no hay comando nuevo
            echo json_encode([
                'estado' => $estado,
                'comando' => 'NADA',
                'procesado' => 1
            ]);
        }
    } else {
        // No existe registro, crear uno por defecto
        $stmt = $conn->prepare("INSERT INTO estado_barrera (id_departamento, estado, procesado) 
                                VALUES (:id_departamento, 'CERRADA', 1)");
        $stmt->execute([':id_departamento' => $id_departamento]);
        
        echo json_encode([
            'estado' => 'CERRADA',
            'comando' => 'NADA',
            'procesado' => 1
        ]);
    }
    
} catch(PDOException $e) {
    echo json_encode([
        'error' => $e->getMessage(), 
        'estado' => 'DESCONOCIDO',
        'comando' => 'NADA'
    ]);
}
?>
