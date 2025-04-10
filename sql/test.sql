-- 测试版
CREATE DATABASE IF NOT EXISTS dyseckill;

USE dyseckill;

CREATE TABLE goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_name VARCHAR(255),
    goods_title VARCHAR(255),
    goods_img VARCHAR(255),
    goods_detail TEXT,
    goods_price DECIMAL(10,2),
    goods_stock INT,
    create_date DATETIME,
    update_date DATETIME
);

CREATE TABLE order_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    goods_id BIGINT,
    addr_id BIGINT,
    goods_name VARCHAR(255),
    goods_count INT,
    goods_price DECIMAL(10,2),
    order_channel INT,
    status INT,
    create_date DATETIME,
    pay_date DATETIME
);

CREATE TABLE seckill_goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    goods_id BIGINT,
    seckill_price DECIMAL(10,2),
    stock_count INT,
    start_date DATETIME,
    end_date DATETIME
);

CREATE TABLE seckill_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    order_id BIGINT,
    goods_id BIGINT
);

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255),
    phone VARCHAR(20),
    password VARCHAR(255),
    salt VARCHAR(255),
    head VARCHAR(255),
    login_count INT,
    register_date DATETIME,
    last_login_date DATETIME
);