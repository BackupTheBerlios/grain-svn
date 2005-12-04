/*
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
 * Created on 2004/12/17
 *
 */
package jp.haw.grain.sprout;


/**
 * フォームの表示
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public interface FormView {
    
    public static final int SEL_NONE = -1;
    public static final int SEL_SELECT = 0;
    public static final int SEL_IME_COMMIT = 2;
    public static final int SEL_IME_CANCEL = 3;

    public static final int ACT_PRESSED = 0;
    public static final int ACT_RELEASED = 1;
    public static final int ACT_IME_RESULT = 2;
    public static final int ACT_FOCUS_IN = 3;
    public static final int ACT_FOCUS_OUT = 4;

    public static final String IME_SCRIPT_LATIN = "latin";
    public static final String IME_SCRIPT_HIRAGANA = "hiragana";
    public static final String IME_SCRIPT_DIGITS = "digits";
    
    int getWidth();
	void render();
	void refresh();
    void setTitle(String title);
    void setRootBlock(Block block);
    Block getRootBlock();
    void addNavigativeElement(InlineElement e);
    InlineElement getFocused();
    /**
     * @param bindingSimpleContent
     * @param inputMode
     * @param b
     * @return
     */
    void launchIME(String text, String inputMode, boolean secret);
    String getIMEText();
    void launchCodeReader();
}
