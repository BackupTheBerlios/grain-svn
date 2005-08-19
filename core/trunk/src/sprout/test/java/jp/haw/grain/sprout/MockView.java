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
 * Created on 2005/07/28 17:43:13
 * 
 */
package jp.haw.grain.sprout;


/**
 * MockView
 * 
 * @version $Id: MockView.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class MockView implements FormView {

    Block root;

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#render()
     */
    public void render() {
        // TODO not implemented yet
    }

    /**
     * @return
     */
    public int getWidth() {
        return 240;
    }
    
    public int getHeight() {
        return 400;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#refresh()
     */
    public void refresh() {
        // TODO not implemented yet
        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        // TODO not implemented yet
        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#setRootBlock(jp.haw.grain.xforms.Block)
     */
    public void setRootBlock(Block root) {
        this.root = root;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#getRootBlock()
     */
    public Block getRootBlock() {
        return this.root;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#addNavigativeElement(jp.haw.grain.sprout.InlineElement)
     */
    public void addNavigativeElement(InlineElement e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.FormView#getFocused()
     */
    public InlineElement getFocused() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.FormView#launchIME(java.lang.String, java.lang.String, boolean)
     */
    public void launchIME(String text, String inputMode, boolean secret) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.FormView#getIMEText()
     */
    public String getIMEText() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.FormView#launchCodeReader()
     */
    public void launchCodeReader() {
        // TODO Auto-generated method stub
        
    }
}