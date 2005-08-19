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
 * Created on 2004/12/17
 *
 */
package jp.haw.grain.xforms;

import java.io.IOException;
import java.util.Vector;

import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.ParseException;

/**
 * XForms‚Ìmodel—v‘f
 * 
 * @version $Id: ModelElement.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class ModelElement extends XFormsElement {

	public static final String TAG_NAME = "model";
	
	private Vector instanceDocs_ = new Vector();

	/**
	 * @param tagName
	 */
	public ModelElement() {
		super(TAG_NAME);
	}
	
	public InstanceDocument getInstanceDocument() {
		// TODO must returns indexed instance 
		return ((InstanceElement)xpathSelectElement(InstanceElement.TAG_NAME)).getInstanceData();
	}
	
	public int getInstanceDocumentSize() {
		return instanceDocs_.size();
	}
	
	/* (non-Javadoc)
	 * @see com.hp.hpl.sparta.Element#performDefaultAction(com.hp.hpl.sparta.Event)
	 */
	protected void performDefaultAction(String eventType) {
		System.out.println("handle default action : " + eventType);
		try {
			if (eventType.equals("xforms-model-constract")) {
				Processor.getInstance().processModelConstract(this);
			} else if (eventType.equals("xforms-model-constract-done")) {
				Processor.getInstance().processModelConstractDone(this);
			} else if (eventType.equals("xforms-ready")) {
				Processor.getInstance().processReady(this);
			} else if (eventType.equals("xforms-destract")) {
				Processor.getInstance().processDestract(this);
			} else if (eventType.equals("xforms-refresh")) {
				Processor.getInstance().processRefresh(this);
			} else if (eventType.equals("xforms-revalidate")) {
				Processor.getInstance().processRevalidate(this);
			} else if (eventType.equals("xforms-submit-error")) {
			    Processor.getInstance().notifyError(eventType);
            }
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public Node getInstanceNode(String bindingExpr) throws ParseException {
		if (getInstanceDocumentSize() == 0) return null;
		System.out.println("get Instance node : " + bindingExpr);
		if (bindingExpr.charAt(0) == '/') {
			return getInstanceDocument().xpathSelectElement(bindingExpr);
		} else {
			return getInstanceDocument().getDocumentElement().xpathSelectElement(bindingExpr);
		}
	}

	/**
	 * @return
	 */
	public Node getInitialContextNode() {
		return getInstanceDocument().getDocumentElement();
	}
}
