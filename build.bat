@echo off
chcp 65001 >nul
where mvn >nul 2>nul
if errorlevel 1 (
  echo Chua tim thay Maven. Hay cai Maven hoac dung Maven tich hop trong IntelliJ IDEA.
  pause
  exit /b 1
)
mvn clean package
pause
