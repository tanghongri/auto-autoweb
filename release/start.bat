:: 设置环境变量  
  
:: 关闭终端回显  
@echo on
java -version
set "err=%errorlevel%"
if "%err%"=="0" (
	echo "java环境正确..."
) else (
	echo "未找到java命令,请设置号java环境变量后再次尝试"
	pause
	exit
)  

if exist "%~dp0web-0.0.1-SNAPSHOT.jar" (
	echo "%~dp0web-0.0.1-SNAPSHOT.jar 存在" 
) else (
	echo "%~dp0web-0.0.1-SNAPSHOT.jar 未找到"
	pause
	exit
)
start java.exe -jar %~dp0web-0.0.1-SNAPSHOT.jar
pause  