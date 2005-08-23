/*
 * $Id: BinaryXMLFilterTest.java 3228 2005-06-26 04:28:14Z go $
 * 
 * Created on 2005/05/09
 *
 */
package jp.haw.grain.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jp.haw.grain.framework.xml.BinaryXMLEncoderTest;
import jp.haw.grain.framework.xml.ParseException;

import org.apache.cactus.FilterTestCase;
import org.apache.cactus.WebRequest;
import org.apache.log4j.Logger;

import com.meterware.httpunit.WebResponse;

/**
 * @author go
 *
 * BinaryXMLFilterのテストクラス
 */
public class BinaryXMLFilterTest extends FilterTestCase {
    
    public static final String APPLICATION_GBXML = "application/gbxml";
    private static final Logger log = Logger.getLogger(BinaryXMLFilterTest.class);
	/*
	 * リクエストヘッダのContentTypeがapplication/gbxmlの場合
     * フィルタは動作し、変換される。
	 */
	public void beginUserAgentIsGrain(WebRequest request) {
		request.setContentType(APPLICATION_GBXML);
	}

	public void testUserAgentIsGrain() throws ServletException, IOException {
	    BinaryXMLFilter filter = new BinaryXMLFilter();
	    filter.init(config);
	    FilterChain mockFilterChain = new WriterChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
	    filter.doFilter(request, response, mockFilterChain);
	}
	
	public void endUserAgentIsGrain(WebResponse response) throws IOException {
        assertEquals(APPLICATION_GBXML, response.getHeaderField("Content-Type"));
        InputStream is = response.getInputStream();
        for (int i = 0; i < BinaryXMLEncoderTest.TEST_BIN_STREAM_NS.length; ++i) {
            assertEquals(BinaryXMLEncoderTest.TEST_BIN_STREAM_NS[i], (byte)is.read());            
        }
	}
	
	/*
	 * リクエストヘッダのContentTypeがapplication/gbxmlでない場合
     * フィルタは動作しない。
	 */
	public void beginUserAgentIsNotGrain(WebRequest request) {
        request.setContentType("application/xml");
	}

	public void testUserAgentIsNotGrain() throws ServletException, IOException {
	    BinaryXMLFilter filter = new BinaryXMLFilter();
	    filter.init(config);
	    FilterChain mockFilterChain = new WriterChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
	    filter.doFilter(request, response, mockFilterChain);
	}
	
	public void endUserAgentIsNotGrain(WebResponse response) throws IOException {
        assertTrue(response.getHeaderField("Content-Type").matches("^application/xml;[ ]*charset=UTF-8$"));
        assertEquals(BinaryXMLEncoderTest.TEST_TEXT_NS, response.getText());
	}

	/*
	 * リクエストヘッダのContentTypeが設定されていない場合
     * フィルタは動作しない。
	 */

	public void testUserAgentIsNull() throws ServletException, IOException {
	    BinaryXMLFilter filter = new BinaryXMLFilter();
	    filter.init(config);
	    FilterChain mockFilterChain = new WriterChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
	    filter.doFilter(request, response, mockFilterChain);
	}
	
	public void endUserAgentIsNull(WebResponse response) throws IOException {
	    assertEquals(BinaryXMLEncoderTest.TEST_TEXT_NS, response.getText());
	}

    /*
     * リクエストヘッダのContentTypeはapplication/gbxmlだが、
     * レスポンスのContent-Typeがapplication/xmlで無い場合。
     * フィルタは動作するが、変換は行われない。
     */
    public void beginContentTypeNotXML(WebRequest request) {
        request.setContentType(APPLICATION_GBXML);
    }

    public void testContentTypeNotXML() throws ServletException, IOException {
        BinaryXMLFilter filter = new BinaryXMLFilter();
        filter.init(config);
        MockFilterChain chain = new WriterChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
        chain.setContentType("text/html; charset=UTF-8");
        filter.doFilter(request, response, chain);
    }
    
    public void endContentTypeNotXML(WebResponse response) throws IOException {
        assertTrue(response.getHeaderField("Content-Type").matches("^text/html;[ ]*charset=UTF-8$"));
        assertEquals(BinaryXMLEncoderTest.TEST_TEXT_NS, response.getText());
    }
    
    /*
     * OutputStream経由で出力される場合。
     */
    public void beginUseOutputStream(WebRequest request) {
        request.setContentType(APPLICATION_GBXML);
    }

    public void testUseOutputStream() throws ServletException, IOException {
        BinaryXMLFilter filter = new BinaryXMLFilter();
        filter.init(config);
        MockFilterChain chain = new StreamChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
        chain.setContentType("application/xml; charset=Shift_JIS");
        filter.doFilter(request, response, chain);
    }
    
    public void endUseOutputStream(WebResponse response) throws IOException {
        assertEquals(response.getHeaderField("Content-Type"), APPLICATION_GBXML);
        InputStream is = response.getInputStream();
        for (int i = 0; i < BinaryXMLEncoderTest.TEST_BIN_STREAM_NS.length; ++i) {
            assertEquals(BinaryXMLEncoderTest.TEST_BIN_STREAM_NS[i], (byte)is.read());            
        }
    }

    /*
     * getOutputStreamとgetWriterは排他でなければならない。
     * IllegalStatusExceptionが発生すること。
     */
    public void beginUseWriterAndOutputStream(WebRequest request) {
        request.setContentType(APPLICATION_GBXML);
    }

    public void testUseWriterAndOutputStream() throws ServletException, IOException {
        BinaryXMLFilter filter = new BinaryXMLFilter();
        filter.init(config);
        MockFilterChain chain = new WriterAndStreamChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
        chain.setContentType("application/xml");
        try {
            filter.doFilter(request, response, chain);
            fail("IllegalStateException has not raised.");
        } catch(IllegalStateException e) {
        }
    }
    
    public void testUseWriterAndOutputStream(WebResponse response) throws IOException {
        assertEquals("text/html", response.getHeaderField("Content-Type"));
        assertEquals(BinaryXMLEncoderTest.TEST_TEXT_NS, response.getText());
    }

    /*
     * charsetが設定されなかった場合。
     */
    public void beginUseOutputStreamWithoutCharset(WebRequest request) {
        request.setContentType(APPLICATION_GBXML);
    }

    public void testUseOutputStreamWithoutCharset() throws ServletException, IOException {
        BinaryXMLFilter filter = new BinaryXMLFilter();
        filter.init(config);
        MockFilterChain chain = new StreamChain(BinaryXMLEncoderTest.TEST_TEXT_NS);
        chain.setContentType("application/xml");
        filter.doFilter(request, response, chain);
    }
    
    public void endUseOutputStreamWithoutCharset(WebResponse response) throws IOException, ParseException {
        assertEquals(response.getHeaderField("Content-Type"), APPLICATION_GBXML);
        InputStream is = response.getInputStream();
        for (int i = 0; i < BinaryXMLEncoderTest.TEST_BIN_STREAM_NS.length; ++i) {
            assertEquals(BinaryXMLEncoderTest.TEST_BIN_STREAM_NS[i], (byte)is.read());            
        }
//        assertEquals("application/xml", response.getHeaderField("Content-Type"));
//        BinaryXMLOutputter encoder = new BinaryXMLOutputter(BinaryXMLEncoderTest.TEST_TEXT_NS);
//        encoder.setOutputEncoding("ISO-8859-1");
//        assertEquals(encoder.encode(), response.getText());
    }

    /*
     * contentType設定のタイミングと、streamやwriter取得時のエンコーディングの
     * 関係の調査
     */    
    
    public void beginSetContentTypeWithStream(WebRequest request) {
        request.setContentType(APPLICATION_GBXML);
    }
    
    public void testSetContentTypeWithStream() throws ServletException, IOException {
        BinaryXMLFilter filter = new BinaryXMLFilter();
        filter.init(config);
        MockFilterChain chain = new MockFilterChain(null) {
            public void writeResponse(ServletResponse res) throws IOException {
                byte[] d = new byte[1024*5];
                for (int i = 0; i < d.length; ++i) d[i] = 'a';
                res.setContentType("text/html");
                assertEquals("ISO-8859-1", res.getCharacterEncoding());
                res.setContentType("application/xml; charset=Shift_JIS");
                res.setContentLength(6000);
                assertEquals(1024*8, res.getBufferSize());
                assertEquals("Shift_JIS", res.getCharacterEncoding());
                assertEquals(1024*8, res.getBufferSize());
                assertTrue(!res.isCommitted());
                OutputStream os = res.getOutputStream();
                os.write("<a>".getBytes("ISO-8859-1"));
                res.setContentType("text/xml; charset=ISO-8859-1");
                assertEquals("ISO-8859-1", res.getCharacterEncoding());
                os.write(d);
                assertEquals(1024*8, res.getBufferSize());
                assertTrue(!res.isCommitted());
                res.setContentType("text/xml; charset=ISO-8859-1");
                os.write(d);
                os.write("</a>".getBytes("ISO-8859-1"));
                assertEquals(1024*8, res.getBufferSize());
                assertTrue(!res.isCommitted());
                res.setContentType("application/xml; charset=ISO-8859-1");
            } 
        };
        chain.setContentType(null);
        filter.doFilter(request, response, chain);        
    }
    
    public void endSetContentTypeWithStream(WebResponse response) throws IOException {
        
        // この動作はCoyoteにて確認した。
        // OutputStreamを取得したのち、writeすると以下のどちらかの条件が成立した時点でcommittedとなる。
        //   1. contentLengthが設定されている場合は、それを超えるバイト数をwriteしたとき。
        //   2. OutputBufferのDEFAULT_BUFFER_SIZEを超えるバイト数をwriteしたとき。
        // contentType/charsetについては、committedとなる直前に設定されていたものが有効。
        
        // FIXME Now, we have not emulated the operation as above.
        
        assertEquals(APPLICATION_GBXML, response.getContentType());
        // ContentLengthを設定しても、バイナリ変換が行われた場合には、-1が設定される。
        assertEquals(-1, response.getContentLength());
    }
    
    /*
     * contentType設定のタイミングと、streamやwriter取得時のエンコーディングの
     * 関係の調査
     */    
    
    public void beginSetContentTypeWithWriter(WebRequest request) {
        request.setContentType(APPLICATION_GBXML);
    }    
    
    public void testSetContentTypeWithWriter() throws ServletException, IOException {
        BinaryXMLFilter filter = new BinaryXMLFilter();
        filter.init(config);
        MockFilterChain chain = new MockFilterChain(null) {
            public void writeResponse(ServletResponse res) throws IOException {
                char[] c = new char[1024*5];
                for (int i = 0; i < c.length; ++i) c[i] = 'a';
                res.setContentType("text/html");
                assertEquals("ISO-8859-1", res.getCharacterEncoding());
                res.setContentType("text/xml; charset=Shift_JIS");
                assertEquals("Shift_JIS", res.getCharacterEncoding());
                Writer w = res.getWriter();
                w.write("<a>");
                assertTrue(!res.isCommitted());
                res.setContentType("text/xml; charset=ISO-8859-1");
                assertEquals("Shift_JIS", res.getCharacterEncoding());
                w.write(c);
                assertTrue(!res.isCommitted());
                res.setContentType("application/xml; charset=UTF-8");
                w.write(c);
                w.write("</a>");
                assertTrue(!res.isCommitted());
                res.setContentType("application/xml; charset=Shift_JIS");
            } 
        };
        chain.setContentType(null);
        filter.doFilter(request, response, chain);        
    }
    
    public void endSetContentTypeWithWriter(WebResponse response) throws IOException {
        
        // この動作はCoyoteにて確認した。
        // Writerを取得したのち、writeすると以下のどちらかの条件が成立した時点でcommittedとなる。
        //   1. contentLengthが設定されている場合は、それを超える文字数をwriteしたとき。
        //   2. OutputBufferのDEFAULT_BUFFER_SIZEを超える文字数をwriteしたとき。
        // charsetについてはgetWriterの呼び出し直前に設定されていたものが有効。
        // contentTypeについては、committedとなる直前に設定されていたものが有効。
        
        // FIXME Now, we have not emulated the operation as above.

        assertEquals(APPLICATION_GBXML, response.getContentType());
        assertEquals(-1, response.getContentLength());
    }    
    
    /*
	 * FilterChainのモックアップ
	 */
    abstract class MockFilterChain implements FilterChain {

        String contentType = "application/xml; charset=UTF-8";
        protected String testString;
        
        MockFilterChain(String testString) {
            this.testString = testString;
        }
        
        void setContentType(String contentType) {
            this.contentType = contentType;
        }
        
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        	if (this.contentType != null) response.setContentType(this.contentType);
        	writeResponse(response);
        }
        
        public void init(FilterConfig conf) {
        }
        
        public void destroy() {
        }

        abstract void writeResponse(ServletResponse response) throws IOException;    
    }

    class WriterChain extends MockFilterChain {

        WriterChain(String testString) {
            super(testString);
        }

        void writeResponse(ServletResponse response) throws IOException {
            PrintWriter writer = response.getWriter();
            writer.print(this.testString);
        }
    }

    class StreamChain extends MockFilterChain {
       
        StreamChain(String testString) {
            super(testString);
        }
        
        void writeResponse(ServletResponse response) throws IOException {
            OutputStream os = response.getOutputStream();
            // TODO: test other charsets; Shift_JIS
            os.write(this.testString.getBytes("UTF-8"));
        }
    }
    
    class WriterAndStreamChain extends MockFilterChain {

        WriterAndStreamChain(String testString) {
            super(testString);
        }
        
        void writeResponse(ServletResponse response) throws IOException {
            PrintWriter writer = response.getWriter();
            writer.print(this.testString);
            OutputStream os = response.getOutputStream();
            // TODO: test other charsets; Shift_JIS
            os.write(this.testString.getBytes("UTF-8"));
        }
    }

}
