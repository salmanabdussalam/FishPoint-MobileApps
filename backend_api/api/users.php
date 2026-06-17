<?php
require_once 'db.php';
header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    // Ambil semua user kecuali password
    $users = $pdo->query("SELECT id, username, full_name, role, is_banned, created_at FROM users ORDER BY created_at DESC")->fetchAll(PDO::FETCH_ASSOC);
    
    // Format agar sesuai dengan model Android
    foreach ($users as &$user) {
        $user['created_at'] = date('Y', strtotime($user['created_at']));
        $user['is_banned'] = (bool)$user['is_banned'];
    }
    
    sendResponse("success", "Data pengguna berhasil diambil", $users);
}

if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    if (!$data) $data = $_POST;
    
    $action = $data['action'] ?? null;
    $id = $data['id'] ?? null;
    
    if (!$action || !$id) {
        sendResponse("error", "Data tidak valid");
        exit;
    }
    
    if ($action === 'ban') {
        $stmt = $pdo->prepare("UPDATE users SET is_banned = 1 WHERE id = ?");
        if ($stmt->execute([$id])) {
            sendResponse("success", "Pengguna berhasil di-ban");
        } else {
            sendResponse("error", "Gagal melakukan ban pengguna");
        }
    }
    
    if ($action === 'unban') {
        $stmt = $pdo->prepare("UPDATE users SET is_banned = 0 WHERE id = ?");
        if ($stmt->execute([$id])) {
            sendResponse("success", "Pengguna berhasil di-unban");
        } else {
            sendResponse("error", "Gagal melakukan unban pengguna");
        }
    }
}
?>
