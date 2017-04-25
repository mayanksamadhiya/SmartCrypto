
echo off

set password=%2
if "%password%"=="" set password=""

call command.interface.FileEncoder.bat -encode -decodedFileName %1 -encodedFileName "%~1.jfe" -useFileSizeForEncryptingParams -password %password%

exit /B %ERRORLEVEL%
