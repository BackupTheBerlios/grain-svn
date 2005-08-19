/*
 * License
 * 
 * Created on 2005/08/18 20:57:45
 * 
 */
package jp.haw.grain.framework.servlet;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * CharArrayBuffer
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class CharArrayBuffer extends CharArrayWriter {

    private boolean commit;

    public CharArrayBuffer() {
        super();
    }
    
    /**
     * @param initialSize
     */
    public CharArrayBuffer(int initialSize) {
        super(initialSize);
    }
    
    public void commit() {
        this.commit = true;
    }
    
    public int length() {
        return this.count;
    }
    
    public void write(int c) {
        if (this.commit) throw new IllegalStateException("already commited.");
        super.write(c);
    }
    
    public Reader getReader() {
        if (!this.commit) throw new IllegalStateException("not commited yet.");
        return new CharArrayReader(CharArrayBuffer.this.buf);
    }
    
    public InputStream getInputStream(String encoding) throws UnsupportedEncodingException {
        if (!this.commit) throw new IllegalStateException("not commited yet.");
        StringBuffer buf = new StringBuffer(CharArrayBuffer.this.count);
        return new ByteArrayInputStream(buf.append(CharArrayBuffer.this.buf).toString().getBytes(encoding));
    }

}
