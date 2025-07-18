<template>
  <div class="analytics-page">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1 class="page-title">
        <q-icon name="analytics" class="title-icon" />
        數據分析中心
      </h1>
      <p class="page-subtitle">深入洞察您的業務數據與倉儲狀況</p>
    </div>

    <!-- 施工中提示 -->
    <div class="construction-notice">
      <q-icon name="construction" class="construction-icon" />
      <div class="construction-text">
        <h3>分析功能施工中</h3>
        <p>正在努力為您打造更好的分析體驗，敬請期待！</p>
      </div>
    </div>

    <!-- 分析區塊 -->
    <div class="analytics-container">
      <!-- 庫存分析卡片 -->
      <div class="analytics-card">
        <div class="card-header">
          <q-icon name="inventory" class="card-icon" />
          <h3>庫存分析</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">庫存分析功能即將推出</p>
          <div class="placeholder-items">
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="100px" />
            </div>
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="100px" />
            </div>
          </div>
        </div>
      </div>

      <!-- 銷售分析卡片 -->
      <div class="analytics-card">
        <div class="card-header">
          <q-icon name="trending_up" class="card-icon" />
          <h3>銷售分析</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">銷售分析功能即將推出</p>
          <div class="placeholder-items">
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="100px" />
            </div>
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="100px" />
            </div>
          </div>
        </div>
      </div>

      <!-- 報表區塊 -->
      <div class="analytics-card full-width">
        <div class="card-header">
          <q-icon name="summarize" class="card-icon" />
          <h3>報表中心</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">專業報表功能即將推出</p>

          <!-- 報表預覽區 -->
          <div class="reports-grid">
            <div class="report-item">
              <q-icon name="insert_chart" class="report-icon" />
              <h4>月度庫存報表</h4>
              <q-skeleton type="rect" height="80px" />
            </div>
            <div class="report-item">
              <q-icon name="show_chart" class="report-icon" />
              <h4>年度庫存報表</h4>
              <q-skeleton type="rect" height="80px" />
            </div>
            <div class="report-item">
              <q-icon name="assessment" class="report-icon" />
              <h4>商品銷售表現</h4>
              <q-skeleton type="rect" height="80px" />
            </div>
            <div class="report-item">
              <q-icon name="stacked_line_chart" class="report-icon" />
              <h4>庫存周轉率</h4>
              <q-skeleton type="rect" height="80px" />
            </div>
          </div>
        </div>
      </div>

      <!-- 預警系統卡片 -->
      <div class="analytics-card">
        <div class="card-header">
          <q-icon name="warning" class="card-icon" />
          <h3>庫存預警</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">智能預警系統即將推出</p>
          <div class="placeholder-items">
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="60px" />
            </div>
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="60px" />
            </div>
          </div>
        </div>
      </div>

      <!-- 效率分析卡片 -->
      <div class="analytics-card">
        <div class="card-header">
          <q-icon name="speed" class="card-icon" />
          <h3>倉儲效率</h3>
        </div>
        <div class="card-content">
          <p class="coming-soon">效率分析工具即將推出</p>
          <div class="placeholder-items">
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="60px" />
            </div>
            <div class="placeholder-item">
              <q-skeleton type="text" width="120px" />
              <q-skeleton type="rect" height="60px" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 未來將添加數據分析相關邏輯
</script>

<style scoped>
.analytics-page {
  min-height: 100vh;
  padding: 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.title-icon {
  font-size: 2.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  margin: 0;
  font-weight: 300;
}

.construction-notice {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1rem;
  padding: 2rem;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.construction-icon {
  font-size: 3rem;
  color: #f39c12;
}

.construction-text h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.construction-text p {
  margin: 0;
  color: #7f8c8d;
  font-size: 1rem;
}

.analytics-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.analytics-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.analytics-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.analytics-card.full-width {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.card-icon {
  font-size: 1.5rem;
  color: #667eea;
}

.card-header h3 {
  margin: 0;
  font-size: 1.25rem;
  color: #2c3e50;
  font-weight: 600;
}

.card-content {
  color: #7f8c8d;
}

.coming-soon {
  margin: 0 0 1rem 0;
  font-style: italic;
  color: #95a5a6;
}

.placeholder-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.placeholder-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.report-item {
  background: rgba(245, 245, 245, 0.7);
  border-radius: 0.75rem;
  padding: 1rem;
  text-align: center;
  transition: all 0.3s ease;
}

.report-item:hover {
  background: rgba(235, 235, 235, 0.9);
  transform: translateY(-2px);
}

.report-icon {
  font-size: 2rem;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.report-item h4 {
  margin: 0.5rem 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
}

/* 動畫效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.analytics-card,
.construction-notice {
  animation: fadeInUp 0.6s ease-out;
}

.analytics-card:nth-child(1) {
  animation-delay: 0.1s;
}

.analytics-card:nth-child(2) {
  animation-delay: 0.2s;
}

.analytics-card:nth-child(3) {
  animation-delay: 0.3s;
}

.analytics-card:nth-child(4) {
  animation-delay: 0.4s;
}

.analytics-card:nth-child(5) {
  animation-delay: 0.5s;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .analytics-page {
    padding: 1rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .construction-notice {
    flex-direction: column;
    text-align: center;
    padding: 1.5rem;
  }

  .analytics-container {
    grid-template-columns: 1fr;
  }

  .reports-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 480px) {
  .reports-grid {
    grid-template-columns: 1fr;
  }
}
</style>
