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
 * Created on 2005/02/09
 *
 */
package jp.haw.grain.xforms;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;

/**
 * XForms‚Ìinstance—v‘f
 * 
 * @version $Id: InstanceElement.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class InstanceElement extends XFormsElement {

	public static final String TAG_NAME = "instance";
	private InstanceDocument _instance;
	
	/**
	 * @param tagName
	 */
	public InstanceElement() {
		super(TAG_NAME);
	}
	
	public void init() {
		// TODO clone namespases
		_instance = new InstanceDocument(this);
	}

	public void loadInlineInstanceData() {
		_instance.setDocumentElement((Element)xpathSelectElement("*").clone());
	}

	public void loadExternalInstanceData(Document external) {
		_instance.setDocumentElement(external.getDocumentElement());
	}
	
	public InstanceDocument getInstanceData() {
		return _instance;
	}	
	
	public boolean isInitialized() {
		return _instance != null;
	}
	
	public String getCanonicalSrcUri() {
		return getCanonicalUri("src");
	}
}
