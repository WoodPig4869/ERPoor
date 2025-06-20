
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


-- 列出所有資料表
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_type = 'BASE TABLE'
  AND table_schema NOT IN ('pg_catalog', 'information_schema');

