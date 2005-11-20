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
package jp.grain.xforms;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;
import jp.grain.sprout.ui.DrawContext;
import jp.grain.sprout.ui.FormView;
import jp.grain.sprout.ui.InlineElement;
import jp.grain.sprout.ui.Row;
import jp.grain.xforms.XHTMLElement;

/**
 * A test of LayoutManager
 * @version  $Id$
 * @author Go Takahashi
 */
public class RowTest extends TestCase {

    public RowTest() {
    }

    public RowTest(String name, TestMethod method) {
        super(name, method);
    }

    public void testSimpleInlineElement() {
        XHTMLElement elem = new XHTMLElement("p");
        Row row = new Row(elem, 300);
        assertEquals("max width", 300, row.getMaxWidth());
        assertTrue("1st element", row.append(new MockInlineElement(100, 20)));
        row.apply();
        assertEquals("width", 100, row.getWidth());
        assertEquals("height", 20, row.getHeight());
        assertTrue("2nd element", row.append(new MockInlineElement(150, 10)));
        row.apply();
        assertEquals("width", 250, row.getWidth());
        assertEquals("height", 20, row.getHeight());
        assertTrue("3rd element", row.append(new MockInlineElement(30, 25)));
        row.apply();
        assertEquals("width", 280, row.getWidth());
        assertEquals("height", 25, row.getHeight());
        assertTrue("4th element", !row.append(new MockInlineElement(30, 50)));
        row.apply();
        assertEquals("width", 280, row.getWidth());
        assertEquals("height", 25, row.getHeight());
        assertEquals("max width", 300, row.getMaxWidth());
    }
    
    public void testLargeInlineElement() {
        XHTMLElement elem = new XHTMLElement("p");
        Row row = new Row(elem, 300);
        assertEquals("max width", 300, row.getMaxWidth());
        assertTrue("1st element", row.append(new MockInlineElement(350, 20)));
        row.apply();
        assertEquals("width", 350, row.getWidth());
        assertEquals("height", 20, row.getHeight());
        assertEquals("max width", 300, row.getMaxWidth());
        assertTrue("2nd element", !row.append(new MockInlineElement(50, 20)));
        row.apply();
        assertEquals("width", 350, row.getWidth());
        assertEquals("height", 20, row.getHeight());
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new RowTest("testSimpleInlineElement", new TestMethod() {
            public void run(TestCase tc) { ((RowTest)tc).testSimpleInlineElement(); }
        }));
        suite.addTest(new RowTest("testLargeInlineElement", new TestMethod() {
            public void run(TestCase tc) { ((RowTest)tc).testLargeInlineElement(); }
        }));
        return suite;
    }
    
    class MockInlineElement extends InlineElement {
        
        MockInlineElement(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        /* (non-Javadoc)
         * @see jp.haw.grain.xforms.InlineElement#paint(int, int)
         */
        public void paint(int originX, int originY) {
        }

        /* (non-Javadoc)
         * @see jp.haw.grain.sprout.Renderer#draw(jp.haw.grain.sprout.DrawContext)
         */
        public void draw(DrawContext dc) {
            // Nothing to do            
        }
        
        public void apply() {
            // Nothing to do
        }

        /* (non-Javadoc)
         * @see jp.haw.grain.sprout.InlineElement#action(jp.haw.grain.sprout.FormView, int, int)
         */
        public boolean action(FormView view, int action, int selector) {
            // TODO Auto-generated method stub
            return false;
        }   
    }
    
}
