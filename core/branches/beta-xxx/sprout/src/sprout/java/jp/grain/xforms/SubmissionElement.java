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
package jp.grain.xforms;

import com.hp.hpl.sparta.ParseException;

/**
 * XFormsÇÃëóêMÉÇÉWÉÖÅ[Éã
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class SubmissionElement extends XFormsElement {

	public static final String TAG_NAME = "submission";
	/**
	 * @param tagName
	 */
	public SubmissionElement() {
		super(TAG_NAME);
	}
	
	public void evaluateContext() throws ParseException {
	}
		
	protected void performDefaultAction(String eventType) {
		try {
			if (eventType.equals("xforms-submit")) {
				Processor.getInstance().processSubmit(this);
			} else if (eventType.equals("xforms-submit-done")) {
				Processor.getInstance().processSubmitDone(this);
			}		
		} catch (ParseException e) {
			// FIXME nothify errors
			e.printStackTrace();
		}
	}
	
//	public Node getBindingNode() throws ParseException {
//		if (!(getParentNode() instanceof ModelElement)) throw new ParseException("parent is not ModelElement");
//		ModelElement model = (ModelElement)getParentNode();
//		String bindingExpr = "/*";
//		if (isBindingElement()) bindingExpr = getAttribute("ref");
//		return model.getInstanceNode(bindingExpr);
//	}
//
//	public Enumeration getBindingNodeset() throws ParseException {
//		throw new ParseException("not supported");
//	}
	
	public ModelElement getParentModel() {
		if (!(getParentNode() instanceof ModelElement)) throw new ParseException("parent is not ModelElement");
		return (ModelElement)getParentNode();		
	}
	
	public String getCanonicalActionUri() {
		return getCanonicalUri("action");
	}
}
