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
 * Created on 2005/07/21 16:11:59
 * 
 */
package jp.grain.sprout;

import java.util.Vector;

import jp.grain.xforms.RenderableElement;

/**
 * ボックス要素を内包できる物理レイアウト要素。
 * インライン要素を直接、内包することはできない。
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Block extends Box {

    protected Vector children = new Vector();
    private int maxWidth;
    
    public Block(RenderableElement element) {
        this(element, -1);
    }

    public Block(RenderableElement element, int maxWidth) {
        this.element = element;
        this.maxWidth = maxWidth;
    }

    public void apply() {
        this.border = this.element.getStyleByPixel("border");
        if (this.border < 0) this.border = 0;
        this.width = this.element.getStyleByPixel("width");
        if (this.width < 0) {
            if (this.maxWidth >= 0) {
                this.width = this.maxWidth;
            } else if (this.parent != null) {
                this.width = this.parent.getBoxWidth();
            } else {
                this.width = 0;
            }
        }
        this.height = this.element.getStyleByPixel("height");
        if (this.height < 0) {
            this.height = 0;
            for (int i = 0; i < this.children.size(); ++i) {
                Box box = (Box)this.children.elementAt(i);
                box.setRelativePosition(0, height);
                box.apply();
                this.height += box.getBoxHeight();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Block#addChildBox(jp.haw.grain.xforms.Box)
     */
    public void addChildBox(Box child) {
        this.children.addElement(child);
        child.setParent(this);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Block#getChildCount()
     */
    public int getChildCount() {
        return this.children.size();
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Block#getChildBox(int)
     */
    public Box getChildBox(int index) {
        return (Box)this.children.elementAt(index);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Box#append(jp.haw.grain.xforms.InlineElement)
     */
    public void append(InlineElement e) {
        // TODO enable/disable to append inline element
        getFictionalColumn().append(e);
    }
        
    private Column getFictionalColumn() {
        int size = this.children.size();
        Box box = null;
        if (size > 0) box = (Box)this.children.elementAt(size - 1);
        if (box == null || box instanceof Block || !((Column)box).isFictional()) {
            box = new Column(this.element, true);
            addChildBox(box);
        }
        return (Column)box;
    }
    
    public void draw(DrawContext dc) {
        applyStyles(dc);
        for (int i = 0; i < this.children.size(); ++i) {
            Box child = (Box)this.children.elementAt(i);
            child.draw(dc.moveTo(child.x, child.y));
        }        
    }
    



    
}
