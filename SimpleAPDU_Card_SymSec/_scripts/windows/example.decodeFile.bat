
set password=

call .\decodeFile.bat ".\text.100_KB.txt.jfe" "%password%"

if %ERRORLEVEL% NEQ 0   ( echo Error decrypting file
) else ( echo Decryption successful )

