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
 * Created on 2005/07/20 14:57:21
 * 
 */
package jp.haw.grain.sprout;

import jp.haw.grain.xforms.FormControlElement;

import com.hp.hpl.sparta.Event;
import com.hp.hpl.sparta.Text;

/**
 * Button実装クラス
 * 
 * @version $Id: Button.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class Button extends InlineElement {

    private static final int COLOR_BLACK = 0x000000; // 黒
    private static final int COLOR_BUTTON = 0x999999; // ボタン背景
    private static final int COLOR_EDGE_DARK = 0x333333; // ボタンエッジ(暗）
    private static final int COLOR_EDGE_LIGHT = 0xCCCCCC; // ボタンエッジ(明)
    private static final int COLOR_FOCUS = 0xFF6666; // ボタン選択時選択色
    private static final int SIZE_BORDER = 1;
    private static final int SIZE_EDGE = 1;
    private static final int SIZE_MARGIN = SIZE_EDGE + SIZE_BORDER;
    private static final int SIZE_MINIMUM = SIZE_MARGIN + 3;
    
    private Text label;
    private int padding = 2;
    private boolean pressed;
    
    public Button(FormControlElement element, Text label) {
        this.element = element;
        this.label = label;
    }
    
    private String getLabelName() {
        return this.label.getData();
    }
    
    public void apply() {
        super.apply();
        if (width < SIZE_MINIMUM) 
            this.width = Font.getDefaultFont().getWidth(getLabelName()) + (SIZE_MARGIN + this.padding) * 2;
        if (height < SIZE_MINIMUM) 
            this.height = Font.getDefaultFont().getHeight() + (SIZE_MARGIN + this.padding) * 2;        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Renderer#draw(jp.haw.grain.sprout.DrawContext)
     */
    public void draw(DrawContext dc) {
        dc.setColor(COLOR_BUTTON);
        dc.fillRect(SIZE_MARGIN, SIZE_MARGIN, getWidth() - SIZE_MARGIN * 2, getHeight() - SIZE_MARGIN * 2);
        dc.setColor(this.pressed ?COLOR_EDGE_DARK :COLOR_EDGE_LIGHT);
        dc.drawLine(SIZE_BORDER, SIZE_BORDER, getWidth() - SIZE_MARGIN * 2, SIZE_BORDER);
        dc.drawLine(SIZE_BORDER, SIZE_BORDER, SIZE_BORDER, getHeight() - SIZE_MARGIN * 2);
        dc.setColor(this.pressed ? COLOR_EDGE_LIGHT : COLOR_EDGE_DARK);
        dc.drawLine(getWidth() - SIZE_BORDER * 2, SIZE_BORDER, getWidth() - SIZE_BORDER * 2, getHeight() - SIZE_BORDER * 2);
        dc.drawLine(SIZE_BORDER, getHeight() - SIZE_BORDER * 2, getWidth() - SIZE_BORDER * 2, getHeight() - SIZE_BORDER * 2);
        if (dc.getFormView().getFocused() == this) {
            dc.setColor(COLOR_FOCUS);
            dc.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        dc.setColor(COLOR_BLACK);
        dc.drawString(this.label.getData(), SIZE_MARGIN + this.padding, SIZE_MARGIN + this.padding);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.InlineElement#action(int, int)
     */
    public boolean action(FormView view, int action, int selector) {
        super.action(view, action, selector);
        if (selector == FormView.SEL_SELECT) {
            if (action == FormView.ACT_PRESSED) {
                this.pressed = true;
                return true;
            } else if (action == FormView.ACT_RELEASED) {
                this.pressed = false;
                this.element.dispatchEvent(new Event("DOMActivate", true, true));
                return true;
            }
        }
        return false;
    }
}
