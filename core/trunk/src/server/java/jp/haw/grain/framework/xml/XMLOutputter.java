/*
 * License
 * 
 * Created on 2005/08/06 9:54:07
 * 
 */
package jp.haw.grain.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParserException;

/**
 * TODO XMLOutputter
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class XMLOutputter {
	

    private static final Logger log = Logger.getLogger(XMLOutputter.class);
    
    private BinaryXMLParser parser;
    
    public XMLOutputter(byte[] bs, String encoding) {
        this(new ByteArrayInputStream(bs), encoding);
    }    

    public XMLOutputter(InputStream is) {
        this(is, null);
    }
    
    public XMLOutputter(InputStream is, String encoding) {
        this.parser = BinaryXMLParser.newInstance(is, encoding);
;    }
    
    boolean versionread=false;
    
    public void writeTo(Writer writer) throws IOException, XmlPullParserException {
        
        for (int et = parser.getEventType(); et != BinaryXMLParser.END_DOCUMENT; et = parser.next()) {
        	
             if (et == BinaryXMLParser.START_TAG) {
            	if(versionread==false){
            		 writer.write("<?xml version=\"");
            	     writer.write(this.parser.getProperty(BinaryXMLParser.XMLDECL_VERSION).toString());
            	     writer.write("\" encoding=\"");
            	     writer.write(this.parser.getInputEncoding());
            	     writer.write("\" ?>\n");
            	     versionread=true;
            	}
                log.debug("start tag name: ");
                writer.write('<');
                writeNameWithPrefix(writer);
                writePrefixMappings(writer);               
                writeAttributes(writer);
                if(this.parser.isEmptyElementTag()) {
                	writer.write("/>\n");
                	et=parser.next();
                }
                else writer.write(">\n");
                
            } else if (et == BinaryXMLParser.END_TAG) {
                	writer.write("</");
                	log.debug("end tag name: ");
                	writeNameWithPrefix(writer);
                	writer.write(">\n");
            } else if (et == BinaryXMLParser.TEXT) {
                writer.write(parser.getText());
                writer.write("\n");
                log.debug(this.parser.getText());
            }
        }
        log.debug(writer.toString());
    }

    private void writeNameWithPrefix(Writer writer) throws IOException, XmlPullParserException {
        String prefix = getCurrentPrefix();
        if(prefix!=null){
        	if (prefix.length() > 0) {
        		writer.write(prefix);
        		writer.write(':');
        		log.debug(prefix+":");
        	}
        }
        writer.write(this.parser.getName()); 
        log.debug(this.parser.getName());
    }
    
    private String getCurrentPrefix() throws XmlPullParserException {
        String elementNs = this.parser.getNamespace();
        int nsCount = this.parser.getNamespaceCount(parser.getDepth());
        for (int i = 0; i < nsCount; ++i) {
            if (this.parser.getNamespaceUri(i).equals(elementNs)) {
                return this.parser.getNamespacePrefix(i);
            }
        }
        return "";
    }
    private void writePrefixMappings(Writer writer)throws IOException,XmlPullParserException{
    	
    	int nsCount = this.parser.getNamespaceCount(this.parser.getDepth());
        int nsParentCount = this.parser.getNamespaceCount(this.parser.getDepth() - 1);
        if (nsCount > nsParentCount) {
        	for (int i = nsParentCount; i < nsCount; ++i) {
        		String prefix = this.parser.getNamespacePrefix(i);
        		String uri = this.parser.getNamespaceUri(i);
        		writer.write(" xmlns");
        		if(prefix!=null) {
        			writer.write(':');
        			writer.write(prefix);
        		}
        		writer.write("=\"");
        		writer.write(uri);
        		writer.write('"');
        	}
        }
    }
    private void writeAttributes(Writer writer)throws IOException{
    	int attcount=this.parser.getAttributeCount();
    	for(int i=0;i<attcount;i++){
            writer.write(' ');
    		if(this.parser.getAttributePrefix(i)!=null){
    			writer.write(this.parser.getAttributePrefix(i));
    			writer.write(':');
    		}
    		log.debug("attr ["+i+"]prefix:"+this.parser.getAttributePrefix(i));
    		writer.write(this.parser.getAttributeName(i));
    		log.debug("attr ["+i+"]name:"+this.parser.getAttributeName(i));
    		writer.write("=\"");
    		writer.write(this.parser.getAttributeValue(i));
    		log.debug("attr ["+i+"]value:"+this.parser.getAttributeValue(i));
    		writer.write('"');
    		
    	}
    }
///////////////////////////////////////////
//    
// ˆÈ‰º‚ÍABinaryXMLParser‚ÌŽg‚¢•û‚Ì—á‚Å‚·B
//
///////////////////////////////////////////
    
//    private Element createElement() throws XmlPullParserException {
//                
//        Element element = null;
//        String name = this.parser.getName();
//        if (XFormsElement.XFORMS_NS_URI.equals(this.parser.getNamespace())) {
//            if (ModelElement.TAG_NAME.equals(name)) {
//                element = new ModelElement();
//            } else if (InstanceElement.TAG_NAME.equals(name)) {
//                element = new InstanceElement();
//            } else if (SubmissionElement.TAG_NAME.equals(name)) {
//                element = new SubmissionElement();
//            } else if (BindElement.TAG_NAME.equals(name)) {
//                element = new BindElement();                
//            } else if (name.equals("input") || name.equals("select1") || name.equals("trigger") || name.equals("submit") || name.equals("output")) {
//                element = new FormControlElement(name);
//            } else if (name.equals("switch") || name.equals("case") || name.equals("toggle")) {
//                element = new SwitchModuleElement(name);
//            } else if (name.equals("action") || name.equals("setvalue") || name.equals("setvalue") || name.equals("send")) {
//                element = new ActionElement(name);
//            } else {
//                element = new XFormsElement(name);
//            }
//        } else if (XHTML.equals(this.parser.getNamespace())) {
//            element = new XHTMLElement(name);
//        } else {
//            element = new Element(name);
//        }
//                
//        element.setPrefix(this.parser.getPrefix());
//        element.gsNamespaceUri(this.parser.getNamespace());
//        
//        for (int i = 0; i < this.parser.getAttributeCount(); ++i) {
//            String attrName = this.parser.getAttributeName(i);
//            String attrNS = this.parser.getAttributeNamespace(i);
//            String attrValue = this.parser.getAttributeValue(i);
//            element.setAttribute(attrNS, attrName, attrValue);
//            
//            // TODO check attribure type is xsd:id
//            if (name.equals("id")) {
//                doc.registElement(attrValue, element);
//            }
//        }
//        
//        int nsCount = this.parser.getNamespaceCount(this.parser.getDepth());
//        int nsParentCount = this.parser.getNamespaceCount(this.parser.getDepth() - 1);
//        if (nsCount > nsParentCount) {
//            for (int i = nsParentCount; i < nsCount; ++i) {
//                String prefix = this.parser.getNamespacePrefix(i);
//                String uri = this.parser.getNamespaceUri(i);
//                element.setNamespace((prefix == null) ? "" : prefix, uri);
//            }
//        }
//        
//        if (element instanceof RenderableElement) {
//            RenderableElement re = (RenderableElement)element;
//            re.applyStyles();
//        }
//
//        
//        return element;
//    }
    
}
