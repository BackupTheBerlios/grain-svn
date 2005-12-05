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
 * Created on 2005/07/24 16:50:00 
 */
package jp.grain.sprout.ui;

import com.hp.hpl.sparta.Event;

import jp.grain.xforms.FormControlElement;
import jp.grain.xforms.XFormsElement;

/**
 * TextBox
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class TextBox extends InlineElement {

    public static final int INPUT_DEVICE_IME = 0;
    public static final int INPUT_DEVICE_QR = 0;
        
    private static final int COLOR_BLACK = 0x000000; // 黒
    private static final int COLOR_FIELD = 0xFFFFFF; // 背景
    private static final int COLOR_EDGE_DARK = 0x333333; // エッジ(暗）
    private static final int COLOR_EDGE_LIGHT = 0xCCCCCC; // エッジ(明)
    private static final int COLOR_FOCUS = 0xFF6666; // 選択時選択色
    private static final int SIZE_BORDER = 1;
    private static final int SIZE_EDGE = 1;
    private static final int SIZE_MARGIN = SIZE_EDGE + SIZE_BORDER;
    private static final int SIZE_MINIMUM = SIZE_MARGIN + 3;
    
    private int padding = 2;
    private int inputDevice;
    private String inputMode;
    
    /**
     * 
     */
    public TextBox() {
    }
    
    public void apply() {
        super.apply();
        if (this.width < SIZE_MINIMUM) 
            this.width = SIZE_MINIMUM + (SIZE_MARGIN + this.padding) * 2;
        if (this.height < SIZE_MINIMUM) 
            this.height = Font.getDefaultFont().getHeight() + (SIZE_MARGIN + this.padding) * 2;        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.Renderer#draw(jp.haw.grain.sprout.DrawContext)
     */
    public void draw(DrawContext dc) {
        dc.setColor(COLOR_FIELD);
        dc.fillRect(SIZE_MARGIN, SIZE_MARGIN, getWidth() - SIZE_MARGIN * 2, getHeight() - SIZE_MARGIN * 2);
        dc.setColor(COLOR_EDGE_DARK);
        dc.drawLine(SIZE_BORDER, SIZE_BORDER, getWidth() - SIZE_MARGIN - 1, SIZE_BORDER);
        dc.drawLine(SIZE_BORDER, SIZE_BORDER, SIZE_BORDER, getHeight() - SIZE_MARGIN - 1);
        dc.setColor(COLOR_EDGE_LIGHT);
        dc.drawLine(getWidth() - SIZE_BORDER - 1, SIZE_BORDER, getWidth() - SIZE_BORDER - 1, getHeight() - SIZE_BORDER - 1);
        dc.drawLine(SIZE_BORDER, getHeight() - SIZE_BORDER - 1, getWidth() - SIZE_BORDER - 1, getHeight() - SIZE_BORDER - 1);
        if (dc.getFormView().getFocused() == this) {
            dc.setColor(COLOR_FOCUS);
            dc.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        dc.setColor(COLOR_BLACK);
        String text = getBindingSimpleContent();
        dc.clipRect(SIZE_MARGIN, SIZE_MARGIN, getWidth() - SIZE_MARGIN * 2, getHeight() - SIZE_MARGIN * 2);
        dc.drawString((text != null) ? text : "", SIZE_MARGIN + this.padding, SIZE_MARGIN + this.padding);    
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.InlineElement#action(int, int)
     */
    public boolean action(FormContext view, int action, int selector) {
        super.action(view, action, selector);
        if (action == FormContext.ACT_RELEASED) {
            if (selector == FormContext.SEL_SELECT) {
                if (this.inputDevice == INPUT_DEVICE_QR) {
                    view.launchCodeReader();                    
                } else {
                    view.launchIME(getBindingSimpleContent(), this.inputMode, false);
                }
            }
        } else if (action == FormContext.ACT_IME_RESULT) {
            if (selector == FormContext.SEL_IME_COMMIT) {
                setBindingSimpleContent(view.getIMEText());
                //TODO dispatch event
                //fce.dispatchEvent(new Event("xforms-value-changed", true, false));
                return true;
            }
        }
        return false;
    }

}
