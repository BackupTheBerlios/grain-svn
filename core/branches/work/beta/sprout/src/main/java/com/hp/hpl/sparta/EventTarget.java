/*
 * EventTarget.java : An interface for XMLEvents observer.
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

//import com.hp.hpl.sparta.DOMException;    // not used

/**
 * @version $Id$
 * @author Go Takahashi
 * @author Ryan Bayhonan
 */
public interface EventTarget {
	void addEventListener(String type, EventListener listener, boolean useCapture);
	void removeEventListener(String type, EventListener listener, boolean useCapture);
    // 2004.12.28 HAW-Ryan commented throws DOMException	
	boolean dispatchEvent(Event event) /*throws DOMException*/;
}
