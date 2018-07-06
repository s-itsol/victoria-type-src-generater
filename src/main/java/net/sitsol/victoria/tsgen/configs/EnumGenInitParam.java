/**
 * 
 */
package net.sitsol.victoria.tsgen.configs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Enum2JAVAジェネレータ-静的パラメータクラス
 * 
 * @author rei_shibano
 */
public class EnumGenInitParam {
	
	private static EnumGenInitParam instance_ = new EnumGenInitParam();	// シングルトン・インスタンス

	/**
	 * シングルトン・インスタンスの取得
	 * @return 本ファサードのインスタンス
	 */
	public static EnumGenInitParam getInstance() {
		return instance_;
	}
	
	public static final String DELAULT_PROP_RESOURCE_PATH = "example_enumgen/enumgen.properties";
	
	private Properties props = new Properties();		// プロパティリソース
	
	/**
	 * 初期処理
	 * @param propFilePath プロパティファイルパス
	 */
	public static void init(String propFilePath) {
		
		
		try (
			InputStream inputStream = propFilePath == null
										? ClassLoader.getSystemResourceAsStream(DELAULT_PROP_RESOURCE_PATH)
										: new FileInputStream(propFilePath)
		) {
			// プロパティファイル読込み
			EnumGenInitParam.getInstance().props.load(inputStream);
			
		} catch(Exception ex) {
			
			throw new RuntimeException("プロパティファイルの読込みでエラーが発生しました。"
											+ "プロパティファイルパス：[" + propFilePath + "]"
										, ex
			);
		}
	}

	/**
	 * デフォルトコンストラクタ
	 */
	public EnumGenInitParam() { }

	/**
	 * 文字列値取得
	 * @param key プロパティキー
	 * @return 文字列値
	 */
	private String getStringValue(String key) {
		return this.getProps().getProperty(key);
	}

	/**
	 * 数値取得
	 * @param key プロパティキー
	 * @return 数値
	 */
	private int getIntValue(String key) {
		return Integer.parseInt(this.getStringValue(key));
	}

	/**
	 * 真偽取得
	 * @param key プロパティキー
	 * @return 真／偽
	 */
	private boolean getBooleanValue(String key) {
		// ※「1」ならON
		return "1".equals(this.getStringValue(key));
	}

	public boolean isReadTestMode() {
		return this.getBooleanValue("readTestModeFlg");
	}

	public String getEnumFilePath() {
		return this.getStringValue("enumFilePath");
	}

	public String getEnumSheetName() {
		return this.getStringValue("enumSheetName");
	}

	public int getValueStartRowIdx() {
		return this.getIntValue("valueStartRowIdx");
	}

	public int getEnumPackageNameColIdx() {
		return this.getIntValue("enumPackageNameColIdx");
	}

	public int getEnumClassNameColIdx() {
		return this.getIntValue("enumClassNameColIdx");
	}

	public int getEnumClassCommentColIdx() {
		return this.getIntValue("enumClassCommentColIdx");
	}

	public int getEnumFieldNameColIdx() {
		return this.getIntValue("enumFieldNameColIdx");
	}

	public int getEnumFieldCommentColIdx() {
		return this.getIntValue("enumFieldCommentColIdx");
	}

	public int getEnumCodeColIdx() {
		return this.getIntValue("enumCodeColIdx");
	}

	public int getEnumDecodeColIdx() {
		return this.getIntValue("enumDecodeColIdx");
	}
	
	public Properties getProps() {
		return props;
	}

}
