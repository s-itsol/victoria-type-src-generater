/**
 * 
 */
package net.sitsol.victoria.tsgen.mains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import net.sitsol.victoria.tsgen.ExcelReadHelper;
import net.sitsol.victoria.tsgen.configs.EnumGenInitParam;
import net.sitsol.victoria.tsgen.models.enumgen.EnumFieldModel;
import net.sitsol.victoria.tsgen.models.enumgen.EnumModel;

/**
 * Enum2JAVAジェネレータ-起動クラス
 * 
 * @author rei_shibano
 */
public class Enum2JavaGenetater {
	
	/**
	 * 起動メソッド
	 * @param args 引数
	 */
	public static void main(String[] args) {
		
		String propFilePath = null;
		
		System.out.println("コマンドライン引数->" + Arrays.asList(args));
		
		// 引数指定なし
		if ( args == null || args.length < 1 || args[0] == null ) {
			
			System.out.println("[ERROR]第1引数(プロパティファイルのパス)が指定されていません。");
			System.out.println(EnumGenInitParam.DELAULT_PROP_RESOURCE_PATH + "のプロパティファイルを使い、読込みテストモードで実行します。");
			
		} else {
			propFilePath = args[0];
		}
		
		// 静的パラメータ初期処理
		EnumGenInitParam.init(propFilePath);
		
		EnumGenInitParam iniParam = EnumGenInitParam.getInstance();		// ※コーディングが長くなるので、ローカル変数に保持させているだけ
		
		System.out.println("Enumソース生成を開始します。"
								+ "読込みテストモード：[" + iniParam.isReadTestMode() + "]"
								+ ", 読込みExcelファイルパス：[" + iniParam.getEnumFilePath() + "]"
		);
		
		Map<Integer, String> currentClassInfoMap = new LinkedHashMap<>();		// 処理中クラス情報マップ ※キー：列インデックス、値：セル値(文字列)
		Map<Integer, String> currentFieldInfoMap = new LinkedHashMap<>();		// 処理中フィールド情報マップ ※キー：列インデックス、値：セル値(文字列)
		List<Map<Integer, String>> fieldInfoMapList = new ArrayList<>();		// フィールド情報マップリスト
		
		// Excelファイル読込み支援クラス生成
		ExcelReadHelper readHelper = new ExcelReadHelper() {
			
			/**
			 * シート読込み
	 		 * @param sheet シート
			 */
			@Override
			protected void readSheet(Sheet sheet) {
				
				// 処理対象外のシートは何もしない
				if ( !iniParam.getEnumSheetName().equals(sheet.getSheetName()) ) {
					return;
				}
				
				super.readSheet(sheet);
			}
			
			/**
			 * 行読込み
			 * @param sheet シート
			 * @param row 行
			 */
			@Override
			protected void readRow(Sheet sheet, Row row) {
				
				// 値開始位置未満の行は何もしない
				if ( iniParam.getValueStartRowIdx() > row.getRowNum() ) {
					return;
				}
				
				super.readRow(sheet, row);
			}
			
			/**
			 * セル値読込み
			 * @param sheet シート
			 * @param row 行
			 * @param cell 列
			 * @param cellValue セル値(文字列)
			 */
			@Override
			protected void readCellValue(Sheet sheet, Row row, Cell cell, String cellValue) {
				
				int colIdx = cell.getColumnIndex();
				
				// 対象範囲外の列は何もしない
				if ( colIdx < iniParam.getEnumPackageNameColIdx() || iniParam.getEnumDecodeColIdx() < colIdx ) {
					return;
				}
				
				// パッケージ名～クラスコメントのセルだった場合
				if ( iniParam.getEnumPackageNameColIdx() <= colIdx && colIdx <= iniParam.getEnumClassCommentColIdx() ) {
					
					// セル値が空なら何もしない
					if ( cellValue == null || cellValue.isEmpty() ) {
						return;
					}
					
					// パッケージのセル(＝クラス情報部の読込み開始)だった場合
					if ( iniParam.getEnumPackageNameColIdx() == colIdx ) {
						
						// 処理中クラス情報・フィールド情報マップリストに要素あり(＝１番最初の処理ではない)
						if ( !currentClassInfoMap.isEmpty() && !fieldInfoMapList.isEmpty() ) {
							// Enumモデル確定
							compEnumModel(currentClassInfoMap, fieldInfoMapList);
						}
						
						// 処理中クラス情報マップを初期化
						currentClassInfoMap.clear();
						currentClassInfoMap.put(iniParam.getEnumPackageNameColIdx(), null);			// パッケージ名
						currentClassInfoMap.put(iniParam.getEnumClassNameColIdx(), null);			// クラス名
						currentClassInfoMap.put(iniParam.getEnumClassCommentColIdx(), null);		// クラスコメント
						
						// フィールド情報マップリストを初期化
						fieldInfoMapList.clear();
					}
					
					// クラスコメントのセルだった場合
					if ( iniParam.getEnumClassCommentColIdx() == colIdx ) {
						
						String currentComment = currentClassInfoMap.get(iniParam.getEnumClassCommentColIdx());
						
						// さらに、処理中クラスコメントありなら、空白区切りで繋げたセル値に差し替え ※クラスコメントは複数行に跨る入力が在り得る
						if ( currentComment != null ) {
							cellValue = currentComment + " " + cellValue;
						}
					}
					
					// 対象キーのセル値を上書き保持
					currentClassInfoMap.put(colIdx, cellValue);
					
					return;			// ここで終了
				}
				
				// フィールド名～デコード値のセルだった場合
				if ( iniParam.getEnumFieldNameColIdx() <= colIdx && colIdx <= iniParam.getEnumDecodeColIdx() ) {
					
					// フィールド名のセル(＝フィールド情報部の読込み開始)だった場合
					if ( iniParam.getEnumFieldNameColIdx() == colIdx ) {
						// 処理中フィールド情報マップを初期化
						currentFieldInfoMap.clear();
						currentFieldInfoMap.put(iniParam.getEnumFieldNameColIdx(), null);		// フィールド名
						currentFieldInfoMap.put(iniParam.getEnumFieldCommentColIdx(), null);	// フィールドコメント
						currentFieldInfoMap.put(iniParam.getEnumCodeColIdx(), null);			// コード値
						currentFieldInfoMap.put(iniParam.getEnumDecodeColIdx(), null);			// デコード値
					}
					
					// 対象キーのセル値を上書き保持
					currentFieldInfoMap.put(colIdx, cellValue);
				}
			}
			
			/**
			 * 行読込み終了
			 * @param sheet シート
			 * @param row 行
			 */
			@Override
			protected void finishReadedRow(Sheet shee, Row row) {
				// 処理中フィールド情報マップを複製して、フィールド情報マップ情報リストへ追加
				fieldInfoMapList.add( new LinkedHashMap<>(currentFieldInfoMap) );
			}
			
			/**
			 * シート読込み終了
			 * @param sheet シート
			 */
			@Override
			protected void finishReadedSheet(Sheet shee) {
				// Enumモデル確定 ※最後に読込んだ１件分はここでしか検知できない
				compEnumModel(currentClassInfoMap, fieldInfoMapList);
			}
			
		};
		
		// Excelファイル読込み
		readHelper.readExcelFile(iniParam.getEnumFilePath());
		
		System.out.println("Enumソース生成が終了しました。");
	}
	
	/**
	 * Enumモデル確定
	 * @param classInfoMap 処理中クラス情報マップ
	 * @param fieldInfoMapList フィールド情報マップリスト
	 */
	private static void compEnumModel(Map<Integer, String> classInfoMap, List<Map<Integer, String>> fieldInfoMapList) {
		
		List<EnumFieldModel> fieldModelList = new ArrayList<>();
		
		fieldInfoMapList.forEach(
			(fieldInfoMap) -> {
				// Enumフィールドモデルリストへ追加
				fieldModelList.add(
					// Enumフィールドモデル生成
					new EnumFieldModel(fieldInfoMap.get(EnumGenInitParam.getInstance().getEnumFieldNameColIdx())
										, fieldInfoMap.get(EnumGenInitParam.getInstance().getEnumFieldCommentColIdx())
										, fieldInfoMap.get(EnumGenInitParam.getInstance().getEnumCodeColIdx())
										, fieldInfoMap.get(EnumGenInitParam.getInstance().getEnumDecodeColIdx())
					)
				);
			} 
		);
		
		// Enumモデル生成
		EnumModel enumModel = new EnumModel(classInfoMap.get(EnumGenInitParam.getInstance().getEnumPackageNameColIdx())
											, classInfoMap.get(EnumGenInitParam.getInstance().getEnumClassNameColIdx())
											, classInfoMap.get(EnumGenInitParam.getInstance().getEnumClassCommentColIdx())
											, fieldModelList
		);
		
		// 読込みテストモードの場合
		if ( EnumGenInitParam.getInstance().isReadTestMode() ) {
			
			System.out.println("──────────────────────────────");
			System.out.println(enumModel.getPackageName() + "." + enumModel.getClassName() + "/*" + enumModel.getClassComment() + "*/");
			System.out.println("──────────────────────────────");
			
			enumModel.getFieldModelList().forEach(
				(fieldModel) -> {
					System.out.println("  " + fieldModel.getFieldName() + "/*" + fieldModel.getFieldComment() + "*/"
											+ " -> " + fieldModel.getCode() + "：[" + fieldModel.getDecode() + "]"
					);
				} 
			);
			
			System.out.println("");
			
			// ここで終了
			return;
		}
		
		// Enumソース出力
		writeEnumSrc(enumModel);
	}
	
	/**
	 * Enumソース出力
	 * 
	 * @param enumModel Enumモデル
	 */
	private static void writeEnumSrc(EnumModel enumModel) {
		
		// ●●●TODO：
	}
	
}
