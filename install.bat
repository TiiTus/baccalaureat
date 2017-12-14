@echo OFF
:: ----------------------------------------
:: Script de demarrage de l'application : <nom-application>
:: Author : Azim Roussanaly
:: Organization : Université de Lorraine
:: date : oct, 2017
:: ---------------------------------------
:: exemple:
:: > install
java -cp ant\ant.jar;ant\ant-launcher.jar;"%JAVA_HOME%\lib\tools.jar" org.apache.tools.ant.Main
