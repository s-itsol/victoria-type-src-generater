/**
 * 
 */
package net.sitsol.victoria.tsgen.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogChute;

/**
 * Velocityテキスト出力支援クラス
 * 
 * @author rei_shibano
 */
public class VelocityTextWriter {

	private VelocityEngine velocityEngine = new VelocityEngine();			// Velocityエンジン
	private String templateReadDirPath = null;								// テンプレート読込みディレクトリパス
	private String inputEncoding = null;									// 入力エンコーディング
	private String outputEncoding = null;									// 出力エンコーディング

	/**
	 * コンストラクタ
	 * @param templateReadDirPath テンプレート読込みディレクトリパス
	 * @param inputEncoding 入力エンコーディング
	 * @param outputEncoding 出力エンコーディング
	 */
	public VelocityTextWriter(String templateReadDirPath, String inputEncoding, String outputEncoding) {
		
		this.templateReadDirPath	= templateReadDirPath;
		this.inputEncoding			= inputEncoding;
		this.outputEncoding			= outputEncoding;
		
		// Velocityエンジン初期化
		this.init();
	}

	/**
	 * Velocityエンジン初期化
	 */
	private void init() {
		
		try {
			// Velocity用プロパティ設定の生成
			Properties props = this.createVelocityProperties();
			
			// Velocityエンジン初期化
			this.getVelocityEngine().init(props);
			
		} catch (Exception ex) {
			throw new RuntimeException("Velocityエンジン初期化でエラーが発生しました。"
										, ex
			);
		}
	}

	/**
	 * Velocity用プロパティ設定の生成
	 * @return Velocity用プロパティ設定のインスタンス
	 */
	protected Properties createVelocityProperties() {
		
		// Velocity用プロパティ設定
		Properties props = new Properties();
		{
			// テンプレートローダー
			props.setProperty(VelocityEngine.RESOURCE_LOADER, "file");
			props.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, this.getTemplateReadDirPath());
			
			// 生成ログ ※出力させない
			props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
			
			// 入力ファイル(＝テンプレート)文字コード
			props.setProperty(VelocityEngine.INPUT_ENCODING, this.getInputEncoding());
			// 出力ファイル(＝生成JAVAソース)文字コード
			props.setProperty(VelocityEngine.OUTPUT_ENCODING, this.getOutputEncoding());
		}
		
		return props;
	}

	/**
	 * 文字列生成
	 * @param templateFineName	IN    ：テンプレートファイル名
	 * @param bindObjList		IN    ：バインドオブジェクトリスト
	 * @return 生成した文字列
	 */
	public String createString(String templateFineName, List<Pair<String, Object>> bindObjList) {
		
		try (
			Writer writer = new StringWriter();
		) {
			// リソース出力
			this.writeResource(templateFineName, bindObjList, writer);
			
			// 文字列化して返す
			return writer.toString();
			
		} catch (Exception ex) {
			throw new RuntimeException("Velocity文字列生成でエラーが発生しました。"
										, ex
			);
		}
	}

	/**
	 * テキストファイル生成
	 * @param templateFineName	IN    ：テンプレートファイル名
	 * @param bindObjList		IN    ：バインドオブジェクトリスト
	 * @param writeFilePath	IN/OUT：出力先ファイルパス
	 */
	public void createTextFile(String templateFineName, List<Pair<String, Object>> bindObjList, String writeFilePath) {
		
		try {
			// 出力先ファイル情報
			File writeFile = new File(writeFilePath);
			
			// 出力先ディレクトリ情報
			File dirInfo = new File(writeFile.getParent());
			
			// ディレクトリが存在しなかった場合は作成
			if ( !dirInfo.exists() ) {
				dirInfo.mkdirs();
			}
			
			try (
				OutputStream outStream = new FileOutputStream(writeFile);
				Writer writer = new OutputStreamWriter(outStream, this.getOutputEncoding());
			) {
				// リソース出力
				this.writeResource(templateFineName, bindObjList, writer);
			}
			
		} catch (Exception ex) {
			throw new RuntimeException("Velocityテキストファイル生成でエラーが発生しました。"
											+ "出力先ファイルパス：[" + writeFilePath + "]"
										, ex
			);
		}
	}

	/**
	 * リソース出力
	 * @param templateFineName	IN    ：テンプレートファイル名
	 * @param bindObjList		IN    ：バインドオブジェクトリスト
	 * @param writer			IN/OUT：リソース出力先
	 */
	private void writeResource(String templateFineName, List<Pair<String, Object>> bindObjList, Writer writer) {
		
		try {
			// Velocityテンプレート取得
			Template template = this.getVelocityEngine().getTemplate(templateFineName);
			
			// Velocityコンテキスト生成
			VelocityContext context = new VelocityContext();
			if ( bindObjList != null ) {
				// バインドオブジェクトのループ
				bindObjList.forEach(
					(bindObj) -> {
						// オブジェクトインスタンスのバインド
						context.put(bindObj.getKey(), bindObj.getValue());
					}
				); 
			}
			
			// コンテキストをテンプレートへマージ＆生成出力
			template.merge(context, writer);
			
		} catch (Exception ex) {
			
			throw new RuntimeException("Velocityファイル生成でエラーが発生しました。"
											+ "テンプレート読込みディレクトリ：[" + this.getTemplateReadDirPath() + "]"
											+ ", テンプレートファイル名：[" + templateFineName + "]"
										, ex
			);
		}
	}

	/**
	 * テンプレート読込みディレクトリパス取得
	 * @return テンプレート読込みディレクトリパス
	 */
	protected String getTemplateReadDirPath() {
		return templateReadDirPath;
	}

	/**
	 * 入力エンコーディング取得
	 * @return 入力エンコーディング
	 */
	protected String getInputEncoding() {
		return inputEncoding;
	}

	/**
	 * 出力エンコーディング取得
	 * @return 出力エンコーディング
	 */
	protected String getOutputEncoding() {
		return outputEncoding;
	}

	/**
	 * Velocityエンジン取得
	 * @return Velocityエンジンのインスタンス
	 */
	protected VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

}
