-- =============================================================
-- 图书管理系统 建库建表脚本
-- 依据学习计划「附录 B」参考设计,两人共同定稿
-- 用法:mysql -u root -p < db/init.sql
-- =============================================================

-- 1. 建库
CREATE DATABASE IF NOT EXISTS `book_management`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `book_management`;

-- 2. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0=普通用户，1=管理员',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=正常，0=禁用',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 3. 图书表
CREATE TABLE IF NOT EXISTS `book` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `isbn`            VARCHAR(20)  NOT NULL COMMENT 'ISBN号',
  `title`           VARCHAR(100) NOT NULL COMMENT '书名',
  `author`          VARCHAR(50)  DEFAULT NULL COMMENT '作者',
  `publisher`       VARCHAR(100) DEFAULT NULL COMMENT '出版社',
  `category`        VARCHAR(50)  DEFAULT NULL COMMENT '分类',
  `total_count`     INT          NOT NULL DEFAULT 0 COMMENT '馆藏总量',
  `available_count` INT          NOT NULL DEFAULT 0 COMMENT '可借数量',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_isbn` (`isbn`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '图书表';

-- 4. 借阅记录表
CREATE TABLE IF NOT EXISTS `borrow_record` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `book_id`     BIGINT   NOT NULL COMMENT '图书ID',
  `borrow_time` DATETIME NOT NULL COMMENT '借出时间',
  `due_time`    DATETIME NOT NULL COMMENT '应还时间',
  `return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
  `status`      TINYINT  NOT NULL DEFAULT 0 COMMENT '0=借出中,1=已归还,2=逾期',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '借阅记录表';
