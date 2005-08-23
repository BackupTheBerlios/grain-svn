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
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package jp.haw.grain.xforms;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import jp.haw.grain.sprout.DocumentManager;
import jp.haw.grain.sprout.FormDocumentSerializeOperation;
import jp.haw.grain.sprout.FormView;
import jp.haw.grain.sprout.SproutApp;

import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Event;
import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Text;

/**
 * XFormsプロセッサ
 * 
 * @version $Id: Processor.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class Processor {
	
	public static final String XFORMS_NS = "http://www.w3.org/2002/xforms";
	private static Processor _instance;

	private boolean _submitting;
	private boolean _constractDoneProcessed;
	private FormDocument doc_;
	private FormView view_;
	private SproutApp _app;

	private Processor(SproutApp app) {
		_app = app;		
	}

	public static Processor getInstance() {
		return _instance;
	}
	
	public static void setup(SproutApp app) {
		if (_instance == null) {
			_instance = new Processor(app);
		}
	}
	
//	public void loadFromUrl(String url) throws Exception {
//		doc_ = _docLoader.loadFromUrl(url);
//		doc_.init();
//	}
//	
//	public void loadFromStream(InputStream is) throws Exception {
//		doc_ = _docLoader.load();
//		doc_.init();
//	}

	public void initForm(FormDocument doc) throws ParseException {
		FormView view = _app.createFormView(doc);
        if (doc == null) {
            view.render();
            return;
        }
        view_ = view;
		doc_ = doc;
		System.out.println("init form begin : " + doc);
		Vector models = new Vector();

		for (Enumeration e = doc_.xpathSelectElements("//model"); e.hasMoreElements(); ) {
			models.addElement((Element)e.nextElement());
		}
		
		_constractDoneProcessed = false;
		for (int i = 0; i < models.size(); ++i) {
			Element model = (Element)models.elementAt(i);
			System.out.println("xforms-model-constract : " + model);
			model.dispatchEvent(new Event("xforms-model-constract", true, false));
		}
		
		for (int i = 0; i < models.size(); ++i) {
			Element model = (Element)models.elementAt(i);
			model.dispatchEvent(new Event("xforms-model-constract-done", true, false));
		}
		
		for (int i = 0; i < models.size(); ++i) {
			Element model = (Element)models.elementAt(i);
			model.dispatchEvent(new Event("xforms-ready", true, false));
		}
		view_.render();
	}
	
	public void initModel(ModelElement model) {
		System.out.println("init model begin : " + model);
		model.dispatchEvent(new Event("xforms-model-constract", true, false));
		doc_.evaluateContext();
		view_.refresh();
	}
	
	public FormDocument getCurrentFormDocument() {
		return doc_;
	}
	
	public FormView getCurrentFormView() {
		return view_;
	}
	
	public void controlStatusChanged(FormControlElement element) throws ParseException {
		//if (!element.isBindingElement()) return;
		System.out.println("before model");
		if (element.getTagName().equals("input")) {
			valueChangeWithFocusChange(element);
		} else if (element.getTagName().equals("select1")) {
			valueChangeWithFocusChange(element);
		} else if (element.getTagName().equals("trigger") || element.getTagName().equals("submit")) {
			activateTrigger(element);
		}				
	}
	
	public void activateTrigger(FormControlElement target) {
		boolean result = target.dispatchEvent(new Event("DOMActivate", true, true));
	}
	
	public void valueChangeWithFocusChange(FormControlElement target) throws ParseException {
		if (!target.isBindingElement()) return;
		System.out.println("before model");
		ModelElement model = target.getContextModel();
		System.out.println("after model");
		model.dispatchEvent(new Event("xforms-recalculate", true, true));
		model.dispatchEvent(new Event("xforms-revalidate", true, true));
		System.out.println("before vc");
		target.dispatchEvent(new Event("xforms-value-changed", false, true));
//		target.dispatchEvent(new Event("DOMFocusOut", false, true));
//		target.dispatchEvent(new Event("DOMFocusIn", false, true));
		System.out.println("after vc");
		model.dispatchEvent(new Event("xforms-refresh", true, true));
	}
	
	public void submission(SubmissionElement target) {
		target.dispatchEvent(new Event("xforms-submit", true, true));
	}
	
	
	/*****************************
	 * actions
	 * @throws IOException 
	 *****************************/

	void processModelConstract(ModelElement model) throws ParseException, IOException {
		
		// schema does not support
		// external source does not support
		// instance date construct
		for (Enumeration e = model.xpathSelectElements("instance"); e.hasMoreElements(); ) {
			InstanceElement element = (InstanceElement)e.nextElement();
			if (element.isInitialized()) continue;
			element.init();
			if (element.getAttribute("src") == null) {
				element.loadInlineInstanceData();
			} else {
				FormDocumentSerializeOperation ope = _app.createExternalInstanceLoadOperation(element);
				DocumentManager.execSerializeOperation(ope);
				element.loadExternalInstanceData(ope.getFormDocuemnt());
			}
		}
		
		doc_.evaluateContext();
		
		// TODO binding element processing
		model.dispatchEvent(new Event("xforms-rebuild", true, false));
		model.dispatchEvent(new Event("xforms-recalcutate", true, false));
		model.dispatchEvent(new Event("xforms-revalidate", true, false));
	}

	void processModelConstractDone(ModelElement model) throws ParseException {
		if (_constractDoneProcessed) return;

		_constractDoneProcessed = true;
	}	
	
	void processReady(ModelElement model) {
		// TODO Not implemented yet
	}

	void processDestract(ModelElement model) {
		// TODO Not implemented yet
	}

	void processRefresh(ModelElement model) throws ParseException {
		view_.refresh();
	}

	/**
	 * @param element
	 * @throws IOException
	 **/
	public void processSubmit(SubmissionElement element) {
		if (_submitting) return;
		FormDocument doc = null;
		try {
			_submitting = true;
			element.getParentNode().dispatchEvent(new Event("xforms-revalidate", true, false));
			System.out.println("process-submit: " + element.getCanonicalActionUri());
			doSubmission(element);
			element.dispatchEvent(new Event("xforms-submit-done", true, false));
		} catch (Exception e) {
			e.printStackTrace();
			element.getParentNode().dispatchEvent(new Event("xforms-submit-error", true, false));
		} finally {
			_submitting = false;
		}
	}

	private void doSubmission(SubmissionElement element) throws Exception {
		FormDocumentSerializeOperation ope = _app.createSubmissionOperation(element);
		DocumentManager.execSerializeOperation(ope);
		if (!ope.hasResponseBody()) return;
		String replace = element.getAttribute("replace");
		if (ope.getFormDocuemnt() != null) {
			if (replace == null || "all".equals(replace)) {
				initForm(ope.getFormDocuemnt());
			} else if ("instance".equals(replace)) {
				InstanceDocument instance = (InstanceDocument)element.getBindingNode().getOwnerDocument();
				InstanceElement ie = instance.getOwner();
				ie.loadExternalInstanceData(ope.getFormDocuemnt());
				
				element.getParentModel().dispatchEvent(new Event("xforms-model-construct", true, false));
				initModel(element.getParentModel());
			}
		} else {
			if ("instance".equals(replace)) {
				throw new Exception("unable to replace : invalid media-type");
			}
		}
	}
	
	
	/**
	 * @param element
	 */
	public void processSubmitDone(SubmissionElement element) {
		// nothing to do
	}

	/**
	 * @param element
	 */
	public void processActivateSubmit(FormControlElement element) {
		SubmissionElement submission = (SubmissionElement)doc_.getElementById(element.getAttribute("submission"));
		submission.dispatchEvent(new Event("xforms-submit", true, true));
	}

	/**
	 * @param element
	 */
	public void processToggle(SwitchModuleElement element) {
		System.out.println("process toggle");
		String caseId = element.getAttribute("case");
		Element nextCaseElement = doc_.getElementById(caseId);
		Element currentCaseElement = nextCaseElement.xpathSelectElement("../case[@selected='true']");
		currentCaseElement.dispatchEvent(new Event("xforms-deselect", true, false));
		nextCaseElement.dispatchEvent(new Event("xforms-select", true, false));
	}

	/**
	 * @param element
	 */
	public void processCaseSelect(SwitchModuleElement element) {
		System.out.println("process case select");
		element.setAttribute(null, "selected", "true");
		view_ = _app.createFormView(doc_);
		view_.render();
	}

	/**
	 * @param element
	 */
	public void processCaseDeselect(SwitchModuleElement element) {
		System.out.println("process case deselect");
		element.setAttribute(null, "selected", "false");		
	}

	/**
	 * @param element
	 */
	public void processAction(ActionElement element) { 
		System.out.println("process action");
		for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (!(n instanceof ActionModule)) continue;
			System.out.println("action = " + ((Element)n).getTagName());
			((ActionModule)n).performAction();
		}
	}

	/**
	 * @param element
	 */
	public void processSetValue(ActionElement element) {
		String value = "";
		if (element.getAttribute("value") == null) {
			System.out.println("1");
			value = element.getChildText();
		} else {
			Node node = element.calcXPath(element.getAttribute("value"));
			if (node instanceof Text) {
				value = ((Text)node).getData();
			} else if (node instanceof Element) {
				Element e = (Element)node;
				System.out.println("src node : " + e.getTagName());
				for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
					if (!(n instanceof Text)) continue;
					value = ((Text)n).getData();
					break;
				}
			}
		}
        value = (value != null) ? value : "";
		System.out.println("setvalue : src value = " + value);
		Node node = element.getBindingNode();
		if (node instanceof Text) {
			System.out.println("setvalue : binding orginal value = " + ((Text)node).getData());
			((Text)node).setData(value);
			return;
		} else if (node instanceof Element) {
			Element elem = (Element)node;
			for (Node n = elem.getFirstChild(); n != null; n = n.getNextSibling()) {
				if (!(n instanceof Text)) continue;
				System.out.println("setvalue : binding orginal value = " + ((Text)n).getData());
				((Text)n).setData(value);
				return;
			}
			System.out.println("setvalue : binding orginal value none");
			elem.appendChild(new Text(value));
		}
	}

	/**
	 * @param element
	 */
	public void processSend(ActionElement element) {
		SubmissionElement submission = (SubmissionElement)doc_.getElementById(element.getAttribute("submission"));
		submission.dispatchEvent(new Event("xforms-submit", true, true));
	}

	/**
	 * @param element
	 */
	public void processRevalidate(ModelElement element) {
		System.out.println("process revalidate");
		for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (!(n instanceof BindElement)) continue;
			BindElement bind = (BindElement)n;
			bind.revalidate();
		}
	}

    /**
     * 
     */
    public SproutApp getCurrentApp() {
        return this._app;
    }

    /**
     * @param eventType
     */
    public void notifyError(String eventType) {
        this._app.showMessage(eventType);
    }
}
