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

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParserException;

/**
 * BinaryXMLReader
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class BinaryXMLReader extends Reader {

    private static final Logger log = Logger.getLogger(BinaryXMLReader.class);
    private XMLOutputter out;
    private boolean versionread = false;
    private CharBuffer buffer;
    private CharsetEncoder encoder;

    public BinaryXMLReader(XMLOutputter out) {
        // FIXME get raw inputstream
        this.out = out;
        this.encoder = Charset.forName(out.getEncoding()).newEncoder();
        this.encoder.onMalformedInput(CodingErrorAction.REPLACE);
        this.encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    /* (non-Javadoc)
     * @see java.io.Reader#read(char[], int, int)
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        int pos = 0;
        while (pos < len) {
            if (this.buffer == null || this.buffer.remaining() == 0) {
                allocateBuffer();
            }
            if (this.buffer == null) return (pos > 0) ? pos : -1;
            int size = this.buffer.remaining();
            if (pos + size > len) size = len - pos;
            this.buffer.get(cbuf, off + pos, size);
            pos += size;
        }
        return pos;
    }   

    /* (non-Javadoc)
     * @see java.io.Reader#close()
     */
    public void close() throws IOException {
        // FIXME close given input stream
    }

    private void allocateBuffer() throws IOException {
        try {
            do {
                if (this.out.isEndOfDocument()) {
                    this.buffer = null;
                    return;
                }
                StringWriter writer = new StringWriter();
                this.out.writeNextTagTo(writer);
                log.debug("read tag while allocate buffer: " + writer.toString());
                this.buffer = CharBuffer.wrap(writer.getBuffer());
                log.debug("initial remaining: " + this.buffer.remaining());
            } while (this.buffer.limit() == 0);
        } catch (XmlPullParserException e) {
            log.warn("parse error while filling buffer", e);
            try {
                throw new IOException("binary xml parse error").initCause(e);
            } catch (Throwable e1) {
            }
        } catch (IOException e) {
            log.warn("io error while parsing", e);
            throw e;
        }
    }

}
