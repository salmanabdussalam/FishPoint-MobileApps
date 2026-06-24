<?php
// Izinkan akses dari mana saja (CORS)
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

$host = 'localhost';
$db   = 'tusymvvm_fishpoint';
$user = 'tusymvvm_tusymvvm';
$pass = 'PBsuU74d2p#kPJv';
$charset = 'utf8mb4';            

try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Koneksi database gagal: " . $e->getMessage()]);
    exit;
}

// Fungsi pembantu untuk response JSON
function sendResponse($status, $message, $data = null) {
    echo json_encode(["status" => $status, "message" => $message, "data" => $data]);
    exit;
}
?>
