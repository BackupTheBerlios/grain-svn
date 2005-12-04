/*
 * EventManager.java : XMLEvents event listener interface.
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
 * Created on 2004/11/30
 *
 */
package com.hp.hpl.sparta;

/**
 * @version $Id$
 * @author Go Takahashi
 * @author Ryan Bayhonan
 */
public class EventManager {

	private Document doc_;

	public EventManager(Document doc) {
		doc_ = doc;
	}

    // 2004.12.28 HAW-Ryan commented throws DOMException
	public void nofifyEvent(Event event) /*throws DOMException*/ {
		if (event.getTarget() == null || event.getTarget().getOwnerDocument() != doc_) {
			//throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "wrong document");
			throw new ParseException(ParseException.WRONG_DOCUMENT_ERR, "wrong document");
		}
		
	}

}
