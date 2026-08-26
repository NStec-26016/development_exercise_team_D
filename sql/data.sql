-- ---- 1. 既存オブジェクトの削除 ----
drop table if exists order_detail;
drop table if exists product_stock;
drop table if exists product;
drop table if exists product_category;
drop table if exists orders;
drop table if exists payment_method;
drop table if exists order_status;
drop table if exists customer;
drop table if exists employee_account;
drop table if exists employee;
drop table if exists department;

-- ---- 2. テーブルの作成 ----
create table department(
    id serial,
    name varchar(100) not null,
    PRIMARY KEY (id)
);

create table employee(
    id serial,
    department_id integer,
    name varchar(100) not null,
    name_kana varchar(100) not null,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES department(id)
);

create table employee_account(
    id serial,
    employee_id integer,
    name varchar(20) not null,
    password varchar(200),
    failed_attempts integer default 0 not null,
    lock_time timestamp null,
    primary key (id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

create table customer(
    id serial,
    name varchar(20) not null,
    name_kana varchar(20) not null,
    address1 varchar(100),
    address2 varchar(100),
    phone_number varchar(20),
    mail_address varchar(200),
    username varchar(30) not null,
    password varchar(200),
    register_date timestamp,
    PRIMARY KEY (id)
);

create table order_status(
    id serial,
    name varchar(100) not null,
    PRIMARY KEY (id)
);

create table payment_method(
    id serial,
    name varchar(100) not null,
    PRIMARY KEY(id)
);

create table orders(
    id serial,
    customer_id integer,
    order_status_id integer,
    payment_method_id integer,
    order_date timestamp,
    amount_total integer,
    PRIMARY KEY(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (order_status_id) REFERENCES order_status(id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(id)
);

create table product_category(
    id serial,
    name varchar(20) not null,
    PRIMARY KEY(id)
);

create table product(
    id serial,
    product_category_id integer,
    name varchar(20) not null,
    price integer,
    image_url varchar(200),
    delete_flag integer,
    PRIMARY KEY(id),
    FOREIGN KEY(product_category_id) REFERENCES product_category(id)
);

create table product_stock(
    id serial,
    product_id integer,
    quantity integer,
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

create table order_detail(
    id serial,
    order_id integer,
    product_id integer,
    customer_id integer,
    count integer,
    PRIMARY KEY (id),
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);

-- ---- 3. 初期データの投入 ----
insert into department (name) values ('人事部'), ('営業部'), ('人材戦略部'), ('管理部'), ('統括部');
insert into employee (department_id, name, name_kana) values (1, '丸本翔太郎', 'マルモトショウタロウ'), (3, '石川太郎', 'イシカワタロウ'), (5, '鈴木太郎', 'スズキタロウ'), (3, '渡辺太郎', 'ワタナベタロウ'), (1, '佐藤太郎', 'サトウタロウ'), (4, '田中太郎', 'タナカタロウ');
insert into employee_account (employee_id, name, password) values (1, 'marumoto', 'maru1'), (2, 'ishikawa001', 'pass001'), (3, 'suzuki003', 'pass002');

insert into product_category (name) values ('文房具'), ('ガジェット'), ('ファッション'), ('日用品');

insert into product (product_category_id, name, price, image_url, delete_flag) values
(1, '黒筆ペン', 250, '/images/black_fudepen.jpg', 0),
(1, '黒ボールペン（太字）', 150, '/images/black_pen_o.jpg', 0),
(1, '黒ボールペン（細字）', 150, '/images/black_pen_w.jpeg', 0), -- ここだけ .jpeg
(1, '黒ボールペン', 120, '/images/black_pen.jpg', 0),
(1, '青マーカー', 180, '/images/blue_maker.jpg', 0),
(1, '青筆ペン', 250, '/images/blue_fudepen.jpg', 0), -- ファイルがないので表示されません
(1, '青ボールペン（太字）', 150, '/images/blue_pen_o.jpg', 0),
(1, '青ボールペン（細字）', 150, '/images/blue_pen_w.jpeg', 0), -- ここだけ .jpeg
(1, 'カラーペン 12色', 800, '/images/color_pen12.jpeg', 0), -- ここだけ .jpeg
(1, 'カラーペン 48色', 2800, '/images/color_pen48.jpeg', 0), -- ここだけ .jpeg
(1, '緑マーカー', 180, '/images/green_maker.jpg', 0),
(1, '黄色マーカー', 180, '/images/yellow_maker.jpg', 0),
(1, '赤ボールペン', 120, '/images/red_pen.jpg', 0),
(1, '赤ボールペン（太字）', 150, '/images/red_pen_o.jpg', 0),
(2, 'マウス A', 2980, '/images/mouse_a.jpg', 0),
(2, 'マウス B', 3480, '/images/mouse_b.jpg', 0),
(2, 'マウス C', 2500, '/images/mouse_c.jpg', 0),
(2, 'マウス D', 2700, '/images/mouse_d.jpg', 0),
(3, 'トートバッグ', 4500, '/images/bag.jpg', 0),
(4, 'マスク', 1000, '/images/mask.jpg', 0),
(4, '傘', 2200, '/images/umbrella.jpg', 0);

insert into product_stock (product_id, quantity)
select i, 20 from generate_series(1, 21) as s(i);