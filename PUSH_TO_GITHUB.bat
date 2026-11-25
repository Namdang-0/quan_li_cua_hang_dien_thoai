@echo off
chcp 65001 >nul
setlocal EnableExtensions EnableDelayedExpansion

echo ================================================
echo   DUA DO AN QUAN LY CUA HANG DIEN THOAI LEN GITHUB
echo ================================================
where git >nul 2>nul
if errorlevel 1 (
  echo Chua tim thay Git. Cai Git for Windows truoc: https://git-scm.com/download/win
  pause
  exit /b 1
)

echo.
echo Buoc 1: Tao repository moi tren GitHub, khong tao README/.gitignore/License.
echo Nen chon Public neu giang vien can xem link ma khong can xin quyen.
echo.
set /p REPO_URL=Dan URL repository, vi du https://github.com/USERNAME/quan-ly-cua-hang-dien-thoai.git : 
if "%REPO_URL%"=="" (
  echo Ban chua nhap URL repository.
  pause
  exit /b 1
)

if not exist ".git" git init
if errorlevel 1 goto :error

git add .
if errorlevel 1 goto :error

echo.
echo Danh sach file se duoc dua len GitHub:
git status --short

echo.
set /p CONFIRM=Nhap Y de tiep tuc commit va push: 
if /I not "%CONFIRM%"=="Y" (
  echo Da huy. Khong co thay doi nao duoc push.
  pause
  exit /b 0
)

git config user.name >nul 2>nul
if errorlevel 1 (
  set /p GIT_NAME=Nhap ten hien thi Git: 
  git config user.name "!GIT_NAME!"
)
git config user.email >nul 2>nul
if errorlevel 1 (
  set /p GIT_EMAIL=Nhap email Git: 
  git config user.email "!GIT_EMAIL!"
)

git commit -m "Submit Java Swing phone store management project"
if errorlevel 1 (
  echo Khong tao duoc commit. Neu bao nothing to commit thi co the code da commit truoc do.
)
git branch -M main
if errorlevel 1 goto :error

git remote get-url origin >nul 2>nul
if errorlevel 1 (
  git remote add origin "%REPO_URL%"
) else (
  git remote set-url origin "%REPO_URL%"
)
if errorlevel 1 goto :error

git push -u origin main
if errorlevel 1 goto :error

echo.
echo THANH CONG! Mo repository tren GitHub va sao chep link de nop cho giang vien.
pause
exit /b 0

:error
echo.
echo Co loi khi thao tac Git. Kiem tra URL repository, mang va dang nhap GitHub.
pause
exit /b 1
