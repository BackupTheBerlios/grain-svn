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
 * Created on 2005/02/04
 *
 */
package jp.haw.grain.sprout;

import jp.haw.grain.xforms.FormDocument;
import jp.haw.grain.xforms.InstanceElement;
import jp.haw.grain.xforms.SubmissionElement;

/**
 * sproutアプリケーションのインターフェース
 * 
 * @version $Id: SproutApp.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public interface SproutApp {
	void goToDefaultForm();
	void saveCurrentForm();
	void storeBasicPref();
	void loadBasicPref();
	void openBasicPrefDialog();
	void closeBasicPrefDialog();
	void openApplicationMenu();
	void closeApplicationMenu();
	void exitApplication();
	FormView createFormView(FormDocument doc);
	String getDefaultFormUrl();
	void setDefaultFormUrl(String url);
	int getDefaultFormLocation();
	void setDefaultFormLocation(int location);
	FormDocumentSerializeOperation createSubmissionOperation(SubmissionElement element);
	FormDocumentSerializeOperation createExternalInstanceLoadOperation(InstanceElement element);
//	void execOperation(FormDocumentSerializeOperation ope) throws Exception;
    /**
     * @param eventType
     */
    void showMessage(String eventType);
}