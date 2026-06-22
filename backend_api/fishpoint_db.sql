-- Struktur tabel untuk `users`
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `role` enum('member','admin') DEFAULT 'member',
  `is_banned` tinyint(1) DEFAULT 0,
  `created_at` timestamp DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Struktur tabel untuk `spots`
CREATE TABLE IF NOT EXISTS `spots` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `water_type` varchar(50) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `target_fish` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_spot_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Struktur tabel untuk `reviews`
CREATE TABLE IF NOT EXISTS `reviews` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `spot_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL,
  `comment` text DEFAULT NULL,
  `created_at` timestamp DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `spot_id` (`spot_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_review_spot` FOREIGN KEY (`spot_id`) REFERENCES `spots` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data awal untuk tabel `users`
-- Password untuk admin dan user1 adalah: 123456
INSERT IGNORE INTO `users` (`id`, `username`, `password_hash`, `full_name`, `role`) VALUES
(1, 'admin', '$2y$10$vNnS5fG5zYm1EaXlQ8P8Aea5i9v3o0G9Z8J6o2w/Zq0C6zM3C6D6e', 'Admin Utama', 'admin'),
(2, 'user1', '$2y$10$vNnS5fG5zYm1EaXlQ8P8Aea5i9v3o0G9Z8J6o2w/Zq0C6zM3C6D6e', 'Pemancing Handal', 'member');

-- Data awal untuk tabel `spots` (3 Spot Awal)
INSERT IGNORE INTO `spots` (`id`, `user_id`, `name`, `water_type`, `category`, `latitude`, `longitude`, `target_fish`, `description`) VALUES
(1, 1, 'Danau Rawa Pening', 'Air Tawar', 'Danau', -7.2882, 110.4323, 'Nila, Mujair, Gabus', 'Spot mancing danau dengan pemandangan indah dan ikan yang melimpah.'),
(2, 2, 'Waduk Gajah Mungkur', 'Air Tawar', 'Waduk', -7.8631, 110.9067, 'Patin, Nila, Nila Merah', 'Kawasan waduk yang luas, sangat cocok untuk memancing bersama keluarga.'),
(3, 1, 'Pantai Menganti', 'Air Asin', 'Pantai', -7.7686, 109.4143, 'Kerapu, Kakap', 'Spot mancing tebing karang di pesisir pantai selatan yang menantang.');

-- Data awal untuk tabel `reviews`
INSERT IGNORE INTO `reviews` (`id`, `spot_id`, `user_id`, `rating`, `comment`) VALUES
(1, 1, 2, 5, 'Spotnya mantap, ikannya banyak banget!');
