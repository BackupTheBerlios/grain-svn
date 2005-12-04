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
package jp.grain.profile.doja;

import java.util.Hashtable;

import jp.grain.sprout.ui.Font;
import jp.grain.sprout.ui.LayoutManager;
import jp.grain.xforms.RenderableElement;

import com.hp.hpl.sparta.Text;

/**
 * A concrete class of Font for doja
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FontImpl extends Font {

    Hashtable cache = new Hashtable();
    
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

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Font#createFontOf(com.hp.hpl.sparta.Text)
     */
    protected Font createFontOf(Text text) {
        RenderableElement parent = LayoutManager.getImmediatelyEnclosingElementOf(text);
        if (parent == null) return new FontImpl(com.nttdocomo.ui.Font.getDefaultFont());
        int type = 0;
        String fontSize = parent.getStyle("font-size");
        if ("medium".equals(fontSize)) {
            type |= com.nttdocomo.ui.Font.SIZE_MEDIUM;
        } else if ("large".equals(fontSize)) {
            type |= com.nttdocomo.ui.Font.SIZE_LARGE;
        } else if ("small".equals(fontSize)) {
            type |= com.nttdocomo.ui.Font.SIZE_SMALL;
        } else if ("x-small".equals(fontSize)) {
            type |= com.nttdocomo.ui.Font.SIZE_TINY;
        }
        String fontWeight = parent.getStyle("font-weight");
        String fontStyle = parent.getStyle("font-style");
        if ("bold".equals(fontWeight) && "italic".equals(fontWeight)) {
            type |= com.nttdocomo.ui.Font.STYLE_BOLDITALIC;
        } else if ("bold".equals(fontWeight)) {
            type |= com.nttdocomo.ui.Font.STYLE_BOLD;
        } else if ("italic".equals(fontWeight)) {
            type |= com.nttdocomo.ui.Font.STYLE_ITALIC;
        }
        com.nttdocomo.ui.Font aFont = (com.nttdocomo.ui.Font)this.cache.get(new Integer(type));
        if (aFont == null) {
            aFont = com.nttdocomo.ui.Font.getFont(type);
            this.cache.put(new Integer(type), aFont);
        }
        return new FontImpl(aFont);
    }

    /**
     * @return
     */
    com.nttdocomo.ui.Font getFont() {
        return this.font;
    }

}
