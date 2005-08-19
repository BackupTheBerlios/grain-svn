/*
 * $Id: BinaryXMLFilterWebTest.java 3228 2005-06-26 04:28:14Z go $
 * 
 * Created on 2005/05/17
 *
 */
package jp.haw.grain.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import jp.haw.grain.framework.xml.BinaryXMLEncoderTest;
import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * BinaryXMLFilter‚Ì‘a’ÊƒeƒXƒg
 * 
 * @author go
 */
public class BinaryXMLFilterWebTest extends TestCase {

    String baseUri;
    String webBasePath;
    
    public BinaryXMLFilterWebTest() {
        String host = System.getProperty("test.host", "localhost");
        String port = System.getProperty("test.port", "8080");
        String context = System.getProperty("test.context", "test");
        this.webBasePath = System.getProperty("test.webapp.base", ".");        
        this.baseUri = "http://" + host + ":" + port + "/" + context;
    }
    
    public void testGetBinaryXMLFile() throws MalformedURLException, IOException, SAXException {
        WebConversation wc = new WebConversation();
        wc.setHeaderField("Content-Type", "application/gbxml");
        WebRequest req = new GetMethodWebRequest(this.baseUri + "/test/BinaryXMLFilterTest.jsp" );
        WebResponse res = wc.getResponse(req);
        InputStream ris = res.getInputStream();
        for (int i = 0; i < BinaryXMLEncoderTest.TEST_BIN_STREAM_NS.length; ++i) {
            assertEquals("pos: " + i, BinaryXMLEncoderTest.TEST_BIN_STREAM_NS[i], (byte)ris.read());
        }
    }
    
    public void testFromDefaultServletFile() throws MalformedURLException, IOException, SAXException {
        WebConversation wc = new WebConversation();
        wc.setHeaderField("Content-Type", "application/gbxml");
        WebRequest req = new GetMethodWebRequest(this.baseUri + "/test/index.xml" );
        WebResponse res = wc.getResponse(req);
        assertEquals("application/gbxml", res.getContentType());
    }
}
