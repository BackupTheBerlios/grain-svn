/*
 * $Header$
 * 
 * Created on 2005/05/07
 *
 */
package jp.haw.grain.framework.xml;

/**
 * XMLのパース中に問題が発生した場合に生成される例外<br>
 * I/Oエラー、XMLの構文エラーなどで発生
 * 
 * @author go
 */
public class ParseException extends Exception {
	/**
	 * <code>serialVersionUID = 1L</code>
	 */
	private static final long serialVersionUID = 1L;

	public ParseException(Throwable cause) {
		super(cause);
	}
}	
