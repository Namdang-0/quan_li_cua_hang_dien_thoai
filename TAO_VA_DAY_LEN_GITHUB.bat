@echo off
chcp 65001 >nul
setlocal EnableExtensions
cd /d "%~dp0"

echo =======================================================
echo  TAO REPOSITORY GITHUB VA DAY DO AN LEN TU DONG
echo =======================================================
echo.
where git >nul 2>nul
if errorlevel 1 (
  echo Chua cai Git. Cai Git for Windows: https://git-scm.com/download/win
  pause
  exit /b 1
)
where gh >nul 2>nul
if errorlevel 1 (
  echo Chua cai GitHub CLI. Cai tai: https://cli.github.com/
  echo Sau do mo lai file nay.
  pause
  exit /b 1
)

echo Kiem tra dang nhap GitHub...
gh auth status >nul 2>nul
if errorlevel 1 (
  echo Ban chua dang nhap. GitHub CLI se mo quy trinh dang nhap tren trinh duyet.
  gh auth login
  if errorlevel 1 goto :error
  gh auth status >nul 2>nul
  if errorlevel 1 goto :error
)

echo.
set /p REPO_NAME=Ten repository (Enter de dung quan-ly-cua-hang-dien-thoai): 
if "%REPO_NAME%"=="" set REPO_NAME=quan-ly-cua-hang-dien-thoai

echo.
echo Chon quyen xem repository:
echo   1. Public - giang vien mo link xem duoc (khuyen nghi)
echo   2. Private - chi nguoi duoc moi moi xem duoc
set /p VISIBILITY=Nhap 1 hoac 2 (mac dinh 1): 
if "%VISIBILITY%"=="2" (
  set VIS_FLAG=--private
) else (
  set VIS_FLAG=--public
)

if not exist ".git" git init
if errorlevel 1 goto :error

git add .
if errorlevel 1 goto :error

echo.
echo Cac file se duoc dua len GitHub:
git status --short

echo.
echo Luu y: config\db.properties va target\ da duoc bo qua boi .gitignore.
set /p CONFIRM=Nhap Y de tao repository va day ma nguon: 
if /I not "%CONFIRM%"=="Y" (
  echo Da huy. Chua co repository nao duoc tao.
  pause
  exit /b 0
)

git config user.name >nul 2>nul
if errorlevel 1 (
  set /p GIT_NAME=Nhap ten hien thi Git: 
  git config user.name "%GIT_NAME%"
)
git config user.email >nul 2>nul
if errorlevel 1 (
  set /p GIT_EMAIL=Nhap email Git: 
  git config user.email "%GIT_EMAIL%"
)

git diff --cached --quiet
if errorlevel 1 (
  git commit -m "Submit Java Swing phone store management project"
  if errorlevel 1 goto :error
) else (
  echo Khong co thay doi moi de commit; tiep tuc buoc tao repository.
)

git branch -M main
if errorlevel 1 goto :error

git remote get-url origin >nul 2>nul
if not errorlevel 1 git remote remove origin

echo.
echo Dang tao repository va push ma nguon...
gh repo create "%REPO_NAME%" %VIS_FLAG% --source=. --remote=origin --push
if errorlevel 1 goto :error

echo.
echo =======================================================
echo DA DAY MA NGUON LEN GITHUB THANH CONG!
echo Mo repository vua tao de sao chep URL nop cho giang vien.
echo =======================================================
gh repo view --web
pause
exit /b 0

:error
echo.
echo Co loi. Neu repository da ton tai, hay tao repository rong tren GitHub
 echo va dung file PUSH_TO_GITHUB.bat de day code vao repository do.
echo Kiem tra ket noi mang va trang thai dang nhap bang lenh: gh auth status
pause
exit /b 1
