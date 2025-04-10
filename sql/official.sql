-- 正式版
-- 正式版加了很多约束，只是想测试一下用测试版就可以了
CREATE DATABASE IF NOT EXISTS dyseckill;

USE dyseckill;

CREATE TABLE goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_name VARCHAR(255) NOT NULL,
    goods_title VARCHAR(255),
    goods_img VARCHAR(255),
    goods_detail TEXT,
    goods_price DECIMAL(10,2) NOT NULL,
    goods_stock INT NOT NULL DEFAULT 0,
    create_date DATETIME NOT NULL,
    update_date DATETIME
);

CREATE TABLE order_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    addr_id BIGINT NOT NULL,
    goods_name VARCHAR(255) NOT NULL,
    goods_count INT NOT NULL DEFAULT 1,
    goods_price DECIMAL(10,2) NOT NULL,
    order_channel INT NOT NULL,
    status INT NOT NULL,
    create_date DATETIME NOT NULL,
    pay_date DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (goods_id) REFERENCES goods(id)
);

CREATE TABLE seckill_goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    seckill_price DECIMAL(10,2) NOT NULL,
    stock_count INT NOT NULL DEFAULT 0,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    FOREIGN KEY (goods_id) REFERENCES goods(id)
);

CREATE TABLE seckill_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (order_id) REFERENCES order_info(id),
    FOREIGN KEY (goods_id) REFERENCES goods(id)
);

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    salt VARCHAR(255),
    head VARCHAR(255),
    login_count INT DEFAULT 0,
    register_date DATETIME NOT NULL,
    last_login_date DATETIME
);

-- Optional: Create indexes for better performance
CREATE INDEX idx_order_user ON order_info(user_id);
CREATE INDEX idx_order_goods ON order_info(goods_id);
CREATE INDEX idx_seckill_goods ON seckill_goods(goods_id);
CREATE INDEX idx_seckill_order_user ON seckill_order(user_id);
CREATE INDEX idx_seckill_order_goods ON seckill_order(goods_id);