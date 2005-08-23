/*
 * $Header$
 * 
 * Created on 2005/02/07
 *
 */
package jp.haw.grain.framework;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * リクエストのパースなどを行う機能と
 * デモアプリケーションのビジネスロジックが一体化している。
 * 
 * TODO 機能の分離が必要
 * 
 * @author go
 * 
 */
public class XMLUtil {
	
	private static final XMLOutputter xmlOut = new XMLOutputter();
	private static final Logger log = Logger.getLogger(XMLUtil.class);
	private static Document productDoc;
	private static final RecentlyHistoryComparator recentlyHistoryComparator = new RecentlyHistoryComparator();
	
	static {
		try {
			SAXBuilder builder = new SAXBuilder();
			productDoc = builder.build(XMLUtil.class.getClassLoader().getResourceAsStream("product.xml"));
			log.debug("product doc builded");
			xmlOut.output(productDoc, new OutputStreamWriter(System.out));
		} catch (JDOMException e) {
			log.error("unable to parse", e);
		} catch (IOException e) {
			log.error("io error", e);
		}
	}
	
	public static Document parseRequest(HttpServletRequest request) throws ParseException {
		try {
			log.debug("parsing request");
			SAXBuilder builder = new SAXBuilder(); 
			Document doc = builder.build(request.getInputStream());
			log.debug("request doc request");
			xmlOut.output(doc, new OutputStreamWriter(System.out));
			return doc;
		} catch (IOException e) {
			throw new ParseException(e);
		} catch (JDOMException e) {
			throw new ParseException(e);
		}
	}
	
	public static Document getProductInfo(Document request) throws ParseException {
		String serialNo = request.getRootElement().getText();
		log.debug("search-serialNo = " + serialNo);
		Element support = getSupportBySerialNo(serialNo);
		log.debug("get support : " + support);
		if (support == null) return null;
		log.debug("output to writer");
		Element root = new Element("root").setNamespace(Namespace.getNamespace("ns"));
		log.debug("namespace : prefix = '" + root.getNamespace().getPrefix() + "', uri = '" + root.getNamespace().getURI() + "'");
		root.addContent((Element)support.getChild("pInfo").clone());
		root.addContent(new Element("searchDone"));
		root.addContent(new Element("search-serialNo"));
		return new Document(root);
	}
	
	private static Element getSupportBySerialNo(String serialNo) {
		log.debug("search support entry");
		List supports = productDoc.getRootElement().getChildren();
		log.debug("support size = " + supports.size());
		for (int i = 0; i < supports.size(); ++i) {
			Element support = (Element)supports.get(i);
			if (support.getChild("pInfo").getChildText("serialNo").equals(serialNo))
				return support;
		}
		return null;
	}
	
	public static Document getHistoryList(Document request) throws ParseException {
		String serialNo = request.getRootElement().getText();
		log.debug("serarch-serialNo = " + serialNo);
		Element support = getSupportBySerialNo(serialNo);
		log.debug("get support : " + support);
		if (support == null) return null;
		log.debug("output to writer");
		List histories= support.getChild("historyList").cloneContent();
		log.debug("before sorting");
		for (int i = 0; i < histories.size(); ++i) {
			Element history = (Element)histories.get(i);
			history.detach();
			log.debug("date : " + history.getChildText("maintenanceDate"));
		}
		Collections.sort(histories, recentlyHistoryComparator);
		log.debug("after sorting");
		Element historyList = new Element("historyList");
		for (int i = 0; i < histories.size() && i < 3; ++i) {
			Element history = (Element)histories.get(i);
			log.debug("date : " + history.getChildText("maintenanceDate"));
			historyList.addContent(history);
		}
		Element root = new Element("root").setNamespace(Namespace.getNamespace("ns"));
		return new Document(root.addContent(historyList));
	}

	public static Document addHistory(Document request) throws ParseException {
		String serialNo = request.getRootElement().getChildText("search-serialNo");
		log.debug("search-serialNo = " + serialNo);
		Element support = getSupportBySerialNo(serialNo);
		log.debug("get support : " + support);
		if (support == null) return null;
		log.debug("output to writer");
		Element historyElem = (Element)support.getChild("historyList");
		historyElem.addContent(request.getRootElement().getChild("history").detach());
		Element history = new Element("history");
		history.addContent(new Element("maintenanceDate"));
		history.addContent(new Element("applyerCode"));
		history.addContent(new Element("kind"));
		history.addContent(new Element("note"));
		Element root = new Element("root").setNamespace(Namespace.getNamespace("ns"));
		root.addContent(history);
		root.addContent(new Element("search-serialNo").addContent(serialNo));
		return new Document(root);
	}
	
	static class RecentlyHistoryComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			long val0 = Long.parseLong(((Element)arg0).getChildText("maintenanceDate"));
			long val1 = Long.parseLong(((Element)arg1).getChildText("maintenanceDate"));
			if (val0 == val1) {
				return 0;
			} else if (val0 > val1) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
