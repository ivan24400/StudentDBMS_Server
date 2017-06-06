@echo off
title Typh Server
color f0
mode con: cols=85 lines=24
:start
cls
java -jar sys/typhserver.jar help
echo.
set /p action=" Enter a command:  "
if "%action%"=="exit" ( exit ) else (java -jar sys/typhserver.jar %action% )
echo.
pause
goto start
