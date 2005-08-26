/*
 * Member manager - Grain Sample Code
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
 * Created on 2005/03/15
 *
 */
package jp.grain.samples.membermgr.struts;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Grain Sproutとの間で発生するXMLのリクエスト／レスポンスの
 * パース処理を隠蔽するアクションフォーム。
 * 利用者はHTTPのリクエスト／レスポンスを意識せず、DOMツリーでの
 * データの送受信を可能にする。
 * 
 * @version $Id$
 * @author Go Takahashi
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
            log.debug("parsing request");
            SAXBuilder builder = new SAXBuilder();
            this.request = builder.build(request.getInputStream());
            StringWriter writer = new StringWriter();
            new XMLOutputter().output(this.request, writer);
            log.debug(writer.toString());
        } catch (IOException e) {
            log.error("unable to parse request", e);
        } catch (JDOMException e) {
            log.error("unable to parse request", e);
		} catch (Throwable e) {
		    log.error("unexpected error", e);
        }
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		return errors;
	}
}
