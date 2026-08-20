-- ---- 1. 既存オブジェクトの削除（CASCADEなし、依存関係順） ----
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

-- ---- 2. テーブルの作成（SERIAL必須、手動シーケンスなし） ----
-- 部署
create table department(
    id serial, -- :o: SERIAL を使用（自動採番は1から開始）
    name varchar(100) not null,
    PRIMARY KEY (id)
);

-- 社員
create table employee(
    id serial,
    department_id integer,
    name varchar(100) not null,
    name_kana varchar(100) not null,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 社員アカウント
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

-- 顧客
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

-- 注文ステータス
create table order_status(
    id serial,
    name varchar(100) not null,
    PRIMARY KEY (id)
);

-- 支払方法
create table payment_method(
    id serial,
    name varchar(100) not null,
    PRIMARY KEY(id)
);

-- 注文
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

-- 商品カテゴリー
create table product_category(
    id serial,
    name varchar(20) not null,
    PRIMARY KEY(id)
);

-- 商品テーブル
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

-- 商品在庫テーブル
create table product_stock(
    id serial,
    product_id integer,
    quantity integer,
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

-- 注文明細
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

-- ---- 3. 初期データの投入（SERIALのルールに従う） ----
-- 部署（自動的に id = 1 で登録される）
insert into department (name) values ('人事部');
insert into department (name) values ('営業部');
insert into department (name) values ('人材戦略部');
insert into department (name) values ('管理部');
insert into department (name) values ('統括部');

-- 社員（自動的に id = 1 で登録。部署IDには上で生成された「1」を指定）
insert into employee (department_id, name, name_kana) values (1, '丸本翔太郎', 'マルモトショウタロウ');
insert into employee (department_id, name, name_kana) values (3, '石川太郎', 'イシカワタロウ');
insert into employee (department_id, name, name_kana) values (5, '鈴木太郎', 'スズキタロウ');
insert into employee (department_id, name, name_kana) values (3, '渡辺太郎', 'ワタナベタロウ');
insert into employee (department_id, name, name_kana) values (1, '佐藤太郎', 'サトウタロウ');
insert into employee (department_id, name, name_kana) values (4, '田中太郎', 'タナカタロウ');


-- 結合テストロックかかった時の解除用SQL
delete from employee_account where employee_id = 1;
delete from employee_account where employee_id = 2;
delete from employee_account where employee_id = 3;

-- 社員アカウント（自動的に id = 1 で登録。社員IDには上で生成された「1」を指定）
-- insert into employee_account (employee_id, name, password) values (1, '丸ちゃん', '$2a$10$wO3l2UiwZ3U13B0r8G9T2O6ZfL3r2zWjR3M7q6Nn/y5u8u7xMvKy6');
insert into employee_account (employee_id, name, password) values (1, 'marumoto', 'maru1');
insert into employee_account (employee_id, name, password) values (2, 'ishikawa001', 'pass001');
insert into employee_account (employee_id, name, password) values (3, 'suzuki003', 'pass002');

