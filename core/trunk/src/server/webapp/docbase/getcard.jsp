<?xml version="1.0"?>
<%
	Payment payment = null;
	try {
		Document doc = XMLUtil.parseRequest(request);
		Element e = doc.getRootElement();
		log.debug("received element : " + e.getName());
		log.debug("received element content : " + e.getText());
		payment = Payment.getInstance(e.getText());
	} catch (ParseException e) {
		log.error("unable to parse request", e);
		payment = Payment.getInstance();
	}
%>
<payment xmlns="http://www.haw.co.jp/ns/my" as="credit">
	<card-code-input><%= payment.getCardCode() %></card-code-input>
	<card-code><%= payment.getCardCode() %></card-code>
	<exp-year><%= payment.getExpYear() %></exp-year>
	<exp-month><%= payment.getExpMonth() %></exp-month>
	<name><%= payment.getName() %></name>
</payment>
<%@page 
	pageEncoding="Shift_JIS" 
	contentType="application/xml; charset=Shift_JIS" 
	import="jp.haw.grain.framework.*, jp.haw.grain.test.*, org.jdom.*, org.apache.log4j.*"
%>
<%!
	private static final Logger log = Logger.getLogger("jp.haw.grain.JSP");
%>