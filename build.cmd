@echo off
rem set java_home=c:\bin\jdk7
set path=%java_home%\bin;%path%
call mvn -e clean install
pause
