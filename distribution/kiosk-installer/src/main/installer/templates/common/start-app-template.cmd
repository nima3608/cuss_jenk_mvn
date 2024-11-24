@echo off
rem ============================================================================
rem
rem SITA IPS Kiosk Applicatin Start file
rem
rem Copyright (C) 2024 SITA IPS GmbH
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
rem $Id: start-app.cmd 2265 2024-02-29 08:54:35Z mbrinkma $
rem
rem ============================================================================

setlocal
set PROXY=${-proxy.host-}:${-proxy.port-}

rem -----------------
rem start the kiosk-application and application-provider:
set JAVA_HOME=${-application.path-}\jdk
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if NOT "%PROXY%" == ":" set JPROXY_ARGS=-Dhttps.proxyHost=${-proxy.host-} -Dhttps.proxyPort=${-proxy.port-} -Dhttp.proxyHost=${-proxy.host-} -Dhttp.proxyPort=${-proxy.port-} -Dhttp.nonProxyHosts=localhost -Dhttps.nonProxyHosts=localhost

cd ${-application.path-}\ka
start %JAVA_EXE% %JPROXY_ARGS% -jar ${-kiosk_app.jar-} --logging.config=config\logback.xml

cd ${-application.path-}\ap
start %JAVA_EXE% %JPROXY_ARGS% -jar ${-action_provider.jar-} --logging.config=config\logback.xml

rem ------------------
rem start the browser:
cd ${-application.path-}\chrome-portable
set "BROWSER_APP_URL=${-application.url-}"
set BROWSER_EXE=${-application.path-}\chrome-portable\App\Chrome-bin\chrome.exe
set BROWSER_ARGS=--kiosk --start-maximized --disable-translate --disable-features=PointerEventsForTouch,OverscrollHistoryNavigation,OptimizationGuideModelDownloading,OptimizationHintsFetching,OptimizationTargetPrediction,OptimizationHints,InfiniteSessionRestore --overscroll-history-navigation=0 --disable-session-crashed-bubble --hide-crash-restore-bubble --no-first-run --no-default-browser-check --touch-events --disable-infobars --disable-background-networking --enable-npapi --disable-pinch --user-data-dir=${-application.data.path-}\chrome ${-browser.args.additional-}
if NOT "%PROXY%" == ":" set BROWSER_ARGS=--proxy-server=%PROXY% %BROWSER_ARGS%

start %BROWSER_EXE% %BROWSER_ARGS% "%BROWSER_APP_URL%"

pause

endlocal
