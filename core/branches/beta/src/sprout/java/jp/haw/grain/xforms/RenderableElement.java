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
 * Created on 2005/07/14 4:15:54
 * 
 */
package jp.haw.grain.xforms;

import java.util.Hashtable;

import com.hp.hpl.sparta.Element;

/**
 * 物理レイアウト可能な要素
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public abstract class RenderableElement extends Element {

    public static final int LAYOUT_BLOCK_CONTAINER = 0;
    public static final int LAYOUT_COLUMN = 1;
    public static final int LAYOUT_INLINE_CONTAINER = 2;
    public static final int LAYOUT_INLINE_REPLACEMENT = 3;
    
    private Hashtable styles = new Hashtable();
    
    RenderableElement(String name) {
        super(name);
    }

    public abstract void accept(Visitor visitor);
    
    public void setStyle(String name, String value) {
        setStyle(name, value, true);
    }

    public void setStyle(String name, String value, boolean overWrite) {
        if (!overWrite && this.styles.containsKey(name)) return;
        this.styles.put(name, value);
    }    
    
    public String getStyle(String name, boolean inherit) {
        for (Element e = this; (e != null) && inherit; e = e.getParentNode()) {
            if (e instanceof RenderableElement) {
                String value = (String)((RenderableElement)e).styles.get(name);
                if (value != null) return value;
            }
        }
        return null;
    }
    
    public String getStyle(String name) {
        return getStyle(name, true);
    }
    
    /**
     * @param string
     * @return
     */
    public int getStyleByPixel(String name) {
        String value = getStyle(name);
        if (value == null) return -1;
        int index = value.indexOf("px");
        if (index > -1) return Integer.parseInt(value.substring(0, index));
        // FIXME throws exception
        return 0;
    }

    /**
     * 
     */
    public void applyStyles() {
        String styles = getAttribute("style");
        if (styles == null) return;
        for (int start = 0, delim; start < styles.length(); start = delim + 1) {
            delim = styles.indexOf(';', start);
            if (delim < 0) {
                applyStyle(styles.substring(start).trim());
                break;
            } else {
                applyStyle(styles.substring(start, delim).trim());
            }
        }
    }
    
    private void applyStyle(String style) {
        int colon = style.indexOf(':');
        if (colon < 0 || colon == style.length() - 1) return;
        setStyle(style.substring(0, colon).trim(), style.substring(colon + 1).trim());
    }
    
}
