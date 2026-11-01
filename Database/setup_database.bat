@echo off
setlocal
echo ========================================================
echo   Kasnet Internship Portal - Database Setup Utility
echo ========================================================
echo.
set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if not exist "%MYSQL_EXE%" (
    set "MYSQL_EXE=mysql"
)

echo Importing database.sql into MySQL...
echo Please enter your MySQL root password when prompted:
echo.
"%MYSQL_EXE%" -u root -p < "%~dp0database.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================================
    echo   Database 'online_internship' created successfully!
    echo ========================================================
) else (
    echo.
    echo [ERROR] Database import failed. Please verify your password.
)
echo.
pause
