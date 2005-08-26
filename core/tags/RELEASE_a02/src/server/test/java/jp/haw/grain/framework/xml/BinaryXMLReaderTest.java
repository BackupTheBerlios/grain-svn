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
 * Created on 2005/08/24
 *
 */

package jp.haw.grain.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * A test of BinaryXMLReader
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class BinaryXMLReaderTest extends TestCase {
    
    public void testRead() throws ParseException, IOException {
        XMLOutputter out = new XMLOutputter(new ByteArrayInputStream(TEST_BIN_STREAM_NS), "UTF-8");
        BinaryXMLReader reader = new BinaryXMLReader(out);
        StringBuffer buf = new StringBuffer();
        for (;;) {
            int i = reader.read();
            if (i < 0) break;
            buf.append((char)i);
        }
        System.out.println(TEST_TEXT_NS);
        System.out.println(buf.toString());
        assertEquals("incollect xml data", TEST_TEXT_NS, buf.toString());
    }    
    
    
	public static final String SRC = 
		"<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>\n"
		+"<gml xmlns=\"http://www.haw.co.jp/ns/gml\" xmlns:xforms=\"http://www.w3.org/2002/xforms\" xmlns:my=\"http://www.haw.co.jp/ns/my\">\n"
		+"\t<head>\n"
		+"\t\t<title>gml layout test</title>\n"
		+"\t\t<xforms:model>\n"
		+"\t\t\t<xforms:instance >\n"
		+"\t\t\t\t<my:payment as=\"credit\">\n"
		+"\t\t\t\t\t<my:card-code-input/>\n"
		+"\t\t\t\t\t<my:card-code/>\n"
		+"\t\t\t\t\t<my:exp-year />\n"
		+"\t\t\t\t\t<my:exp-month />\n"
		+"\t\t\t\t\t<my:name/>\n"
		+"\t\t\t\t</my:payment>\n"
		+"\t\t\t</xforms:instance>\n"
		+"\t\t\t<xforms:submission action=\"index2.xml\" method=\"post\" id=\"s00\" />\n"
		+"\t\t\t<xforms:submission action=\"getcard.jsp\" ref=\"card-code-input\" replace=\"instance\" method=\"post\" id=\"getcard\"/>\n"
		+"\t\t\t<xforms:bind nodeset=\"card-code\" relevant=\"../@as='credit'\" required=\"true()\" />\n"
		+"\t\t\t<xforms:bind nodeset=\"exp-date\" relevant=\"../@as='credit'\" required=\"true()\" />\n"
		+"\t\t</xforms:model>\n"
		+"\t</head>\n"
		+"\t<body>\n"
		+"\t\tようこそ<br/>\n"
		+"\t\t<xforms:input ref=\"card-code-input\">\n"
		+"\t\t\t<xforms:label>カード番号</xforms:label>\n"
		+"\t\t</xforms:input>\n"
		+"\t\t<xforms:submit submission=\"getcard\">\n"
		+"\t\t\t<xforms:label>update</xforms:label>\n"
		+"\t\t</xforms:submit><br/>\n"
		+"\t\t<xforms:output ref=\"card-code\">\n"
		+"\t\t\t<xforms:label>card-code : </xforms:label>\n"
		+"\t\t</xforms:output><br/>\n"
		+"\t\t<xforms:output ref=\"exp-year\">\n"
		+"\t\t\t<xforms:label>exp-year : </xforms:label>\n"
		+"\t\t</xforms:output><br/>\n"
		+"\t\t<xforms:output ref=\"exp-month\">\n"
		+"\t\t\t<xforms:label>exp-month : </xforms:label>\n"
		+"\t\t</xforms:output><br/>\n"
		+"\t\t<xforms:output ref=\"name\">\n"
		+"\t\t\t<xforms:label>name : </xforms:label>\n"
		+"\t\t</xforms:output><br/>\n"
		+"\t\t<xforms:submit submission=\"s00\">\n"
		+"\t\t\t<xforms:label>submit</xforms:label>\n"
		+"\t\t</xforms:submit>\n"
		+"\t\t</body>\n"
		+"</gml>";

	public static final String DST =
		"<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>\n"
		+"<gml xmlns=\"http://www.haw.co.jp/ns/gml\" xmlns:xforms=\"http://www.w3.org/2002/xforms\" xmlns:my=\"http://www.haw.co.jp/ns/my\"><head><title>gml layout test</title><xforms:model><xforms:instance><my:payment as=\"credit\"><my:card-code-input /><my:card-code /><my:exp-year /><my:exp-month /><my:name /></my:payment></xforms:instance><xforms:submission action=\"index2.xml\" method=\"post\" id=\"s00\" /><xforms:submission action=\"getcard.jsp\" ref=\"card-code-input\" replace=\"instance\" method=\"post\" id=\"getcard\" /><xforms:bind nodeset=\"card-code\" relevant=\"../@as='credit'\" required=\"true()\" /><xforms:bind nodeset=\"exp-date\" relevant=\"../@as='credit'\" required=\"true()\" /></xforms:model></head><body>\n"
		+"\t\tようこそ<br /><xforms:input ref=\"card-code-input\"><xforms:label>カード番号</xforms:label></xforms:input><xforms:submit submission=\"getcard\"><xforms:label>update</xforms:label></xforms:submit><br /><xforms:output ref=\"card-code\"><xforms:label>card-code : </xforms:label></xforms:output><br /><xforms:output ref=\"exp-year\"><xforms:label>exp-year : </xforms:label></xforms:output><br /><xforms:output ref=\"exp-month\"><xforms:label>exp-month : </xforms:label></xforms:output><br /><xforms:output ref=\"name\"><xforms:label>name : </xforms:label></xforms:output><br /><xforms:submit submission=\"s00\"><xforms:label>submit</xforms:label></xforms:submit></body></gml>\n";	
	
    public static final byte[] TEST_BIN_STREAM = {
        0x24, 0x05, // データ部Length, 辞書部項目数
        '1', '.', '0', 0x00, 'U', 'T', 'F', '-', '8', 0x00,  //XMLバージョン、エンコーディング
        0x01, 'h', 'a', 'w', 0x00,                                  // 辞書インデックス, 項目....
        0x02, 't', 'y', 'p', 'e', 0x00,
        0x03, 'u', 'n', 'i', 't', 0x00,
        0x04, 's', 'c', 'f', 0x00,
        0x05, 'f', 'i', 'l', 'e', 0x00,
        0x01, 0x01,                                                 // <haw         2
        0x03, 0x02, 'n', 'o', 'w', 0x00,                            // type="now"   10
        0x03, 0x03, 'y', 'e', 'n', 0x00,                            // unit="yen"   10
        0x01, 0x04,                                                 // ><scf        2
        0x03, 0x05, 't', 'e', 's', 't', 0x00,                       // file="test"  11
        0x04, 'a', 'b', 'c', 0x00, 0x09,                            // >abc</scf>   6
        0x02, 0x04,                                                 // <scf         2
        0x03, 0x05, 'x', 0x00,                                      // file="x"     7
        0x09                                                        // /></haw>     1
    };
    
    public static final String TEST_TEXT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        +"<haw type=\"now\" unit=\"yen\">\n"
        +"\t<scf file=\"test\">abc</scf>\n"
        +"\t<scf file=\"x\"/>\n"
        +"</haw>";
    
    public static final byte[] TEST_BIN_STREAM_NS = {
        0x77, 0x07, // データ部Length, 辞書部項目数
        '1', '.', '0', 0x00, 'U', 'T', 'F', '-', '8', 0x00,  //XMLバージョン、エンコーディング
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
        0x23, 0x01, 0x03, 'y', 'e', 'n', 0x00,                      // xforms:unit="yen"   10
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
    
    public static final String TEST_TEXT_NS =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        +"<haw xmlns=\"http://haw.co.jp/haw\" xmlns:xforms=\"http://www.w3.org/2002/xforms\" type=\"now\" xforms:unit=\"yen\">"
        +"<xforms:output ref=\"test\">abc</xforms:output>"
        +"<xforms:output ref=\"x\"/>"
        +"<xforms:model xmlns=\"\">"
        +"<scf>"
        +"<d>test</d>"
        +"</scf>"
        +"</xforms:model>"
        +"</haw>";
    
}
