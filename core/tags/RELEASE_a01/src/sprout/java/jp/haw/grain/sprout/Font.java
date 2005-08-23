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
 * Created on 2005/08/08 11:48:42
 * 
 */
package jp.haw.grain.sprout;

import jp.haw.grain.xforms.RenderableElement;

/**
 * スタイルシート指定されるFont
 * 
 * @version $Id: Font.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public abstract class Font {
    
    private static Font defaultFont;
    
    public static void setDefaultFont(Font defaultFont) {
        Font.defaultFont = defaultFont;
    }
    
    public static Font getDefaultFont() {
        return Font.defaultFont;
    }
    
    public static Font getFontOf(RenderableElement element) {
        return Font.defaultFont.createFontOf(element);
    }
    
    protected abstract Font createFontOf(RenderableElement element); 
    public abstract int getLengthUntilLineBreak(String src, int start, int length, int width);
    public abstract int getWidth(String src);
    public abstract int getHeight();


}
