/*
 * $Id: BinaryXMLFilter.java 3228 2005-06-26 04:28:14Z go $
 * 
 * Created on 2005/05/06
 *
 */
package jp.haw.grain.framework.servlet;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import jp.haw.grain.framework.xml.BinaryXMLOutputter;
import jp.haw.grain.framework.xml.ParseException;

import org.apache.log4j.Logger;

/**
 * Grain Sproutからの要求に対するレスポンスがXMLの場合バイナリ形式に変換する。
 * @author go
 */
public class BinaryXMLFilter implements Filter {
	
	private static final Logger log = Logger.getLogger(BinaryXMLFilter.class);

	public static final Pattern XML_CONTENT_TYPE_PATTERN = Pattern.compile("^\\w*/xml");
    public static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("^(.*?)(; ?charset=(.*))?$");
    
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String GBXML_CONTENT_TYPE = "application/gbxml";

    private String textEncoding = "UTF-8";
    private boolean ignoreRequestContentType = false;
	
	public void init(FilterConfig config) throws ServletException {
	    String textEncoding = config.getInitParameter("TextEncoding");
        if (textEncoding != null) this.textEncoding  = textEncoding;
        String ignoreRequestContentType = config.getInitParameter("IgnoreRequestContentType");
        if ("true".equals(ignoreRequestContentType)) this.ignoreRequestContentType = true;
    }

	/** 
	 * リクエストのContent-Typeヘッダが "application/gbxml" で、かつ Response の Content-Type が "?/xml" の場合、
	 * Response中のXMLデータをバイナリXMLに変換する。<br>
     * ただし、Dojaでは、GETの場合、Content-Typeヘッダを設定できない。その場合は、InitParamで
     * IgnoreRequestContentTypeにtrueを設定し、すべてのリクエストを変換対象にすること。
     * 
     * TODO 他の判定方法の調査。
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException {
		String contentType = ((HttpServletRequest)request).getContentType();
		log.debug("Request Content-Type: " + contentType);
        if (GBXML_CONTENT_TYPE.equals(contentType) || this.ignoreRequestContentType) {
            try {
    			BinaryXMLEncodedServletResponse xmlResponse = new BinaryXMLEncodedServletResponse((HttpServletResponse)response);
    			chain.doFilter(request, xmlResponse);
				xmlResponse.commit();
			} catch (ParseException e) {
				throw new ServletException(e);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}
    
    interface ResponseBuffer {
        void close() throws IOException;
        void commit() throws IOException, ParseException;
        boolean isCharStream();
    }
	
	class BinaryXMLEncodedServletResponse extends HttpServletResponseWrapper {
		
	    private ResponseBuffer buffer;
		private String contentType;
        private String characterEncoding;
        private int contentLength = -1;
        private boolean commited;
		
		/**
		 * @param arg0
		 */
		public BinaryXMLEncodedServletResponse(HttpServletResponse response) {
			super(response);
		}

//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#flushBuffer()
//         */
//        public void flushBuffer() throws IOException {
//            // TODO Auto-generated method stub
//            super.flushBuffer();
//        }
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#getBufferSize()
//         */
//        public int getBufferSize() {
//            // TODO Auto-generated method stub
//            return super.getBufferSize();
//        }
        
        /**
         * @see javax.servlet.ServletResponse#getCharacterEncoding()
         */
        public String getCharacterEncoding() {
            if (this.characterEncoding == null) return DEFAULT_CHARSET;
            return this.characterEncoding;
        }
        
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#getLocale()
//         */
//        public Locale getLocale() {
//            // TODO Auto-generated method stub
//            return super.getLocale();
//        }
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponseWrapper#getResponse()
//         */
//        public ServletResponse getResponse() {
//            // TODO Auto-generated method stub
//            return super.getResponse();
//        }
        /**
         * @see javax.servlet.ServletResponse#isCommitted()
         */
        public boolean isCommitted() {
            if (commited) return true;
            return false;
        }
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#reset()
//         */
//        public void reset() {
//            // TODO Auto-generated method stub
//            super.reset();
//        }
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#resetBuffer()
//         */
//        public void resetBuffer() {
//            // TODO Auto-generated method stub
//            super.resetBuffer();
//        }
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#setBufferSize(int)
//         */
//        public void setBufferSize(int arg0) {
//            // TODO Auto-generated method stub
//            super.setBufferSize(arg0);
//        }
        
        /**
         * @see javax.servlet.ServletResponse#setContentLength(int)
         */
        public void setContentLength(int contentLength) {
            this.contentLength = contentLength;
        }
        
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
//         */
//        public void setLocale(Locale arg0) {
//            // TODO Auto-generated method stub
//            super.setLocale(arg0);
//        }
//        /* (non-Javadoc)
//         * @see javax.servlet.ServletResponseWrapper#setResponse(javax.servlet.ServletResponse)
//         */
//        public void setResponse(ServletResponse arg0) {
//            // TODO Auto-generated method stub
//            super.setResponse(arg0);
//        }
        
		public void setContentType(String contentType) {
            log.debug("request Content-Type to: " + contentType);
            if (this.buffer != null && isCommitted()) return;
            log.debug("setting Content-Type to: " + contentType);
            Matcher m = CONTENT_TYPE_PATTERN.matcher(contentType);
            if (!m.matches()) return;
            this.contentType = m.group(1);
            if (!usingWriter()) { 
                this.characterEncoding = m.group(3);
            }
            log.debug("buffer: " + (buffer != null));
        }
        
        public ServletOutputStream getOutputStream() throws IOException {
            log.debug("getOutputStream");
            if (this.buffer == null) {
                this.buffer = new BufferedServletOutputStream();
            }
            if (usingWriter()) {
                throw new IllegalStateException("method getWriter() was already called.");
            } 
            return (ServletOutputStream)this.buffer;
        }
        
		public PrintWriter getWriter() throws IOException {
            log.debug("getWriter");
            if (this.buffer == null) {
                this.buffer = new BufferedPrintWriter();
            }
            if (usingOutputStream()) {
                throw new IllegalStateException("method getOutputStream() was already called.");
            }
            return (PrintWriter)this.buffer;
		}
		
		public void commit() throws IOException, ParseException {
            log.debug("commited content-type: " + this.contentType);
            if (isXML()) {
                log.debug("commit xml content-type: " + GBXML_CONTENT_TYPE);
                super.setContentType(GBXML_CONTENT_TYPE);
            } else {
                log.debug("commit non-xml content-type: " + this.contentType);
                if (this.characterEncoding == null || this.characterEncoding.length() == 0) {
                    super.setContentType(this.contentType);
                } else {
                    super.setContentType(this.contentType + ";charset=" + this.characterEncoding);
                }
                if (this.contentLength > -1) super.setContentLength(this.contentLength);
            }
            if (this.buffer != null) this.buffer.commit();
		}
        
        private boolean isXML() {
            if (this.contentType == null) return false;
            return XML_CONTENT_TYPE_PATTERN.matcher(this.contentType).lookingAt();
        }
        
        private boolean usingWriter() {
            if (this.buffer == null) return false;
            return (this.buffer instanceof PrintWriter);
        }

        private boolean usingOutputStream() {
            if (this.buffer == null) return false;
            return (this.buffer instanceof ServletOutputStream);            
        }
        
        class BufferedServletOutputStream extends ServletOutputStream implements ResponseBuffer {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            public void commit() throws IOException, ParseException {
               OutputStream os = BinaryXMLEncodedServletResponse.this.getResponse().getOutputStream();
                if (BinaryXMLEncodedServletResponse.this.isXML()) {
                    BinaryXMLOutputter bxo = new BinaryXMLOutputter(this.buffer.toByteArray());
                    bxo.setTextEncoding(BinaryXMLFilter.this.textEncoding);
                    bxo.writeTo(os);                    
                } else {
                    log.debug("non encoded commit");
                    os.write(this.buffer.toByteArray());
                }
            }
                        
            public void write(int data) throws IOException {
                this.buffer.write(data);
            }
            
            public void close() throws IOException {
                this.buffer.close();
            }

            public boolean isCharStream() {
                return false;
            }            
        }
        
        class BufferedPrintWriter extends PrintWriter implements ResponseBuffer {
            
            public BufferedPrintWriter() {
                super(new CharArrayWriter());
            }

            public void commit() throws IOException, ParseException {
                CharArrayWriter buf = (CharArrayWriter)this.out;
                if (BinaryXMLEncodedServletResponse.this.isXML()) {
                    OutputStream os = BinaryXMLEncodedServletResponse.this.getResponse().getOutputStream();
                    log.debug("encoded commit");
                    BinaryXMLOutputter bxo = new BinaryXMLOutputter(buf.toCharArray());
                    bxo.writeTo(os);
                } else {
                    log.debug("non encoded commit");
                    PrintWriter writer = BinaryXMLEncodedServletResponse.this.getResponse().getWriter();
                    writer.print(buf.toCharArray());
                }
            }

            public boolean isCharStream() {
                return true;
            }
        }
	}

}
