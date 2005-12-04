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
 * @version $Id$ 
 * @author Go Takahashi
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
