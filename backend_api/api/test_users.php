<?php
require 'db.php';
$stmt = $pdo->query('SELECT id, username, full_name, role FROM users');
$users = $stmt->fetchAll(PDO::FETCH_ASSOC);
print_r($users);
?>
