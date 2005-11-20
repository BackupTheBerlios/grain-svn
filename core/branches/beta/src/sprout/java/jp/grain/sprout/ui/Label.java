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
 * Created on 2005/08/12 20:07:00 
 */
package jp.grain.sprout.ui;

import jp.grain.xforms.FormControlElement;

/**
 * TextBox
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Label extends InlineElement {

    private static final int COLOR_BLACK = 0x000000; // 黒
    private static final int COLOR_FIELD = 0xFFFFFF; // 背景
    private static final int COLOR_EDGE_DARK = 0x333333; // エッジ(暗）
    private static final int COLOR_EDGE_LIGHT = 0xCCCCCC; // エッジ(明)
    private static final int COLOR_FOCUS = 0xFF6666; // 選択時選択色
    private static final int SIZE_MINIMUM = 3;
            
    /**
     * 
     */
    public Label(FormControlElement element) {
       this.element = element;
    }
    
    public void apply() {
        super.apply();
        if (this.width < SIZE_MINIMUM) {
            String text = ((FormControlElement)this.element).getBindingSimpleContent();
            this.width = Font.getDefaultFont().getWidth(text);
        }
        if (this.height < SIZE_MINIMUM)
            this.height = Font.getDefaultFont().getHeight();
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Renderer#draw(jp.haw.grain.sprout.DrawContext)
     */
    public void draw(DrawContext dc) {
        applyStyles(dc);
        String text = ((FormControlElement)this.element).getBindingSimpleContent();
        dc.clipRect(this.margin, this.margin, getBoxWidth() - this.margin * 2, getBoxHeight() - this.margin * 2);
        dc.drawString((text != null) ? text : "", getContentX(), getContentY() + (getHeight() - Font.getDefaultFont().getHeight()) / 2);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.InlineElement#action(int, int)
     */
    public boolean action(FormView view, int action, int selector) {
        return false;
    }

}
