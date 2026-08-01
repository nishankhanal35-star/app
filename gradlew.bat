@echo off
SETLOCAL
set DIR=%~dp0
set JAVA_CMD=java
if defined JAVA_HOME (
  if exist "%JAVA_HOME%\bin\java.exe" (
    set JAVA_CMD=%JAVA_HOME%\bin\java.exe
  )
)
set WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
if not exist "%WRAPPER_JAR%" (
  echo Warning: gradle-wrapper.jar not found. You may need to generate it by running 'gradle wrapper' locally or installing Gradle.
)
"%JAVA_CMD%" -cp "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
