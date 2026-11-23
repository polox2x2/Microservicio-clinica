$ErrorActionPreference = "Stop"

Write-Host "Verificando conexion con Docker Daemon..."
docker info > $null 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Docker Desktop no esta ejecutandose. Por favor, abre Docker Desktop en Windows y vuelve a ejecutar este script." -ForegroundColor Red
    exit 1
}

Write-Host "1. Construyendo imagenes Docker con docker compose build..." -ForegroundColor Cyan
docker compose build

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error al compilar imagenes Docker." -ForegroundColor Red
    exit 1
}

Write-Host "2. Subiendo imagenes actualizadas a Docker Hub (polox2x2)..." -ForegroundColor Cyan
docker compose push

Write-Host "Actualizacion en Docker Hub completada exitosamente." -ForegroundColor Green
