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
import jp.grain.sprout.ui.Column;
import jp.grain.sprout.ui.DrawContext;
import jp.grain.sprout.ui.FormContext;
import jp.grain.sprout.ui.InlineElement;

/**
 * A test of Column
 * @version $Id$
 * @author Go Takahashi
 */
public class ColumnTest extends TestCase {

    public ColumnTest() {
    }

    public ColumnTest(String name, TestMethod method) {
        super(name, method);
    }

    public void testSimpleColumn() {
        Column column = new Column();
        column.setWidth(300);
        column.apply();
        assertEquals("width", 300, column.getWidth());
        assertEquals("line count", 0, column.getRowCount());
        column.append(new MockInlineElement(100, 20));
        column.apply();
        assertEquals("width", 300, column.getWidth());
        assertEquals("height", 20, column.getHeight());
        assertEquals("line count", 1, column.getRowCount());
        column.append(new MockInlineElement(150, 10));
        column.apply();
        assertEquals("width", 300, column.getWidth());
        assertEquals("height", 20, column.getHeight());
        assertEquals("line count", 1, column.getRowCount());
        column.append(new MockInlineElement(30, 25));
        column.apply();
        assertEquals("width", 300, column.getWidth());
        assertEquals("height", 25, column.getHeight());
        assertEquals("line count", 1, column.getRowCount());
        column.append(new MockInlineElement(30, 50));
        column.apply();
        assertEquals("width", 300, column.getWidth());
        assertEquals("height", 75, column.getHeight());
        assertEquals("line count", 2, column.getRowCount());
    }

    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ColumnTest("testSimpleColumn", new TestMethod() {
            public void run(TestCase tc) { ((ColumnTest)tc).testSimpleColumn(); }
        }));
        return suite;
    }
    
    class MockInlineElement extends InlineElement {
        
        MockInlineElement(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
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
        public boolean action(FormContext view, int action, int selector) {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
