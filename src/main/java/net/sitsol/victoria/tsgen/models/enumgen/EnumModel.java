/**
 * 
 */
package net.sitsol.victoria.tsgen.models.enumgen;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 * Enumモデル
 * 
 * @author rei_shibano
 */
@AllArgsConstructor
public class EnumModel {
	
	private String packageName;							// パッケージ名
	private String className;							// 型名
	private String classComment;						// 型コメント
	private List<EnumFieldModel> fieldModelList;		// フィールドモデル名
	
	
	public String getPackageName() {
		return packageName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getClassComment() {
		return classComment;
	}
	
	public List<EnumFieldModel> getFieldModelList() {
		return fieldModelList;
	}

}
