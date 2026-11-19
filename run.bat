@echo off
echo ========================================================
echo   Book My Ticket - Movie Ticket Booking System (Java)
echo ========================================================
echo.

if not exist out mkdir out

echo Compiling Java source files into out/...
javac -d out src/com/bookmyticket/exception/*.java src/com/bookmyticket/model/*.java src/com/bookmyticket/util/*.java src/com/bookmyticket/service/*.java src/com/bookmyticket/ui/*.java src/com/bookmyticket/Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful! Launching Book My Ticket GUI...
echo.
java -cp out com.bookmyticket.Main
