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
	
	private String packageName;
	private String className;
	private String classComment;
	private List<EnumFieldModel> fieldModelList;
	
	
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
