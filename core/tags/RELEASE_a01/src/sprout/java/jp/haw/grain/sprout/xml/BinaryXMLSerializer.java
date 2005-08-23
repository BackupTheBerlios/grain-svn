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
 * Created on 2005/07/26 15:07:37
 * 
 */
package jp.haw.grain.sprout.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.Text;

/**
 * BinaryXMLSerializer
 * @version $Id: BinaryXMLSerializer.java 3279 2005-08-06 23:52:13Z go $
 * @author Le Phuong Thuy
 */

public class BinaryXMLSerializer {

    private Node node;

    // private OutputStream os;
    // private ByteArrayOutputStream os;
    public static final int ELEMENT_START_TAG = 0x01;

    public static final int EMPTY_ELEMENT_TAG = 0x02;

    public static final int ATTRIBUTE_TAG = 0x03;

    public static final int TEXT_TAG = 0x04;

    public static final int PREFIXMAPPING_TAG = 0x05;

    public static final int ELEMENT_END_TAG = 0x09;

    public static final int NONE_MASK = 0x00;

    public static final int INDEXED_MASK = 0x10;

    public static final int NS_MASK = 0x20;

    public static final int TEXT_TERMINATOR = 0x00;

    private String textEncoding = "SJIS";

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    private Vector nameList = new Vector();

    private Hashtable nameMap = new Hashtable();

    private Vector prefixMap = new Vector();

    /**
     * @param node
     *            シリアライズ対象のXMLノード
     */
    public BinaryXMLSerializer(Node node) {
        this.node = node;
    }

    /**
     * XMLドキュメントツリーを指定した出力ストリームにシリアライズする。
     */

    public void serializeTo(OutputStream os) throws IOException {
        DOMWrite(this.node);
        this.out.write(this.toVariableLengthNumber(ELEMENT_END_TAG));
        System.out.println("size: " + this.out.size());
        os.write(toVariableLengthNumber(this.out.size()));
        System.out.println("dic-size: " + this.nameMap.size());
        os.write(toVariableLengthNumber(this.nameMap.size()));
        os.write(toVariableLengthText("1.0"));
        os.write(TEXT_TERMINATOR);
        os.write(toVariableLengthText(this.textEncoding));
        os.write(TEXT_TERMINATOR);

        for (int i = 0; i < this.nameList.size(); ++i) {
            String name = (String) this.nameList.elementAt(i);
            os.write(toVariableLengthNumber(i + 1));
            os.write(toVariableLengthText(name));
            os.write(TEXT_TERMINATOR);
            System.out.println("dic entry: " + (i + 1) + " = " + name);
        }
        os.write(this.out.toByteArray());

    }

    // DOMツリーのすべてのノードをバイナリ変換
    private void DOMWrite(Node node) throws UnsupportedEncodingException, IOException {
        if (node instanceof Element) {
            Element ne = (Element) node;
            // 要素のすべてのnamespaceの取得
            if (ne.getPrefixList() != null) {
                Vector pf = ne.getPrefixList();
                for (Enumeration e = pf.elements(); e.hasMoreElements();) {
                    String[] prefix_map = (String[]) e.nextElement();
                    this.registPrefix(prefix_map);
                }
            }
            if (ne.getTagName() != null) {
                this.registName(ne.getTagName());
                if (ne.getFirstChild() != null) {
                    this.writeStartEmptyTag(false, ne.getPrefix(), ne.getTagName(), ne.getAttributes());
                } else
                    this.writeStartEmptyTag(true, ne.getPrefix(), ne.getTagName(), ne.getAttributes());
            }
            Node child = ne.getFirstChild();
            while (child != null) {
                DOMWrite(child);
                if (child instanceof Element) {
                    Element echild = (Element) child;
                    if (echild.getFirstChild() != null) {
                        this.out.write(this.toVariableLengthNumber(ELEMENT_END_TAG));
                    }
                }
                child = child.getNextSibling();
            }
        } else if (node instanceof Text) {
            this.out.write(this.toVariableLengthNumber(TEXT_TAG));
            System.out.println("Text:" + node);
            String text = node.toString();
            this.out.write(this.toVariableLengthText(text));
            this.out.write(this.toVariableLengthNumber(TEXT_TERMINATOR));
            return;
        }
    }

    private byte[] toVariableLengthNumber(int i) {

        byte[] b = null;

        if (0x0 <= i && i <= 0x7F) {
            b = new byte[1];
            b[0] = (byte) (0x0 | i);
        } else if (0x80 <= i && i <= 0x7FF) {
            b = new byte[2];
            b[0] = (byte) (0xc0 | (byte) (i >> 6));
            b[1] = (byte) (0x80 | (0x3F & (byte) i));
        } else if (0x800 <= i && i <= 0xFFFF) {
            b = new byte[3];
            b[0] = (byte) (0xe0 | (byte) (i >> 12));
            b[1] = (byte) (0x80 | (0x3F & (byte) (i >> 6)));
            b[2] = (byte) (0x80 | (0x3F & (byte) i));
        } else if (0x10000 <= i && i <= 0x1FFFFF) {
            b = new byte[4];
            b[0] = (byte) (0xf0 | (byte) (i >> 18));
            b[1] = (byte) (0x80 | (0x3F & (byte) (i >> 12)));
            b[2] = (byte) (0x80 | (0x3F & (byte) (i >> 6)));
            b[3] = (byte) (0x80 | (0x3F & (byte) i));
        }
        return b;
    }

    private byte[] toVariableLengthText(String text)
            throws UnsupportedEncodingException {
        return text.getBytes(this.textEncoding);
    }

    private int getNameIndexOf(String name) {
        Integer index = (Integer) this.nameMap.get(name);
        if (index == null)
            return -1;
        return index.intValue();
    }

    private int getPrefixIndexOf(String prefix) {
        for (int i = this.prefixMap.size() - 1; i >= 0; i--) {
            String[] ns = (String[]) this.prefixMap.elementAt(i);
            if (prefix.equals(ns[0]))
                return this.prefixMap.indexOf(ns) + 1;
        }
        return -1;
    }

    private void registName(String name) {
        if (name.length() < 3)
            return;
        if (this.nameList.contains(name))
            return;
        this.nameList.addElement(name);
        this.nameMap.put(name, new Integer(this.nameList.size()));

    }

    private void registPrefix(String[] ns) throws IOException {
        if (!this.containnamespace(ns)) {
            this.prefixMap.addElement(ns);
        }
        int indexed = (ns[0].length() == 0) ? NONE_MASK : INDEXED_MASK;
        this.out.write(indexed | PREFIXMAPPING_TAG);
        if (indexed == INDEXED_MASK) {
            this.out.write(toVariableLengthNumber(getPrefixIndexOf(ns[0])));
            System.out.println("ns index : " + getPrefixIndexOf(ns[0]));
            this.out.write(toVariableLengthText(ns[0]));
            System.out.print(ns[0] + ",");
            this.out.write(TEXT_TERMINATOR);
        }
        this.out.write(toVariableLengthText(ns[1]));
        System.out.println(ns[1]);
        this.out.write(TEXT_TERMINATOR);
    }

    private boolean containnamespace(String[] ns) {
        if (ns[0].equals(this.getPrefixbyUri(ns[1])))
            return true;
        return false;
    }

    private String getPrefixbyUri(String uri) {
        if (uri == null)
            return null; // uriを持つprefixを返す
        Enumeration e = this.prefixMap.elements();
        while (e.hasMoreElements()) {
            String[] str = (String[]) e.nextElement();
            if (str[1].equals(uri)) {
                return str[0];
            }
        }
        return null;
    }

    protected void writeName(String Name) throws IOException {
        int index = this.getNameIndexOf(Name);
        if (index > 0) {
            this.out.write(this.toVariableLengthNumber(index));
            System.out.println("write name: " + index + " as " + Name);
        } else {
            this.out.write(this.toVariableLengthText(Name));
            this.out.write(TEXT_TERMINATOR);
            System.out.println("write name: " + Name);
        }
    }

    protected void writeValue(String value) throws IOException {
        this.out.write(this.toVariableLengthText(value));
        this.out.write(TEXT_TERMINATOR);
        System.out.println("write value: " + value);
    }

    void writeStartEmptyTag(boolean empty, String Prefix, String Name,
            Enumeration attributes) throws IOException {
        System.out.println("write start tag...");
        this.writeTagHeader(empty, Prefix, Name);
        this.writeName(Name);
        if (attributes != null) {
            while (attributes.hasMoreElements()) {
                String[] str = (String[]) attributes.nextElement();
                this.registName(str[1]);
                str[0] = this.getPrefixbyUri(str[0]);
                this.writeAttribute(str);
            }
        }

    }

    /*
     * void writeStartTag(String Prefix,String Name,Enumeration attributes)
     * throws IOException { System.out.println("write start tag...");
     * writeTagHeader(false,Prefix,Name); writeName(Name); if(attributes!=null){
     * while(attributes.hasMoreElements()){ String[]
     * str=(String[])attributes.nextElement(); String
     * p=BinaryXMLSerializer.this.getPrefixbyUri(str[0]); str[0]=p;
     * writeAttribute(str); } } } void writeEmptyTag(String Prefix,String
     * Name,Enumeration attributes) throws IOException {
     * System.out.println("write start tag...");
     * writeTagHeader(true,Prefix,Name); writeName(Name); if(attributes!=null){
     * while(attributes.hasMoreElements()){ String[]
     * str=(String[])attributes.nextElement(); String
     * p=BinaryXMLSerializer.this.getPrefixbyUri(str[0]); str[0]=p;
     * writeAttribute(str); } } }
     */

    private void writeTagHeader(boolean empty, String Prefix, String Name)
            throws IOException {
        int n = (Prefix == null || Prefix.length() == 0) ? 0x00 : NS_MASK;
        int i = (this.nameMap.containsKey(Name)) ? 0x00 : INDEXED_MASK;
        int e = (!empty) ? ELEMENT_START_TAG : EMPTY_ELEMENT_TAG;
        this.out.write(n | i | e);
        System.out.println("write header: " + Integer.toHexString(n | i | e));
        this.writeNSIndex(Prefix);
    }

    protected void writeNSIndex(String Prefix) throws IOException {
        if (Prefix == null || Prefix.length() == 0)
            return;
        int index = getPrefixIndexOf(Prefix);
        this.out.write(this.toVariableLengthNumber(index));
        System.out.println("write ns: " + index + " as " + Prefix);
    }

    void writeAttribute(String[] attribute) throws IOException {
        int n = (attribute[0] == null || attribute[0].length() == 0) ? 0x00
                : NS_MASK;
        int i = (this.nameMap.containsKey(attribute[1])) ? 0x00 : INDEXED_MASK;
        this.out.write(ATTRIBUTE_TAG | n | i);

        System.out.println("write attribute ...");
        System.out.println("write header: "
                + Integer.toHexString(ATTRIBUTE_TAG | n | i));
        this.writeNSIndex(attribute[0]);
        this.writeName(attribute[1]);
        this.writeValue(attribute[2]);
    }
}