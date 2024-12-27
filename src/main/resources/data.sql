INSERT INTO `user`
(`user_id`, `uid`, `upw`, `nick_name`, `pic`, `created_at`) VALUES
(1, 'mic', '$2a$10$cVRpfytmEdzvs1I4oqqRXOWUleJHm6xKjOnHqA3EEJ5.Q0cAtbhte', '홍길동', NULL, '2024-11-01 10:10:01'),
(2, 'jacob', '$2a$10$cVRpfytmEdzvs1I4oqqRXOWUleJHm6xKjOnHqA3EEJ5.Q0cAtbhte', '제이콥', NULL, '2024-11-01 10:10:02');

INSERT INTO `feed`
(`feed_id`, `writer_user_id`, `contents`, `location`, `created_at`, `updated_at`) VALUES
(1, 1, '1번 글', '1번 위치', '2024-11-01 10:10:01', NULL),
(2, 2, '2번 글', '2번 위치', '2024-11-01 10:10:02', NULL),
(3, 1, '3번 글', '3번 위치', '2024-11-01 10:10:03', NULL),
(4, 2, '4번 글', '4번 위치', '2024-11-01 10:10:04', NULL),
(5, 1, '5번 글', '5번 위치', '2024-11-01 10:10:05', NULL);

INSERT INTO `feed_like`
(`feed_id`, `user_id`, `created_at`) VALUES
(1, 2, '2024-11-01 10:10:01'),
(2, 1, '2024-11-01 10:10:02'),
(3, 2, '2024-11-01 10:10:03');

INSERT INTO `feed_comment`
(`feed_comment_id`, `feed_id`, `user_id`, `COMMENT`, `created_at`) VALUES
(1, 1, 1, '1번 댓글', '2024-11-01 10:10:01'),
(2, 2, 2, '2번 댓글', '2024-11-01 10:10:02'),
(3, 3, 1, '3번 댓글', '2024-11-01 10:10:03'),
(4, 4, 2, '4번 댓글', '2024-11-01 10:10:04'),
(5, 5, 1, '5번 댓글', '2024-11-01 10:10:05'),
(6, 1, 2, '6번 댓글', '2024-11-01 10:10:06');