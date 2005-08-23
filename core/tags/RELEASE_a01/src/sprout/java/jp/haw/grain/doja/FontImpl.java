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
 * Created on 2005/08/08 12:30:36
 * 
 */
package jp.haw.grain.doja;

import jp.haw.grain.sprout.Font;
import jp.haw.grain.xforms.RenderableElement;

/**
 * A concrete class of Font for doja
 * 
 * @version $Id: FontImpl.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class FontImpl extends Font {

    com.nttdocomo.ui.Font font;
    
    public FontImpl(com.nttdocomo.ui.Font font) {
        this.font = font;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Font#getLengthUntilLineBreak(java.lang.String, int, int, int)
     */
    public int getLengthUntilLineBreak(String src, int start, int length, int width) {
        return this.font.getLineBreak(src, start, length, width);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Font#createFontOf(jp.haw.grain.xforms.RenderableElement)
     */
    protected Font createFontOf(RenderableElement element) {
        return new FontImpl(com.nttdocomo.ui.Font.getDefaultFont());
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Font#getWidth()
     */
    public int getWidth(String src) {
        return this.font.getBBoxWidth(src);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Font#getHeight(java.lang.String)
     */
    public int getHeight() {
        return this.font.getHeight();
    }

}
