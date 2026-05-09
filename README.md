
---

## 🚀 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

```bash
# 1. 克隆项目
git clone https://github.com/Florence3272/SutixAI.git
cd SutixAI

# 2. 初始化数据库
mysql -u root -p < sql/init.sql

# 3. 启动后端服务
cd backend
./mvnw spring-boot:run

# 4. 启动前端服务
cd ../frontend
npm install
npm run dev
