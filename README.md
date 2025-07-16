# ERPoor (凱亞 ERP)

一個專為中小企業設計的全端 ERP 系統，提供庫存管理、銷售訂單處理和客戶資料管理功能。

## 📋 目錄

- [系統概述](#系統概述)
- [功能特色](#功能特色)
- [技術架構](#技術架構)
- [快速開始](#快速開始)
- [開發環境設置](#開發環境設置)
- [API 文檔](#api-文檔)
- [資料庫結構](#資料庫結構)
- [部署指南](#部署指南)
- [貢獻指南](#貢獻指南)
- [授權條款](#授權條款)

## 系統概述

ERPoor 是一個現代化的企業資源規劃系統，採用前後端分離架構。前端使用 Vue 3 + Quasar Framework 構建響應式用戶界面，後端採用 Spring Boot 3.5.0 提供強大的 API 服務。

### 主要模組

- **庫存管理** - 產品管理、庫存追蹤、庫存預警
- **銷售訂單** - 訂單處理、客戶管理、銷售分析
- **儀表板** - 業務數據可視化、關鍵指標監控
- **用戶管理** - 身份驗證、角色權限、安全控制

## 功能特色

### 🎨 用戶界面
- **響應式設計** - 支援桌面和移動設備
- **多主題支持** - 深色/淺色主題切換
- **國際化支持** - 預設繁體中文，可擴展多語言
- **Material Design** - 現代化的界面設計

### 💼 業務功能
- **即時庫存追蹤** - 自動更新庫存狀態
- **銷售訂單管理** - 完整的訂單處理流程
- **客戶關係管理** - 客戶資料與銷售記錄
- **數據分析** - 業務報表與趨勢分析

### 🔒 安全性
- **JWT 身份驗證** - 安全的用戶認證機制
- **CORS 配置** - 跨域請求安全控制
- **輸入驗證** - 前後端雙重數據驗證
- **SQL 注入防護** - 使用 JPA 防止資料庫攻擊

## 技術架構

### 前端技術棧
```
Vue 3 (Composition API)
├── Quasar Framework v2.16.0
├── TypeScript (嚴格模式)
├── Pinia (狀態管理)
├── Vue Router 4
├── Vue i18n (國際化)
└── Vite (建構工具)
```

### 後端技術棧
```
Spring Boot 3.5.0
├── Spring Security (JWT)
├── Spring Data JPA
├── PostgreSQL
├── Maven
└── Java 17
```

### 系統架構圖
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   Vue 3 前端    │───▶│  Spring Boot    │───▶│   PostgreSQL    │
│   (Port 80)     │    │   後端 API      │    │     資料庫      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 快速開始

### 系統需求

- **Node.js** 18.x 或更高版本
- **Java** 17 或更高版本
- **PostgreSQL** 13 或更高版本
- **Git** 版本控制

### 快速安裝

```bash
# 克隆專案
git clone <repository-url>
cd erpoor

# 安裝前端依賴
cd Frontend
npm install

# 安裝後端依賴
cd ../Backend
./mvnw clean install
```

### 資料庫設置

1. 創建 PostgreSQL 資料庫
2. 執行初始化腳本：
```sql
-- 執行 Backend/initialize.sql
```

3. 更新 `application.properties` 中的資料庫連接配置

### 啟動應用

```bash
# 啟動後端服務
cd Backend
./mvnw spring-boot:run

# 啟動前端開發服務器
cd Frontend
npm run dev
```

訪問 `http://localhost` 即可使用系統。

## 開發環境設置

### 前端開發

```bash
cd Frontend

# 開發模式
npm run dev

# 建構生產版本
npm run build

# 代碼檢查
npm run lint

# 代碼格式化
npm run format
```

### 後端開發

```bash
cd Backend

# 運行應用
./mvnw spring-boot:run

# 運行測試
./mvnw test

# 建構 JAR 檔案
./mvnw clean package
```

### 開發規範

#### 前端
- 使用 Composition API 與 `<script setup>`
- TypeScript 嚴格模式
- Quasar 組件響應式設計
- 使用 ESLint 和 Prettier 保持代碼品質

#### 後端
- 遵循 Spring Boot 最佳實踐
- RESTful API 設計
- JPA 實體關係映射
- 完整的單元測試覆蓋

## API 文檔

### 身份驗證
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

### 銷售訂單
```http
# 獲取訂單列表
GET /saleOrder

# 創建新訂單
POST /saleOrder

# 獲取產品選項
GET /saleOrder/products
```

### 儀表板
```http
# 獲取儀表板數據
GET /dashboard
```

## 資料庫結構

### 主要數據表

- **users** - 用戶帳戶資訊
- **products** - 產品主檔
- **inventory** - 庫存記錄
- **sale_orders** - 銷售訂單
- **customers** - 客戶資料

### 資料庫視圖

- **inventory_view** - 庫存狀態匯總
- **alert_view** - 庫存預警查詢

## 部署指南

### 生產環境部署

1. **前端部署**
```bash
cd Frontend
npm run build
# 將 dist/ 目錄部署到 Web 服務器
```

2. **後端部署**
```bash
cd Backend
./mvnw clean package
# 部署 target/*.jar 到應用服務器
```

3. **環境配置**
- 設置生產環境資料庫連接
- 配置 JWT 密鑰
- 調整 CORS 設置

### Docker 部署

```dockerfile
# 前端 Dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 後端 Dockerfile
FROM openjdk:17-jdk-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 專案結構

```
erpoor/
├── Frontend/                 # 前端應用
│   ├── src/
│   │   ├── components/      # 可重用組件
│   │   ├── pages/          # 頁面組件
│   │   ├── layouts/        # 布局組件
│   │   ├── stores/         # Pinia 狀態管理
│   │   ├── router/         # 路由配置
│   │   └── boot/           # 啟動配置
│   ├── quasar.config.ts    # Quasar 設定
│   └── package.json
├── Backend/                 # 後端應用
│   ├── src/main/java/
│   │   ├── account/        # 用戶管理模組
│   │   ├── dashboard/      # 儀表板模組
│   │   ├── inventory/      # 庫存管理模組
│   │   ├── saleOrder/      # 銷售訂單模組
│   │   └── util/           # 通用工具
│   ├── pom.xml
│   └── initialize.sql      # 資料庫初始化
├── README.md
└── CLAUDE.md               # 開發指南
```

## 貢獻指南

1. Fork 本專案
2. 創建功能分支 (`git checkout -b feature/新功能`)
3. 提交更改 (`git commit -am '添加新功能'`)
4. 推送分支 (`git push origin feature/新功能`)
5. 創建 Pull Request

### 代碼提交規範

- 使用清晰的提交信息
- 遵循現有代碼風格
- 添加必要的測試
- 更新相關文檔

## 常見問題

### Q: 如何重置管理員密碼？
A: 直接在資料庫中更新 users 表的密碼欄位。

### Q: 如何添加新的產品類別？
A: 在產品管理頁面添加，或直接在資料庫中插入新記錄。

### Q: 系統支援多用戶同時使用嗎？
A: 是的，系統支援多用戶併發訪問。

## 授權條款

本專案採用 MIT 授權條款。詳細內容請參閱 [LICENSE](LICENSE) 文件。

---

**開發團隊**  
如有任何問題或建議，請提交 Issue 或聯繫開發團隊。
