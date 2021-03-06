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
 * Created on 2005/07/14 15:15:16
 * 
 */
package jp.grain.sprout.ui;

import java.util.Vector;

/**
 * 物理レイアウト上の一行を表す。
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Row extends Renderer {
    
    protected Vector inlineElements = new Vector();
    private int maxWidth;
    private int minHeight;
    
    public Row(int maxWidth) {
        this.maxWidth = maxWidth;
    }
    
    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void apply() {
        this.height = this.minHeight;
        int x = 0;
        for (int i = 0; i < this.inlineElements.size(); ++i) {
            InlineElement ie = (InlineElement)this.inlineElements.elementAt(i);
            ie.setRelativePosition(x, ie.getLeading(getBoxHeight()) / 2);
            x += ie.getBoxWidth();
        }
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Row#append(jp.haw.grain.xforms.InlineElement)
     */
    public boolean append(InlineElement element) {
        int w = this.maxWidth - this.width;
        element.apply();
        InlineElement e = element.fitWidth(w, false);
        if (e == null) {
            if (this.inlineElements.size() > 0) return false;
            e = element.fitWidth(w, true);
        }
        this.inlineElements.addElement(e);
        e.setParent(this);
        this.width += e.getBoxWidth();
        if (this.minHeight < e.getBoxHeight()) this.minHeight = e.getBoxHeight();
        return true;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Row#getChildCount()
     */
    public int getChildCount() {
        return this.inlineElements.size();
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Row#getChildElement(int)
     */
    public InlineElement getChildElement(int index) {
        return (InlineElement)this.inlineElements.elementAt(index);
    }
    
    public void draw(DrawContext dc) {
        for (int i = 0; i < this.inlineElements.size(); ++i) {
            InlineElement ie = (InlineElement)this.inlineElements.elementAt(i);
            ie.draw(dc.moveTo(ie.x, ie.y));
        }
    }
}
