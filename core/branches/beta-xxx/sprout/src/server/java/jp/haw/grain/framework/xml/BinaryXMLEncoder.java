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
 * Created on 2005/05/24
 *
 */
package jp.haw.grain.framework.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XMLパースをgXML(バイナリXML)に変換してバイトストリームに出力するSAXHandler。
 *
 * @version $Id$
 * @author Go Takahshi
 * 
 * TODO メモリ効率のよい出力方法の模索
 * TODO 出現頻度の低い単語の辞書登録の抑制
 */
public class BinaryXMLEncoder extends DefaultHandler {

    private static final Logger log = Logger.getLogger(BinaryXMLEncoder.class);
    
    public static final int ELEMENT_START_TAG = 0x01;
    public static final int EMPTY_ELEMENT_TAG = 0x02;
    public static final int ATTRIBUTE_TAG =     0x03;
    public static final int TEXT_TAG =          0x04;
    public static final int PREFIXMAPPING_TAG = 0x05;
    public static final int ELEMENT_END_TAG =   0x09;
    
    public static final int NONE_MASK =         0x00;
    public static final int INDEXED_MASK =      0x10;    
    public static final int NS_MASK =           0x20;
    
    public static final int TEXT_TERMINATOR =   0x00;

    
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private List nameList = new ArrayList();
    private Map nameMap = new HashMap();
    private Map prefixMap = new HashMap();
    private int prefixIndex = 0;    
    private Element current;

    private String textEncoding = "UTF-8";
    
    public BinaryXMLEncoder(String textEncoding) {
        if (textEncoding != null) this.textEncoding = textEncoding;
    }
    
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            log.debug("*start element:" + qName);
            log.debug("*attributes:" + attributes.getLength());
            if (this.current != null) this.current.writeStartTag();
            this.current = new Element(uri, localName, qName, attributes);
        } catch (IOException e) {
            log.fatal(e.toString(), e);
            throw new SAXException(e);
        } catch (RuntimeException e) {
            log.debug(e.toString(), e);
            throw e;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            log.debug("*end element:" + qName);
            if (this.current != null) {
                this.current.writeEmptyTag();
                this.current = null;
            } else {
                this.out.write(ELEMENT_END_TAG);                
            }
        } catch (IOException e) {
            log.fatal(e.toString(), e);
            throw new SAXException(e);
        } catch (RuntimeException e) {
            log.debug(e.toString(), e);
            throw e;
        }    
    }
    
    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int len) throws SAXException {
        try {            
            if (this.current != null) {
                this.current.writeStartTag();
                this.current = null;
            }
            String text = new String(ch, start, len);
            if (Pattern.matches("^[\\s]*$", text)) return; // Normalizeを行う。
            this.out.write(TEXT_TAG);
            this.out.write(toVariableLengthText(text));
            this.out.write(TEXT_TERMINATOR);
            log.debug("*charactor[" + start + "][" + len + "]: " + new String(ch, start, len));
        } catch (IOException e) {
            log.fatal(e.toString(), e);
            throw new SAXException(e);
        }
    }
    
    /**
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            PrefixMapping pm = (PrefixMapping)this.prefixMap.get(prefix);
            if (pm == null) {
                pm = new PrefixMapping(prefix);
                this.prefixMap.put(prefix, pm);
            }
            // TODO: 等価なPrefixMappingが追加されないように
            pm.addUri(uri);
            int indexed = (prefix.length() == 0) ? NONE_MASK : INDEXED_MASK;
            this.out.write(indexed | PREFIXMAPPING_TAG);
            if (indexed == INDEXED_MASK) {
                this.out.write(toVariableLengthNumber(pm.getIndex()));
                this.out.write(toVariableLengthText(prefix));
                this.out.write(TEXT_TERMINATOR);
            }
            this.out.write(toVariableLengthText(uri));
            this.out.write(TEXT_TERMINATOR);
            log.debug("*start prefix mapping[" + pm.getIndex() + "]: " + prefix + "=" + uri);
        } catch (IOException e) {
            log.fatal(e.toString(), e);
            throw new SAXException(e);            
        }
    }
    
    /**
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        PrefixMapping pm = (PrefixMapping)this.prefixMap.get(prefix);
        if (pm == null) throw new SAXException("Illegal prefix mapping: " + prefix);
        pm.disposeCurrentUri();
        log.debug("*end prefix mapping[" + pm.getIndex() + "]: " + prefix);
    }

    /**
     * パース結果であるgbXMLを指定されたバイトストリームに書き出す。
     * @param os gbXMLを書き出すOutputStream
     * @throws IOException
     */
    public void writeTo(OutputStream os) throws IOException {
        log.debug("size: " + this.out.size());
        os.write(toVariableLengthNumber(this.out.size()));
        log.debug("dic-size: " + this.nameMap.size());
        os.write(toVariableLengthNumber(this.nameMap.size()));
        os.write(toVariableLengthText("1.0"));
        os.write(TEXT_TERMINATOR);        
        os.write(toVariableLengthText(this.textEncoding));
        os.write(TEXT_TERMINATOR);

        for (int i = 0; i < this.nameList.size(); ++i) {
            String name = (String)this.nameList.get(i);
            os.write(toVariableLengthNumber(i + 1));
            os.write(toVariableLengthText(name));
            os.write(TEXT_TERMINATOR);
            log.debug("dic entry: " + (i + 1) + " = " + name);
        }
        os.write(this.out.toByteArray());
    }
    
    private SortedSet createSortedEntries() {
        SortedSet set = new TreeSet(new Comparator() {
            public int compare(Object obj1, Object obj2) {
                Map.Entry entry1 = (Map.Entry)obj1;
                Map.Entry entry2 = (Map.Entry)obj2;
                return ((Integer)entry1.getValue()).compareTo((Integer)entry2.getValue());
            }
        });
        set.addAll(this.nameMap.entrySet());
        return set;
    }
    
    private byte[] toVariableLengthNumber(int i) throws UnsupportedEncodingException {
        return String.valueOf((char)i).getBytes("UTF-8");
    }
    
    private byte[] toVariableLengthText(String text) throws UnsupportedEncodingException {
        return text.getBytes(this.textEncoding);
    }
    
    private boolean contains(String name) {
        return this.nameMap.containsKey(name);
    }
        
    private int getNameIndexOf(String name) {
        Integer index = (Integer)this.nameMap.get(name);
        if (index == null) return -1;
        return index.intValue();
    }
            
    private void registName(String name) {
        if (name.length() < 3) return;
        if (BinaryXMLEncoder.this.nameMap.get(name) != null) return;
        this.nameList.add(name);
        this.nameMap.put(name, new Integer(this.nameList.size()));
    }
    
    private int getPrefixIndexOf(String prefix) {
        PrefixMapping pm = (PrefixMapping)this.prefixMap.get(prefix);
        if (pm == null) return "".equals(prefix) ? 0 : -1;
        return pm.getIndex();
    }
    
    /**
     * パースしたノードに関連する情報の保持およびバイトストリームへの出力を行う。
     * サブクラスにて詳細な動作は定義する。
     * @author go
     */
    abstract class Node {
        
        protected String localName;
        protected String qName;
        protected String prefix;
        
        Node(String localName, String qName) {
            this.localName = localName;
            this.qName = qName;
            if (localName.length() == 0) return;
            if (localName.equals(qName)) {
                this.prefix = "";
            } else {
                this.prefix = qName.substring(0, qName.indexOf(':'));
            }
        }
        
        protected void writeName() throws IOException {
            int index = BinaryXMLEncoder.this.getNameIndexOf(this.localName);
            if (index > 0) {
                BinaryXMLEncoder.this.out.write(toVariableLengthNumber(index));   
                log.debug("write name: " + index + " as " + this.localName);                 
            } else {
                BinaryXMLEncoder.this.out.write(toVariableLengthText(this.localName));
                BinaryXMLEncoder.this.out.write(TEXT_TERMINATOR);    
                log.debug("write name: " + this.localName);                 
            }
        }
        
        protected void writeValue(String value) throws IOException {
            BinaryXMLEncoder.this.out.write(toVariableLengthText(value));
            BinaryXMLEncoder.this.out.write(TEXT_TERMINATOR);    
            log.debug("write value: " + value);                 
        }

        protected void writeNSIndex() throws IOException {
            if (this.prefix == null || this.prefix.length() == 0) return;
            int index = BinaryXMLEncoder.this.getPrefixIndexOf(this.prefix);
            BinaryXMLEncoder.this.out.write(toVariableLengthNumber(index));
            log.debug("write ns: " + index + " as " + prefix);
        }
        
        protected String getPrefix() {
            return this.qName.substring(0, this.qName.indexOf(':'));
        }

    }
    
    /**
     * パースした要素に関連する情報の保持およびバイトストリームへの出力を行う。
     * 
     * @author go
     */
    class Element extends Node {
        
        private String uri;
        private List attributes = new ArrayList();
        
        Element(String uri, String localName, String qName, Attributes attributes) {
            super(localName, qName);
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
            BinaryXMLEncoder.this.registName(localName);
            for (int i = 0; i < attributes.getLength(); ++i) {
                log.debug("attributes[" + i + "]: localName=" + attributes.getLocalName(i) + ", QName=" + attributes.getQName(i));
                if (attributes.getQName(i).equals("xmlns")) continue;
                if (attributes.getQName(i).startsWith("xmlns:")) continue;
                this.attributes.add(new Attribute(
                        attributes.getLocalName(i),
                        attributes.getQName(i),
                        attributes.getValue(i)));
            }
            log.debug("create element: localName=" +localName + ", QName=" + qName);
            log.debug("attributes[" + this.attributes + "]: " + this.attributes.size());
        }
        
        void writeStartTag() throws IOException {
            log.debug("write start tag...");
            writeTagHeader(false);
            writeName();
            writeAttributes();
        }

        void writeEmptyTag() throws IOException {
            log.debug("write empty tag...");
            writeTagHeader(true);
            writeName();
            writeAttributes();
        }
                
        private void writeTagHeader(boolean empty) throws IOException {
            int n = (this.prefix == null || this.prefix.length() == 0) ? 0x00 : NS_MASK;
            int i = (BinaryXMLEncoder.this.contains(this.localName)) ? 0x00 : INDEXED_MASK;
            int e = (!empty) ? ELEMENT_START_TAG : EMPTY_ELEMENT_TAG;
            BinaryXMLEncoder.this.out.write( n | i | e );
            log.debug("write header: " + Integer.toHexString( n | i | e ));
            writeNSIndex();
        }

        private void writeAttributes() throws IOException {
            log.debug("write attributes");
            log.debug("attributes[" + this.attributes + "]: " + this.attributes.size());            
            for (int i = 0; i < this.attributes.size(); ++i) {
                Attribute attr = (Attribute)this.attributes.get(i);
                attr.writeAttibute();
            }
        }
        
    }
    
    /**
     * パースした属性に関連する情報の保持およびバイトストリームへの出力を行う。
     *
     * @author go
     */
    class Attribute extends Node {
       
        private String value;
        
        Attribute(String localName, String qName, String value) {
            super(localName, qName);
            this.localName = localName;
            this.qName = qName;
            this.value = value;
            BinaryXMLEncoder.this.registName(localName);
        }
        
        void writeAttibute() throws IOException {
            int n = (this.prefix == null || this.prefix.length() == 0) ? 0x00 : NS_MASK;
            int i = (BinaryXMLEncoder.this.contains(this.localName)) ? 0x00 : INDEXED_MASK;
            BinaryXMLEncoder.this.out.write(ATTRIBUTE_TAG | n | i );
            log.debug("write attribute ...");
            log.debug("tag header: " + Integer.toHexString(ATTRIBUTE_TAG | n | i));
            writeNSIndex();
            writeName();
            writeValue(this.value);
        }
        
    }
    
    /**
     * 名前空間接頭辞とURIの対応を保持する。
     * BinaryXMLEncoder.prefixMapのエントリ。
     * 
     * @author go
     */
    class PrefixMapping {
        
        private String prefix;
        private List list = new ArrayList();
        private int index = 0;
       
        PrefixMapping(String prefix) {
            this.prefix = prefix;
            if (prefix.length() == 0) return;
            this.index = ++BinaryXMLEncoder.this.prefixIndex;
        }

        void addUri(String uri) {
            this.list.add(uri);
        }
        
        String getCurrentUri() {
            if (this.list.size() == 0) return null;
            return (String) this.list.get(this.list.size() - 1);
        }
        
        String disposeCurrentUri() {
            if (this.list.size() == 0) return null;
            return (String) this.list.remove(this.list.size() - 1);
        }
        
        int getIndex() {
            return this.index;
        }
        
    }
}
