setlocal enabledelayedexpansion
for /f "delims=" %%i in ('cd') do set cwd=%%i
cd %1%
git log --oneline > gitlog.txt

echo [INFO] Log export ok

set cpt=0

for /f "delims=" %%i in ('type gitlog.txt') do (
	echo [INFO] Clone !cpt!
	
	set commitId=%%i%
	set commitId=!commitId:~0,7!
	
	echo [INFO] Clone directory
	
	git clone -l -s -n . %cwd%/%1_!cpt!
	cd %cwd%
	cd %1_!cpt!
	git checkout %3%
	git checkout !commitId!
	cd %cwd%/%1%
	
	set /a cpt=!cpt!+1
	
	if !cpt! == %2% GOTO FIN
)
:FIN
echo [INFO] Fin du process
