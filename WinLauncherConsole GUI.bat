:: Хорошая справка по консольной Jav`е
:: http://habrahabr.ru/post/125210/

SET MAVENREPO=%USERPROFILE%\.m2\repository

@echo off

java -Dfile.encoding=Cp866 -classpath ".\target\classes;%MAVENREPO%\commons-net\commons-net\3.3\commons-net-3.3.jar;%MAVENREPO%\commons-configuration\commons-configuration\1.10\commons-configuration-1.10.jar;%MAVENREPO%\commons-lang\commons-lang\2.6\commons-lang-2.6.jar;%MAVENREPO%\commons-logging\commons-logging\1.1.1\commons-logging-1.1.1.jar;%MAVENREPO%\commons-collections\commons-collections\3.2.1\commons-collections-3.2.1.jar;%MAVENREPO%\org\eclipse\core\commands\3.3.0-I20070605-0010\commands-3.3.0-I20070605-0010.jar;%MAVENREPO%\org\eclipse\equinox\common\3.6.200-v20130402-1505\common-3.6.200-v20130402-1505.jar" UploadProgress

::if ERRORLEVEL 1 pause
pause