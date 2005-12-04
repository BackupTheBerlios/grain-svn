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

import com.nttdocomo.ui.AnchorButton;
import com.nttdocomo.ui.Component;
import com.nttdocomo.ui.ComponentListener;
import com.nttdocomo.ui.Font;
import com.nttdocomo.ui.HTMLLayout;
import com.nttdocomo.ui.Label;
import com.nttdocomo.ui.Panel;

/**
 * アプリケーションメニューパネル
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class AppMenuPanel extends Panel implements ComponentListener {
	
	private static final Font BASE_FONT = Font.getFont(Font.FACE_SYSTEM | Font.SIZE_SMALL | Font.STYLE_PLAIN);

	private AnchorButton _goToDefaultForm;
	private AnchorButton _saveCurrentForm;
	private AnchorButton _basicPreference;
	private AnchorButton _exitApp;
	private AnchorButton _closeAppMenu;	
	private HTMLLayout _layout = new HTMLLayout();
	private SproutApp _app;

	private boolean _closed;
	
	public AppMenuPanel(SproutApp app) {
		setComponentListener(this);	
		_app = app;

		Font old = Font.getDefaultFont();
		try {
			Font.setDefaultFont(BASE_FONT);
			setLayoutManager(_layout);
			setTitle("アプリケーションメニュー");
			_closeAppMenu = new AnchorButton("メニューを閉じる");
			_goToDefaultForm = new AnchorButton("デフォルトフォームを表示" );
			_saveCurrentForm = new AnchorButton("現在の表示フォームを保存");
			_basicPreference = new AnchorButton("基本設定...");
			_exitApp = new AnchorButton("アプリを終了");
			_layout.begin(HTMLLayout.LEFT);
			add(new Label(String.valueOf((char)0xE6EB)));
			add(_closeAppMenu);
			_layout.br();
			add(new Label(String.valueOf((char)0xE6E2)));
			add(_goToDefaultForm);
			_layout.br();
			add(new Label(String.valueOf((char)0xE6E3)));
			add(_saveCurrentForm);
			_layout.br();
			add(new Label(String.valueOf((char)0xE6E4)));
			add(_basicPreference);
			_layout.br();
			add(new Label(String.valueOf((char)0xE6EA)));
			add(_exitApp);
			_layout.end();
		} finally {
			Font.setDefaultFont(old);
		}
	}
	
	public void init() {
		// TODO use resource bundle
	}
	
	/* (non-Javadoc)
	 * @see com.nttdocomo.ui.ComponentListener#componentAction(com.nttdocomo.ui.Component, int, int)
	 */
	public void componentAction(Component component, int type, int param) {
		if (type == ComponentListener.BUTTON_PRESSED) {
			if (component == _goToDefaultForm) {
				_app.goToDefaultForm();
				_app.closeApplicationMenu();
			} else if (component == _saveCurrentForm) {
				_app.saveCurrentForm();
				_app.closeApplicationMenu();
			} else if (component == _basicPreference) {
				_app.openBasicPrefDialog();
			} else if (component == _closeAppMenu) {
				_app.closeApplicationMenu();
			} else if (component == _exitApp) {
				_app.exitApplication();
			}
		}
	}

}
