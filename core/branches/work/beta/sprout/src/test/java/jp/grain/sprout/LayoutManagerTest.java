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
package jp.haw.grain.sprout;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;
import jp.haw.grain.doja.FontImpl;
import jp.haw.grain.sprout.Block;
import jp.haw.grain.xforms.FormControlElement;
import jp.haw.grain.xforms.FormDocument;
import jp.haw.grain.xforms.XHTMLElement;

import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Text;

/**
 * A test of LayoutManager.
 * @version $Id$
 * @author Go Takahashi
 */
public class LayoutManagerTest extends TestCase {

    public LayoutManagerTest() {
    }

    public LayoutManagerTest(String name, TestMethod method) {
        super(name, method);
    }

    protected void setUp() {
        Font.setDefaultFont(new FontImpl(com.nttdocomo.ui.Font.getDefaultFont()));
    }
    
    public void testSimpleLayout() {
        FormDocument doc = new FormDocument("form");
        Element html = new XHTMLElement("html");
        doc.setDocumentElement(html);
        Element head = new XHTMLElement("head");
        html.appendChild(head);
        Element title = new XHTMLElement("title");
        head.appendChild(title);
        title.appendChild(new Text("title"));
        XHTMLElement body = new XHTMLElement("body");
        html.appendChild(body);
        XHTMLElement p = new XHTMLElement("p");
        p.appendChild(new Text("test string!!"));
        p.setStyle("line-height", "15px");
        body.appendChild(p);
        FormControlElement trigger1 = new FormControlElement("trigger");
        trigger1.setStyle("width", "200px");
        trigger1.setStyle("height", "20px");
        body.appendChild(trigger1);
        FormControlElement trigger2 = new FormControlElement("trigger");
        trigger2.setStyle("width", "200px");
        trigger2.setStyle("height", "20px");
        body.appendChild(trigger2);
        FormControlElement trigger3 = new FormControlElement("trigger");
        trigger3.setStyle("width", "200px");
        trigger3.setStyle("height", "20px");
        body.appendChild(trigger3);
        MockView view = new MockView();
        LayoutManager manager = new LayoutManager(doc, view);
        manager.layout();
        Block bodyBlock = view.getRootBlock();
        assertEquals("body block width by pixel", view.getWidth(), bodyBlock.getWidth());
        assertEquals("body count", 2, bodyBlock.getChildCount());        
        assertEquals("body height by pixel", 75, bodyBlock.getHeight());
    }
    
    public void testBR() {
        FormDocument doc = new FormDocument("form");
        Element html = new XHTMLElement("html");
        doc.setDocumentElement(html);
        Element head = new XHTMLElement("head");
        html.appendChild(head);
        Element title = new XHTMLElement("title");
        head.appendChild(title);
        title.appendChild(new Text("title"));
        XHTMLElement body = new XHTMLElement("body");
        html.appendChild(body);
        XHTMLElement p = new XHTMLElement("p");
        p.appendChild(new Text("test string!!"));
        p.appendChild(new XHTMLElement("br"));
        p.appendChild(new Text("VSCL 2.0 Bar Code Capture API は、BarcodeControl インタフェースによって定義されています。この API は、MMAPI (JSR-135) のメディアフレームワークを使用します。"));
        p.appendChild(new XHTMLElement("br"));
        p.appendChild(new Text("end."));        
        p.setStyle("line-height", "15px");
        body.appendChild(p);
        MockView view = new MockView();
        LayoutManager manager = new LayoutManager(doc, view);
        manager.layout();
        Block bodyBlock = view.getRootBlock();
        assertEquals("body block width by pixel", view.getWidth(), bodyBlock.getWidth());
        assertEquals("body count", 1, bodyBlock.getChildCount());
        Column column = (Column)bodyBlock.getChildBox(0);
        assertEquals("line count", 6, column.getRowCount());
    }
    
    public void testInclusion() {
        FormDocument doc = new FormDocument("form");
        Element html = new XHTMLElement("html");
        doc.setDocumentElement(html);
        Element head = new XHTMLElement("head");
        html.appendChild(head);
        Element title = new XHTMLElement("title");
        head.appendChild(title);
        title.appendChild(new Text("title"));
        XHTMLElement body = new XHTMLElement("body");
        html.appendChild(body);
        XHTMLElement p = new XHTMLElement("p");
        p.appendChild(new Text("test string!!"));
        p.setStyle("line-height", "15px");
        body.appendChild(p);
        FormControlElement trigger1 = new FormControlElement("trigger");
        trigger1.setStyle("width", "200px");
        trigger1.setStyle("height", "250px");
        body.appendChild(trigger1);
        FormControlElement trigger2 = new FormControlElement("trigger");
        trigger2.setStyle("width", "200px");
        trigger2.setStyle("height", "250px");
        body.appendChild(trigger2);
        FormControlElement trigger3 = new FormControlElement("trigger");
        trigger3.setStyle("width", "200px");
        trigger3.setStyle("height", "250px");
        body.appendChild(trigger3);
        MockView view = new MockView();
        LayoutManager manager = new LayoutManager(doc, view);
        manager.layout();
        Block bodyBlock = view.getRootBlock();
        assertEquals("body count", 2, bodyBlock.getChildCount());
        Column fc = (Column)bodyBlock.getChildBox(1);
        assertEquals("line count", 3, fc.getRowCount());
        Row row = fc.getRow(1);
        assertEquals("inline elem count", 1, row.getChildCount());
        InlineElement ie = row.getChildElement(0);
        //assertTrue("trigger1 ", ie.isIncludedIn(0, 0, view.getWidth(), view.getHeight());
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new LayoutManagerTest("testSimpleLayout", new TestMethod() {
            public void run(TestCase tc) { ((LayoutManagerTest)tc).testSimpleLayout(); }
        }));
        suite.addTest(new LayoutManagerTest("testBR", new TestMethod() {
            public void run(TestCase tc) { ((LayoutManagerTest)tc).testBR(); }
        }));
        return suite;
    }
    
}
