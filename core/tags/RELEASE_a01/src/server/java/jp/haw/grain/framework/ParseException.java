/*
 * $Header$
 * 
 * Created on 2005/02/07
 *
 * TODO jp.haw.grain.framework.xml.ParserExceptionとの統合
 */
package jp.haw.grain.framework;

/**
 * デモアプリケーションで使用されるParse例外
 * 
 * @author go
 */
public class ParseException extends Exception {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3690759479604359216L;

	public ParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ParseException(Throwable cause) {
		super(cause);
	}
}
