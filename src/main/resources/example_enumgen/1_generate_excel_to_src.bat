@echo on
@rem ---------------------------------------------------------------------------
@rem [�T���v��]Enum2JAVA�W�F�l���[�^-���s�R�}���h
@rem ---------------------------------------------------------------------------

@rem ------------------------------------
@rem JAVA�R�}���h�p�X���I�v�V����
@rem ------------------------------------
set JAVA_CMD_PATH=/Dev/jdk/jdk1.8.0_172/bin/java
set JAR_CLASS_PATH=/Dev/Java/workspace/victoria-type-src-generater/build/libs/victoria-type-src-generater-0.1.0.jar
set JAVA_MAIN_CLASS=net.sitsol.victoria.tsgen.mains.Enum2JavaGenetater
set JAVA_ARG=/Dev/Java\workspace/victoria-type-src-generater/src/main/resources/example_enumgen/enumgen.xml

@rem �c�[�����s
%JAVA_CMD_PATH% -classpath %JAR_CLASS_PATH% %JAVA_MAIN_CLASS% %JAVA_ARG%

pause
