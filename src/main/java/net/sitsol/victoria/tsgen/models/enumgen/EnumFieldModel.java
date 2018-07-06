/**
 * 
 */
package net.sitsol.victoria.tsgen.models.enumgen;

import lombok.AllArgsConstructor;

/**
 * Enumフィールドモデル
 * 
 * @author rei_shibano
 */
@AllArgsConstructor
public class EnumFieldModel {

	private String fieldName;			// フィールド名
	private String fieldComment;		// フィールドコメント
	private String code;				// コード値
	private String decode;				// デコード値
	
	public String getFieldName() {
		return fieldName;
	}
	
	public String getFieldComment() {
		return fieldComment;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDecode() {
		return decode;
	}

}
