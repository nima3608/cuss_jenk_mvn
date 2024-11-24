@echo off
rem ============================================================================
rem
rem MATERNA IPS Connect Kiosk Installation file
rem
rem Copyright (C) 2024 MATERNA IPS GmbH
rem
rem All rights reserved.
rem
rem DISCLAIMER: THIS SOFTWARE IS PROVIDED BY MATERNA IPS GmbH WITH NO WARRANTY
rem OF ANY KIND, EITHER EXPRESSED OR IMPLIED.
rem In no event shall the MATERNA IPS GmbH or the Contributors be liable for any
rem damages suffered by the users arising out of the use of this software,
rem even if advised of the possibility of such damage.
rem
rem
rem $Id: setup.cmd 2265 2024-02-14 08:54:35Z mbrinkma $
rem
rem ============================================================================
rem echo on
setlocal ENABLEDELAYEDEXPANSION
cd "%~dp0"

if "%1" == "IPS" set IPS=1

set CFG_FILE=kiosk-install.yaml

if not exist %CFG_FILE% (
  echo Could not find config file %CFG_FILE%, exiting^^!
  exit /B 10
)

set THIRDPARTY_DIR=applications\thirdparty
for /D %%f in ( %THIRDPARTY_DIR%\jdk* ) do (
  set JBIN=%%f\bin\java.exe
)

call :readConfigEntryToEnv application.path TARGET_DIR

call :installJava

call :installChrome

call :installKA

call :installAP

call :installCommon

if %IPS% == 1 (
echo installing IPSConfiguration !!!
call :installIPSConfiguration
)

goto :eof

rem ------------------------------------------------
:readConfigEntryToEnv
  FOR /F "tokens=* USEBACKQ" %%G IN (`%JBIN% -jar installer/support_tools/cuss-bridge-install-lib.jar -cfgFile %CFG_FILE% getCfgEntry -itemKey %1`) DO (
    SET "%2=%%G"
  )
goto :eof


rem ------------------------------------------------
:installJava
echo installing java 17 to %TARGET_DIR%\jdk
if not exist %TARGET_DIR%\jdk\ mkdir %TARGET_DIR%\jdk
call :copyDir %THIRDPARTY_DIR%\jdk* %TARGET_DIR%\jdk
set JAVA_HOME=%TARGET_DIR%\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
call :readConfigEntryToEnv trust_store.password PWD
keytool -import -file ssl/IPS_Root_CA.crt -alias IPS-Root-CA -cacerts -trustcacerts -storepass "%PWD%" -noprompt
goto :eof

rem ------------------------------------------------
:installKA
echo installing KA
set KA_SRC_DIR=applications\kioskApp
set KA_TARGET_DIR=%TARGET_DIR%\ka
call :copyFile %KA_SRC_DIR%\kioskApp-service*.jar %KA_TARGET_DIR%
call :copyFileWithPlaceholders %KA_SRC_DIR%\config\application-template.yaml %KA_TARGET_DIR%\config\application.yaml
call :copyFileWithPlaceholders %KA_SRC_DIR%\config\logback.xml %KA_TARGET_DIR%\config
goto :eof

rem ------------------------------------------------
:installChrome
echo installing chrome portable to %TARGET_DIR%\chrome-portable
call :readConfigEntryToEnv application.data.path DATA_PATH
set CHROME_SRC_DIR=applications\chrome
if not exist %DATA_PATH%\chrome\profile mkdir %DATA_PATH%\chrome\profile
call :copyDir  %THIRDPARTY_DIR%\google-chrome-portable* %TARGET_DIR%\chrome-portable
call :copyDir  %CHROME_SRC_DIR%\profile %DATA_PATH%\chrome\profile
call :copyFile %CHROME_SRC_DIR%\ChromeProfile.zip %DATA_PATH%
goto :eof

rem ------------------------------------------------
:installCommon
echo doing common installation tasks
call :copyFileWithPlaceholders common\start-app-template.cmd %TARGET_DIR%\start-app.cmd
call :readConfigEntryToEnv application.data.path DATA_PATH
if not exist %DATA_PATH%\ mkdir %DATA_PATH%
goto :eof

rem ------------------------------------------------
:installAP
echo installing actionProvider
set AP_SRC_DIR=applications\actionProvider
set AP_TARGET_DIR=%TARGET_DIR%\ap
call :copyFile %AP_SRC_DIR%\actionProvider*.jar %AP_TARGET_DIR%
call :copyFileWithPlaceholders %AP_SRC_DIR%\config\application-template.yaml %AP_TARGET_DIR%\config\application.yaml
call :copyFileWithPlaceholders %AP_SRC_DIR%\config\logback.xml %AP_TARGET_DIR%\config
goto :eof

rem ------------------------------------------------
:copyDir
  for /D %%f in ( %1 ) do (
    xcopy /Y /S /E /I %%f %2 > nul
  )
goto :eof

rem ------------------------------------------------
:copyFile
  xcopy /Y /S /E /I %1 %2 > nul
goto :eof


rem ------------------------------------------------
:copyFileWithPlaceholders
  %JBIN% -jar installer/support_tools/cuss-bridge-install-lib.jar -cfgFile %CFG_FILE% copyAndAdaptFile -sourceFile %1 -destFile %2 > nul
goto :eof

rem ------------------------------------------------
:installIPSConfiguration
echo installing IPSConfiguration
.\installer\support_tools\cfgvaluess.exe -write:IPS-UA_CUSS.cfg -autoinitial
goto :eof

