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

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Text;

/**
 * BinaryXMLParserTest
 * @version $Id: BinaryXMLSerializerTest.java 3279 2005-08-06 23:52:13Z go $
 * @author Go Takahashi
 */
public class BinaryXMLSerializerTest extends TestCase {
	
    public BinaryXMLSerializerTest() {
    }

    public BinaryXMLSerializerTest(String name, TestMethod method) {
        super(name, method);
    }
    
    protected void setUp() throws XmlPullParserException {
    }

    public void testCreateInstance() throws IOException{
    	Document doc = new Document();
        Element haw = new Element("haw");
        haw.setNamespace("","http://haw.co.jp/haw");
        haw.setNamespace("xforms","http://www.w3.org/2002/xforms");
        haw.setAttribute(null, "type", "now");
        haw.setAttribute("http://www.w3.org/2002/xforms", "unit", "yen");
        doc.setDocumentElement(haw);

        Element output=new Element("output");
        output.setPrefix("xforms");
        output.setAttribute(null,"ref","test");
        haw.appendChild(output);
        
        Text text1=new Text("abc");
        output.appendChild(text1);
               
        Element output2=new Element("output");
        output2.setPrefix("xforms");
        //output2.setNamespace("xfo","http://www.w2.org/2002/xforms");
        output2.setAttribute(null,"ref","x");
        haw.appendChild(output2);
            
        Element model=new Element("model");
        model.setPrefix("xforms");
        model.setNamespace("","");
        //model.setNamespace("xforms","http://www.w3.org/2002/xforms");
        haw.appendChild(model);
        
        Element scf=new Element("scf");
        model.appendChild(scf);
        
        Element d=new Element("d");
        scf.appendChild(d);
        
        Text text2=new Text("test");
        d.appendChild(text2);
        BinaryXMLSerializer serializer = new BinaryXMLSerializer(doc.getDocumentElement());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        serializer.serializeTo(os);
        assertNotNull("parser not null", serializer);
        System.out.println("expected:");        
        System.out.println(toHex(TEST_BIN_STREAM_NS));
        System.out.println("result:");
        System.out.println(toHex(os.toByteArray()));
        assertEquals("incollect binary data", toHex(TEST_BIN_STREAM_NS), toHex(os.toByteArray()));   
    }
    
    private String toHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; ++i) {
            buf.append(toHex(data[i]));
        }
        return buf.toString();
    }
    
    private String toHex(byte data) {
        char[] hex = new char[2];
        hex[0] = getHexChar((data & 0xF0) >>> 4);        
        hex[1] = getHexChar(data & 0x0F);
        return String.valueOf(hex);
    }
    
    private char getHexChar(int val) {
        if (val < 10) {
            return (char)('0' + val);
        } else {
            return (char)('a' + (val - 10));
        }
    }
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new BinaryXMLSerializerTest("testCreateInstance", new TestMethod() {
            public void run(TestCase tc) throws IOException{ ((BinaryXMLSerializerTest)tc).testCreateInstance(); }
        }));
        return suite;
    }
    
    public static final byte[] TEST_BIN_STREAM_NS = {
        0x77, 0x07, // データ部Length, 辞書部項目数
        '1', '.', '0', 0x00, 'S', 'J', 'I', 'S', 0x00,  //XMLバージョン、エンコーディング
        0x01, 'h', 'a', 'w', 0x00,                                  // 辞書インデックス, 項目....
        0x02, 't', 'y', 'p', 'e', 0x00,
        0x03, 'u', 'n', 'i', 't', 0x00,
        0x04, 'o', 'u', 't', 'p', 'u', 't', 0x00,
        0x05, 'r', 'e', 'f', 0x00,
        0x06, 'm', 'o', 'd', 'e', 'l', 0x00,
        0x07, 's', 'c', 'f', 0x00,
        
        
        // xmlns:xforms=\"http://www.w3.org/2002/xforms\"
        0x15, 0x01, 'x', 'f', 'o', 'r', 'm', 's', 0x00, 'h', 't', 't', 'p', ':', '/', '/', 'w', 'w', 'w', '.', 'w', '3', '.', 'o', 'r', 'g', '/', '2', '0', '0', '2', '/', 'x', 'f', 'o', 'r', 'm', 's', 0x00,
        
//      xmlns=\"http://haw.co.jp/haw\"     
        0x05, 'h', 't', 't', 'p', ':', '/', '/', 'h', 'a', 'w', '.', 'c', 'o', '.', 'j', 'p', '/', 'h', 'a', 'w' , 0x00,
        
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