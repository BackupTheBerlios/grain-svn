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
package jp.haw.grain.doja;

import jp.haw.grain.sprout.SproutApp;

import com.nttdocomo.ui.Button;
import com.nttdocomo.ui.Component;
import com.nttdocomo.ui.ComponentListener;
import com.nttdocomo.ui.Font;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.HTMLLayout;
import com.nttdocomo.ui.Label;
import com.nttdocomo.ui.ListBox;
import com.nttdocomo.ui.Panel;
import com.nttdocomo.ui.TextBox;


/**
 * 基本設定ダイアログ用パネル<br>
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class BasicPrefPanel extends Panel implements ComponentListener {
	
	public static final Font BASE_FONT = Font.getFont(Font.FACE_SYSTEM | Font.SIZE_SMALL | Font.STYLE_PLAIN);
	public static final Font SMALL_FONT = Font.getFont(Font.FACE_SYSTEM | Font.STYLE_PLAIN);
	private static String dummyString;
	
	static {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < 256; ++i) {
			buf.append(' ');
		}
		dummyString = buf.toString();
	}

	private Button _okBtn;
	private Button _cancelBtn;
	private Label _urlLabel;
	private TextBox _urlText;
	private ListBox _defaultLocationList;
	private HTMLLayout _layout = new HTMLLayout();
	private SproutApp _app;

	private boolean _closed;
	
	public BasicPrefPanel(SproutApp app) {
		setComponentListener(this);	
		_app = app;
		// TODO use resource bundle
		setLayoutManager(_layout);
		setTitle("基本設定");

		int width = getWidth();
		int offset = 0;
		
		Font old = Font.getDefaultFont();
		try {
			Font.setDefaultFont(SMALL_FONT);
			_urlLabel = new Label("URL：");
			int col = SMALL_FONT.getLineBreak(dummyString, 0, dummyString.length(), width);
			_urlText = new TextBox("", col, 3, TextBox.DISPLAY_ANY);
			_urlText.setInputMode(TextBox.ALPHA);
			
			Font.setDefaultFont(BASE_FONT);
			_defaultLocationList = new ListBox(ListBox.RADIO_BUTTON, 2);
			_defaultLocationList.append("ローカル保存領域から取得");
			_defaultLocationList.append("HTTP経由で取得");
			_defaultLocationList.setSize(width, _defaultLocationList.getHeight());
			
			_okBtn = new Button("OK");		
			_cancelBtn = new Button("キャンセル");		
					
			_layout.begin(HTMLLayout.LEFT);
			add(new Label("デフォルトフォームの取得："));
			_layout.br();
			add(_defaultLocationList);
			_layout.br();
			add(_urlLabel);
			_layout.br();
			add(_urlText);
			_layout.br();
			_layout.br();
			add(_okBtn);
			add(_cancelBtn);
			_layout.end();
		} finally {
			Font.setDefaultFont(old);
		}
	}
	
	public void init() {
		_urlText.setText(_app.getDefaultFormUrl());
		_defaultLocationList.select(_app.getDefaultFormLocation());
		updateState();
	}
	
	/* (non-Javadoc)
	 * @see com.nttdocomo.ui.ComponentListener#componentAction(com.nttdocomo.ui.Component, int, int)
	 */
	public void componentAction(Component component, int type, int param) {
		if (type == ComponentListener.BUTTON_PRESSED) {
			if (component == _okBtn) {
				if (!validate()) return;
				_app.setDefaultFormUrl(_urlText.getText());
				_app.setDefaultFormLocation(_defaultLocationList.getSelectedIndex());
				_app.storeBasicPref();
				_app.closeBasicPrefDialog();
			} else if (component == _cancelBtn) {
				_app.closeBasicPrefDialog();
			}
		} else if (type == ComponentListener.SELECTION_CHANGED) {
			if (component == _defaultLocationList) {
				updateState();
			}
		}
	}

	private boolean validate() {
		ValidationList list = new ValidationList();
		if (_defaultLocationList.getSelectedIndex() == 1) {
			if (_urlText.getText().length() == 0) list.addError("HTTP経由で取得を選択した場合には、URLを必ず入力してください。");
		}
		if (list.hasError()) GrainApp.showWarningDialog("入力エラー", list);
		return !list.hasError();
	}
	
	private void updateState() {
		if (_defaultLocationList.getSelectedIndex() == 0) {
			_urlLabel.setForeground(Graphics.getColorOfName(Graphics.GRAY));
			_urlText.setEnabled(false);
		} else {
			_urlLabel.setForeground(Graphics.getColorOfName(Graphics.BLACK));
			_urlText.setEnabled(true);
		}
	}
}
