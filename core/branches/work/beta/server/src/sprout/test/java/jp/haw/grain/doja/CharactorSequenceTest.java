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


import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;
import jp.haw.grain.sprout.CharactorSequence;
import jp.haw.grain.sprout.DrawContext;
import jp.haw.grain.sprout.Font;
import jp.haw.grain.sprout.Row;
import jp.haw.grain.xforms.XHTMLElement;

import com.hp.hpl.sparta.Text;
import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Graphics;

/**
 * A test of CharacterSequence
 * @version $Id: CharactorSequenceTest.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class CharactorSequenceTest extends TestCase {

    public CharactorSequenceTest() {
    }

    public CharactorSequenceTest(String name, TestMethod method) {
        super(name, method);
    }

    public void testAlphabetSeq() {
        Text text = new Text("this is test string of testAlphabetSeq. I like test driven development. And you're done?");
        XHTMLElement elem = new XHTMLElement("p");
        Font.setDefaultFont(new FontImpl(com.nttdocomo.ui.Font.getDefaultFont()));
        CharactorSequence cs = new CharactorSequence(text);
        Row row1 = new Row(elem, 240);
        row1.append(cs);
        assertTrue("continue", cs.isContinue());
        row1.apply();
        Row row2 = new Row(elem, 200);
        row2.append(cs);
        assertTrue("continue", cs.isContinue());
        row2.apply();
        Row row3 = new Row(elem, 240);
        row3.append(cs);
        assertTrue("not continue", !cs.isContinue());
        row3.apply();
        MockDrawContext dc = new MockDrawContext();
        row1.draw(dc);
        assertEquals("width", 240, row1.getChildElement(0).getWidth());
        assertEquals("height", 12, row1.getChildElement(0).getHeight());
        assertEquals("toString", "this is test string of testAlphabetSeq. ", dc.drawnString);    
        row2.draw(dc);
        assertEquals("width", 198, row2.getChildElement(0).getWidth());
        assertEquals("height", 12, row2.getChildElement(0).getHeight());
        assertEquals("toString", "I like test driven development. A", dc.drawnString);        
        row3.draw(dc);
        assertEquals("width", 90, row3.getChildElement(0).getWidth());
        assertEquals("height", 12, row3.getChildElement(0).getHeight());
        assertEquals("toString", "nd you're done?", dc.drawnString);      
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new CharactorSequenceTest("testAlphabetSeq", new TestMethod() {
            public void run(TestCase tc) { ((CharactorSequenceTest)tc).testAlphabetSeq(); }
        }));
        return suite;
    }
    
    class MockDrawContext extends DrawContextImpl {

        String drawnString;
        
        public MockDrawContext() {
            super(new MockCanvas().getGraphics(), 0, 0);
        }
        
        public void drawString(String sub, int x, int y) {
            this.drawnString = sub;
        }
        
        public DrawContext moveTo(int x, int y) {
            return this;
        }
        
    }
    
    class MockCanvas extends Canvas {

        public void paint(Graphics arg0) {
        }
        
    }
}
