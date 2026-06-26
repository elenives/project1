-- ============================================================
-- 数据库示例：基本 SQL + 索引
-- 索引说明：
--   idx_user_email     -> 唯一索引，加速按邮箱查询、保证唯一
--   idx_order_user_id  -> 普通索引，加速按用户查订单（外键关联场景）
--   idx_order_status   -> 组合索引前缀，加速按状态筛选
-- ============================================================

CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL,
    balance     DECIMAL(10, 2) DEFAULT 0.00,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON t_user (email);
CREATE INDEX IF NOT EXISTS idx_user_username ON t_user (username);

CREATE TABLE IF NOT EXISTS t_order (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    product     VARCHAR(100) NOT NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    status      VARCHAR(20)  DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_order_user_id ON t_order (user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON t_order (status);
