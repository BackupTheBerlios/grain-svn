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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import jp.grain.sprout.DocumentManager;
import jp.grain.sprout.Font;
import jp.grain.sprout.FormDocumentSerializeOperation;
import jp.grain.sprout.FormView;
import jp.grain.sprout.LayoutManager;
import jp.grain.sprout.SerializeOperation;
import jp.grain.sprout.SproutApp;
import jp.grain.xforms.FormDocument;
import jp.grain.xforms.InstanceElement;
import jp.grain.xforms.Processor;
import jp.grain.xforms.SubmissionElement;

import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.ParseException;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Frame;
import com.nttdocomo.ui.IApplication;

/**
 * Doja用のアプリケーション
 * 
 * @version $Id$
 * @author go
 */
public class GrainApp extends IApplication implements SproutApp {
	
	public static final int DEFAULT_FORM_LOC_LOCAL = 0;
	public static final int DEFAULT_FORM_LOC_HTTP = 1;
	public static final String BASIC_PREF_URI = "scratchpad:///0;pos=0";
	public static final String DEFAULT_FORM_URI = "scratchpad:///1;pos=0";
	
	private AppMenuPanel _appMenuDialog;
	private BasicPrefPanel _basicPrefDialog;
	private Processor _processor;
	private int _defaultFormLocation = DEFAULT_FORM_LOC_HTTP;
	private String _defaultFormUrl = "http://sphere2.haw.jp/grain/index.xml";
	private String _defaultContentType = "application/xml";
	
	/* (non-Javadoc)
	 * @see com.nttdocomo.ui.IApplication#start()
	 */
	public void start() {
		try {
			Processor.setup(this);
			_processor = Processor.getInstance();
            Font.setDefaultFont(new FontImpl(com.nttdocomo.ui.Font.getDefaultFont()));
			loadBasicPref();
			goToDefaultForm();
		} catch (Exception e) {
			showErrorDialog("初期化に失敗しました。", e);
		}
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.xforms.FormViewFactory#create(jp.haw.grain.xforms.FormDocument)
	 */
	public FormView createFormView(FormDocument doc) {
        FormViewImpl view = FormViewImpl.recycle();
        LayoutManager manager = new LayoutManager(doc, view);
        manager.layout();
        view.init();
		return view;
	}
	
	/**
	 * 
	 */
	public void goToDefaultForm() {
		try {
			FormDocumentSerializeOperation ope = null;
			if (_defaultFormLocation == DEFAULT_FORM_LOC_HTTP) {
				ope = createSubmissionOperation(null);
				DocumentManager.execSerializeOperation(ope);
			} else {
				ope = FormStoreOperation.createForLoad(DEFAULT_FORM_URI);
				DocumentManager.execSerializeOperation(ope);
			}
			_processor.initForm((FormDocument)ope.getFormDocuemnt());
		} catch (Exception e) {
			showErrorDialog("デフォルトフォームの取得に失敗しました。\n基本設定を確認してください。", e);
            _processor.initForm(null);
		}
	}

	/**
	 * 
	 */
	public void saveCurrentForm() {
		try {
			SerializeOperation ope = FormStoreOperation.createForSave(DEFAULT_FORM_URI, _processor.getCurrentFormDocument());
			DocumentManager.execSerializeOperation(ope);
		} catch (IOException e) {
			showErrorDialog("フォームの保存に失敗しました。", e);
		}
	}

	/**
	 * @param e
	 */
	public static void showErrorDialog(String msg, Throwable t) {
		t.printStackTrace();
		Dialog d = new Dialog(Dialog.DIALOG_ERROR, "エラー");
		d.setText(msg + "\n" + (t.getMessage() != null ? t.getMessage() : ""));
		d.show();
	}
	
	public static void showWarningDialog(String title, ValidationList list) {
		Dialog d = new Dialog(Dialog.DIALOG_WARNING, title);
		StringBuffer buf = new StringBuffer();
		for (Enumeration e = list.getErrors(); e.hasMoreElements();) {
			buf.append("・");
			buf.append(e.nextElement());
			buf.append("\n");
		}
		d.setText(buf.toString());
		d.show();		
	}

	/**
	 * 
	 */
	public void openBasicPrefDialog() {
		if (_basicPrefDialog == null) {
			_basicPrefDialog = new BasicPrefPanel(this);
		}
		_basicPrefDialog.init();
		Display.setCurrent(_basicPrefDialog);
	}
	
	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#closeApplicationPreferenceDialog()
	 */
	public void closeBasicPrefDialog() {
		Display.setCurrent(_appMenuDialog);
	}
	
	/**
	 * 
	 */
	public void openApplicationMenu() {
		if (_appMenuDialog == null) {
			_appMenuDialog = new AppMenuPanel(this);
			_appMenuDialog.init();
		}
		Display.setCurrent(_appMenuDialog);
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#closeApplicationMenu()
	 */
	public void closeApplicationMenu() {
		Frame f = (Frame)_processor.getCurrentFormView();
		if (f == null) f =new EmptyPanel(); 
		Display.setCurrent(f);
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#submit(java.lang.String, java.lang.String, com.hp.hpl.sparta.Node)
	 */
	public FormDocument submit(String uri, String contentType, Node node) throws ParseException, IOException {
		FormDocumentSerializeOperation ope = new FormSubmissionOperation(uri, contentType);
		DocumentManager.execSerializeOperation(ope);
		return (FormDocument)ope.getFormDocuemnt();
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#getDefaultFormUri()
	 */
	public String getDefaultFormUrl() {
		return _defaultFormUrl;
	}
	
	public int getDefaultFormLocation() {
		return _defaultFormLocation;
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#setDefaultFormUrl(java.lang.String)
	 */
	public void setDefaultFormUrl(String defaultFormUrl) {
		_defaultFormUrl = defaultFormUrl;
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#setDefaultFormLocation(int)
	 */
	public void setDefaultFormLocation(int defaultFormLocation) {
		_defaultFormLocation = defaultFormLocation;
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#exitApplication()
	 */
	public void exitApplication() {
		Dialog d = new Dialog(Dialog.DIALOG_YESNO, "確認");
		d.setText("アプリを終了します。\nよろしいですか？\n");
		int result = d.show();
		if (result == Dialog.BUTTON_YES) terminate();
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#storeBasicPref()
	 */
	public void storeBasicPref() {
		try {
			DocumentManager.execSerializeOperation(new BasicPrefStoreOperation());
		} catch (IOException e) {
			showErrorDialog("基本設定の保存に失敗しました。", e);
		}
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#loadBasicPref()
	 */
	public void loadBasicPref() {
		try {
			DocumentManager.execSerializeOperation(new BasicPrefLoadOperation());
		} catch (IOException e) {
			e.printStackTrace();
			_defaultFormLocation = DEFAULT_FORM_LOC_HTTP;
			_defaultFormUrl = "";
		}
	}
	
	class BasicPrefStoreOperation implements SerializeOperation {
		
		public void exec(Connection conn) throws IOException {
			OutputConnection oc = (OutputConnection)conn;
			System.out.println("writing basic_pref to scratchpad");
			DataOutputStream dos = null;
			try {
				dos = oc.openDataOutputStream();
				dos.writeInt(_defaultFormLocation);
				dos.writeUTF(_defaultFormUrl);
			} finally {
				if (dos != null) dos.close();
			}
		}

		public String getConnectionString() {
			return BASIC_PREF_URI;
		}

		public int getMode() {
			return Connector.WRITE;
		}
	}
	
	class BasicPrefLoadOperation implements SerializeOperation {

		public void exec(Connection conn) throws IOException {
			InputConnection oc = (InputConnection)conn;
			System.out.println("reading basic_pref from scratchpad");
			DataInputStream dis = null;
			try {
				dis = oc.openDataInputStream();
				_defaultFormLocation = dis.readInt();
				System.out.println("default form loc : " + _defaultFormLocation);
				_defaultFormUrl = dis.readUTF();
				System.out.println("default form url : " + _defaultFormUrl);
			} finally {
				if (dis != null) dis.close();
			}
		}
		
		public String getConnectionString() {
			return BASIC_PREF_URI;
		}

		public int getMode() {
			return Connector.READ;
		}
	}

    
	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#createSubmissionOperation(jp.haw.grain.xforms.SubmissionElement)
	 */
	public FormDocumentSerializeOperation createSubmissionOperation(SubmissionElement element) {
	    if (element == null) {
            return new FormSubmissionOperation(_defaultFormUrl, _defaultContentType);
        } else {
            String uri = element.getCanonicalActionUri();
    		String contentType = "application/xml";
    		Node node = element.getBindingNode();
            if (node == null) node = element.getContextModel().getInitialContextNode();
//    		if (node == null) node = element.getContextModel().getInstanceNode("/");
    		System.out.println("createSubmissionOperation: bindingNode = " + node);
    		return new FormSubmissionOperation(uri, contentType, node);
        }
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.SproutApp#createSubmissionOperation(jp.haw.grain.xforms.SubmissionElement)
	 */
	public FormDocumentSerializeOperation createExternalInstanceLoadOperation(InstanceElement element) {
		String uri = element.getCanonicalSrcUri();
		String contentType = "application/xml";
		return new FormSubmissionOperation(uri, contentType);
	}

    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.SproutApp#showMessage(java.lang.String)
     */
    public void showMessage(String eventType) {
        showErrorDialog(eventType, new Throwable("processing error"));
    }
		
}
