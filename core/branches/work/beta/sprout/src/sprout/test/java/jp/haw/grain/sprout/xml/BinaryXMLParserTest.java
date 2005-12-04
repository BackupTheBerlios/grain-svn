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
 * Created on 2005/06/16 14:11:18
 * 
 */
package jp.haw.grain.sprout.xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import jp.haw.grain.sprout.xml.BinaryXMLParser;
import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

/**
 * BinaryXMLParserTest
 * @version $Id: BinaryXMLParserTest.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class BinaryXMLParserTest extends TestCase {

    private BinaryXMLParser parser;

    public BinaryXMLParserTest() {
    }

    public BinaryXMLParserTest(String name, TestMethod method) {
        super(name, method);
    }
    
    protected void setUp() throws XmlPullParserException {
        parser = BinaryXMLParser.newInstance();
        parser.setFeature(BinaryXMLParser.DETECT_ENCODING, true);
    }

    public void testCreateInstance() {
        assertNotNull("parser not null", parser);
        assertEquals("get BinaryXMLParser", "jp.haw.grain.sprout.xml.BinaryXMLParser", parser.getClass().getName());
    }
   
    public void testReadInteger() {
        try {
            ByteArrayInputStream is;
            
            byte[] data1 = { 0x7F };
            is = new ByteArrayInputStream(data1);
            parser.setInput(is, null);
            assertEquals("no1", 0x7F, parser.readInteger());
            
            byte[] data2 = { (byte)0xC2, (byte)0x80 };
            is = new ByteArrayInputStream(data2);
            parser.setInput(is, null);
            assertEquals("no2", 0x80, parser.readInteger());

            byte[] data3 = { (byte)0xEF, (byte)0xBF, (byte)0xBF };
            is = new ByteArrayInputStream(data3);
            parser.setInput(is, null);
            assertEquals("no3", 0xFFFF, parser.readInteger());

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testStartDocument() {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(TEST_BIN_STREAM_NS);
            parser.setInput(is, null);
            assertEquals("event type", XmlPullParser.START_DOCUMENT, parser.getEventType());
            assertEquals("start tag", XmlPullParser.START_TAG, parser.next());
            assertEquals("xml version", "1.0", parser.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-version"));
            assertEquals("char encoding", "SJIS", parser.getInputEncoding());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    public void testParseDocument() {
        try {
            this.parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            ByteArrayInputStream is = new ByteArrayInputStream(TEST_BIN_STREAM_NS);
            parser.setInput(is, null);
            assertEquals("document", XmlPullParser.START_DOCUMENT, parser.getEventType());
            assertEquals("1st level element", XmlPullParser.START_TAG, parser.nextTag());
            assertEquals("depth", 1, parser.getDepth());
            assertEquals("namespace uri", "http://haw.co.jp/haw", parser.getNamespace());
            assertNull("namespace prefix", parser.getPrefix());            
            assertEquals("name", "haw", parser.getName());
            assertEquals("attribute count", 2, parser.getAttributeCount());
            assertEquals("attribute[0] name", "type", parser.getAttributeName(0));
            assertEquals("attribute[0] value", "now", parser.getAttributeValue(0));
            assertEquals("attribute[0] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(0));
            assertNull("attribute[0] namespace prefix", parser.getAttributePrefix(0));
            assertTrue("not empty element", !parser.isEmptyElementTag());

            assertEquals("namespace count", 2, parser.getNamespaceCount(parser.getDepth()));
            assertEquals("namespace[0] prefix", null, parser.getNamespacePrefix(0));
            assertEquals("namespace[1] ptrfix", "xforms", parser.getNamespacePrefix(1));
            assertEquals("namespace[0] uri", "http://haw.co.jp/haw", parser.getNamespaceUri(0));
            assertEquals("namespace[1] uri", "http://www.w3.org/2002/xforms", parser.getNamespaceUri(1));
            assertEquals("namespace[0] for prefix=null", "http://haw.co.jp/haw", parser.getNamespace(null));
            assertEquals("namespace[1] for prefix='xforms'", "http://www.w3.org/2002/xforms", parser.getNamespace("xforms"));                   
            
            assertEquals("2nd level element start", XmlPullParser.START_TAG, parser.nextTag());
            assertEquals("depth", 2, parser.getDepth());
            assertEquals("namespace uri", "http://www.w3.org/2002/xforms", parser.getNamespace());
            assertEquals("namespace prefix", "xforms", parser.getPrefix());            
            assertEquals("name", "output", parser.getName());
            assertEquals("attribute count", 1, parser.getAttributeCount());
            assertEquals("attribute[0] name", "ref", parser.getAttributeName(0));
            assertEquals("attribute[0] value", "test", parser.getAttributeValue(0));
            assertEquals("attribute[0] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(0));
            assertNull("attribute[0] namespace prefix", parser.getAttributePrefix(0));
            assertTrue("not empty element", !parser.isEmptyElementTag());

            assertEquals("text element", "abc", parser.nextText());

            assertEquals("element end", XmlPullParser.END_TAG, parser.getEventType());
            assertEquals("name", "output", parser.getName());            
            try {
                parser.isEmptyElementTag();
                fail("isEmptyElementTag() not thrown Exception on END_TAG state");
            } catch (XmlPullParserException e) {
            }

            assertEquals("empty element", XmlPullParser.START_TAG, parser.next());
            assertEquals("name", "output", parser.getName());
            assertTrue("empty element", parser.isEmptyElementTag());
            assertEquals("empty element end", XmlPullParser.END_TAG, parser.next());
            
            assertEquals("element start", XmlPullParser.START_TAG, parser.next());
            assertEquals("name", "model", parser.getName());
            assertEquals("namespace uri", "http://www.w3.org/2002/xforms", parser.getNamespace());
            assertEquals("namespace prefix", "xforms", parser.getPrefix());            
            
            assertEquals("3rd level element start", XmlPullParser.START_TAG, parser.next());
            assertEquals("depth", 3, parser.getDepth());
            assertEquals("name", "scf", parser.getName());
            assertEquals("namespace uri", "", parser.getNamespace());
            assertNull("namespace prefix", parser.getPrefix());            
            assertEquals("namespace count", 3, parser.getNamespaceCount(parser.getDepth()));
            
            assertEquals("4th level element", XmlPullParser.START_TAG, parser.next());
            assertEquals("depth", 4, parser.getDepth());
            assertEquals("name", "d", parser.getName());
            assertEquals("namespace uri", "", parser.getNamespace());
            assertNull("namespace prefix", parser.getPrefix());
            
            assertEquals("namespace count", 3, parser.getNamespaceCount(parser.getDepth()));


            try {
                parser.nextTag();
                fail("nextTag() not thrown Exception on TEXT state");
            } catch (XmlPullParserException e) {
            }
            assertEquals("text element", XmlPullParser.TEXT, parser.getEventType());
            assertEquals("text", "test", parser.getText());
            
            assertEquals("element end", XmlPullParser.END_TAG, parser.nextTag());
            assertEquals("name", "d", parser.getName());

            assertEquals("element end", XmlPullParser.END_TAG, parser.nextTag());
            assertEquals("name", "scf", parser.getName());

            assertEquals("element end", XmlPullParser.END_TAG, parser.nextTag());
            assertEquals("name", "model", parser.getName());

            assertEquals("element end", XmlPullParser.END_TAG, parser.nextTag());
            assertEquals("name", "haw", parser.getName());
            
            
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    public void testNamespaceFeatureIsFalse() {
        try {
            assertTrue("namespace feature is true" , this.parser.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES) == true);
            ByteArrayInputStream is = new ByteArrayInputStream(TEST_BIN_STREAM_NS);
            parser.setInput(is, null);
            assertEquals("start document", XmlPullParser.START_DOCUMENT, parser.getEventType());
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            assertTrue("namespace feature is false" , this.parser.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES) == false);
            try {
                parser.setFeature(null, true);
                fail("name is null...");
            } catch (IllegalArgumentException e) {
            }
            assertEquals("1st level element", XmlPullParser.START_TAG, parser.nextTag());

            try {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                fail("feature changes on start_tag state");
            } catch (XmlPullParserException e) {
            }
            assertTrue("namespace feature is still false" , this.parser.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES) == false);
            
            assertEquals("depth", 1, parser.getDepth());
            assertEquals("namespace uri", XmlPullParser.NO_NAMESPACE, parser.getNamespace());
            assertNull("namespace prefix", parser.getPrefix());        
            assertEquals("name", "haw", parser.getName());
            assertEquals("attribute count", 4, parser.getAttributeCount());  

            assertEquals("namespace count", 0, parser.getNamespaceCount(parser.getDepth()));
            try {
                parser.getNamespacePrefix(0);
                fail("getNamespacePrefix not throws exception");
            } catch (IndexOutOfBoundsException e) {                
            }
            try {
                parser.getNamespaceUri(0);
                fail("getNamespaceUri not throws exception");
            } catch (IndexOutOfBoundsException e) {                
            }
            try {
                parser.getNamespace(null);
                fail("getNamespace not throws exception");
            } catch (IndexOutOfBoundsException e) {                
            }
            
            assertEquals("attribute[0] name", "xmlns", parser.getAttributeName(0));
            assertEquals("attribute[0] value", "http://haw.co.jp/haw", parser.getAttributeValue(0));
            assertEquals("attribute[0] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(0));
            assertNull("attribute[0] namespace prefix", parser.getAttributePrefix(0));

            assertEquals("attribute[1] name", "xmlns:xforms", parser.getAttributeName(1));
            assertEquals("attribute[1] value", "http://www.w3.org/2002/xforms", parser.getAttributeValue(1));
            assertEquals("attribute[1] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(1));
            assertNull("attribute[1] namespace prefix", parser.getAttributePrefix(1));

            assertEquals("attribute[2] name", "type", parser.getAttributeName(2));
            assertEquals("attribute[2] value", "now", parser.getAttributeValue(2));
            assertEquals("attribute[2] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(2));
            assertNull("attribute[2] namespace prefix", parser.getAttributePrefix(2));

            assertEquals("attribute[3] name", "xforms:unit", parser.getAttributeName(3));
            assertEquals("attribute[3] value", "yen", parser.getAttributeValue(3));
            assertEquals("attribute[3] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(3));
            assertNull("attribute[3] namespace prefix", parser.getAttributePrefix(3));

            assertEquals("2nd level element start", XmlPullParser.START_TAG, parser.nextTag());
            assertEquals("namespace uri", XmlPullParser.NO_NAMESPACE, parser.getNamespace());
            assertNull("namespace prefix", parser.getPrefix());            
            assertEquals("name", "xforms:output", parser.getName());
            assertEquals("attribute count", 1, parser.getAttributeCount());
            assertEquals("attribute[0] name", "ref", parser.getAttributeName(0));
            assertEquals("attribute[0] value", "test", parser.getAttributeValue(0));
            assertEquals("attribute[0] namespace uri", XmlPullParser.NO_NAMESPACE, parser.getAttributeNamespace(0));
            assertNull("attribute[0] namespace prefix", parser.getAttributePrefix(0));
            
            
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new BinaryXMLParserTest("testCreateInstance", new TestMethod() {
            public void run(TestCase tc) { ((BinaryXMLParserTest)tc).testCreateInstance(); }
        }));
        suite.addTest(new BinaryXMLParserTest("testReadInteger", new TestMethod() {
            public void run(TestCase tc) { ((BinaryXMLParserTest)tc).testReadInteger(); }
        }));
        suite.addTest(new BinaryXMLParserTest("testStartDocument", new TestMethod() {
            public void run(TestCase tc) { ((BinaryXMLParserTest)tc).testStartDocument(); }
        }));
        suite.addTest(new BinaryXMLParserTest("testParseDocument", new TestMethod() {
            public void run(TestCase tc) { ((BinaryXMLParserTest)tc).testParseDocument(); }
        }));
        suite.addTest(new BinaryXMLParserTest("testNamespaceFeatureIsFalse", new TestMethod() {
            public void run(TestCase tc) { ((BinaryXMLParserTest)tc).testNamespaceFeatureIsFalse(); }
        }));
        return suite;
    }
    
    public static final byte[] TEST_BIN_STREAM_NS = {
        0x76, 0x07, // データ部Length, 辞書部項目数
        '1', '.', '0', 0x00, 'S', 'J', 'I', 'S', 0x00,  //XMLバージョン、エンコーディング
        0x01, 'h', 'a', 'w', 0x00,                                  // 辞書インデックス, 項目....
        0x02, 't', 'y', 'p', 'e', 0x00,
        0x03, 'u', 'n', 'i', 't', 0x00,
        0x04, 'o', 'u', 't', 'p', 'u', 't', 0x00,
        0x05, 'r', 'e', 'f', 0x00,
        0x06, 'm', 'o', 'd', 'e', 'l', 0x00,
        0x07, 's', 'c', 'f', 0x00,
        
        // xmlns=\"http://haw.co.jp/haw\"     
        0x05, 'h', 't', 't', 'p', ':', '/', '/', 'h', 'a', 'w', '.', 'c', 'o', '.', 'j', 'p', '/', 'h', 'a', 'w' , 0x00,
        
        // xmlns:xforms=\"http://www.w3.org/2002/xforms\"
        0x15, 0x01, 'x', 'f', 'o', 'r', 'm', 's', 0x00, 'h', 't', 't', 'p', ':', '/', '/', 'w', 'w', 'w', '.', 'w', '3', '.', 'o', 'r', 'g', '/', '2', '0', '0', '2', '/', 'x', 'f', 'o', 'r', 'm', 's', 0x00,
        
        0x01, 0x01,                                                 // <haw         2
        0x03, 0x02, 'n', 'o', 'w', 0x00,                            // type="now"   10
        0x23, 0x01, 0x03, 'y', 'e', 'n', 0x00,                            // xforms:unit="yen"   10
        0x21, 0x01, 0x04,                                           // ><xforms:output        2
        0x03, 0x05, 't', 'e', 's', 't', 0x00,                       // ref="test"  11
        0x04, 'a', 'b', 'c', 0x00, 0x09,                            // >abc</scf>   6
        0x22, 0x01, 0x04,                                           // <xforms:output         2
        0x03, 0x05, 'x', 0x00,                                      // ref="x"

        // xmlns=\"\"
        0x05, 0x00,
        
        0x21, 0x01, 0x06,                                           // /><xforms:model
        0x01, 0x07,                                                 // ><scf
        0x11, 'd', 0x00, 0x04, 't', 'e', 's', 't', 0x00, 0x09,                   // ><d>test</d>
        0x09,                                                       // </scf>
        0x09,                                                       // </xforms:model>
        0x09                                                        // </haw>     1
    };
}