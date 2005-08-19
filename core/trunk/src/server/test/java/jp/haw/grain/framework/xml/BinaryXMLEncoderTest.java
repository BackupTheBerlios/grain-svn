/*
 * $Id: BinaryXMLEncoderTest.java 3228 2005-06-26 04:28:14Z go $
 * 
 * Created on 2005/05/06
 *
 */

package jp.haw.grain.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import junit.framework.TestCase;

/**
 * @author go
 *
 */
public class BinaryXMLEncoderTest extends TestCase {
//	/**
//	 * スペースおよび改行およびタブのみで構成されたテキストノードが
//	 * 取り除かれているか
//	 */
//	public void testTrimFullWhiteTextNode() {
//		try {
//            BinaryXMLEncoder bxe = new BinaryXMLEncoder(SRC);
//			String encoded = bxe.encode();
//			//テストデータのチェック用
//			System.out.println("src = \n" + SRC + "[EOF]" + SRC.length()); 
//			//System.out.println("dst = \n" + dst + "[EOF]" + dst.length());
//			//System.out.println("encoded = \n" + encoded + "[EOF]" + encoded.length());
//			
//			assertEquals(DST, encoded);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			fail(e.toString());
//		}
//	}
    
    public void testJAXP() throws ParseException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(new InputSource(new StringReader("<head><br/></head>")), new DefaultHandler());
        assertEquals(0x08, ((byte)0x80 & 0xF0) >>> 4);
        assertEquals((byte)0x00, (byte)0x80 & (byte)0x0F);
        assertEquals("7f", toHex((byte)0x7F));
        assertEquals("80", toHex((byte)0x80));
        assertEquals(3, "あ".getBytes("UTF-8").length);
        byte[] b = "あ".getBytes("UTF-8");
        int i = ((b[0] & 0x0F) << 12) | ((b[1] & 0x3F) << 6) | (b[2] & 0x3F);
        assertEquals("T", 'あ', i);
        assertEquals(1, String.valueOf((char)0x05).getBytes("UTF-8").length);
        assertEquals(3, String.valueOf((char)0xFF05).getBytes("UTF-8").length);
        assertEquals(76, TEST_BIN_STREAM.length);
        assertEquals(118, TEST_TEXT.getBytes("Shift_JIS").length);
    }    
    
    public void testWriter() throws ParseException, IOException {
        BinaryXMLOutputter bxo = new BinaryXMLOutputter(new StringReader(TEST_TEXT));
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        bxo.writeTo(encoded);

        assertEquals("incollect binary data", toHex(TEST_BIN_STREAM), toHex(encoded.toByteArray()));
    }    
    
    public void testOutputStream() throws ParseException, IOException {
        BinaryXMLOutputter bxo = new BinaryXMLOutputter(new ByteArrayInputStream(TEST_TEXT.getBytes("UTF-8")));
        bxo.setTextEncoding("UTF-8");
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        bxo.writeTo(encoded);
        
        assertEquals("incollect binary data", toHex(TEST_BIN_STREAM), toHex(encoded.toByteArray()));
    }  
    
    public void testNS() throws ParseException, IOException {
        assertEquals("test text ns size:", 297, TEST_TEXT_NS.length());
        assertEquals("test bin  ns size", 173, TEST_BIN_STREAM_NS.length);

        BinaryXMLOutputter bxo = new BinaryXMLOutputter(new StringReader(TEST_TEXT_NS));
        ByteArrayOutputStream encoded = new ByteArrayOutputStream();
        bxo.writeTo(encoded);
        
//        System.out.println("***excepted:");
//        System.out.println(toHex(TEST_BIN_STREAM_NS));
//        System.out.println("***result:");
//        System.out.println(toHex(encoded.toByteArray()));
        
        assertEquals("incollect binary data", toHex(TEST_BIN_STREAM_NS), toHex(encoded.toByteArray()));
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
        +"<haw xmlns=\"http://haw.co.jp/haw\" xmlns:xforms=\"http://www.w3.org/2002/xforms\" type=\"now\" xforms:unit=\"yen\">\n"
        +"\t<xforms:output ref=\"test\">abc</xforms:output>\n"
        +"\t<xforms:output ref=\"x\"/>\n"
        +"\t<xforms:model xmlns=\"\">\n"
        +"\t\t<scf>"
        +"\t\t\t<d>test</d>"
        +"\t\t</scf>"
        +"\t</xforms:model>"
        +"</haw>";
    
}
