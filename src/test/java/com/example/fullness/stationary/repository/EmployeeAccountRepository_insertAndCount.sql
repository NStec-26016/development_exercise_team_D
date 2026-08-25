-- 1. 既存のテストデータと重複しないように、ID4の古いデータをきれいに掃除します
DELETE FROM employee_account WHERE employee_id = 4;
DELETE FROM employee WHERE id = 4;

-- 2. 💡 修正ポイント：部署ID（department_id）を、
-- 既存の marumoto さんのテストでも安全に使用されている「1」や「2」といった、
-- 実在する部署IDのレコードから「サブクエリ」を使って自動的に引っ張ってきて安全に挿入します。
INSERT INTO employee (id, department_id, name, name_kana) 
VALUES (
    4, 
    (SELECT department_id FROM employee LIMIT 1), -- 現在DBにある本物の部署IDを自動で使い回します
    '渡辺太郎', 
    'ワタナベタロウ'
);
