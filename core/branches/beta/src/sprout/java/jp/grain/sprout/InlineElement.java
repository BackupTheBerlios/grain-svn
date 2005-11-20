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
 * Created on 2005/07/19 13:11:28
 * 
 */
package jp.grain.sprout;

import com.hp.hpl.sparta.Event;

import jp.grain.xforms.FormControlElement;

/**
 * インライン要素をあらわす抽象クラス。
 * 
 * @version $Id$
 * @author Go Takahashi
 */
abstract public class InlineElement extends Renderer {

    public InlineElement() {
    }
    
    public InlineElement fitWidth(int width, boolean force) {
        if (!force && width < getBoxWidth()) return null;
        return this;
    }    
    
    /**
     * 
     */
    public void init() {
        // Default: Nothing to do.        
    }
    
    public int getLeading(int height) {
        return height - getHeight();
    }

    /**
     * @return
     */
    public boolean isLineBreak() {
        return false;
    }

    /**
     * @return
     */
    public boolean isContinue() {
        return false;
    }
    
    public void apply() {
        if (this.element == null) {
            this.width = -1;
            this.height = -1;
        } else {
            this.width = this.element.getStyleByPixel("width");
            this.height = this.element.getStyleByPixel("height");
        }
    }
   
    /**
     * 
     * @param selector
     * @param action
     * @return returns true when this element need to repaint, otherwise false 
     */
    public boolean action(FormView view, int action, int selector) {
        if (this.element == null) return false;
        if (action == FormView.ACT_FOCUS_IN) {
            if (this.element instanceof FormControlElement) {
                ((FormControlElement)this.element).dispatchEvent(new Event("DOMFocusIn", true, false));
            }
        } else if (action == FormView.ACT_FOCUS_OUT) {
            if (this.element instanceof FormControlElement) {
                ((FormControlElement)this.element).dispatchEvent(new Event("DOMFocusOut", true, false));
            }
        }
        return false;
    }
    
}
