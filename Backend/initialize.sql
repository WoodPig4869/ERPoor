
-- 初始化 account 使用者帳號資料表
DROP TABLE IF EXISTS account;
CREATE TABLE account (
  user_id SERIAL PRIMARY KEY,  -- 使用者唯一識別碼，自動遞增
  username VARCHAR(50) UNIQUE NOT NULL,  -- 登入帳號，需唯一
  password VARCHAR(255) NOT NULL,  -- 使用者密碼（加密後儲存）
  role VARCHAR(20) NOT NULL DEFAULT 'USER',  -- 使用者角色，預設為 USER
  nickname VARCHAR(50),  -- 顯示用暱稱，可為空可重複
  phone VARCHAR(50),  -- 聯絡電話，可為空
  deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 軟刪除標記，用來避免資料實體刪除
  registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 註冊時間
);

-- 🔍 加入 username 搜尋用索引
DROP INDEX IF EXISTS idx_account_username;
CREATE INDEX idx_account_username ON account(username);

-- 💬 為 account 加入欄位註解
COMMENT ON TABLE account IS '使用者帳號資料表，用於管理登入資訊與使用者狀態';

COMMENT ON COLUMN account.user_id IS '使用者唯一識別碼，自動遞增';
COMMENT ON COLUMN account.username IS '登入帳號，需唯一';
COMMENT ON COLUMN account.password IS '加密後的登入密碼';
COMMENT ON COLUMN account.role IS '使用者角色（如 USER / ADMIN），控制權限';
COMMENT ON COLUMN account.nickname IS '暱稱，可顯示於前端';
COMMENT ON COLUMN account.phone IS '使用者聯絡電話';
COMMENT ON COLUMN account.deleted IS '軟刪除標記，true 表示帳號已被標記刪除';
COMMENT ON COLUMN account.registration_date IS '帳號註冊時間（時間戳）';

-- 為 account 加入測試資料（密碼為加鹽雜湊的後的123456）
INSERT INTO account (username, password, role,nickname,phone)VALUES
('admin', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'ADMIN','嬴政','0912345678'),
('user1', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'USER','王五','0911111111'),
('user2', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'USER','趙六','0922222222'),
('user3', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'USER','孫七','0933333333')
ON CONFLICT (username) DO NOTHING;

-- 初始化 supplier 供應商資料表
DROP TABLE IF EXISTS supplier CASCADE;
CREATE TABLE supplier (
  supplier_id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  contact_info TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP INDEX IF EXISTS idx_supplier_name;
CREATE INDEX idx_supplier_name ON supplier(name);

COMMENT ON TABLE supplier IS '供應商資料表，紀錄供應商基本聯絡資訊';
COMMENT ON COLUMN supplier.supplier_id IS '供應商唯一識別碼，自動遞增';
COMMENT ON COLUMN supplier.name IS '供應商名稱';
COMMENT ON COLUMN supplier.contact_info IS '聯絡資訊';
COMMENT ON COLUMN supplier.created_at IS '供應商建立時間';

-- 初始化 refresh_token 資料表，儲存使用者的 refresh token 與相關資訊
DROP TABLE IF EXISTS refresh_token;
CREATE TABLE refresh_token (
  token_id BIGSERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  token VARCHAR(512) NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 🔍 加入 user_id 及 token 的搜尋索引（token 已 UNIQUE）
DROP INDEX IF EXISTS idx_refresh_token_user_id;
CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);

-- 💬 為 refresh_token 加入欄位註解
COMMENT ON TABLE refresh_token IS '儲存使用者 refresh token 與狀態，管理登入延續';

COMMENT ON COLUMN refresh_token.token_id IS 'refresh_token 唯一識別碼，自動遞增';
COMMENT ON COLUMN refresh_token.user_id IS '對應 account 表的使用者 id';
COMMENT ON COLUMN refresh_token.token IS '唯一 refresh token 字串，限制長度以避免異常資料';
COMMENT ON COLUMN refresh_token.expires_at IS 'refresh token 到期時間';
COMMENT ON COLUMN refresh_token.revoked IS '標記 token 是否已撤銷';
COMMENT ON COLUMN refresh_token.created_at IS 'refresh token 產生時間';

-- 初始化 product 商品主檔
DROP TABLE IF EXISTS product CASCADE;
CREATE TABLE product (
  product_id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  category VARCHAR(50),
  unit VARCHAR(20) NOT NULL DEFAULT '件',
  expiry_alert_days INT NOT NULL DEFAULT 7,
  min_stock INT NOT NULL DEFAULT 0,
  description TEXT,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  price DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP INDEX IF EXISTS idx_product_name;
CREATE INDEX idx_product_name ON product(name);

COMMENT ON TABLE product IS '商品主檔，紀錄商品基本資訊';
COMMENT ON COLUMN product.product_id IS '商品唯一識別碼，自動遞增';
COMMENT ON COLUMN product.name IS '商品名稱';
COMMENT ON COLUMN product.category IS '商品分類';
COMMENT ON COLUMN product.unit IS '計量單位';
COMMENT ON COLUMN product.expiry_alert_days IS '即將到期的預警天數';
COMMENT ON COLUMN product.min_stock IS '最低庫存量，用於觸發低庫存警告';
COMMENT ON COLUMN product.description IS '商品簡述';
COMMENT ON COLUMN product.enabled IS '是否啟用（上架）';
COMMENT ON COLUMN product.price IS '商品單價';
COMMENT ON COLUMN product.created_at IS '商品建立時間';

-- 初始化 product_batch 商品批次資料表
DROP TABLE IF EXISTS product_batch CASCADE;
CREATE TABLE product_batch (
  batch_id SERIAL PRIMARY KEY,
  product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
  batch_code VARCHAR(50),
  quantity INT NOT NULL CHECK (quantity >= 0),
  expiration_date DATE NOT NULL,
  purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0,
  received_date DATE NOT NULL,
  supplier_id INT REFERENCES supplier(supplier_id) ON DELETE SET NULL,
  supplier_name VARCHAR(100) NOT NULL,                           -- 加入 supplier_name
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP INDEX IF EXISTS idx_product_batch_product_id_expiration_date;
CREATE INDEX idx_product_batch_product_id_expiration_date ON product_batch(product_id, expiration_date);
CREATE INDEX idx_product_batch_product_id_expiry ON product_batch(product_id, expiration_date);
CREATE INDEX idx_product_batch_product_id_received ON product_batch(product_id, received_date);

COMMENT ON TABLE product_batch IS '商品批次資料表，紀錄每批進貨數量與有效日期';
COMMENT ON COLUMN product_batch.batch_id IS '批次唯一識別碼，自動遞增';
COMMENT ON COLUMN product_batch.product_id IS '對應商品 ID';
COMMENT ON COLUMN product_batch.batch_code IS '批號';
COMMENT ON COLUMN product_batch.quantity IS '進貨數量';
COMMENT ON COLUMN product_batch.expiration_date IS '有效日期';
COMMENT ON COLUMN product_batch.purchase_price IS '進貨單價';
COMMENT ON COLUMN product_batch.received_date IS '進貨日期';
COMMENT ON COLUMN product_batch.supplier_id IS '供應商 ID';
COMMENT ON COLUMN product_batch.supplier_name IS '供應商名稱，方便查詢';
COMMENT ON COLUMN product_batch.created_at IS '資料建立時間';

-- 初始化 sale_order 訂單資料表
DROP TABLE IF EXISTS sale_order CASCADE;
CREATE TABLE sale_order (
  order_id SERIAL PRIMARY KEY,
  order_date DATE NOT NULL,
  customer_name VARCHAR(100),
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP INDEX IF EXISTS idx_order_order_date;
CREATE INDEX idx_order_order_date ON sale_order(order_date);

COMMENT ON TABLE sale_order IS '訂單資料表，紀錄客戶訂單資訊';
COMMENT ON COLUMN sale_order.order_id IS '訂單唯一識別碼，自動遞增';
COMMENT ON COLUMN sale_order.order_date IS '訂單日期';
COMMENT ON COLUMN sale_order.customer_name IS '客戶名稱';
COMMENT ON COLUMN sale_order.total_amount IS '訂單總金額';
COMMENT ON COLUMN sale_order.created_at IS '資料建立時間';


-- 初始化 order_item 訂單明細資料表
DROP TABLE IF EXISTS order_item CASCADE;
CREATE TABLE order_item (
  order_item_id SERIAL PRIMARY KEY,
  order_id INT NOT NULL REFERENCES sale_order(order_id) ON DELETE CASCADE,
  product_id INT NOT NULL REFERENCES product(product_id),
  product_name VARCHAR(100) NOT NULL,  -- 當下訂單的商品名稱備份，避免商品改名影響歷史訂單
  quantity INT NOT NULL CHECK (quantity > 0),
  sale_price DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total_price DECIMAL(12,2) GENERATED ALWAYS AS (quantity * sale_price) STORED
);

CREATE INDEX idx_order_item_order_id ON order_item(order_id);

COMMENT ON TABLE order_item IS '訂單商品明細，紀錄每筆訂單所包含的商品與數量';
COMMENT ON COLUMN order_item.order_item_id IS '訂單明細唯一識別碼';
COMMENT ON COLUMN order_item.order_id IS '所屬訂單 ID';
COMMENT ON COLUMN order_item.product_id IS '商品 ID';
COMMENT ON COLUMN order_item.product_name IS '商品名稱備份';
COMMENT ON COLUMN order_item.quantity IS '商品數量';
COMMENT ON COLUMN order_item.sale_price IS '銷售單價備份';
COMMENT ON COLUMN order_item.created_at IS '建立時間';
COMMENT ON COLUMN order_item.total_price IS '總價格';

-- 初始化建立庫存查詢視圖
DROP VIEW IF EXISTS product_inventory_view;
CREATE OR REPLACE VIEW product_inventory_view AS
SELECT 
    p.product_id,
    p.name,
    p.category,
    p.unit,
    p.price,
    COALESCE(SUM(pb.quantity), 0) as total_stock,
    COALESCE(SUM(CASE WHEN pb.expiration_date > CURRENT_DATE THEN pb.quantity ELSE 0 END), 0) as available_stock,
    COALESCE(SUM(CASE WHEN pb.expiration_date <= CURRENT_DATE THEN pb.quantity ELSE 0 END), 0) as expired_stock,
    MIN(CASE WHEN pb.quantity > 0 AND pb.expiration_date > CURRENT_DATE THEN pb.expiration_date END) as nearest_expiry_date
FROM product p
LEFT JOIN product_batch pb ON p.product_id = pb.product_id
WHERE p.enabled = TRUE
GROUP BY p.product_id, p.name, p.category, p.unit, p.price;

COMMENT ON VIEW product_inventory_view IS '商品庫存彙總檢視表，提供每個商品的總庫存、可用庫存、已過期庫存與最接近的有效期限';

COMMENT ON COLUMN product_inventory_view.product_id IS '商品唯一識別碼';
COMMENT ON COLUMN product_inventory_view.name IS '商品名稱';
COMMENT ON COLUMN product_inventory_view.category IS '商品分類';
COMMENT ON COLUMN product_inventory_view.unit IS '商品單位';
COMMENT ON COLUMN product_inventory_view.price IS '商品單價';

COMMENT ON COLUMN product_inventory_view.total_stock IS '總庫存數量，包含所有批次的商品數量';
COMMENT ON COLUMN product_inventory_view.available_stock IS '可用庫存數量，僅包含尚未過期的商品批次';
COMMENT ON COLUMN product_inventory_view.expired_stock IS '已過期庫存數量，包含所有已過期的商品批次數量';
COMMENT ON COLUMN product_inventory_view.nearest_expiry_date IS '最近一筆尚未過期庫存的到期日，用於提示即將過期商品';

-- 初始化低庫存警告視圖
DROP VIEW IF EXISTS low_stock_alert_view;
CREATE OR REPLACE VIEW low_stock_alert_view AS
SELECT 
    p.product_id,
    p.name,
    p.min_stock,
    COALESCE(SUM(CASE WHEN pb.expiration_date > CURRENT_DATE THEN pb.quantity ELSE 0 END), 0) as available_stock,
    (p.min_stock - COALESCE(SUM(CASE WHEN pb.expiration_date > CURRENT_DATE THEN pb.quantity ELSE 0 END), 0)) as shortage
FROM product p
LEFT JOIN product_batch pb ON p.product_id = pb.product_id
WHERE p.enabled = TRUE
GROUP BY p.product_id, p.name, p.min_stock
HAVING COALESCE(SUM(CASE WHEN pb.expiration_date > CURRENT_DATE THEN pb.quantity ELSE 0 END), 0) < p.min_stock;

-- 初始化即將到期商品警告視圖
DROP VIEW IF EXISTS expiry_alert_view;
CREATE OR REPLACE VIEW expiry_alert_view AS
SELECT 
    p.product_id,
    p.name,
    pb.batch_id,
    pb.batch_code,
    pb.quantity,
    pb.expiration_date,
    pb.supplier_name,
    (pb.expiration_date - CURRENT_DATE) as days_to_expiry
FROM product p
JOIN product_batch pb ON p.product_id = pb.product_id
WHERE p.enabled = TRUE
AND pb.quantity > 0
AND pb.expiration_date > CURRENT_DATE
AND pb.expiration_date <= CURRENT_DATE + INTERVAL '1 day' * p.expiry_alert_days
ORDER BY pb.expiration_date ASC;

-- 初始化庫存扣除觸發器
-- 1. 建立庫存扣除函數
CREATE OR REPLACE FUNCTION deduct_inventory()
RETURNS TRIGGER AS $$
DECLARE
    remaining_qty INTEGER;
    batch_record RECORD;
    deduct_qty INTEGER;
    total_available INTEGER;
BEGIN
    -- 初始化剩餘數量
    remaining_qty := NEW.quantity;
    
    -- 檢查總庫存是否足夠
    SELECT COALESCE(SUM(quantity), 0) INTO total_available
    FROM product_batch 
    WHERE product_id = NEW.product_id 
    AND quantity > 0 
    AND expiration_date > CURRENT_DATE;
    
    -- 如果庫存不足，拋出錯誤
    IF total_available < remaining_qty THEN
        RAISE EXCEPTION '商品ID % 庫存不足，需要 % 個，可用庫存 % 個', 
            NEW.product_id, remaining_qty, total_available;
    END IF;
    
    -- 按照到期日由近到遠扣除庫存
    FOR batch_record IN 
        SELECT batch_id, quantity, expiration_date
        FROM product_batch 
        WHERE product_id = NEW.product_id 
        AND quantity > 0 
        AND expiration_date > CURRENT_DATE
        ORDER BY expiration_date ASC, batch_id ASC
    LOOP
        -- 如果已經扣完了，跳出循環
        IF remaining_qty <= 0 THEN
            EXIT;
        END IF;
        
        -- 計算這批要扣多少
        deduct_qty := LEAST(remaining_qty, batch_record.quantity);
        
        -- 更新批次庫存
        UPDATE product_batch 
        SET quantity = quantity - deduct_qty
        WHERE batch_id = batch_record.batch_id;
        
        -- 更新剩餘需要扣除的數量
        remaining_qty := remaining_qty - deduct_qty;
        
        -- 記錄扣除明細（可選，用於追蹤）
        INSERT INTO inventory_transaction (
            product_id, 
            batch_id, 
            transaction_type, 
            quantity, 
            order_id, 
            transaction_date
        ) VALUES (
            NEW.product_id, 
            batch_record.batch_id, 
            'SALE', 
            -deduct_qty, 
            NEW.order_id, 
            CURRENT_TIMESTAMP
        );
    END LOOP;
    
    -- 最終檢查是否成功扣除完畢
    IF remaining_qty > 0 THEN
        RAISE EXCEPTION '庫存扣除失敗，商品ID % 還有 % 個未扣除', 
            NEW.product_id, remaining_qty;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2. 建立庫存交易記錄表（可選，用於追蹤庫存變動）
CREATE TABLE IF NOT EXISTS inventory_transaction (
    transaction_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(product_id),
    batch_id INT REFERENCES product_batch(batch_id),
    transaction_type VARCHAR(20) NOT NULL, -- 'PURCHASE', 'SALE', 'ADJUSTMENT'
    quantity INT NOT NULL, -- 正數表示增加，負數表示減少
    order_id INT REFERENCES sale_order(order_id),
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note TEXT
);

-- 3. 建立 trigger
DROP TRIGGER IF EXISTS trigger_deduct_inventory ON order_item;
CREATE TRIGGER trigger_deduct_inventory
    AFTER INSERT ON order_item
    FOR EACH ROW
    EXECUTE FUNCTION deduct_inventory();

-- 4. 建立訂單總金額更新函數
CREATE OR REPLACE FUNCTION update_order_total()
RETURNS TRIGGER AS $$
BEGIN
    -- 更新訂單總金額
    UPDATE sale_order 
    SET total_amount = (
        SELECT COALESCE(SUM(total_price), 0)
        FROM order_item 
        WHERE order_id = COALESCE(NEW.order_id, OLD.order_id)
    )
    WHERE order_id = COALESCE(NEW.order_id, OLD.order_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- 5. 建立更新訂單總金額的 trigger
DROP TRIGGER IF EXISTS trigger_update_order_total ON order_item;
CREATE TRIGGER trigger_update_order_total
    AFTER INSERT OR UPDATE OR DELETE ON order_item
    FOR EACH ROW
    EXECUTE FUNCTION update_order_total();

-- 6. 建立庫存查詢視圖
CREATE OR REPLACE VIEW product_inventory_view AS
SELECT 
    p.product_id,
    p.name,
    p.category,
    p.unit,
    p.price,
    COALESCE(SUM(pb.quantity), 0) as total_stock,
    COALESCE(SUM(CASE WHEN pb.expiration_date > CURRENT_DATE THEN pb.quantity ELSE 0 END), 0) as available_stock,
    COALESCE(SUM(CASE WHEN pb.expiration_date <= CURRENT_DATE THEN pb.quantity ELSE 0 END), 0) as expired_stock,
    MIN(CASE WHEN pb.quantity > 0 AND pb.expiration_date > CURRENT_DATE THEN pb.expiration_date END) as nearest_expiry_date
FROM product p
LEFT JOIN product_batch pb ON p.product_id = pb.product_id
WHERE p.enabled = TRUE
GROUP BY p.product_id, p.name, p.category, p.unit, p.price;

-- 初始化訂單建立函數
CREATE OR REPLACE FUNCTION create_sale_order_json(
    p_order_date DATE,
    p_customer_name VARCHAR(100),
    p_order_items JSONB
)
RETURNS TABLE(
    order_id INTEGER,
    total_amount DECIMAL(12,2),
    message TEXT,
    success BOOLEAN
) AS $$
DECLARE
    v_order_id INTEGER;
    v_total_amount DECIMAL(12,2) := 0;
    v_item JSONB;
    v_product_id INTEGER;
    v_product_name VARCHAR(100);
    v_quantity INTEGER;
    v_sale_price DECIMAL(10,2);
    v_available_stock INTEGER;
    v_product_enabled BOOLEAN;
    v_message TEXT := '';
BEGIN
    -- 驗證訂單明細不得為空
    IF p_order_items IS NULL OR jsonb_array_length(p_order_items) = 0 THEN
        RETURN QUERY SELECT NULL, 0, '訂單明細不能為空', FALSE;
        RETURN;
    END IF;

    -- 預檢所有商品項目
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_order_items)
    LOOP
        v_product_id := (v_item ->> 'product_id')::INTEGER;
        v_product_name := v_item ->> 'product_name';
        v_quantity := (v_item ->> 'quantity')::INTEGER;
        v_sale_price := (v_item ->> 'sale_price')::DECIMAL;

        -- 商品存在與啟用性檢查
        SELECT name, enabled INTO v_product_name, v_product_enabled
        FROM product
        WHERE product_id = v_product_id;

        IF NOT FOUND THEN
            RETURN QUERY SELECT NULL, 0, format('商品ID %s 不存在', v_product_id), FALSE;
            RETURN;
        END IF;

        IF NOT v_product_enabled THEN
            RETURN QUERY SELECT NULL, 0, format('商品 %s 已停用', v_product_name), FALSE;
            RETURN;
        END IF;

        -- 庫存檢查
        SELECT COALESCE(SUM(quantity), 0) INTO v_available_stock
        FROM product_batch
        WHERE product_id = v_product_id
          AND quantity > 0
          AND expiration_date > CURRENT_DATE;

        IF v_available_stock < v_quantity THEN
            RETURN QUERY SELECT NULL, 0,
                format('商品 %s 庫存不足，需要 %s 個，可用庫存 %s 個', v_product_name, v_quantity, v_available_stock), FALSE;
            RETURN;
        END IF;

        -- 數量、價格合法性檢查
        IF v_quantity <= 0 THEN
            RETURN QUERY SELECT NULL, 0, format('商品 %s 的數量必須大於 0', v_product_name), FALSE;
            RETURN;
        END IF;

        IF v_sale_price < 0 THEN
            RETURN QUERY SELECT NULL, 0, format('商品 %s 的價格不能為負數', v_product_name), FALSE;
            RETURN;
        END IF;
    END LOOP;

    -- 建立訂單主檔
    INSERT INTO sale_order (order_date, customer_name, total_amount)
    VALUES (p_order_date, p_customer_name, 0)
    RETURNING order_id INTO v_order_id;

    -- 寫入訂單明細
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_order_items)
    LOOP
        v_product_id := (v_item ->> 'product_id')::INTEGER;
        v_product_name := v_item ->> 'product_name';
        v_quantity := (v_item ->> 'quantity')::INTEGER;
        v_sale_price := (v_item ->> 'sale_price')::DECIMAL;

        INSERT INTO order_item (order_id, product_id, product_name, quantity, sale_price)
        VALUES (v_order_id, v_product_id, v_product_name, v_quantity, v_sale_price);

        v_total_amount := v_total_amount + (v_quantity * v_sale_price);
    END LOOP;

    -- 更新總金額
    UPDATE sale_order
    SET total_amount = v_total_amount
    WHERE order_id = v_order_id;

    -- 回傳結果
    v_message := format('訂單建立成功，訂單編號：%s', v_order_id);
    RETURN QUERY SELECT v_order_id, v_total_amount, v_message, TRUE;

EXCEPTION
    WHEN OTHERS THEN
        RETURN QUERY SELECT NULL, 0, format('訂單建立失敗：%s', SQLERRM), FALSE;
END;
$$ LANGUAGE plpgsql;


--------------
--------------
--------------
--------------
-- 建立測試資料
INSERT INTO supplier(name)VALUES('凱亞良品');
INSERT INTO product (name, category, unit,price,min_stock) VALUES
('虱目魚肚/130-150g', '虱目魚系列', '片',148,600),
('虱目魚肚(小)/150-170g', '虱目魚系列', '片',158,600),
('虱目魚肚(中)/170-200g', '虱目魚系列', '片',178,600),
('虱目魚皮', '虱目魚系列', '包',118,600),

('金目鱸魚排/250-300g', '金目鱸系列', '片',238,600),
('金目鱸魚排/300-350g', '金目鱸系列', '片',268,600),
('金目鱸魚下巴', '金目鱸系列', '包',98,600),

('100%純烏魚鬆120g', '休閒食品(常溫)', '罐',370,600),
('頂級烏魚子醬', '休閒食品(常溫)', '瓶',750,600),
('乾燥魚鱗', '休閒食品(常溫)', '包',268,600),

('安心鮮撥白蝦仁', '達人鮮蝦系列', '包',258,600),
('達人白蝦', '達人鮮蝦系列', '包',368,600),

('原味烏魚排/300-400g', '烏魚系列', '片',268,600),
('原味烏魚排/400-500g', '烏魚系列', '片',298,600),
('烏魚腱/300g', '烏魚系列', '片',588,600);

INSERT INTO product_batch (
  product_id, batch_code, quantity,
  expiration_date, purchase_price, received_date,
  supplier_id, supplier_name
)
SELECT
  (floor(random() * 15)::int + 1),  -- 隨機 product_id (1-15)
  'BATCH-TEST-' || i,
  (random() * 50)::int + 20,  -- 數量 20-70
  CURRENT_DATE + (random() * 20 +6)::int,  -- 6-20天內到期
  (random() * 888)::numeric(10,2) + 112,  -- 價格 128-888
  CURRENT_DATE - (random() * 180 + 30)::int,  -- 30-210天前收到
  1,  -- 供應商ID 1
  	(SELECT name FROM supplier WHERE supplier_id = 1)
  END
FROM generate_series(1, 200) AS i;  -- 創建200筆測試資料
--------------
--------------
--------------
--------------
-- 列出所有資料表
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_type = 'BASE TABLE'
  AND table_schema NOT IN ('pg_catalog', 'information_schema');

SELECT * FROM low_stock_alert_view