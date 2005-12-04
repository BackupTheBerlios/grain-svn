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
 * Created on 2005/06/25 14:46:01
 * 
 */
package jp.haw.grain.xforms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.hp.hpl.sparta.Element;

import jp.haw.grain.sprout.xml.BinaryXMLParser;
import jp.haw.grain.sprout.xml.BinaryXMLParserTest;
import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

/**
 * A test of FormDocumentBuilder.
 * @version  $Id$
 * @author Go Takahashi
 */
public class FormDocumentBuilderTest extends TestCase {

    public FormDocumentBuilderTest() {
    }

    public FormDocumentBuilderTest(String name, TestMethod method) {
        super(name, method);
    }
    
    public void testBuildDocument() {
        try {
            XmlPullParser parser = BinaryXMLParser.newInstance();
            parser.setFeature(BinaryXMLParser.DETECT_ENCODING, true);
            parser.setInput(new ByteArrayInputStream(BinaryXMLParserTest.TEST_BIN_STREAM_NS), null);
            FormDocumentBuilder builder = new FormDocumentBuilder(parser, "http://test.haw.co.jp/");
            builder.build();
            FormDocument doc = builder.getDocument();
            assertNotNull("document is not null", doc);
            Element root = doc.getDocumentElement();
            assertNotNull("has root element", root);
            assertEquals("root element name", "haw", root.getTagName());
            assertEquals("root element ns", "http://haw.co.jp/haw", root.gsNamespaceUri(null));
            assertEquals("root element prefix", "", root.findNamespacePrefix("http://haw.co.jp/haw"));
            Enumeration e = root.getAttributes();
            assertTrue(e.hasMoreElements());
            String[] attr = null;
            attr = (String[])e.nextElement();
            assertEquals("attr1 ns", "", attr[0]);
            assertEquals("attr1 name", "type", attr[1]);
            assertEquals("attr1 value", "now", attr[2]);
            assertTrue(e.hasMoreElements());
            attr = (String[])e.nextElement();
            assertEquals("attr2 ns", "http://www.w3.org/2002/xforms", attr[0]);
            assertEquals("attr2 name", "unit", attr[1]);
            assertEquals("attr2 value", "yen", attr[2]);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new FormDocumentBuilderTest("testBuildDocument", new TestMethod() {
            public void run(TestCase tc) { ((FormDocumentBuilderTest)tc).testBuildDocument(); }
        }));
        return suite;
    }    
    
}
