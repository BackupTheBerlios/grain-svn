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
 * Created on 2004/12/05
 *
 */
package jp.haw.grain.xforms;

import java.util.Enumeration;
import java.util.Hashtable;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Event;
import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.ParseException;

/**
 * ÉtÉHÅ[ÉÄï∂èë
 * 
 * @version $Id: FormDocument.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class  FormDocument extends Document {
	
	private Hashtable indexedElements_ = new Hashtable();
	private boolean _initialized = false;
	private String _uri;
	
	public FormDocument(String uri) {
		_uri = uri;
	}
	
	public void init() throws Exception {
	}
	
	public void refresh() {
		// TODO not implemented yet.
	}
	
	public void destruct() throws Exception {
		for (Enumeration e = xpathSelectElements("//model"); e.hasMoreElements(); ) {
			Element model = (Element)e.nextElement();
			model.dispatchEvent(new Event("xforms-destruct", true, false));
		}		
	}

	public void registElement(String id, Element element) {
		indexedElements_.put(id, element);
	}
	
	public Element getElementById(String id) {
		return (Element)indexedElements_.get(id);	
	}
	
	public void evaluateContext() {
		evaluateContext(getDocumentElement(), null, null);
	}
	
	private void evaluateContext(Element target, ModelElement contextModel, Node contextNode) throws ParseException {

		// TODO support for 'bind attribute'
		if (target instanceof ModelElement) {
			
			contextModel = (ModelElement)target;
			contextNode = contextModel.getInitialContextNode();
			
		} else if (target.getAttribute("model") != null) {
			
			// 7.4-3-a: context model which 'model' atttibeute determines is used
			String id = target.getAttribute("model");
			contextModel = (ModelElement)getElementById(id);

			// reset the context node to the top-level element node
			contextNode = contextModel.getInitialContextNode();
		}
		if (contextModel == null) {

			// 7.4-3-c: The first model in document order is used
			contextModel = getInitialContextModel();

			// 7.4-1: 'outermost element' uses the top-level element node
			contextNode = contextModel.getInitialContextNode();
		}
		
		if (target instanceof XFormsElement) 
			((XFormsElement)target).setContextNode(contextNode);

			System.out.println("evaluete : context node[" + target.getTagName() + "] = " + ((InstanceDocument)contextNode.getOwnerDocument()).getOwner().getAttribute("id"));
		
		for (Node n = target.getFirstChild(); n != null; n = n.getNextSibling()) {
			
			if (!(n instanceof Element)) continue;

			// 7.4.3-b: The context model of immediately enclosing element is used
			// 7.4-2: 'non-outermost element' uses 
			//  first node of the binding expression of the immediately enclosing element
			if (target instanceof XFormsElement) {
				Node bindingNode = ((XFormsElement)target).getBindingNode();
				if (bindingNode != null) {
					evaluateContext((Element)n, contextModel, bindingNode);
					continue;
				}
			}
			evaluateContext((Element)n, contextModel, contextNode);
		}
	}

	private ModelElement getInitialContextModel() {
		return (ModelElement)xpathSelectElement("//model");
	}
	
	public String getUri() {
		return _uri;
	}
}
