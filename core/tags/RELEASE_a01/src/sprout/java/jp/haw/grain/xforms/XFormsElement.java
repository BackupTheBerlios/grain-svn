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
 * Created on 2004/12/18
 * 
 */
package jp.haw.grain.xforms;

import java.util.Enumeration;
import java.util.Hashtable;

import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Text;

/**
 * 
 * @version $Id: XFormsElement.java 3385 2005-08-18 22:12:13Z go $
 * @author go
 */
public class XFormsElement extends RenderableElement {

	public static final String XFORMS_NS_URI = "http://www.w3.org/2002/xforms";
    public static final String GRAIN_NS_URI = "http://grain.jp/grain";
    
	protected Node _contextNode;
    private Hashtable style = new Hashtable();


	/**
	 * @param tagName
	 */
	public XFormsElement(String tagName) {
		super(tagName);
	}

	public boolean isBindingElement() {
		// TODO only the element that is explicitly allowed to have a binding expression attribute
		return true; 			
	}

	public boolean isBinded() {
		if (getAttribute("ref") != null || getAttribute("nodeset") != null || getAttribute("") != null) return true;
		return false;
	}
	
	public BindElement getBindElement() {
		if (getAttribute("bind") == null) return null;
		return (BindElement)((FormDocument)getOwnerDocument()).getElementById(getAttribute("bind"));
	}
	
	public Node getBindingNode() {
		return calcXPath(getAttribute("ref"));
	}
	
	public Node calcXPath(String expr) throws ParseException {
		if (expr == null) return null;
		System.out.println("original ref : " + expr);	
		System.out.println("context node = " + _contextNode);
		System.out.println("context instance = " + ((InstanceDocument)_contextNode.getOwnerDocument()).getOwner().getAttribute("id"));
		System.out.println("context node tag = " + ((Element)_contextNode).getTagName());
		if (expr.charAt(0) == '/') {;
			System.out.println("absolute");
			return _contextNode.getOwnerDocument().xpathSelectElement(expr);
		} else {
			Node contextNode = _contextNode;
			if (expr.startsWith("instance('")) {
				int end = expr.indexOf("')", 10);
				if (end < -1) throw new ParseException("invalid instance function : closure mismatch");
				String id = expr.substring(10, end);
				FormDocument doc = (FormDocument)getOwnerDocument();
				Element e = doc.getElementById(id);
				if (!(e instanceof InstanceElement)) throw new ParseException("invalid instance function : id indicates non instance element");
				if (expr.length() > end + 2) {
					if (expr.charAt(end + 2) != '/') throw new ParseException("invalid instaince function : '/' was expected");
					expr = expr.substring(end + 3);
				} else {
					expr = ".";
				}
				contextNode = ((InstanceElement)e).getInstanceData().getDocumentElement();
			}
			System.out.println("ref : " + expr);			
			Node node = contextNode.xpathSelectElement(expr);
			System.out.println("node : " + (node != null ? ((Element)node).getTagName() : null));
			return node;
		}
	}
	
	public Enumeration getBindingNodeset() throws ParseException {
		String ref = getAttribute("nodeset");
		if (ref == null) return null;
		if (ref.charAt(0) == '/') {
			return _contextNode.getOwnerDocument().xpathSelectElements(ref);
		} else {
			return _contextNode.xpathSelectElements(ref);
		}
	}

	/**
	 * @param element
	 * @return
	 */
	public boolean isRelatedTo(InstanceDocument instance) {
		return instance == (InstanceDocument)_contextNode.getOwnerDocument();
	}		
/*
	public void evaluateContext() throws ParseException {
		if (getAttribute("model") == null && getAttribute("ref") == null && getAttribute("nodeset") == null) return;
		// TODO support for 'bind attribute'
		XFormsElement immediatelyEnclosingElement = null;
		if (getAttribute("model") != null) {
			// 7.4-3-a: context model which 'model' atttibeute determines is used
			String id = getAttribute("model");
			FormDocument doc = (FormDocument)getOwnerDocument();
			ModelElement contextModel = (ModelElement)doc.getElementById(id);
			evaluateContextWithModel(contextModel);
//			bindTo(contextModel);
			// reset the context node to the top-level element node
		} else {
			immediatelyEnclosingElement = getImmediatelyEnclosingElement();
			if (immediatelyEnclosingElement == null) {
				// 7.4-3-c: The first model in document order is used
				FormDocument doc = (FormDocument)getOwnerDocument();
				ModelElement contextModel = (ModelElement)doc.xpathSelectElement("//model");
//				bindTo(contextModel);			
				// 7.4-1: 'outermost element' uses the top-level element node
				evaluateContextWithModel(contextModel);
			} else {
				// 7.4.3-b: The context model of immediately enclosing element is used
				// 7.4-2: 'non-outermost element' uses 
				//  first node of the binding expression of the immediately enclosing element
				_contextNode = immediatelyEnclosingElement.getBindingNode();
//				bindTo(((InstanceDocument)_contextNode.getOwnerDocument()).getOwnerModel());
			}
		}
	}
*/
		
//	private XFormsElement getImmediatelyEnclosingElement() {
//		for (Element e = getParentNode(); e != null; e = e.getParentNode()) {
//			if (!(e instanceof XFormsElement)) continue;
//			// only the element that is explicitly allowed to have a binding expression attribute
//			return (XFormsElement)e;
//		}
//		return null;
//	}
		
	public String getBindingSimpleContent() {
		try {
			Node node = getBindingNode();
			System.out.println("binding node : " + node);
			if (node == null) return null;
			if (node instanceof Element) {
				return getFirstChildText((Element)node);
			} else if (node instanceof Text) {
				return ((Text)node).getData();	
			}
		} catch (ParseException e) {
			// TODO improve exception handling
		}
		return null;
	}
	
	public void setBindingSimpleContent(String text) {
		try {
			Node node = getBindingNode();
			if (node == null) return;
			if (node instanceof Element) {
				setFirstChildText((Element)node, text);
			} else if (node instanceof Text) {
				node.getParentNode().replaceChild(new Text(text), node);
			}
		} catch (ParseException e) {
		}	
	}
	
	public String getChildText() {
		for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n instanceof Text) return ((Text)n).getData();
		} 
		return null;
	}
	
	public static String getFirstChildText(Element e) {
		for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n instanceof Text) return ((Text)n).getData();
		} 
		return "";	
	}
	
	public static void setFirstChildText(Element e, String text) {
		for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n instanceof Text) {
				e.replaceChild(new Text(text), n);
				return;
			} 
		} 
		insertFirstChildText(e, text);
	}
	
	public static void insertFirstChildText(Element e, String text) {
		Node prev = new Text(text);
		Node next = null;
		for (Node cur = e.getFirstChild(); cur != null; ) {
			if (prev instanceof Text) {
				e.replaceChild((Text)prev, cur);
			} else {
				e.replaceChild((Element)prev, cur);				
			}
			next = prev.getNextSibling();
			prev = cur;
			cur = next;
		}
		e.appendChild(prev);
	}
	
	protected void performDefaultAction(String eventType) {
	}

	protected String getCanonicalUri(String attributeName) {
		String docUri = ((FormDocument)getOwnerDocument()).getUri();
		String action = getAttribute(attributeName);
		if (action.startsWith("http://")) return action;
		if (action.startsWith("/")) return action;
		return docUri.substring(0, docUri.lastIndexOf('/') + 1) + action;
	}

	/**
	 * @param contextNode
	 */
	public void setContextNode(Node contextNode) {
		_contextNode = contextNode;
	}
	
	public ModelElement getContextModel() {
		InstanceDocument instance = (InstanceDocument)_contextNode.getOwnerDocument();
		return (ModelElement)instance.getOwner().getParentNode();
	}
    
    public void setStyle(String name, String value) {
        this.style.put(name, value);
    }

    public String getStyle(String name) {
        return (String)this.style.get(name);
    }
    
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
