<?php
// db_config.php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "fishpoint_db";

// Buat koneksi
$conn = new mysqli($servername, $username, $password, $dbname);

// Cek koneksi
if ($conn->connect_error) {
    die(json_encode([
        "status" => "error",
        "message" => "Connection failed: " . $conn->connect_error
    ]));
}

// Fungsi helper untuk JSON response
function sendResponse($status, $message, $data = null) {
    header('Content-Type: application/json');
    $response = [
        "status" => $status,
        "message" => $message
    ];
    if ($data !== null) {
        $response["data"] = $data;
    }
    echo json_encode($response);
    exit();
}
