
set password=

call .\encodeFile.bat ".\text.100_KB.txt" "%password%"

if %ERRORLEVEL% NEQ 0 ( echo Error encrypting file
) else ( echo Encryption successful )
