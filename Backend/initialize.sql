
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
('admin', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'ADMIN','管理者','0912345678'),
('user1', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'USER','王五','0911111111'),
('user2', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'USER','趙六','0922222222'),
('user3', '$2a$10$.UAoeaAVeH8vhPsxHaw1I.teyo3iBunZllqraM1EmHQJwk1CkwD8u', 'USER','孫七','0933333333')
ON CONFLICT (username) DO NOTHING;

-- 初始化 refresh_token 資料表，儲存使用者的 refresh token 與相關資訊
DROP TABLE IF EXISTS refresh_token;
CREATE TABLE refresh_token (
  token_id BIGSERIAL PRIMARY KEY,                    -- refresh_token 唯一識別碼，自動遞增
  user_id INTEGER NOT NULL,                     -- 對應使用者帳號 id
  token VARCHAR(512) NOT NULL UNIQUE,           -- refresh token 字串，唯一且長度限制512字元
  expires_at TIMESTAMP NOT NULL,                 -- refresh token 到期時間
  revoked BOOLEAN NOT NULL DEFAULT FALSE,        -- 是否已被撤銷
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 產生時間
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
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  product_id SERIAL PRIMARY KEY,                       
  name VARCHAR(100) NOT NULL,                          
  category VARCHAR(50),                                
  unit VARCHAR(20) NOT NULL DEFAULT '件',              
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  
);

DROP INDEX IF EXISTS idx_product_name;
CREATE INDEX idx_product_name ON product(name);

COMMENT ON TABLE product IS '商品主檔，紀錄商品基本資訊';
COMMENT ON COLUMN product.product_id IS '商品唯一識別碼，自動遞增';
COMMENT ON COLUMN product.name IS '商品名稱';
COMMENT ON COLUMN product.category IS '商品分類';
COMMENT ON COLUMN product.unit IS '計量單位';
COMMENT ON COLUMN product.created_at IS '商品建立時間';


-- 初始化 supplier 供應商資料表
DROP TABLE IF EXISTS supplier;
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

-- 第一筆供應商資料
INSERT INTO supplier(name)VALUES('凱亞良品');


-- 初始化 product_batch 商品批次資料表
DROP TABLE IF EXISTS product_batch;
CREATE TABLE product_batch (
  batch_id SERIAL PRIMARY KEY,                         
  product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,  
  batch_code VARCHAR(50),                              
  quantity INT NOT NULL CHECK (quantity >= 0),        
  remaining_quantity INT NOT NULL CHECK (remaining_quantity >= 0), 
  expiration_date DATE NOT NULL,                       
  purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0,    
  received_date DATE NOT NULL,                         
  supplier_id INT REFERENCES supplier(supplier_id) ON DELETE SET NULL, 
  supplier_name VARCHAR(100),                           -- 加入 supplier_name
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP             
);

DROP INDEX IF EXISTS idx_product_batch_product_id_expiration_date;
CREATE INDEX idx_product_batch_product_id_expiration_date ON product_batch(product_id, expiration_date);

COMMENT ON TABLE product_batch IS '商品批次資料表，紀錄每批進貨數量與有效日期';
COMMENT ON COLUMN product_batch.batch_id IS '批次唯一識別碼，自動遞增';
COMMENT ON COLUMN product_batch.product_id IS '對應商品 ID';
COMMENT ON COLUMN product_batch.batch_code IS '批號';
COMMENT ON COLUMN product_batch.quantity IS '進貨數量';
COMMENT ON COLUMN product_batch.remaining_quantity IS '剩餘庫存數量';
COMMENT ON COLUMN product_batch.expiration_date IS '有效日期';
COMMENT ON COLUMN product_batch.purchase_price IS '進貨單價';
COMMENT ON COLUMN product_batch.received_date IS '進貨日期';
COMMENT ON COLUMN product_batch.supplier_id IS '供應商 ID';
COMMENT ON COLUMN product_batch.supplier_name IS '供應商名稱，方便查詢';
COMMENT ON COLUMN product_batch.created_at IS '資料建立時間';

-- 初始化 order 訂單資料表
DROP TABLE IF EXISTS "order";  -- order 是保留字，加雙引號
CREATE TABLE "order" (
  order_id SERIAL PRIMARY KEY,                          
  order_date DATE NOT NULL,                             
  customer_name VARCHAR(100),                          
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,      
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP 
);

DROP INDEX IF EXISTS idx_order_order_date;
CREATE INDEX idx_order_order_date ON "order"(order_date);

COMMENT ON TABLE "order" IS '訂單資料表，紀錄客戶訂單資訊';
COMMENT ON COLUMN "order".order_id IS '訂單唯一識別碼，自動遞增';
COMMENT ON COLUMN "order".order_date IS '訂單日期';
COMMENT ON COLUMN "order".customer_name IS '客戶名稱';
COMMENT ON COLUMN "order".total_amount IS '訂單總金額';
COMMENT ON COLUMN "order".created_at IS '資料建立時間';


-- 初始化 order_item 訂單明細資料表
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
  order_item_id SERIAL PRIMARY KEY,
  order_id INT NOT NULL REFERENCES "order"(order_id) ON DELETE CASCADE,
  product_id INT NOT NULL REFERENCES product(product_id),
  product_name VARCHAR(100) NOT NULL,  -- 當下訂單的商品名稱備份，避免商品改名影響歷史訂單
  batch_id INT NOT NULL REFERENCES product_batch(batch_id),
  quantity INT NOT NULL CHECK (quantity > 0),
  sale_price DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_item_order_id ON order_item(order_id);

COMMENT ON TABLE order_item IS '訂單商品明細，紀錄每筆訂單所包含的商品與數量';
COMMENT ON COLUMN order_item.order_item_id IS '訂單明細唯一識別碼';
COMMENT ON COLUMN order_item.order_id IS '所屬訂單 ID';
COMMENT ON COLUMN order_item.product_id IS '商品 ID';
COMMENT ON COLUMN order_item.product_name IS '商品名稱備份';
COMMENT ON COLUMN order_item.batch_id IS '所使用的商品批次 ID';
COMMENT ON COLUMN order_item.quantity IS '商品數量';
COMMENT ON COLUMN order_item.sale_price IS '銷售單價';
COMMENT ON COLUMN order_item.created_at IS '建立時間';
--------------
--------------
--------------
--------------
-- 建立測試資料
INSERT INTO product (name, category, unit) VALUES
('鮭魚切片', '魚類', '公斤'),
('鱈魚排', '魚類', '公斤'),
('虱目魚肚', '魚類', '片'),
('白帶魚', '魚類', '尾'),
('秋刀魚', '魚類', '尾'),
('鯛魚片', '魚類', '公斤'),
('鯖魚一夜干', '魚類', '包'),
('柳葉魚', '魚類', '包'),
('竹筴魚', '魚類', '尾'),
('午仔魚', '魚類', '尾'),

('草蝦', '甲殼類', '盒'),
('白蝦', '甲殼類', '公斤'),
('泰國蝦', '甲殼類', '公斤'),
('龍蝦', '甲殼類', '隻'),
('藍鑽蝦', '甲殼類', '盒'),

('花枝圈', '軟體類', '包'),
('透抽', '軟體類', '尾'),
('小卷', '軟體類', '尾'),
('章魚腳', '軟體類', '包'),
('魷魚乾', '軟體類', '隻'),

('干貝', '貝類', '顆'),
('生蠔', '貝類', '顆'),
('鮑魚', '貝類', '顆'),
('文蛤', '貝類', '斤'),
('赤嘴蛤', '貝類', '斤'),

('海帶芽', '海藻類', '包'),
('紫菜', '海藻類', '包');


--------------
--------------
--------------
--------------
-- 列出所有資料表
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_type = 'BASE TABLE'
  AND table_schema NOT IN ('pg_catalog', 'information_schema');

