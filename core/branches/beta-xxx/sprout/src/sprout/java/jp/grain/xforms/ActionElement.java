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

import com.hp.hpl.sparta.Event;

/**
 * XFormsのactionモジュール
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class ActionElement extends XFormsElement implements ActionModule {

	/**
	 * @param tagName
	 */
	public ActionElement(String tagName) {
		super(tagName);
	}
		
	public void handleEvent(Event event) {
		System.out.println("action element : handle event = " + event.getType());
		performAction();
	}
	
	protected void performDefaultAction(String eventType) {
	}

	public void performAction() {
		if (getTagName().equals("action")) {
			Processor.getInstance().processAction(this);
		} else if (getTagName().equals("setvalue")) {
			Processor.getInstance().processSetValue(this);
		} else if (getTagName().equals("send")) {
			Processor.getInstance().processSend(this);
		}
	}
}
