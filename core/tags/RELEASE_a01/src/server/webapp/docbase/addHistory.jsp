<?xml version="1.0" encoding="SJIS" ?>
<root xmlns="">
	<search-serialNo/>
	<% 
		try {
			boolean result = XMLUtil.addHistory(request, out);
			if (result) out.write("\n\t<searchDone/>\n");

		} catch (Exception e) {
			log.error("unable to write productInfo", e);
		}
	%>
</root>
<%@page 
	pageEncoding="Shift_JIS" 
	contentType="application/xml; charset=Shift_JIS" 
	import="jp.haw.grain.framework.*, jp.haw.grain.test.*, org.jdom.*, org.apache.log4j.*"
%>
<%!
	private static final Logger log = Logger.getLogger("jp.haw.grain.JSP");
%>