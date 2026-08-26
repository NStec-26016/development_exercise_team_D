-- 既存のデータをきれいに消す（以前と同じ本物DB用のTRUNCATE）
TRUNCATE TABLE product_stock CASCADE;
TRUNCATE TABLE product CASCADE;
TRUNCATE TABLE product_category CASCADE;

-- 1. カテゴリデータ
INSERT INTO product_category (id, name) VALUES (1, '文房具');
INSERT INTO product_category (id, name) VALUES (2, 'ガジェット');
INSERT INTO product_category (id, name) VALUES (3, '日用品');

-- 2. 商品データ
INSERT INTO product (id, product_category_id, name, price, image_url, delete_flag) VALUES (1, 1, '水性ボールペン(黒)', 120, 'black.pen_w.jpg', 0);
INSERT INTO product (id, product_category_id, name, price, image_url, delete_flag) VALUES (2, 1, '水性ボールペン(赤)', 120, 'red.pen_w.jpg', 0);
INSERT INTO product (id, product_category_id, name, price, image_url, delete_flag) VALUES (3, 2, 'マウスB', 10000, 'mouse_b.jpg', 0);
INSERT INTO product (id, product_category_id, name, price, image_url, delete_flag) VALUES (4, 3, '折り畳み傘', 2200, 'umbrella.jpg', 0);

-- 3. 在庫データ
INSERT INTO product_stock (id, product_id, quantity) VALUES (1, 1, 10);
INSERT INTO product_stock (id, product_id, quantity) VALUES (2, 2, 10);
INSERT INTO product_stock (id, product_id, quantity) VALUES (3, 3, 3);
INSERT INTO product_stock (id, product_id, quantity) VALUES (4, 4, 4);
