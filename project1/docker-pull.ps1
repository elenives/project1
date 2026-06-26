# 国内网络拉取镜像辅助脚本（镜像站 429 时使用）
# 用法: 以管理员或普通 PowerShell 运行 .\docker-pull.ps1

$ErrorActionPreference = "Continue"

$images = @(
    @{ Source = "docker.m.daocloud.io/library/mysql:8.0";           Target = "mysql:8.0" },
    @{ Source = "docker.m.daocloud.io/library/redis:7-alpine";     Target = "redis:7-alpine" },
    @{ Source = "docker.m.daocloud.io/apache/rocketmq:5.1.4";      Target = "apache/rocketmq:5.1.4" }
)

Write-Host "开始从 DaoCloud 镜像拉取..." -ForegroundColor Cyan

foreach ($img in $images) {
    Write-Host "`n>>> 拉取 $($img.Target)" -ForegroundColor Yellow
    docker pull $img.Source
    if ($LASTEXITCODE -eq 0) {
        docker tag $img.Source $img.Target
        Write-Host "OK: $($img.Target)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: $($img.Target)，稍后重试或换网络" -ForegroundColor Red
    }
    Start-Sleep -Seconds 3
}

Write-Host "`n完成。执行: docker compose up -d --build" -ForegroundColor Cyan
