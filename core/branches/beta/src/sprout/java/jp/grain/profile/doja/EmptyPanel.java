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
 * Created on 2004/11/21
 * 
 */
package jp.grain.profile.doja;

import jp.grain.sprout.SproutApp;

import com.nttdocomo.ui.Frame;
import com.nttdocomo.ui.IApplication;
import com.nttdocomo.ui.Panel;
import com.nttdocomo.ui.SoftKeyListener;

/**
 * TODO FormViewImplÇ…ìùçá
 * ì‡óeÇ™ãÛÇÃÉpÉlÉã
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class EmptyPanel extends Panel implements SoftKeyListener {

	public EmptyPanel() {
		setSoftLabel(Frame.SOFT_KEY_2, "MENU");
		setSoftKeyListener(this);
	}

	public void softKeyPressed(int arg0) {
		// Nothing to do
	}

	public void softKeyReleased(int softKey) {
		if (softKey == Frame.SOFT_KEY_2) {
			((SproutApp)IApplication.getCurrentApp()).openApplicationMenu();
		}
	}

}
