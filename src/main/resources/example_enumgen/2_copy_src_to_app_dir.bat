@echo off
@rem ---------------------------------------------------------------------------
@rem [�T���v��]Enum2JAVA�W�F�l���[�^-������\�[�X�A�A�v���Ǘ����ւ̃R�s�[
@rem  ��JAVA���s�͂����AMS-DOS�R�}���h�̂�
@rem ---------------------------------------------------------------------------

@rem ------------------------------------
@rem �\�[�X������f�B���N�g��
@rem ------------------------------------
set JAVA_GEN_DIR_01=C:\Dev\Java\workspace\victoria-type-src-generater\bin\enumgen\net\sitsol\victoria\demo\enums
set JAVA_GEN_DIR_02=C:\Dev\Java\workspace\victoria-type-src-generater\bin\enumgen\net\sitsol\victoria\demo\enums\sys

@rem ------------------------------------
@rem �\�[�X�̃R�s�[��(���A�v���Ǘ���)
@rem ------------------------------------
set JAVA_DIST_DIR_01=C:\Dev\Java\workspace\victoria-demo\src\main\java\net\sitsol\victoria\demo\enums
set JAVA_DIST_DIR_02=C:\Dev\Java\workspace\victoria-demo\src\main\java\net\sitsol\victoria\demo\enums\sys


@rem ==== �R�s�[ ====
COPY /B/V/Y %JAVA_GEN_DIR_01%\*.java %JAVA_DIST_DIR_01%\
COPY /B/V/Y %JAVA_GEN_DIR_02%\*.java %JAVA_DIST_DIR_02%\

pause
