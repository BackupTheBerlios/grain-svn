/*
 * Event.java : An XMLEvent.
 * 
 * Copyright (C) 2004-2005 HAW International Inc.
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
 * Created on 2004/11/28
 *
 */
package com.hp.hpl.sparta;

import com.hp.hpl.sparta.Element;

/**
 * @version $Id$
 * @author Go Takahashi
 */
public class Event {
	public static final int IGNORE = 0;    // added by HAW-Ryan 2004.12.22

	public static final int CAPTURING_PHASE = 1;
	public static final int AT_TARGET = 2;
	public static final int BUBBLING_PHASE = 3;
	
	private String type_;
	private Element target_;
	private Element currentTarget_;
	private int eventPhase_;
	private boolean bubbles_;
	private boolean cancelable_;
	private boolean propagationStoped_ = false;
	private boolean preventDefault_ = false;
	
	public Event(String type, boolean bubbles, boolean cancelable) {
		type_ = type;
		bubbles_ = bubbles;
		cancelable_ = cancelable;
	}
	
	public void stopPropagation() {
		propagationStoped_ = true;
	}

	public void preventDefault() {
		if (cancelable_) preventDefault_ = true;
	}
	
	boolean isPropagationStoped() {
		return propagationStoped_;
	}
	
	boolean isPreventDefault() {
		return preventDefault_;
	}
	
	public boolean isBubbles() {
		return bubbles_;
	}

	public boolean isCancelable() {
		return cancelable_;
	}

	public Element getCurrentTarget() {
		return currentTarget_;
	}

	void setCurrentTarget(Element element) {
		currentTarget_ = element;
	}

    // Begin modified by HAW-Ryan 2004.12.22
	//public int getEventPhase() {
	//	return eventPhase_;
	//}
    //
	//void setEventPhase(int i) {
	//	eventPhase_ = i;
	//}
	
	public int gsEventPhase( int i )
	{
		return ( i != IGNORE ) ? eventPhase_ = i : eventPhase_;
	}
	// End modified by HAW-Ryan 2004.12.22

	public Element getTarget() {
		return target_;
	}

	void setTarget(Element element) {
		target_ = element;
	}

	public String getType() {
		return type_;
	}

}
