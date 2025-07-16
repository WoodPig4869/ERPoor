ALTER DATABASE erpoordb SET TIMEZONE TO 'Asia/Taipei';

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

-- 初始化 sale_order 訂單編號生成函式
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS VARCHAR(20) AS $$
DECLARE
    new_number VARCHAR(20);
    counter INT;
    roc_year TEXT;
BEGIN
    SELECT TO_CHAR(EXTRACT(YEAR FROM CURRENT_DATE) - 1911, 'FM000') INTO roc_year;

    SELECT COALESCE(MAX(CAST(SUBSTRING(order_number, 9) AS INT)), 0) + 1
    INTO counter
    FROM sale_order 
    WHERE order_number LIKE 'ORD-' || roc_year || '-%';

    new_number := 'ORD-' || roc_year || '-' || LPAD(counter::TEXT, 3, '0');
    
    RETURN new_number;
END;
$$ LANGUAGE plpgsql;

-- 初始化 sale_order 訂單資料表
DROP TABLE IF EXISTS sale_order CASCADE;
CREATE TABLE sale_order (
  order_id SERIAL PRIMARY KEY,
  order_number VARCHAR(20) UNIQUE NOT NULL DEFAULT generate_order_number(),
  order_date DATE NOT NULL,
  
  -- 客戶資訊
  customer_name VARCHAR(100) NOT NULL,
  customer_phone VARCHAR(20),
  customer_email VARCHAR(100),
  customer_address TEXT,
  
  -- 訂單狀態和金額
  order_status VARCHAR(20) NOT NULL DEFAULT 'pending',
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  
  -- 備註和時間戳
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  shipped_at TIMESTAMP,
  cancelled_at TIMESTAMP,
  cancelled_reason TEXT
);
CREATE INDEX idx_order_order_date ON sale_order(order_date);
CREATE INDEX idx_order_status ON sale_order(order_status);
CREATE INDEX idx_order_customer_name ON sale_order(customer_name);
CREATE UNIQUE INDEX idx_order_number ON sale_order(order_number);

COMMENT ON TABLE sale_order IS '訂單資料主表';
COMMENT ON COLUMN sale_order.order_id IS '訂單編號';
COMMENT ON COLUMN sale_order.order_number IS '訂單編號';
COMMENT ON COLUMN sale_order.order_date IS '訂單日期';
COMMENT ON COLUMN sale_order.customer_name IS '客戶名稱';
COMMENT ON COLUMN sale_order.customer_phone IS '客戶電話';
COMMENT ON COLUMN sale_order.customer_email IS '客戶電子郵件';
COMMENT ON COLUMN sale_order.customer_address IS '客戶地址';
COMMENT ON COLUMN sale_order.order_status IS '訂單狀態';
COMMENT ON COLUMN sale_order.total_amount IS '總金額';
COMMENT ON COLUMN sale_order.notes IS '備註';
COMMENT ON COLUMN sale_order.created_at IS '建立時間';
COMMENT ON COLUMN sale_order.updated_at IS '更新時間';
COMMENT ON COLUMN sale_order.shipped_at IS '出貨時間';
COMMENT ON COLUMN sale_order.cancelled_at IS '取消時間';
COMMENT ON COLUMN sale_order.cancelled_reason IS '取消原因';

-- 自動更新 updated_at 的觸發器
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_sale_order_updated_at 
    BEFORE UPDATE ON sale_order 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 狀態變更時自動更新時間戳的觸發器
CREATE OR REPLACE FUNCTION update_order_status_timestamps()
RETURNS TRIGGER AS $$
BEGIN
    -- 當狀態變更為已出貨時，設定出貨時間
    IF NEW.order_status = 'shipped' AND OLD.order_status != 'shipped' THEN
        NEW.shipped_at = CURRENT_TIMESTAMP;
    END IF;
    
    -- 當狀態變更為已取消時，設定取消時間
    IF NEW.order_status = 'cancelled' AND OLD.order_status != 'cancelled' THEN
        NEW.cancelled_at = CURRENT_TIMESTAMP;
    END IF;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_order_status_timestamps_trigger
    BEFORE UPDATE ON sale_order 
    FOR EACH ROW EXECUTE FUNCTION update_order_status_timestamps();

-- 初始化 order_item 訂單明細資料表
DROP TABLE IF EXISTS order_item CASCADE;
CREATE TABLE order_item (
  order_item_id SERIAL PRIMARY KEY,
  order_id INT NOT NULL REFERENCES sale_order(order_id) ON DELETE CASCADE,
  product_id INT NOT NULL REFERENCES product(product_id),
  
  -- 商品資訊備份（避免商品資料變更影響歷史訂單）
  product_name VARCHAR(100) NOT NULL,
  product_sku VARCHAR(50), 
  
  quantity INT NOT NULL CHECK (quantity > 0),
  sale_price DECIMAL(10,2) NOT NULL CHECK (sale_price >= 0),
  total_price DECIMAL(12,2) GENERATED ALWAYS AS (quantity * sale_price) STORED,
  
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);

COMMENT ON TABLE order_item IS '訂單商品明細，紀錄每筆訂單所包含的商品與數量';
COMMENT ON COLUMN order_item.order_item_id IS '訂單明細唯一識別碼，自動遞增';
COMMENT ON COLUMN order_item.order_id IS '訂單唯一識別碼';
COMMENT ON COLUMN order_item.product_id IS '商品唯一識別碼';
COMMENT ON COLUMN order_item.product_name IS '商品名稱';
COMMENT ON COLUMN order_item.product_sku IS '商品編號';
COMMENT ON COLUMN order_item.quantity IS '商品數量';
COMMENT ON COLUMN order_item.sale_price IS '商品售價';
COMMENT ON COLUMN order_item.total_price IS '商品總價';
COMMENT ON COLUMN order_item.created_at IS '明細建立時間';

-- 初始化訂單狀態變更記錄表（用於追蹤狀態變更歷史）
DROP TABLE IF EXISTS order_status_history CASCADE;
CREATE TABLE order_status_history (
  history_id SERIAL PRIMARY KEY,
  order_id INT NOT NULL REFERENCES sale_order(order_id) ON DELETE CASCADE,
  old_status VARCHAR(20),
  new_status VARCHAR(20) NOT NULL,
  changed_by VARCHAR(100),
  change_reason TEXT,
  changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_changed_at ON order_status_history(changed_at);

COMMENT ON TABLE order_status_history IS '訂單狀態變更歷史記錄';
COMMENT ON COLUMN order_status_history.history_id IS '記錄唯一識別碼，自動遞增';
COMMENT ON COLUMN order_status_history.order_id IS '訂單唯一識別碼';
COMMENT ON COLUMN order_status_history.old_status IS '原始狀態';
COMMENT ON COLUMN order_status_history.new_status IS '新狀態';
COMMENT ON COLUMN order_status_history.changed_by IS '操作人';
COMMENT ON COLUMN order_status_history.change_reason IS '變更原因';
COMMENT ON COLUMN order_status_history.changed_at IS '變更時間';

-- 初始化訂單狀態變更觸發器
DROP FUNCTION IF EXISTS log_order_status_change();
CREATE OR REPLACE FUNCTION log_order_status_change()
RETURNS TRIGGER AS $$
DECLARE
    v_operator TEXT;
BEGIN
    IF OLD.order_status != NEW.order_status THEN
        BEGIN
            v_operator := current_setting('app.current_user', true);
        EXCEPTION
            WHEN OTHERS THEN
                v_operator := 'unknown';
        END;

        INSERT INTO order_status_history (
            order_id, old_status, new_status, changed_by, changed_at
        )
        VALUES (
            NEW.order_id,
            OLD.order_status,
            NEW.order_status,
            v_operator,
            CURRENT_TIMESTAMP AT TIME ZONE 'UTC' AT TIME ZONE 'Asia/Taipei'
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER log_order_status_change
BEFORE UPDATE ON sale_order 
FOR EACH ROW EXECUTE FUNCTION log_order_status_change();


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

-- SELECT * FROM sale_order
SELECT * FROM order_item