@echo off
@rem ---------------------------------------------------------------------------
@rem [サンプル]Enum2JAVAジェネレータ-生成後ソース、アプリ管理下へのコピー
@rem  ※JAVA実行はせず、MS-DOSコマンドのみ
@rem ---------------------------------------------------------------------------

@rem ------------------------------------
@rem ソース生成先ディレクトリ
@rem ------------------------------------
set JAVA_GEN_DIR_01=C:\Dev\Java\workspace\victoria-type-src-generater\bin\enumgen\net\sitsol\victoria\demo\enums
set JAVA_GEN_DIR_02=C:\Dev\Java\workspace\victoria-type-src-generater\bin\enumgen\net\sitsol\victoria\demo\enums\sys

@rem ------------------------------------
@rem ソースのコピー先(＝アプリ管理下)
@rem ------------------------------------
set JAVA_DIST_DIR_01=C:\Dev\Java\workspace\victoria-demo\src\main\java\net\sitsol\victoria\demo\enums
set JAVA_DIST_DIR_02=C:\Dev\Java\workspace\victoria-demo\src\main\java\net\sitsol\victoria\demo\enums\sys


@rem ==== コピー ====
COPY /B/V/Y %JAVA_GEN_DIR_01%\*.java %JAVA_DIST_DIR_01%\
COPY /B/V/Y %JAVA_GEN_DIR_02%\*.java %JAVA_DIST_DIR_02%\

pause
