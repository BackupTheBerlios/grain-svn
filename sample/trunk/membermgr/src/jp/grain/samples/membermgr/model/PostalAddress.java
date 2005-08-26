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
 * Created on 2005/08/25 13:57:45
 * 
 */
package jp.grain.samples.membermgr.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Address
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class PostalAddress {

    private static final Logger log = Logger.getLogger(PostalAddress.class);
    private static Properties addressMap = new Properties();
    
    public static void init(File config) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(config);
            addressMap.load(is);
            log.info("postal address loaded : " + addressMap.size());
        } catch (IOException e) {
            log.error("unable to load address data", e);
            throw e;
        } finally {
            if (is != null) is.close();
        }
    }
    
    private String postCode;
    private String address;

    /**
     * 
     */
    public PostalAddress(String postCode, String address) {
        this.postCode = postCode;
        this.address = address;
    }

    public static String parsePostalCode(Document doc) {
        Element postCodeElem = doc.getRootElement();
        if (!"post-code".equals(postCodeElem.getName())) return null;
        return postCodeElem.getTextNormalize();
    }
    
    /**
     * @param postCode
     * @return
     */
    public static PostalAddress searchAddressByPostCode(String postCode) {
        String address = addressMap.getProperty(postCode);
        if (address == null) {
            log.debug("postcode not found : [" + postCode + "]");
            return new PostalAddress(postCode, "");
        }
        log.debug("postcode found : [" + postCode + "]=" + address);
        return new PostalAddress(postCode, address);
    }

    /**
     * @return
     */
    public Document toXMLDocument() {
        Element postCode = new Element("post-code").setText(this.postCode);
        Element address = new Element("address").setText(this.address);
        Element postalAddress = new Element("post-data").addContent(postCode).addContent(address);
        return new Document().setRootElement(postalAddress);
    }

}
