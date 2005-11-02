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

import com.hp.hpl.sparta.Node;

/**
 * XForms‚Ìbind—v‘f
 * 
 * @version $Id: BindElement.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class BindElement extends XFormsElement {

	public static final String TAG_NAME = "bind";
	
	private boolean _relevant = true;
	
	/**
	 * @param tagName
	 */
	public BindElement() {
		super(TAG_NAME);
	}
		
	public boolean isRelevant() {
		return _relevant;
	}
	
	/**
	 * 
	 */
	public void revalidate() {
		System.out.println("revalidate bind element : " + getAttribute("relevant"));
		_relevant = true;
		String relevant = getAttribute("relevant");
		if (relevant == null) return;
		Node node = calcXPath(relevant);
		if (node == null) _relevant = false;
		System.out.println("revalidate result : " + _relevant);		
	}
}
