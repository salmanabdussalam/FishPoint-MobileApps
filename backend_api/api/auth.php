<?php
require_once 'db.php';
header('Content-Type: application/json');

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['action'])) {
    sendResponse("error", "Action tidak ditemukan");
}

$action = $data['action'];
$username = $data['username'] ?? '';
$password = $data['password'] ?? '';

if ($action === 'register') {
    $fullName = $data['full_name'] ?? '';
    
    // Cek apakah username sudah ada
    $stmt = $pdo->prepare("SELECT id FROM users WHERE username = ?");
    $stmt->execute([$username]);
    if ($stmt->fetch()) sendResponse("error", "Username sudah digunakan");

    // Insert user baru
    $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
    $stmt = $pdo->prepare("INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, 'member')");
    
    if ($stmt->execute([$username, $hashedPassword, $fullName])) {
        $userId = $pdo->lastInsertId();
        $stmt = $pdo->prepare("SELECT id, username, full_name, role, created_at, is_banned FROM users WHERE id = ?");
        $stmt->execute([$userId]);
        $user = $stmt->fetch();
        sendResponse("success", "Registrasi berhasil", $user);
    } else {
        sendResponse("error", "Gagal mendaftar");
    }

} elseif ($action === 'login') {
    $stmt = $pdo->prepare("SELECT * FROM users WHERE username = ?");
    $stmt->execute([$username]);
    $user = $stmt->fetch();

    if ($user && password_verify($password, $user['password_hash'])) {
        if ($user['is_banned'] == 1) {
            sendResponse("error", "Akun Anda telah dinonaktifkan oleh Admin.");
        }
        unset($user['password_hash']); // Jangan kirim password kembali ke Android
        sendResponse("success", "Login berhasil", $user);
    } else {
        // Fallback untuk user admin default yang diinsert manual tanpa hash
        if ($user && $password === $user['password_hash']) {
            unset($user['password_hash']);
            sendResponse("success", "Login berhasil", $user);
        }
        sendResponse("error", "Username atau password salah");
    }
} else {
    sendResponse("error", "Action tidak valid");
}
?>