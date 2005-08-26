<%
	try {
		log.debug("start xmlResponse");
		Document doc = (Document)request.getAttribute("result");
		if (doc == null) return;
		log.debug("doc is not null");
 		xmlOut.output(doc, System.out);
 		xmlOut.output(doc, out);
	} catch (IOException e) {
		log.error("unable to write response document", e);
	}
%>
<%@page 
	pageEncoding="Shift_JIS" 
	contentType="application/xml; charset=Shift_JIS" 
	import="java.io.*, org.jdom.*, org.jdom.output.*, org.apache.log4j.*"
%>
<%!
	private static final Logger log = Logger.getLogger("jp.grain.JSP");
	private static final XMLOutputter xmlOut = new XMLOutputter(Format.getCompactFormat().setEncoding("SJIS"));
%>
<%-- 

	Member manager - Grain Sample Code
	Copyright (C) 2005 HAW International Inc.
	
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.
	
	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

--%>