


set APPLICATION_FOLDER=..\..\_binary

java -cp "%APPLICATION_FOLDER%\lib\libGeneral.jar;%APPLICATION_FOLDER%\lib\libChaoticEncrypting.jar;%APPLICATION_FOLDER%\lib\libConfiguration.jar;%APPLICATION_FOLDER%\lib\FileEncoder.jar" com.hotmail.frojasg1.applications.fileencoder.FileEncoder %*


exit /B %ERRORLEVEL%
