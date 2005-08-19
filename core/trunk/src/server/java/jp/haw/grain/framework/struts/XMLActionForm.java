/*
 * $Header$
 * 
 * Created on 2005/03/15
 *
 */
package jp.haw.grain.framework.struts;

import javax.servlet.http.HttpServletRequest;

import jp.haw.grain.framework.ParseException;
import jp.haw.grain.framework.XMLUtil;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.jdom.Document;

/**
 * XFormsプロセッサとの間で発生するXMLのリクエスト／レスポンスの
 * パース処理を隠蔽するアクションフォーム。
 * 利用者はHTTPのリクエスト／レスポンスを意識せず、DOMツリーでの
 * やり取りとして利用できる。
 * 
 * @author go
 *
 */
public class XMLActionForm extends ActionForm {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3834589898496620083L;

	private static final Logger log = Logger.getLogger(XMLActionForm.class);

	private Document request;
	private Document response;
	
	public Document getRequestDocument() {
		return request;
	}

	public void setRequestDocument(Document request) {
		this.request = request;
	}	
	
	public Document getResponseDocument() {
		return response;
	}
	
	public void setResponseDocument(Document response) {
		this.response = response;
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		try {
			this.response = null;			
			this.request = XMLUtil.parseRequest(request);
		} catch (ParseException e) {
			log.error("unable to parse request", e);
		}
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		return errors;
	}
}
