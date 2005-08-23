/*
 * $Header$
 * 
 * Created on 2005/03/14
 *
 */
package jp.haw.grain.framework.struts.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.haw.grain.framework.XMLUtil;
import jp.haw.grain.framework.struts.XMLActionForm;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * デモアプリケーションのビジネスロジックへのディスパッチ制御
 * 
 * @author go
 */
public class ControlAction extends DispatchAction {
	
	private static final Logger log = Logger.getLogger(ControlAction.class);
	private static final XMLOutputter xmlOut = new XMLOutputter();
	
	static {
		Format format = xmlOut.getFormat();
		format.setEncoding("SJIS");
	}

	public ActionForward getProductInfo(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		XMLActionForm xf = (XMLActionForm)form;
		Document doc = XMLUtil.getProductInfo(xf.getRequestDocument());
		xf.setResponseDocument(doc);
		return mapping.findForward("xml");
	}
	
	public ActionForward getHistoryList(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		XMLActionForm xf = (XMLActionForm)form;
		Document doc = XMLUtil.getHistoryList(xf.getRequestDocument());
		xf.setResponseDocument(doc);
		return mapping.findForward("xml");
	}

	public ActionForward addHistory(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		XMLActionForm xf = (XMLActionForm)form;
		Document doc = XMLUtil.addHistory(xf.getRequestDocument());
		xf.setResponseDocument(doc);
		return mapping.findForward("xml");
	}	
}
