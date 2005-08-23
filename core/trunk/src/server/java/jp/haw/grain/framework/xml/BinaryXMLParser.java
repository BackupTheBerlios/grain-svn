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
 * Created on 2005/06/16 13:53:33
 * 
 */
package jp.haw.grain.framework.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * バイナリXMLのプルパーサ 
 * 
 * @version $Id$
 * @author Go Takahashi
 * @author Le Phuong Thuy
 */
public class BinaryXMLParser implements XmlPullParser {

    public static final String XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
    private static final int INITIAL_STATE = 0;
    private static final int DOCUMENT_READING_STATE = 1;
    
    public static final int NOP_TAG           = 0x00;
    public static final int ELEMENT_START_TAG = 0x01;
    public static final int EMPTY_ELEMENT_TAG = 0x02;
    public static final int ATTRIBUTE_TAG =     0x03;
    public static final int TEXT_TAG =          0x04;
    public static final int PREFIXMAPPING_TAG = 0x05;
    public static final int ELEMENT_END_TAG =   0x09;
    public static final int DOCUMENT_END =      0x0F;
    
    public static final int NONE_MASK =         0x00;
    public static final int INDEXED_MASK =      0x10;
    public static final int DEFAULT_NS_MASK =   0x10;
    
    public static final int NS_MASK =           0x20;
    public static final int TAG_MASK =          0x0F;
    
    public static final int TEXT_TERMINATOR =   0x00;

    public static final String DETECT_ENCODING = "http://xmlpull.org/v1/doc/features.html#detect-encoding";
        
    private InputStream inputStream;
    private int eventType = XmlPullParser.START_DOCUMENT;
    private Vector wordDic = new Vector();
    private Stack nsStack = new Stack();
    private int bodySize;
    private int dicSize;
    private String version;
    private String encoding;
    private byte nextNode;
    private int depth;
    private String namespaceUri;
    private String prefix;
    private String name;
    private Vector attibutes;
    private boolean processingEmptyElement;
    private boolean afterEndTag;
    private Stack elementStack = new Stack();
    private boolean whitespace;
    private boolean headerApplied;
    
    private boolean processNamespace = false;
    private boolean validation = false;
    private boolean processDocDecl;
    private boolean detectEncoding = false;
    
    private BinaryXMLParser() {
    }

    /**
     * デフォルトのインスタンスを生成する。
     */
    public static BinaryXMLParser newInstance() {
        return newInstance(null, null);
    }

    public static BinaryXMLParser newInstance(InputStream is, String encoding) {
        try {
            BinaryXMLParser parser =  new BinaryXMLParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            if (is != null) parser.setInput(is, encoding);
            return parser;
        } catch (XmlPullParserException e) {
            return null;
        }
    }
    
    /**
     * @see org.xmlpull.v1.XmlPullParser#setFeature(java.lang.String, boolean)
     */
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (this.eventType != XmlPullParser.START_DOCUMENT) {
            throw new XmlPullParserException("Illegal state");
        }
        if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(name)) {
            this.processNamespace = state;
        } else if (DETECT_ENCODING.equals(name)) {
            this.detectEncoding = state;
        } else {
            throw new XmlPullParserException("Unsupported feature: " + name);
        }
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getFeature(java.lang.String)
     */
    public boolean getFeature(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(name)) return this.processNamespace;
        if (DETECT_ENCODING.equals(name)) return this.detectEncoding;
        return false;
    }

    /**
     * このパーサにプロパティを設定する。
     * このメソッドは空実装である。
     * 
     * @see org.xmlpull.v1.XmlPullParser#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String arg0, Object arg1) throws XmlPullParserException {
        // noting to do.
    }

    /**
     * このパーサに設定されたプロパティを取得する。<br><br>
     * 有効なプロパティ<br>
     * <table>
     * <tr><td>name</td><td>value</td></tr>
     * <tr>
     * <td>対象XML文書のバージョン</td>
     * <td>http://xmlpull.org/v1/doc/properties.html#xmldecl-version</td>
     * </tr>
     * </table>
     * @see org.xmlpull.v1.XmlPullParser#getProperty(java.lang.String)
     */
    public Object getProperty(String name) {
        if (name.equals(XMLDECL_VERSION)) {
            return this.version;
        }
        return null;
    }

    /**
     * @deprecated このメソッドの代わりに<code>setInput(InputStream inputStream, String encoding)</code>を利用してください。
     * @see org.xmlpull.v1.XmlPullParser#setInput(java.io.Reader)
     */
    public void setInput(Reader arg0) throws XmlPullParserException {
        throw new XmlPullParserException("Unsupported Operation");
    }

    /**
     * @param inputStream バイナリXMLの入力バイトストリーム。
     * @see org.xmlpull.v1.XmlPullParser#setInput(java.io.InputStream, java.lang.String)
     */    
    public void setInput(InputStream inputStream, String encoding) throws XmlPullParserException {
        this.inputStream = inputStream;
        this.encoding = encoding;
    }

    /**
     * @return バイナリXML中の文字列のエンコーディングを返す。
     * @see org.xmlpull.v1.XmlPullParser#getInputEncoding()
     */
    public String getInputEncoding() {
        return this.encoding;
    }

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#defineEntityReplacementText(java.lang.String, java.lang.String)
     */
    public void defineEntityReplacementText(String arg0, String arg1) throws XmlPullParserException {
        // TODO Auto-generated method stub
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getNamespaceCount(int)
     */
    
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        if (!this.processNamespace) return 0;
        int count = 0;
        for (int i = 0; i < this.nsStack.size(); ++i) {
            Namespace ns = (Namespace)this.nsStack.elementAt(i);
            if (ns.depth > depth) break;
            ++count;
        }
        return count;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getNamespacePrefix(int)
     */
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        if (!this.processNamespace) throw new IndexOutOfBoundsException();
        Namespace ns = (Namespace)this.nsStack.elementAt(pos);
        if (ns != null) return ns.prefix;
        return null;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getNamespaceUri(int)
     */
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        if (!this.processNamespace) throw new IndexOutOfBoundsException();
        Namespace ns = (Namespace)this.nsStack.elementAt(pos);
        if (ns != null) return ns.namespaceUri;
        return "";
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getNamespace(java.lang.String)
     */
    public String getNamespace(String prefix) {
        if (!this.processNamespace) throw new IndexOutOfBoundsException();
        for (int i = this.nsStack.size() - 1; i >= 0; --i) {
            Namespace ns = (Namespace)this.nsStack.elementAt(i);
            if (ns == null) continue;
            if (prefix == null && ns.prefix != null) continue;
            if (prefix != null && !prefix.equals(ns.prefix)) continue;
            return ns.namespaceUri;
        }
        return null; 
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getDepth()
     */
    public int getDepth() {
        return this.depth;
    }

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#getPositionDescription()
     */
    public String getPositionDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#getLineNumber()
     */
    public int getLineNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#getColumnNumber()
     */
    public int getColumnNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#isWhitespace()
     */
    public boolean isWhitespace() throws XmlPullParserException {
        if (this.text == null || this.text.length() == 0) return false;
        for (int i = 0; i < this.text.length(); ++i) {
            char c = this.text.charAt(i);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') continue;
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#getText()
     */
    public String getText() {
        return this.text;
    }

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#getTextCharacters(int[])
     */
    public char[] getTextCharacters(int[] arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getNamespace()
     */
    public String getNamespace() {
        return ((Element)this.elementStack.peek()).namespaceUri;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getName()
     */
    public String getName() {
        return ((Element)this.elementStack.peek()).name;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getPrefix()
     */
    public String getPrefix() {
        return ((Element)this.elementStack.peek()).prefix;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#isEmptyElementTag()
     */
    public boolean isEmptyElementTag() throws XmlPullParserException {
        if (this.eventType != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("Illegal state: expected START_TAG");
        }
        return this.processingEmptyElement;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributeCount()
     */
    public int getAttributeCount() {
        return this.attibutes.size();
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributeNamespace(int)
     */
    public String getAttributeNamespace(int index) {
        return ((Attribute)this.attibutes.elementAt(index)).namespaceUri;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributeName(int)
     */
    public String getAttributeName(int index) {
        return ((Attribute)this.attibutes.elementAt(index)).name;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributePrefix(int)
     */
    public String getAttributePrefix(int index) {
        return ((Attribute)this.attibutes.elementAt(index)).prefix;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributeType(int)
     */
    public String getAttributeType(int index) {
        return ((Attribute)this.attibutes.elementAt(index)).type;
    }

    /** (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#isAttributeDefault(int)
     */
    public boolean isAttributeDefault(int arg0) {
        // FIXME unknown specification
        return false;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributeValue(int)
     */
    public String getAttributeValue(int index) {
        return ((Attribute)this.attibutes.elementAt(index)).value;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getAttributeValue(java.lang.String, java.lang.String)
     */
    public String getAttributeValue(String namespace, String name) {
        if (this.eventType != START_TAG) throw new IndexOutOfBoundsException("not START_TAG state.");
        for (int i = 0;  i < this.attibutes.size(); ++i) {
            Attribute attr = (Attribute)this.attibutes.elementAt(i);
            if (!attr.namespaceUri.equals(namespace) || !attr.name.equals(name)) continue;
            return attr.value;
        }
        return null;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#getEventType()
     */
    public int getEventType() {
        return this.eventType;
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#next()
     */
    public int next() throws XmlPullParserException, IOException {
        return nextToken(false);
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#nextToken()
     */
    public int nextToken() throws XmlPullParserException, IOException {
        return nextToken(true);
    }
    
    private int nextToken(boolean reportAll) throws XmlPullParserException {
        try {
            System.out.println("start nextToken: reportAll=" + reportAll);
            StringBuffer buf = null;
            for (;;) {
                this.whitespace = false;
                if (this.eventType == XmlPullParser.START_DOCUMENT && !this.headerApplied) {
                    this.bodySize = readInteger();
                    int dicSize = readInteger();
                    this.version = readAsciiChar();
                    String encoding = readAsciiChar();
                    if (this.detectEncoding) this.encoding = encoding;
                    buildDictionary(dicSize);
                    this.headerApplied = true;
                }
                if (this.nodeApplied) this.node = nextNode();
                System.out.println("processing node: " + Integer.toHexString(this.node));
                System.out.println("buffer: " + buf);
                switch (this.node & TAG_MASK) {
                    case ELEMENT_START_TAG:
                    case EMPTY_ELEMENT_TAG:
                        if (!reportAll && buf != null) break; 
                        applyNode();
                        this.eventType = XmlPullParser.START_TAG;
                        return this.eventType;
                        
                    case ELEMENT_END_TAG:
                        if (!reportAll && buf != null) break; 
                        applyNode();
                        this.eventType = XmlPullParser.END_TAG;
                        return this.eventType;
                        
                    case DOCUMENT_END:
                        if (!reportAll && buf != null) break;
                        this.eventType = XmlPullParser.END_DOCUMENT;
                        return this.eventType;
                        
                    case TEXT_TAG:
                        System.out.println("TEXT_TAG: " + Integer.toHexString(this.node));
                        applyNode();
                        if (getText() == null || getText().length() == 0) {
                            if (reportAll) {
                                this.eventType = XmlPullParser.TEXT;
                                return this.eventType;
                            }
                            continue;
                        }
                        System.out.println("TEXT_TAG: text=" + getText());
                        if (!isWhitespace()) {
                            if (reportAll) {
                                this.eventType = XmlPullParser.TEXT;
                                return this.eventType;
                            } else {
                                if (buf == null) buf = new StringBuffer();
                                buf.append(text);
                                continue;
                            }
                        } else {
                            this.whitespace = true;
                            if (reportAll) {
                                this.eventType = XmlPullParser.IGNORABLE_WHITESPACE;
                                return this.eventType;
                            }
                            continue;
                        }
                    
                    case PREFIXMAPPING_TAG:
                        System.out.println("prefixmapping tag :" + Integer.toHexString(this.node));
                        applyNode();
                        continue;
                        
                    default:
                        throw new XmlPullParserException("unexpected node: " + Integer.toHexString(node));
                }
                
                // reaches here only (!reportAll && buf != null) to return TEXT event.
                this.text = buf.toString();
                this.eventType = XmlPullParser.TEXT;
                return this.eventType;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new XmlPullParserException(e.toString());
        }       
    }
    
    private int nextToken;
    private String text;
    private int node;
    private boolean nodeApplied = true;

    /* (non-Javadoc)
     * @see org.xmlpull.v1.XmlPullParser#require(int, java.lang.String, java.lang.String)
     */
    public void require(int arg0, String arg1, String arg2) throws XmlPullParserException, IOException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#nextText()
     */
    public String nextText() throws XmlPullParserException, IOException {
        System.out.println("start next text");
        if(getEventType() != XmlPullParser.START_TAG) { 
            throw new XmlPullParserException("parser must be on START_TAG to read next text");
        }
        int eventType = next();
        if(eventType == XmlPullParser.TEXT) {
            System.out.println("event type: text");
            String result = getText();
            System.out.println("text: " + result);
            eventType = next();
            if(eventType != END_TAG) {
                throw new XmlPullParserException("event TEXT it must be immediately followed by END_TAG");
            }
            return result;
        } else if(eventType == END_TAG) {
            System.out.println("event type: end tag");
            return "";
        } else {
            throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text");
        }
    }

    /**
     * @see org.xmlpull.v1.XmlPullParser#nextTag()
     */
    public int nextTag() throws XmlPullParserException, IOException {
        int eventType = next();
        if (eventType == TEXT && isWhitespace()) {   // skip whitespace
            eventType = next();
        }
        if (eventType != START_TAG && eventType != END_TAG) {
           throw new XmlPullParserException("expected start or end tag");
        }
        return eventType;
    }
    
    int readInteger() throws XmlPullParserException, IOException {

        // utfの先頭バイトは、最上位ビットから"1"がいくつ並んでいるかで、後続の
        // データバイト数が決まる。
        
        // 先頭バイトが '0xxxxxxx'（最上位ビットから'1'が全く並んでいない）場合、
        // 先頭バイトのみでデータは終わりなので、そのまま、先頭バイトを返す。
        int head = this.inputStream.read();
        if ((head & 0x80) == 0x00) return head;
        
        // 先頭バイトが '110xxxxx'（最上位ビットから'1'が2つ並んでいる）場合から
        // 始めて、'1'の連続数をチェックする。このため、'111xxxxx'(0xE0)をmask
        // にして、maskの1の連続数を増やしていく。
        for (int mask = 0xE0, data = 0, i = 0; mask < 0xFF; mask = ((mask >>> 1) | 0x80), ++i ) {
            int body = this.inputStream.read();
            if (body == -1) break;
            
            // 後続バイトはすべて '10xxxxxx'である。従って、バッファを6ビット
            // 左へシフトした上で、後続バイトから下位6ビットを取り出して合成
            // していく。
            data = (data << 6) | (body & 0x3F);
            
            // maskを1ビット左へシフトして、桁あふれしたビットを0xFFでマスク
            // すると、チェックすべき最上位ビットからの'1'の連続を再現できる。
            // それと先頭バイトをmaskしたものを比較することで、'1'の連続数が
            // わかる。なお(i + 2)が'1'の連続数である。
            if ((head & mask) == ((mask << 1) & 0xFF)) { 
                
                // '1'の連続数にあわせて先頭バイトをシフトし、data（後続バイト
                // から生成された値）と合成する。
                return ((head & ~mask) << ((i + 1) * 6)) | data ;
            }
        }
        throw new XmlPullParserException("Illegal binary format: integer");
    }
    
    String readAsciiChar() throws XmlPullParserException, IOException{
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i != -1; ) {
            i = this.inputStream.read();
            if (i == TEXT_TERMINATOR) return buf.toString();
            buf.append((char)i);
        }
        throw new XmlPullParserException("Illegal binary format: ascii-chars");        
    }

    String readText() throws XmlPullParserException, IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (int i = 0; i != -1; ) {
            i = this.inputStream.read();
            if (i == TEXT_TERMINATOR) return new String(os.toByteArray(), this.encoding);
            os.write(i);
        }
        throw new XmlPullParserException("Illegal binary format: encoded-chars");        
    }

    private void buildDictionary(int dicSize) throws XmlPullParserException, IOException {
        /*for (int i = 0; i < dicSize; ++i) {
            int index = readInteger();
            String word = readText();
            this.wordDic.addElement(word);
        }*/
        for (int i = 0; i < dicSize; ++i) {
            //int index = readInteger();
            //String word = readText();
            String[] word=new String[2];
            word[0]=Integer.toString(readInteger());
            word[1]=readText();
            this.wordDic.addElement(word);
        }
    }
    
    private int nextNode() throws XmlPullParserException, IOException {
        this.nodeApplied = false;
        if (this.processingEmptyElement) {
            this.node = ELEMENT_END_TAG;
            this.processingEmptyElement = false;
        } else {
            this.node = readNode();
        }
        return this.node;
    }
    
    private void applyNode() throws XmlPullParserException, IOException {
        System.out.println("apply node: " + Integer.toHexString(this.node) + ", nodeApplied=" + this.nodeApplied);
        if (this.nodeApplied) throw new XmlPullParserException("already applied");
        if (this.afterEndTag) {
            this.depth -= 1;
            this.elementStack.pop();
            this.afterEndTag = false;
        }
        int tag = this.node & TAG_MASK;
        switch (tag) {
            case ELEMENT_START_TAG:
            case EMPTY_ELEMENT_TAG:
                this.depth += 1;
                Element elem = new Element();
                this.elementStack.push(elem);
                Namespace ns = readNamespace(this.node);
                String name = readName(this.node);
                if (ns != null) {
                    if (this.processNamespace) {
                        elem.namespaceUri = ns.namespaceUri;
                        elem.prefix = ns.prefix;
                        elem.name = name;
                    } else {
                        elem.name = ((ns.prefix == null) ? "" : ns.prefix + ":") + name;  
                    }
                } else {
                    elem.name = name;
                }
                System.out.println("** elem: " +
                        "name=" + name + 
                        (ns != null ? (", namespace=" + ns.namespaceUri + ", prefix=" + ns.prefix) : " no namespace"));
                this.attibutes = new Vector();
                if (this.processNamespace == false) {
                    for (int i = 0; i < this.nsStack.size(); ++i) {
                        Namespace n = (Namespace)this.nsStack.elementAt(i);
                        if (n.depth > this.depth) break;
                        if (n.depth == this.depth) { 
                            Attribute attr = new Attribute();
                            attr.name = "xmlns" + ((n.prefix == null) ? "" : (":" + n.prefix));
                            attr.value = n.namespaceUri;
                            this.attibutes.addElement(attr);
                        }
                    }
                }
                for (;;) {
                    byte attrNode = readNode();
                    System.out.println("attrNode: " + Integer.toHexString(attrNode));
                    if ((attrNode & TAG_MASK) != ATTRIBUTE_TAG) {
                        this.nextNode = attrNode;
                        break;
                    }
                    Attribute attr = new Attribute();
                    this.attibutes.addElement(attr);
                    Namespace attrNs = readNamespace(attrNode);
                    String attrName = readName(attrNode);
                    if (attrNs != null) {
                        if (this.processNamespace) {
                            attr.namespaceUri = attrNs.namespaceUri;
                            attr.prefix = attrNs.prefix;
                            attr.name = attrName;
                        } else {
                            attr.name = ((attrNs.prefix == null) ? "" : attrNs.prefix + ":") + attrName;  
                        }
                    } else {
                        attr.name = attrName;
                    }                    
                    attr.value = readText();
                    System.out.println("** attr: " +
                            "name=" + attrName + ", value=" + attr.value + 
                            (attrNs != null ? (", namespace=" + attrNs.namespaceUri + ", prefix=" + attrNs.prefix) : " no namespace"));
                    // TODO attr.type = ?;
                }
                if (tag == EMPTY_ELEMENT_TAG) this.processingEmptyElement = true;
                break;
                
            case ELEMENT_END_TAG:
                this.afterEndTag = true;
                break;
                
            case DOCUMENT_END:
                return;
            case TEXT_TAG:
                this.text = readText();
                break;
                
            case PREFIXMAPPING_TAG:
                readPrefixMapping(this.node);
                break;
                
            default:
                // FIXME throw new Exception...
        }
        this.nodeApplied = true;
    }
    
    private byte readNode() throws XmlPullParserException, IOException {
        System.out.println("start read node: nextNode=" + Integer.toHexString(this.nextNode));
        if (this.nextNode == NOP_TAG) {
            int node = this.inputStream.read();
            if (node == -1) return DOCUMENT_END;
            return (byte)node;
        } else {
            try {
                return this.nextNode;
            } finally {
                this.nextNode = NOP_TAG;
            }
        }
    }
    
    private String readName(int node) throws XmlPullParserException, IOException {
        System.out.println("start read name");
        if ((node & INDEXED_MASK) == 0) {
            int index = readInteger();
            for(int i=0;i<this.wordDic.size();++i){
            	String[] word=(String[]) this.wordDic.elementAt(i);
            	int wordindex=Integer.parseInt(word[0]);
            	if(wordindex==index) return (String)word[1];
            }
            return null;
            //return (String)this.wordDic.elementAt(index - 1);
       }else {
            return readText();
        }
    }
    
    private Namespace readNamespace(int node) throws XmlPullParserException, IOException {
        System.out.println("start read namespace");
        int index = ((node & NS_MASK) != 0) ? readInteger() : 0;
        if (index == 0 && ((node & TAG_MASK) == ATTRIBUTE_TAG)) return null;
        return getNamespace(index);
    }
    
    private void readPrefixMapping(int node) throws XmlPullParserException, IOException {
        System.out.println("start read prefix mapping");
        Namespace ns = new Namespace();
        if ((node & INDEXED_MASK) != 0) {
            ns.index = readInteger(); 
            ns.prefix = readText();
        }
        ns.namespaceUri = readText();
        ns.depth = this.depth + 1;
        this.nsStack.push(ns);
    }
    
    private Namespace getNamespace(int index) {
        for (int i = this.nsStack.size() - 1; i >= 0; --i) {
            Namespace ns = (Namespace)this.nsStack.elementAt(i);
            if (ns.depth > this.depth) continue;
            if (ns.index != index) continue;
            return ns;
        }
        return null;
    }

    class Element {
        String name;
        String namespaceUri = "";
        String prefix;
    }
    
    class Attribute {
        String name;
        String namespaceUri = "";
        String prefix;
        String type;
        String value;
    }
    
    class Namespace {
        int depth;
        int index;
        String prefix;
        String namespaceUri;
    }
    
    class NamespaceGroup {
        int depth;
        Hashtable namespaces = new Hashtable();
    }
}
