


echo off

set password=%2
if "%password%"=="" set password=""

set fileNameExtension=%~x1
if "%fileNameExtension%"==".jfe" ( echo file extension ok = %fileNameExtension%
) else (
			echo Extension of encrypted file not valid: %fileNameExtension%
			exit /B 1 )

call command.interface.FileEncoder.bat -decode -decodedFileName "%~n1" -encodedFileName %1 -password %password%

exit /B %ERRORLEVEL%
