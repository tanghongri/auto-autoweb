:: ���û�������  
  
:: �ر��ն˻���  
@echo on
java -version
set "err=%errorlevel%"
if "%err%"=="0" (
	echo "java������ȷ..."
) else (
	echo "δ�ҵ�java����,�����ú�java�����������ٴγ���"
	pause
	exit
)  

if exist "%~dp0web-0.0.1-SNAPSHOT.jar" (
	echo "%~dp0web-0.0.1-SNAPSHOT.jar ����" 
) else (
	echo "%~dp0web-0.0.1-SNAPSHOT.jar δ�ҵ�"
	pause
	exit
)
start java.exe -jar %~dp0web-0.0.1-SNAPSHOT.jar
pause  