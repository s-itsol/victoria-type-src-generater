/**
 * 
 */
package net.sitsol.victoria.tsgen.tools;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Excelファイル読込み支援クラス
 * 
 * @author rei_shibano
 */
public abstract class ExcelReader {
	
	public static final int DEFAULT_MAX_READ_ROW_COUNT = 1000;
	public static final int DEFAULT_MAX_READ_COL_COUNT = 100;
	
	private int maxReadRowCount;
	private int maxReadColCount;
	
	/**
	 * コンストラクタ
	 */
	public ExcelReader() {
		this(DEFAULT_MAX_READ_ROW_COUNT, DEFAULT_MAX_READ_COL_COUNT);
	}
	
	/**
	 * コンストラクタ
	 * @param maxReadRowCount 最大読込み行数
	 * @param maxReadColCount 最大読込み列数
	 */
	public ExcelReader(int maxReadRowCount, int maxReadColCount) {
		this.maxReadRowCount = maxReadRowCount;
		this.maxReadColCount = maxReadColCount;
	}
	
	/**
	 * Excelファイル読込み
	 * @param readExcelFilePath 読込みExcelファイルパス
	 */
	public void readExcelFile(String readExcelFilePath) {
		
		try (
			// Excelファイル読込み
			InputStream enumFileStream = new FileInputStream(readExcelFilePath);
			// ワークブック読込み
			Workbook workBook = WorkbookFactory.create(enumFileStream);
		) {
			// シート読込みループ
			workBook.forEach(
				// シート読込みイベントハンドラ
				(sheet) -> {
					// シート読込み
					this.readSheet(sheet);
				}
			);
			
		} catch (Exception ex) {
			
			throw new RuntimeException("Excelファイル読込み処理でエラーが発生しました。"
												+ "読込みExcelファイルパス：[" + readExcelFilePath + "]"
											, ex
			);
		}
	}
	
	/**
	 * シート読込み
	 * @param sheet シート
	 */
	protected void readSheet(Sheet sheet) {
		
		String sheetName = sheet != null ? sheet.getSheetName() : null;
		
		try {
			// 行読込みループ
			sheet.forEach(
				// 行読込みイベントハンドラ
				(row) -> {
					// 行読込み
					this.readRow(sheet, row);
				}
			);
			
			// シート読込み終了
			this.finishReadedSheet(sheet);
			
		} catch (Exception ex) {
			
			throw new RuntimeException("シート読込み処理でエラーが発生しました。"
												+ "シート名：[" + sheetName + "]"
											, ex
			);
		} 
	}
	
	/**
	 * 行読込み
	 * @param sheet シート
	 * @param row 行
	 */
	protected void readRow(Sheet sheet, Row row) {
		
		String sheetName = sheet != null ? sheet.getSheetName() : null;
		Integer rowIdx = row != null ? row.getRowNum() : null;
		
		// 最大行数を超えていたら強制終了
		// 最大行数を超えていたら強制終了 ※0オリジンのインデックスなので、最大数との比較は「>=」で「超えていたら」になる
		if ( rowIdx == null || rowIdx.intValue() >= this.getMaxReadRowCount() ) {
			
			System.out.println("読込み行が最大値を超えたため、行読込みを中断します。"
									+ "シート名：[" + sheetName + "]"
									+ ", ROW-Index：[" + rowIdx + "]"
									+ ", 最大行数：[" + this.getMaxReadRowCount() + "]"
			);
			
			return;
		}
		
		try {
			// セル読込みループ
			row.forEach(
				// セル読込みイベントハンドラ
				(cell) -> {
					// セル読込み
					this.readCell(sheet, row, cell);
				}
			);
			
			// 行読込み終了
			this.finishReadedRow(sheet, row);
			
		} catch (Exception ex) {
			
			throw new RuntimeException("行読込み処理でエラーが発生しました。"
												+ "シート名：[" + sheetName + "]"
												+ ", ROW-Index：[" + rowIdx + "]"
											, ex
			);
		} 
	}
	
	/**
	 * セル読込み
	 * @param sheet シート
	 * @param row 行
	 * @param cell セル
	 */
	protected void readCell(Sheet sheet, Row row, Cell cell) {
		
		String sheetName = sheet != null ? sheet.getSheetName() : null;
		Integer rowIdx = row != null ? row.getRowNum() : null;
		Integer colIdx = cell != null ? cell.getColumnIndex() : null;
		CellType cellType = cell != null ? cell.getCellType() : null;
		
		// 最大列数を超えていたら強制終了 ※0オリジンのインデックスなので、最大数との比較は「>=」で「超えていたら」になる
		if ( colIdx == null || colIdx >= this.getMaxReadColCount() ) {
			
			System.out.println("読込み列が最大値を超えたため、セル読込みを中断します。"
									+ "シート名：[" + sheetName + "]"
									+ ", ROW-Index：[" + rowIdx + "]"
									+ ", COL-Index：[" + colIdx + "]"
									+ ", 最大列数：[" + this.getMaxReadColCount() + "]"
			);
			
			return;
		}
		
		try {
			
			// 数値だった場合
			if ( CellType.NUMERIC.equals(cell.getCellType()) ) {
				// 得る前に、セルの書式を「テキスト」に変換
				cell.setCellType(CellType.STRING);
			}
			
			// セル読込みコールバック
			this.readCellValue(sheet, row, cell, cell.getStringCellValue());
			
		} catch (Exception ex) {
			
			throw new RuntimeException("セル読込み処理でエラーが発生しました。"
												+ "シート名：[" + sheetName + "]"
												+ ", ROW-Index：[" + rowIdx + "]"
												+ ", COL-Index：[" + colIdx + "]"
												+ ", CellType：[" + cellType + "]"
											, ex
			);
		} 
	}
	
	/**
	 * セル値読込み
	 * @param sheet シート
	 * @param row 行
	 * @param cell 列
	 * @param cellValue セル値(文字列)
	 */
	protected abstract void readCellValue(Sheet sheet, Row row, Cell col, String cellValue);
	
	/**
	 * 行読込み終了
	 * @param sheet シート
	 * @param row 行
	 */
	protected abstract void finishReadedRow(Sheet shee, Row row);
	
	/**
	 * シート読込み終了
	 * @param sheet シート
	 */
	protected abstract void finishReadedSheet(Sheet shee);
	
	
	/*-- setter・getter ------------------------------------------------------*/
	
	public int getMaxReadRowCount() {
		return maxReadRowCount;
	}

	public int getMaxReadColCount() {
		return maxReadColCount;
	}
	
}
