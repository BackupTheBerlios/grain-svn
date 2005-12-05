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
 * Created on 2005/08/08 4:25:55
 * 
 */
package jp.grain.sprout.ui;


/**
 * Renderer is an element draws something to display.
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public abstract class Renderer {
    
    public static final int DEFAULT_BG_COLOR = 0xFFFFFF; 
    public static final int COLOR_EDGE_DARK = 0x333333; // エッジ(暗）
    public static final int COLOR_EDGE_LIGHT = 0xCCCCCC; // エッジ(明)
    
    protected int x;
    protected int y;
    protected int width = -1;
    protected int height = -1;
    protected int margin;
    protected int border;
    protected int padding;
    protected Renderer parent;
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getContentX() {
        return this.margin + this.border + this.padding;
    }
    
    public int getContentY() {
        return this.margin + this.border + this.padding;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }
    

    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getBoxWidth() {
        return this.width + (this.margin + this.border + this.padding) * 2;
    }

    public int getBoxHeight() {
        return this.height + (this.margin + this.border + this.padding) * 2;
    }
    
    public boolean isIncludedIn(int bx, int by, int width, int height) {
        int x = 0;
        int y = 0;
        for (Renderer r = this; r != null; r = r.parent) {
            x += r.x;
            y += r.y;
        }
        if (x > bx + width || y > by + height) return false;
        if (x + this.width < bx || y + this.height < by) return false;
        return true;
    }
    
    /**
     * @param x
     * @param y
     */
    public void setRelativePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void draw(DrawContext dc);
    public abstract void apply();

    public void setParent(Renderer parent) {
        this.parent = parent;
    }
    
    /**
     * @return
     */
    public int getAbsoluteY() {
        int y = 0;
        for (Renderer r = this; r != null; r = r.parent) {
            y += r.y;
        }
        return y;
    }
    
    protected void applyStyles(DrawContext dc) {
//        int bgColor = dc.getColorByHex(this.element.getStyle("background-color"));
//        if (bgColor != -1 || this.parent == null) {
//            dc.setColor((bgColor < 0) ? DEFAULT_BG_COLOR : bgColor);
//            dc.fillRect(this.margin, this.margin, getBoxWidth() - this.margin * 2, getBoxHeight() - this.margin * 2);
//        }
//        if (this.border > 0) {
//            if ("inset".equals(this.element.getStyle("border-style"))) {
//                dc.setColor(COLOR_EDGE_DARK);
//                dc.drawLine(this.margin, this.margin, getBoxWidth() - this.margin - 1, this.margin);
//                dc.drawLine(this.margin, this.margin, this.margin, getBoxHeight() - this.margin - 1);
//                dc.setColor(COLOR_EDGE_LIGHT);
//                dc.drawLine(getBoxWidth() - this.margin - 1, this.margin, getBoxWidth() - this.margin - 1, getBoxHeight() - this.margin - 1);
//                dc.drawLine(this.margin, getBoxHeight() - this.margin - 1, getBoxWidth() - this.margin - 1, getBoxHeight() - this.margin - 1);
//            } else {
//                dc.setColor(COLOR_EDGE_DARK);
//                dc.drawRect(this.margin, this.margin, getBoxWidth() - this.margin * 2 - 1, getBoxHeight() - this.margin * 2 - 1);
//            }
//        }
    }

}
