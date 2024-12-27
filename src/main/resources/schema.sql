CREATE DATABASE IF NOT EXISTS `greengram_ver3_test`;

USE `greengram_ver3_test`;

DROP TABLE IF EXISTS `feed_pic`;
DROP TABLE IF EXISTS `feed_like`;
DROP TABLE IF EXISTS `feed_comment`;
DROP TABLE IF EXISTS `feed`;
DROP TABLE IF EXISTS `user_follow`;
DROP TABLE IF EXISTS `user`;


CREATE TABLE `user` (
    `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uid` varchar(30) NOT NULL,
    `upw` varchar(100) NOT NULL,
    `nick_name` varchar(30) DEFAULT NULL,
    `pic` varchar(50) DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uid` (`uid`)
);

CREATE TABLE `user_follow` (
    `from_user_id` bigint(20) NOT NULL,
    `to_user_id` bigint(20) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`from_user_id`,`to_user_id`),
    KEY `to_user_id` (`to_user_id`),
    CONSTRAINT `user_follow_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `user_follow_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `CONSTRAINT_1` CHECK (`from_user_id` <> `to_user_id`)
);

CREATE TABLE `feed` (
    `feed_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `writer_user_id` bigint(20) NOT NULL,
    `contents` varchar(1000) DEFAULT NULL,
    `location` varchar(30) DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
    PRIMARY KEY (`feed_id`),
    KEY `FK_FEED_writer_user_id` (`writer_user_id`),
    CONSTRAINT `FK_FEED_writer_user_id` FOREIGN KEY (`writer_user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
);

CREATE TABLE `feed_comment` (
    `feed_comment_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `feed_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `COMMENT` varchar(150) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
    PRIMARY KEY (`feed_comment_id`),
    KEY `feed_id` (`feed_id`),
    KEY `user_id` (`user_id`),
    CONSTRAINT `feed_comment_ibfk_1` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`feed_id`) ON DELETE CASCADE,
    CONSTRAINT `feed_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
    );


CREATE TABLE `feed_like` (
    `feed_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`feed_id`,`user_id`),
    KEY `user_id` (`user_id`),
    CONSTRAINT `feed_like_ibfk_1` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`feed_id`) ON DELETE CASCADE,
    CONSTRAINT `feed_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
);

CREATE TABLE `feed_pic` (
    `feed_id` bigint(20) NOT NULL,
    `pic` varchar(50) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`feed_id`,`pic`),
    CONSTRAINT `feed_pic_ibfk_1` FOREIGN KEY (`feed_id`) REFERENCES `feed` (`feed_id`) ON DELETE CASCADE
);




