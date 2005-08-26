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
 * Created on 2005/03/14
 *
 */
package jp.grain.samples.membermgr.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.grain.samples.membermgr.model.Job;
import jp.grain.samples.membermgr.model.Member;
import jp.grain.samples.membermgr.model.PostalAddress;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.jdom.Document;

/**
 * メンバー管理アプリケーションのビジネスロジックへのディスパッチ制御
 * 
 * @version $Id$
 * @author GoTakahashi
 */
public class ControlAction extends DispatchAction {
	
	private static final Logger log = Logger.getLogger(ControlAction.class);
	
	public ActionForward searchAddressByPostCode(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {		
            log.debug("searchAddressByPostCode start");
    		XMLActionForm xf = (XMLActionForm)form;
    		Document doc = xf.getRequestDocument();
            if (doc == null) throw new IllegalRequestException("no request xml data");
            String postCode = PostalAddress.parsePostalCode(doc);
            PostalAddress address = PostalAddress.searchAddressByPostCode(postCode);
            request.setAttribute("result", address.toXMLDocument());            
    		return mapping.findForward("xml");
        } catch (Exception e) {
            log.error("searchAddressByPostCode", e);
            throw e;
        }
	}
	
	public ActionForward searchJobByCode(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
            log.debug("searchJobByCode start");
            XMLActionForm xf = (XMLActionForm)form;
            Document doc = xf.getRequestDocument();
            if (doc == null) throw new IllegalRequestException("no request xml data");
            String jobCode = Job.parseJobCode(doc);
            Job job = Job.searchJobByCode(jobCode);
            request.setAttribute("result", job.toXMLDocument());            
            return mapping.findForward("xml");
        } catch (Exception e) {
            log.error("searchJobByCode", e);
            throw e;
        }
	}

	public ActionForward addMember(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
            log.debug("addMember start");
            XMLActionForm xf = (XMLActionForm)form;
            Document doc = xf.getRequestDocument();
            if (doc == null) throw new IllegalRequestException("no request xml data");
            Member member = Member.createMemberByXMLDocument(doc);
            Member.addMember(member);
            request.setAttribute("result", member.toXMLDocument());
            return mapping.findForward("xml");
        } catch (Exception e) {
            log.error("addMember", e);
            throw e;
        }
    }
}
