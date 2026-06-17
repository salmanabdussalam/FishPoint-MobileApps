<?php
require_once 'db.php';
header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    
    if (!$data) {
        sendResponse("error", "Data tidak valid");
    }

    $action = $data['action'] ?? null;

    if ($action === 'delete') {
        $id = $data['id'] ?? null;
        if ($id) {
            $stmt = $pdo->prepare("DELETE FROM reviews WHERE id = ?");
            if ($stmt->execute([$id])) {
                sendResponse("success", "Ulasan berhasil dihapus");
            } else {
                sendResponse("error", "Gagal menghapus ulasan");
            }
        } else {
            sendResponse("error", "ID Ulasan tidak ditemukan");
        }
    } else {
        $spotId = $data['spot_id'];
        $userId = $data['user_id'];
        $rating = $data['rating'];
        $comment = $data['comment'] ?? '';

        try {
            $stmt = $pdo->prepare("INSERT INTO reviews (spot_id, user_id, rating, comment) VALUES (?, ?, ?, ?)");
            $stmt->execute([$spotId, $userId, $rating, $comment]);
            sendResponse("success", "Ulasan berhasil ditambahkan");
        } catch (PDOException $e) {
            if ($e->getCode() == 23000) {
                sendResponse("error", "Anda sudah pernah memberikan ulasan pada spot ini.");
            } else {
                sendResponse("error", "Gagal menyimpan ulasan: " . $e->getMessage());
            }
        }
    }
}
?>