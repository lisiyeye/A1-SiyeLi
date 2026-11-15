@echo off
REM ---------------------------------------------------------------------------
REM  PROG2004 Assignment 1 - same-day medical appointment system.
REM  Compiles every .java in src\ into bin\ and then runs AssignmentOne.
REM  Double-click this file, or run "run.bat /nopause" from a script.
REM ---------------------------------------------------------------------------
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo *** javac was not found. Install a JDK and add its bin folder to PATH. ***
    goto :done
)

echo === Java version ======================================================
java -version 2>&1
echo.
echo === Compiling src\*.java ==============================================
if exist bin rmdir /s /q bin
mkdir bin
javac -encoding UTF-8 -Xlint:all -d bin src\*.java
if errorlevel 1 goto :fail

echo.
echo === Running AssignmentOne =============================================
java -cp bin AssignmentOne
if errorlevel 1 goto :runfail

echo.
echo === Finished without errors ===========================================
goto :done

:fail
echo.
echo *** BUILD FAILED - see the compiler messages above. ***
goto :done

:runfail
echo.
echo *** RUN FAILED - the program exited with an error. ***

:done
if /i not "%~1"=="/nopause" pause
endlocal
