/*
 * 
 * Grain Core - A XForms processor for mobile terminals.
 * Copyright (C) 2005 HAW International Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Created on 2005/05/07
 *
 */
package jp.haw.grain.framework.xml;

/**
 * XMLのパース中に問題が発生した場合に生成される例外<br>
 * I/Oエラー、XMLの構文エラーなどで発生
 * 
 * @version $Id$
 * @author Go Takahashi
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
