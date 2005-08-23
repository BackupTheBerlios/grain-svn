<%
	try {
		XMLActionForm form = (XMLActionForm)request.getAttribute("xmlForm");
		if (form == null) return;
 		xmlOut.output(form.getResponseDocument(), System.out);
 		xmlOut.output(form.getResponseDocument(), out);
	} catch (IOException e) {
		log.error("unable to write response document", e);
	}
%>
<%@page 
	pageEncoding="Shift_JIS" 
	contentType="application/xml; charset=Shift_JIS" 
	import="java.io.*, jp.haw.grain.framework.*, jp.haw.grain.framework.struts.*, 
			org.jdom.*, org.jdom.output.*, org.apache.log4j.*"
%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%!
	private static final Logger log = Logger.getLogger("jp.haw.grain.JSP");
	private static final XMLOutputter xmlOut = new XMLOutputter(Format.getCompactFormat().setEncoding("SJIS"));
%>