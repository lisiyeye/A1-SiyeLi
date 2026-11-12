@echo off
REM ---------------------------------------------------------------------------
REM  One-click build + run for the PROG2004 A1 appointment system.
REM  Double-click this file, or run it from a terminal.
REM  It compiles every .java in src\ into bin\ and then runs AssignmentOne.
REM ---------------------------------------------------------------------------
setlocal
cd /d "%~dp0"

if not exist "bin" mkdir "bin"

echo === Compiling src\*.java ===========================================
javac -encoding UTF-8 -d bin src\*.java
if errorlevel 1 goto :fail

echo === Running AssignmentOne =========================================
echo.
java -cp bin AssignmentOne
if errorlevel 1 goto :runfail

echo.
echo === Finished without errors =======================================
goto :done

:fail
echo.
echo *** BUILD FAILED - see the compiler messages above. ***
goto :done

:runfail
echo.
echo *** RUN FAILED - the program exited with an error. ***

:done
echo.
pause
endlocal
