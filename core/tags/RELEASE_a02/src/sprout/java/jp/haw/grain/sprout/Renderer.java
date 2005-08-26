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
package jp.haw.grain.sprout;

import jp.haw.grain.xforms.RenderableElement;

/**
 * Renderer is an element draws something to display.
 * 
 * @version $Id: Renderer.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public abstract class Renderer {
    
    static final int DEFAULT_BG_COLOR = 0xFFFFFF;
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected RenderableElement element;
    protected Renderer parent;
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
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
        int bgColor = dc.getColorByHex(this.element.getStyle("background-color"));
        if (bgColor != -1) {
            dc.setColor(bgColor);
            dc.fillRect(0, 0, getWidth(), getHeight());
        } else if (this.parent == null){
            dc.setColor(DEFAULT_BG_COLOR);
            dc.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
