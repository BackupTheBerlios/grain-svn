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
 * Created on 2005/07/20 17:26:36
 * 
 */
package jp.grain.sprout.ui;

import java.util.Enumeration;
import java.util.Vector;

import jp.grain.xforms.RenderableElement;

/**
 * インライン要素を内包する物理レイアウト要素
 * ボックス要素を内包することはできない。
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Column extends Box {

    protected Vector inlineElements = new Vector(); 
    protected Vector rows = new Vector();
    private boolean lineBreak;
    private boolean fictional;

    
    // psuide column
    public Column(RenderableElement element) {
        this(element, false);
    }
    
    public Column(RenderableElement element, boolean fictional) {
        this.element = element;
        this.fictional = fictional;
        this.width = element.getStyleByPixel("width");
    }
    
    public void apply() {
        this.width = element.getStyleByPixel("width");
        if (this.width < 0) {
            if (this.parent != null) {
                this.width = this.parent.getBoxWidth();
            } else {
                this.width = 0;
            }
        }
        this.rows.removeAllElements();
        this.height = 0;
        Enumeration elems = this.inlineElements.elements();
        InlineElement ie = null;
        boolean pending = false;
        while (elems.hasMoreElements() || pending) {
            Row row = new Row(this.element, getBoxWidth());
            while(elems.hasMoreElements() || pending) {
                if (!pending) ie = (InlineElement)elems.nextElement();
                pending = !row.append(ie) || ie.isContinue();
                if (pending || ie.isLineBreak()) break;
            }
            row.setRelativePosition(0, height);
            row.apply();
            this.height += row.getBoxHeight();
            this.rows.addElement(row);
            row.setParent(this);
        }   
    }
    
    public int getRowCount() {
        return this.rows.size();
    }
    
    public Row getRow(int index) {
        return (Row)this.rows.elementAt(index);
    }

    public void append(InlineElement element) {
        this.inlineElements.addElement(element);
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Column#isFictional()
     */
    public boolean isFictional() {
        return this.fictional;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Box#addChildBox(jp.haw.grain.xforms.Box)
     */
    public void addChildBox(Box child) {
        throw new RuntimeException("unsupported operation");
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Box#getChildCount()
     */
    public int getChildCount() {
        throw new RuntimeException("unsupported operation");
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Box#getChildBox(int)
     */
    public Box getChildBox(int index) {
        throw new RuntimeException("unsupported operation");
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Renderer#draw(jp.haw.grain.sprout.DrawContext)
     */
    public void draw(DrawContext dc) {
        applyStyles(dc);
        for (int i = 0; i < this.rows.size(); ++i) {
            Row row = (Row)this.rows.elementAt(i);
            row.draw(dc.moveTo(row.x, row.y));
        }
    }
    
}
