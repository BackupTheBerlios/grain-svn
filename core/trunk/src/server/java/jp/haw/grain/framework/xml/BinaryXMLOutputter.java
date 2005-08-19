/*
 * $Id: BinaryXMLOutputter.java 3365 2005-08-15 19:00:16Z go $
 * 
 * Created on 2005/05/07
 *
 */
package jp.haw.grain.framework.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author go
 *
 * gXML(バイナリXML)を適切なストリームに出力する
 */
public class BinaryXMLOutputter {
	
	private static final Logger log = Logger.getLogger(BinaryXMLOutputter.class);
    
    private InputSource source;
    private String encoding;
    private String lineSeparator = "\n";
    
    public BinaryXMLOutputter(byte[] bs) {
        this.source= new InputSource(new ByteArrayInputStream(bs));
    }    

    public BinaryXMLOutputter(char[] cs) {
        this.source= new InputSource(new CharArrayReader(cs));
        String head = new String(cs, 0, cs.length > 50 ? 50 : cs.length); 
        parseEncoding(head);
    }
    
    public BinaryXMLOutputter(InputStream is) {
        this.source = new InputSource(is);
    }

    public BinaryXMLOutputter(Reader reader) {
        this.source = new InputSource(reader);
    }
    
    public BinaryXMLOutputter(String src) {
        this.source= new InputSource(new StringReader(src));
        parseEncoding(src);
    }
    
    private void parseEncoding(String head) {
        if (!head.startsWith("<?xml")) return;
        Matcher m = Pattern.compile("encoding=\"([^\"]*)\"").matcher(head);
        if (m.find() && m.groupCount() > 0) setTextEncoding(m.group(1));
    }
    
    public void setLineSeparator(String separator) {
        this.lineSeparator = separator;
    }
    
    public void setTextEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public void writeTo(OutputStream os) throws IOException, ParseException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            log.debug("SAXParser: " + parser.getClass().getName());
            log.debug("char stream: " + this.source.getCharacterStream());
            log.debug("output stream: " + os.getClass().getName());
            BinaryXMLEncoder enc = new BinaryXMLEncoder(this.encoding);
            parser.parse(this.source, enc);
            enc.writeTo(os);
        } catch (SAXException e) {
            log.debug(e.toString(), e);
            throw new ParseException(e);
        } catch (ParserConfigurationException e) {
            log.debug(e.toString(), e);
            throw new ParseException(e);
        } catch (FactoryConfigurationError e) {
            log.debug(e.toString(), e);
            throw new ParseException(e);
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("ussage: encoding infile(xml) outfile(gbxml)");
            System.exit(1);
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(new FileInputStream(args[1]));
            BinaryXMLOutputter bxo = new BinaryXMLOutputter(is);
            bxo.setTextEncoding(args[0]);
            os = new BufferedOutputStream(new FileOutputStream(args[2]));
            bxo.writeTo(os);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) os.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
