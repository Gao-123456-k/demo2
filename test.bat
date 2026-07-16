@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Testing Weather CLI commands...
echo.

echo help | java -cp out com.weather.Main
echo.
echo ========================================
echo.
echo version | java -cp out com.weather.Main
echo.
echo ========================================
echo.
echo status | java -cp out com.weather.Main
echo.
echo ========================================
echo.
echo weather Beijing | java -cp out com.weather.Main
echo.
echo ========================================
echo.
echo weather | java -cp out com.weather.Main
echo.
echo ========================================
echo.
echo weather InvalidCity123 | java -cp out com.weather.Main
