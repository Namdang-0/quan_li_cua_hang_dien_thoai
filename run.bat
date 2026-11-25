@echo off
chcp 65001 >nul
where mvn >nul 2>nul
if errorlevel 1 (
  echo Chua tim thay Maven. Hay mo project bang IntelliJ IDEA va chay Main.java.
  pause
  exit /b 1
)
mvn exec:java
pause
