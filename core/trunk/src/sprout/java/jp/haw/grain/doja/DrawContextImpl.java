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
 * Created on 2005/08/07 18:09:57
 * 
 */
package jp.haw.grain.doja;

import com.nttdocomo.ui.Font;
import com.nttdocomo.ui.Graphics;

import jp.haw.grain.sprout.DrawContext;
import jp.haw.grain.sprout.FormView;

/**
 * Implementation of interface DrawContext for doja
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class DrawContextImpl implements DrawContext {

    private Graphics grp;
    private int originX;
    private int originY;
    private FormView view;
    private Font font;
    
    /**
     * 
     */
    public DrawContextImpl(Graphics grp, int originX, int originY) {
        grp.setOrigin(originX, originY);
        this.grp = grp;
        this.originX = originX;
        this.originY = originY;
    }
            
    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#getColorByHex(java.lang.String)
     */
    public int getColorByHex(String hexValue) {
        if (hexValue != null && hexValue.startsWith("#") && hexValue.length() == 7 ) {
            int color = 0;
            for (int i = 0; i < 3; ++i) {
                color = color << 8;
                int start = i * 2 + 1;
                int val = Integer.parseInt(hexValue.substring(start, start + 2), 16);
                color |= val;
            }
            return color;
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#setColor(int)
     */
    public void setColor(int c) {
        this.grp.setColor(Graphics.getColorOfRGB((c >>> 16) & 0xFF, (c >>> 8) & 0xFF, c & 0xFF));
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#fillRect(int, int, int, int)
     */
    public void fillRect(int x, int y, int width, int height) {
        this.grp.fillRect(x, y, width, height);
    }

    public void saveOrigin(int[] origin) {
        if (origin == null || origin.length == 2) return;
        origin[0] = this.originX;
        origin[1] = this.originY;
    }
    
    public void restoreOrigin(int[] origin) {
        if (origin == null || origin.length == 2) return;
        this.originX = origin[0];
        this.originY = origin[1];        
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#moveTo(int, int)
     */
    public DrawContext moveTo(int x, int y) {
        DrawContext dc = new DrawContextImpl(this.grp.copy(), this.originX + x, this.originY + y);
        dc.setFormView(this.view);
        return dc;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#drawString(java.lang.String, int, int)
     */
    public void drawString(String sub, int x, int y) {
        this.grp.setColor(COLOR_BLACK);
        Font df = (this.font == null) ? Font.getDefaultFont() : this.font;
        this.grp.drawString(sub,  x, y + df.getAscent());
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#drawRect(int, int, int, int)
     */
    public void drawRect(int x, int y, int width, int height) {
        this.grp.drawRect(x, y, width, height);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#setFormView(jp.haw.grain.xforms.FormView)
     */
    public void setFormView(FormView view) {
        this.view = view;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#getFormView()
     */
    public FormView getFormView() {
        return this.view;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#drawLine(int, int, int, int)
     */
    public void drawLine(int sx, int sy, int ex, int ey) {
        this.grp.drawLine(sx, sy, ex, ey);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#clipRect(int, int, int, int)
     */
    public void clipRect(int x, int y, int width, int height) {
        this.grp.clearClip();
        this.grp.clipRect(x, y, width, height);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.DrawContext#setFont(jp.haw.grain.sprout.Font)
     */
    public void setFont(jp.haw.grain.sprout.Font font) {
        Font df = ((FontImpl)font).getFont();
        this.grp.setFont(df);
        this.font = df;
    }
    
}
