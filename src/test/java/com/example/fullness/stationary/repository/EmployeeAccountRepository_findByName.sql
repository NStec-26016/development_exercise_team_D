-- 1. 既存のテーブルがあれば完全に削除してリセットする
DROP TABLE IF EXISTS employee_account;

-- 2. H2データベース用にテーブルを新規作成
CREATE TABLE employee_account (
    id serial,
    employee_id integer,
    name varchar(20) not null,
    password varchar(200),
    failed_attempts integer default 0 not null, 
    lock_time timestamp null
);

-- 3. MyBatisがキャメルケースをそのまま要求する場合に備えて別名（エイリアス）の列も準備
-- ALTER TABLE employee_account ADD COLUMN IF NOT EXISTS employeeId INT AS (employee_id);
-- ALTER TABLE employee_account ADD COLUMN IF NOT EXISTS employeeAccountRole VARCHAR(255) AS (employee_account_role);

-- 4. テストデータを1件だけ確実に挿入する
INSERT INTO employee_account (id, employee_id, name, password)
VALUES (
    1, 
    1, 
    'marumoto', 
    'maru1'
);