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
 * Created on 2005/07/28 14:15:07
 * 
 */
package jp.grain.sprout.platform.doja;

import java.util.Vector;

import jp.grain.sprout.ui.DrawContext;
import jp.grain.sprout.ui.Form;
import jp.grain.sprout.ui.FormContext;
import jp.grain.sprout.ui.InlineElement;
import jp.grain.xforms.Processor;

import com.nttdocomo.device.CodeReader;
import com.nttdocomo.system.InterruptedOperationException;
import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Frame;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.ShortTimer;
import com.nttdocomo.ui.TextBox;

/**
 * FormViewの実装クラスl
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FormContextImpl extends Canvas implements FormContext {

    public static final int KEY_REPEAT_TIMER = 0;
    public static final int ACCERALATION_TIMER = 1;
    private static int SCAN_CAMERA_ID = 0;
    
    private static FormContextImpl instance = new FormContextImpl();
    private String title;
    private Form form;
    private Vector navigativeElements = new Vector();
    private InlineElement focused;
    private int vOffset = 0;
    private ShortTimer activeTimer;
    private int pressedKey;
    private int accelaration;
    private String imeText;
    
    /**
     * 
     */
    private FormContextImpl() {
    }
    
    /**
     * @return
     */
    public static FormContextImpl recycle() {
        instance.navigativeElements.removeAllElements();
        instance.title = null;
        instance.vOffset = 0;
        instance.focused = null;
        instance.form = null;
        return instance;
    }

    /* (non-Javadoc)
     * @see com.nttdocomo.ui.Frame#paint(com.nttdocomo.ui.Graphics)
     */
    public void paint(Graphics g) {
        g.lock();
        DrawContext dc = new DrawContextImpl(g, 0, - this.vOffset);
        dc.setFormView(this);
        this.form.draw(dc);
        g.unlock(true);
    }

    public void init() {
        this.focused = (InlineElement)findNextNavigation();
        setSoftLabel(Frame.SOFT_KEY_2, "MENU");
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#render()
     */
    public void render() {
        Display.setCurrent(this);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#refresh()
     */
    public void refresh() {
        repaint();
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#setRootBlock(jp.haw.grain.xforms.Block)
     */
    public void setForm(Form form) {
        this.form = form;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#getRootBlock()
     */
    public Form getForm() {
        return this.form;
    }

    /**
     * @return
     */
    public InlineElement getFocused() {
        return this.focused;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#addNavigativeElement(jp.haw.grain.xforms.InlineElement)
     */
    public void addNavigativeElement(InlineElement element) {
        this.navigativeElements.addElement(element);
    }

    private InlineElement findNextNavigation() {
        int next = navigationIndexOf(this.focused, 1);
        return (next < 0) ? null : (InlineElement)this.navigativeElements.elementAt(next);
    }

    private InlineElement findPreviousNavigation() {
        int next = navigationIndexOf(this.focused, -1);
        return (next < 0) ? null : (InlineElement)this.navigativeElements.elementAt(next);
    }
    
    private int navigationIndexOf(InlineElement element, int delta) {
        if (this.navigativeElements == null || this.navigativeElements.size() == 0) return -1;
        if (this.focused == null) return 0;
        int index = this.navigativeElements.indexOf(this.focused);
        int next = index + delta;
        if (next < 0) {
            return this.navigativeElements.size() + next;
        } if (next >= this.navigativeElements.size()) {
            return next - this.navigativeElements.size();
        }
        return next;
    }
    
    public void processEvent(int type, int param) {
        try {
            this.imeText = null;
            if (type == Display.KEY_RELEASED_EVENT) {
                if (this.activeTimer != null) {
                    this.activeTimer.stop();
                    this.activeTimer.dispose();
                    this.activeTimer = null;
                    if (this.accelaration != 0) return;
                }
                if (param == Display.KEY_RIGHT || param == Display.KEY_LEFT) {
                    InlineElement current = this.focused;
                    this.focused = (param == Display.KEY_RIGHT) ? findNextNavigation() : findPreviousNavigation();
                    if (this.focused == null) return;
                    if (current == this.focused) return;
                    current.action(this, FormContext.ACT_FOCUS_OUT, FormContext.SEL_NONE);
                    sctollToFocusedElement();
                    this.focused.action(this, FormContext.ACT_FOCUS_IN, FormContext.SEL_NONE);                
                    refresh();
                } else if (param == Display.KEY_DOWN) {
                    scroll(10);
                    refresh();
                } else if (param == Display.KEY_UP) {
                    scroll(-10);
                    refresh();
                } else if (param == Display.KEY_SELECT) {
                    boolean repaint = this.focused.action(this, FormContext.ACT_RELEASED, FormContext.SEL_SELECT);
                    if (repaint) refresh();
                } else if (param == Display.KEY_SOFT2) {
                    Processor.getInstance().getCurrentApp().openApplicationMenu();
                }
            } else if (type == Display.KEY_PRESSED_EVENT) {
                if (param == Display.KEY_DOWN || param == Display.KEY_UP) {
                    this.pressedKey = param; 
                    this.accelaration = 0;
                    this.activeTimer = ShortTimer.getShortTimer(this, KEY_REPEAT_TIMER, 400, false);
                    this.activeTimer.start();
                } else if (param == Display.KEY_SELECT){
                    boolean repaint = this.focused.action(this, FormContext.ACT_PRESSED, FormContext.SEL_SELECT);
                    if (repaint) refresh();
                }
            } else if (type == Display.TIMER_EXPIRED_EVENT) {
                if (param == KEY_REPEAT_TIMER) {
                    if (this.pressedKey == Display.KEY_DOWN) {
                        this.accelaration = 3;
                    } else if (this.pressedKey == Display.KEY_UP) {
                        this.accelaration = -3;
                    }
                    scroll(this.accelaration);
                    refresh();
                    this.activeTimer.dispose();
                    this.activeTimer = ShortTimer.getShortTimer(this, ACCERALATION_TIMER, 80, true);
                    this.activeTimer.start();
                } else if (param == ACCERALATION_TIMER) {
                    if (Math.abs(this.accelaration) < 30) this.accelaration = this.accelaration * 2;
                    scroll(this.accelaration);
                    refresh();
                }
            }
            if (this.imeText != null) {
                processIMEEvent(IME_COMMITTED, this.imeText);
            }
        } catch (RuntimeException e) {
            Processor.getInstance().notifyError(e.getMessage());
        }
    }

    public void processIMEEvent(int type, String text) {
        this.imeText = text;
        boolean repaint = this.focused.action(this, FormContext.ACT_IME_RESULT, (type == IME_CANCELED) ? FormContext.SEL_IME_CANCEL : FormContext.SEL_IME_COMMIT);
        if (repaint) refresh();
    }
    
    private void scroll(int delta) {
        int contentHeight = this.form.getHeight();
        if (delta > 0 && this.vOffset + Display.getHeight() < contentHeight) {
            this.vOffset += delta;
            if (this.vOffset + Display.getHeight() > contentHeight) {
                this.vOffset = contentHeight - Display.getHeight();
            }
        } else if (delta < 0 && this.vOffset > 0) {
            this.vOffset += delta;
            if (this.vOffset < 0) this.vOffset = 0;
        }
    }
    
    private void sctollToFocusedElement() {
        if (this.focused.isIncludedIn(0, this.vOffset, getWidth(), getHeight())) return;
        // int ax = this.focused.getAbsoluteX();
        int ay = this.focused.getAbsoluteY();
        int bottom = ay + this.focused.getHeight();
        int mid = ay + this.focused.getHeight() / 2;
        int margin = getHeight() / 2;
        if (mid < margin || this.form.getHeight() < getHeight()) {
            this.vOffset = 0;
        } else if (this.form.getHeight() - margin < mid) {
            this.vOffset = this.form.getHeight() - getHeight();
        } else {
            this.vOffset = mid - margin;
        }        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.FormView#getTextByInputMethod(java.lang.String, java.lang.String, boolean)
     */
    public void launchIME(String text, String inputMode, boolean secret) {
        int mode;
        if (inputMode == FormContext.IME_SCRIPT_LATIN) {
            mode = TextBox.NUMBER;
        } else if (inputMode == FormContext.IME_SCRIPT_DIGITS) {
            mode = TextBox.NUMBER;
        } else {
            mode = TextBox.KANA;
        }
        imeOn(text, !secret ? TextBox.DISPLAY_ANY : TextBox.DISPLAY_PASSWORD, mode);
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.FormView#getIMEText()
     */
    public String getIMEText() {
        return this.imeText;
    }
    
    /**
     * バーコードの読み込み、読み込んだ文字列を取得
     */
    public void launchCodeReader() {
        try {            
            CodeReader codeReader = CodeReader.getCodeReader(SCAN_CAMERA_ID);
            int[] codeList = codeReader.getAvailableCodes();
            codeReader.read();
            System.out.println("readCode : type = " + codeReader.getResultCode());
            this.imeText = new String(codeReader.getBytes());
        } catch (RuntimeException e) {
            GrainApp.showErrorDialog("QRコード読み取り中にエラーが発生しました。", e);
        } catch (InterruptedOperationException e) {
            GrainApp.showErrorDialog("QRコード読み取り中に割り込みが発生しました。", e);
        }
    }


}
