INSERT INTO t_user (username, email, balance) VALUES ('张三', 'zhangsan@example.com', 1000.00);
INSERT INTO t_user (username, email, balance) VALUES ('李四', 'lisi@example.com', 500.00);

INSERT INTO t_order (user_id, product, amount, status) VALUES (1, 'Java 书籍', 89.00, 'COMPLETED');
INSERT INTO t_order (user_id, product, amount, status) VALUES (1, 'Spring Boot 教程', 129.00, 'PENDING');
INSERT INTO t_order (user_id, product, amount, status) VALUES (2, 'Redis 实战', 79.00, 'COMPLETED');
