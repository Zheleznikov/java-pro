# Домашка к первому уроку (знакмоство и введение в gradle)

## Основные моменты
##№ По поводу толстых и тонких джарников
- после команды `./gradlew build` собираются два джарника для каждого модуля: толстый и тонкий
- из командной строки можно собрать отдельные модули следующими командами:
  

    ./gradlew build -p hw01-gradle/
    ./gradlew hw01-gradle:build

    

## По поводу основных файлов
- settings.gradle и build.gradle
- запускать джарник можно командой `java - jar название.jar`