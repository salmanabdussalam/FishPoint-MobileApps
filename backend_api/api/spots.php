<?php
require_once 'db.php';
header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];

// ========================== GET: AMBIL SEMUA SPOT ==========================
if ($method === 'GET') {
    $spots = $pdo->query("
        SELECT s.*, u.username as owner_username 
        FROM spots s 
        JOIN users u ON s.user_id = u.id 
        ORDER BY s.created_at DESC
    ")->fetchAll();

    $photos = $pdo->query("SELECT spot_id, id, file_path, sort_order, uploaded_at FROM spot_photos ORDER BY sort_order ASC")->fetchAll(PDO::FETCH_GROUP|PDO::FETCH_ASSOC);
    $reviews = $pdo->query("
        SELECT r.spot_id, r.id, r.user_id, r.rating, r.comment, r.created_at, u.username 
        FROM reviews r 
        JOIN users u ON r.user_id = u.id 
        ORDER BY r.created_at DESC
    ")->fetchAll(PDO::FETCH_GROUP|PDO::FETCH_ASSOC);

    // Format data agar sesuai dengan model Kotlin (Nested List)
    $result = [];
    foreach ($spots as $spot) {
        $spotId = $spot['id'];
        $spot['photos'] = $photos[$spotId] ?? [];
        $spot['reviews'] = $reviews[$spotId] ?? [];
        
        // Ubah tipe data numerik
        $spot['latitude'] = (float)$spot['latitude'];
        $spot['longitude'] = (float)$spot['longitude'];
        $result[] = $spot;
    }
    sendResponse("success", "Data spot berhasil diambil", $result);
}

// ========================== POST: TAMBAH / UPDATE SPOT ==========================
if ($method === 'POST') {
    // Deteksi apakah ini form-data (Upload Foto) atau JSON (Update/Tambah tanpa foto)
    $action = $_POST['action'] ?? null;
    $isMultipart = true;

    if (!$action) {
        $data = json_decode(file_get_contents("php://input"), true);
        if ($data) {
            extract($data);
            $isMultipart = false;
        }
    } else {
        extract($_POST);
    }

    if ($action === 'create') {
        $stmt = $pdo->prepare("INSERT INTO spots (user_id, name, water_type, category, latitude, longitude, target_fish, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->execute([$user_id, $name, $water_type, $category ?? '', $latitude, $longitude, $target_fish ?? '', $description ?? '']);
        $spotId = $pdo->lastInsertId();

        // Proses Upload Foto jika ada
        if ($isMultipart && isset($_FILES['photos'])) {
            $baseURL = "http://" . $_SERVER['HTTP_HOST'] . "/backend_api/uploads/"; // URL untuk diakses Android
            $fileCount = count($_FILES['photos']['name']);
            
            for ($i = 0; $i < $fileCount; $i++) {
                $tmpName = $_FILES['photos']['tmp_name'][$i];
                $fileName = time() . "_" . basename($_FILES['photos']['name'][$i]);
                $targetFilePath = "../uploads/" . $fileName;

                if (move_uploaded_file($tmpName, $targetFilePath)) {
                    $remoteUrl = $baseURL . $fileName;
                    $stmtPhoto = $pdo->prepare("INSERT INTO spot_photos (spot_id, sort_order, file_path) VALUES (?, ?, ?)");
                    $stmtPhoto->execute([$spotId, $i, $remoteUrl]);
                }
            }
        }
        sendResponse("success", "Spot berhasil ditambahkan", ["id" => (int)$spotId]);
    } 
    
    if ($action === 'update') {
        $stmt = $pdo->prepare("UPDATE spots SET name=?, water_type=?, latitude=?, longitude=?, target_fish=?, description=? WHERE id=? AND user_id=?");
        $stmt->execute([$name, $water_type, $latitude, $longitude, $target_fish ?? '', $description ?? '', $id, $user_id]);
        
        // Proses Upload Foto Tambahan jika ada
        if ($isMultipart && isset($_FILES['photos'])) {
            $baseURL = "http://" . $_SERVER['HTTP_HOST'] . "/backend_api/uploads/"; // URL untuk diakses Android
            $fileCount = count($_FILES['photos']['name']);
            
            // Cari urutan terbesar saat ini agar urutan foto baru berlanjut
            $stmtSort = $pdo->prepare("SELECT MAX(sort_order) as max_sort FROM spot_photos WHERE spot_id=?");
            $stmtSort->execute([$id]);
            $maxSort = $stmtSort->fetchColumn() ?? -1;

            for ($i = 0; $i < $fileCount; $i++) {
                $tmpName = $_FILES['photos']['tmp_name'][$i];
                $fileName = time() . "_" . basename($_FILES['photos']['name'][$i]);
                $targetFilePath = "../uploads/" . $fileName;

                if (move_uploaded_file($tmpName, $targetFilePath)) {
                    $remoteUrl = $baseURL . $fileName;
                    $maxSort++;
                    $stmtPhoto = $pdo->prepare("INSERT INTO spot_photos (spot_id, sort_order, file_path) VALUES (?, ?, ?)");
                    $stmtPhoto->execute([$id, $maxSort, $remoteUrl]);
                }
            }
        }
        
        sendResponse("success", "Spot berhasil diupdate");
    }
    
    if ($action === 'delete') {
        // Hapus spot beserta foto dan ulasannya (Asumsi ON DELETE CASCADE sudah ada di DB)
        // Jika belum ada cascade, kita hapus manual
        $pdo->prepare("DELETE FROM spot_photos WHERE spot_id=?")->execute([$id]);
        $pdo->prepare("DELETE FROM reviews WHERE spot_id=?")->execute([$id]);
        
        $stmt = $pdo->prepare("DELETE FROM spots WHERE id=?");
        if ($stmt->execute([$id])) {
            sendResponse("success", "Spot berhasil dihapus");
        } else {
            sendResponse("error", "Gagal menghapus spot");
        }
    }
}
?>