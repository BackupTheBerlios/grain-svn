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
import jp.grain.sprout.ui.Block;
import jp.grain.xforms.XHTMLElement;

/**
 * A test of LayoutManager
 * @version $Id$
 * @author Go Takahashi
 */
public class BlockTest extends TestCase {

    public BlockTest() {
    }

    public BlockTest(String name, TestMethod method) {
        super(name, method);
    }

    public void testNonFixedBlock() {
        XHTMLElement body = new XHTMLElement("body");
        body.setStyle("width", "300px");
        Block outer = new Block(body);
        XHTMLElement div1 = new XHTMLElement("div");
        div1.setStyle("height", "100px");
        Block inner1 = new Block(div1);
        outer.addChildBox(inner1);
        outer.apply();
        assertEquals("outer width", 300, outer.getWidth());
        assertEquals("outer heght", 100, outer.getHeight());
        assertEquals("inner1 width", 300, inner1.getWidth());
        assertEquals("inner1 heght", 100, inner1.getHeight());
        XHTMLElement div2 = new XHTMLElement("div");
        div2.setStyle("height", "150px");
        Block inner2 = new Block(div2);
        outer.addChildBox(inner2);
        outer.apply();
        assertEquals("outer width", 300, outer.getWidth());
        assertEquals("outer heght", 250, outer.getHeight());
        assertEquals("inner2 width", 300, inner2.getWidth());
        assertEquals("inner2 height", 150, inner2.getHeight());
    }
    
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new BlockTest("testNonFixedBlock", new TestMethod() {
            public void run(TestCase tc) { ((BlockTest)tc).testNonFixedBlock(); }
        }));
        return suite;
    }    
}
