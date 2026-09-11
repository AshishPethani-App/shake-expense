@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@setlocal
set DIRNAME=%~dp0
call "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" %*
