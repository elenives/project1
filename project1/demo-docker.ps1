# Docker 环境完整演示脚本
# 前提: docker compose up -d --build 已成功，且 app 容器运行中

$base = "http://localhost:8080"
$h = @{ "X-Token" = "learning-token-123" }

Write-Host "`n=== 1. 健康检查 ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/public/health" | ConvertTo-Json

Write-Host "`n=== 2. 用户列表 (MVC + DI + AOP) ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/users" -Headers $h | ConvertTo-Json -Depth 5

Write-Host "`n=== 3. 创建订单 (事务) ===" -ForegroundColor Green
$body = '{"userId":1,"product":"Docker演示商品","amount":99}'
Invoke-RestMethod "$base/api/orders" -Method POST -Headers $h -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 5

Write-Host "`n=== 4. 下单后余额 ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/users/1" -Headers $h | Select-Object username, balance

Write-Host "`n=== 5. Redis String ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/redis/string?key=demo&value=hello-docker" -Method POST -Headers $h | ConvertTo-Json

Write-Host "`n=== 6. Redis 分布式锁 ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/redis/lock-demo?resource=order-1" -Method POST -Headers $h | ConvertTo-Json

Write-Host "`n=== 7. RocketMQ 发消息 ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/mq/send?tag=notify&message=Docker演示消息" -Method POST -Headers $h | ConvertTo-Json

Write-Host "`n=== 8. 事务回滚 ===" -ForegroundColor Green
Invoke-RestMethod "$base/api/orders/demo-rollback?userId=1&amount=1" -Method POST -Headers $h | ConvertTo-Json

Write-Host "`n演示完成！查看 MQ 消费日志: docker compose logs -f app" -ForegroundColor Cyan
