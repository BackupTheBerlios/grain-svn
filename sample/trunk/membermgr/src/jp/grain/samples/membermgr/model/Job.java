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
 * Job
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Job {

    private static final Logger log = Logger.getLogger(Job.class);
    private static Properties jobMap = new Properties();
    
    public static void init(File config) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(config);
            jobMap.load(is);
            log.info("job data loaded : " + jobMap.size());
        } catch (IOException e) {
            log.error("unable to load address data", e);
            throw e;
        } finally {
            if (is != null) is.close();
        }
    }
    
    private String jobCode;
    private String name;

    /**
     * 
     */
    public Job(String jobCode, String name) {
        this.jobCode = jobCode;
        this.name = name;
    }

    /**
     * @param jobCode
     * @return
     */
    public static Job searchJobByCode(String jobCode) {
        String name = jobMap.getProperty(jobCode);
        if (name == null) {
            log.debug("job code not found : " + jobCode);
            return new Job(jobCode, "");
        }
        log.debug("job code found : " + jobCode + " = " + name);
        return new Job(jobCode, name);
    }

    
    public static String parseJobCode(Document doc) {
        Element jobCodeElem = doc.getRootElement();
        if (!"job-code".equals(jobCodeElem.getName())) return null;
        return jobCodeElem.getTextNormalize();       
    }
    
    /**
     * @return
     */
    public Document toXMLDocument() {
        Element jobCode = new Element("job-code").setText(this.jobCode);
        Element name = new Element("name").setText(this.name);
        Element job = new Element("job-data").addContent(jobCode).addContent(name);
        return new Document().setRootElement(job);
    }

}
