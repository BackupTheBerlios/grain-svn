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
 * Created on 2005/07/09 15:30:31
 * 
 */
package jp.haw.grain.doja;

import jp.haw.grain.sprout.Button;
import jp.haw.grain.xforms.FormControlElement;

import com.hp.hpl.sparta.Text;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

/**
 * A test of Button
 * @version  $Id$
 * @author Go Takahashi
 */
public class ButtonTest extends TestCase {

    public ButtonTest() {
    }

    public ButtonTest(String name, TestMethod method) {
        super(name, method);
    }

    public void testSimpleButton() {
        FormControlElement trigger = new FormControlElement("trigger");
        FormControlElement label = new FormControlElement("label");
        trigger.appendChild(label);
        Text labelText = new Text("button test"); 
        label.appendChild(labelText);
        Button btn = new Button(trigger, labelText);
        btn.apply();
        assertEquals("width", 74, btn.getWidth());
        assertEquals("height", 20, btn.getHeight());
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ButtonTest("testSimpleButton", new TestMethod() {
            public void run(TestCase tc) { ((ButtonTest)tc).testSimpleButton(); }
        }));
        return suite;
    }
    
}
