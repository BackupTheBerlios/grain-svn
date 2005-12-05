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
 * Created on 2004/12/02 14:11:18
 * 
 */
package jp.grain.xforms;

import java.io.IOException;

import jp.grain.sprout.xml.BinaryXMLParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Text;

/**
 * フォームドキュメントを生成するBuilder
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FormDocumentBuilder {

	public static final String XML_EVENTS = "http://www.w3.org/2001/xml-events";
    public static final String XHTML = "http://www.w3.org/1999/xhtml";
	private XmlPullParser parser;
    private Element current;
    private FormDocument doc  = null;
    
    /**
     * @param parser フォームドキュメントの生成に利用するparser
     * @param uri フォームドキュメントを識別するURI
     */
    public FormDocumentBuilder(XmlPullParser parser, String uri) {
        this.parser = parser;
    	this.doc = new FormDocument(uri);
    }

    /**
     * 生成されたフォームドキュメントを取得する。
     * 基本的にbuildメソッドの呼び出し後に利用する。
     * @return 生成されたフォームドキュメント。buildメソッドが呼び出されていない場合はrootElementはnull。
     */
    public FormDocument getDocument(){
        return doc;
    }
    
    /**
     * フォームドキュメントを生成する。
     * @throws IOException フォームドキュメントの生成中に入出力エラーが発生した場合。
     * @throws XmlPullParserException XMLのパース中に構文エラーなどが発生した場合。
     */
    public void build() throws IOException, XmlPullParserException {
        for (int et = this.parser.getEventType(); et != BinaryXMLParser.END_DOCUMENT; et = this.parser.next()) {
            switch (et) {
                case BinaryXMLParser.START_TAG:
                    Element element = createElement();
                    if (this.current != null) {
                        this.current.appendChild(element);
                        String event = element.getAttribute(XML_EVENTS, "event");
                        if (event != null) { 
                            // TODO oveserver & target & handler
                            String capture = element.getAttribute(XML_EVENTS, "capture");
                            boolean useCapture = (capture != null && capture.equals("capture"));
                            this.current.addEventListener(event, element, useCapture);
                        }
                    } else {
                        this.doc.setDocumentElement(element);
                    }
                    this.current = element;
                    break;
                case BinaryXMLParser.END_TAG:
                    this.current = this.current.getParentNode();
                    break;
                case BinaryXMLParser.TEXT:
                    if (this.current == null) throw new XmlPullParserException("there is no root element but text is found");
                    Text text = new Text(this.parser.getText());
                    this.current.appendChild(text);
                    break;
            }
        }
    }

	private Element createElement() throws XmlPullParserException {
				
		Element element = null;
        String name = this.parser.getName();
		if (XFormsElement.XFORMS_NS_URI.equals(this.parser.getNamespace())) {
			if (ModelElement.TAG_NAME.equals(name)) {
				element = new ModelElement();
			} else if (InstanceElement.TAG_NAME.equals(name)) {
				element = new InstanceElement();
			} else if (SubmissionElement.TAG_NAME.equals(name)) {
				element = new SubmissionElement();
			} else if (BindElement.TAG_NAME.equals(name)) {
				element = new BindElement();				
			} else if (name.equals("input") || name.equals("select1") || name.equals("trigger") || name.equals("submit") || name.equals("output")) {
				element = new FormControlElement(name);
			} else if (name.equals("switch") || name.equals("case") || name.equals("toggle")) {
				element = new SwitchModuleElement(name);
			} else if (name.equals("action") || name.equals("setvalue") || name.equals("setvalue") || name.equals("send")) {
				element = new ActionElement(name);
			} else {
				element = new XFormsElement(name);
			}
		} else if (XHTML.equals(this.parser.getNamespace())) {
            element = new XHTMLElement(name);
        } else {
			element = new Element(name);
		}
        		
		element.setPrefix(this.parser.getPrefix());
		element.gsNamespaceUri(this.parser.getNamespace());
        
        for (int i = 0; i < this.parser.getAttributeCount(); ++i) {
            String attrName = this.parser.getAttributeName(i);
            String attrNS = this.parser.getAttributeNamespace(i);
            String attrValue = this.parser.getAttributeValue(i);
            element.setAttribute(attrNS, attrName, attrValue);
            
            // TODO check attribure type is xsd:id
            if (attrName.equals("id")) {
                doc.registElement(attrValue, element);
            }
        }
        
        int nsCount = this.parser.getNamespaceCount(this.parser.getDepth());
        int nsParentCount = this.parser.getNamespaceCount(this.parser.getDepth() - 1);
        if (nsCount > nsParentCount) {
            for (int i = nsParentCount; i < nsCount; ++i) {
                String prefix = this.parser.getNamespacePrefix(i);
                String uri = this.parser.getNamespaceUri(i);
                element.setNamespace((prefix == null) ? "" : prefix, uri);
            }
        }
        
        if (element instanceof RenderableElement) {
            RenderableElement re = (RenderableElement)element;
            re.applyStyles();
        }

        
        return element;
    }

}