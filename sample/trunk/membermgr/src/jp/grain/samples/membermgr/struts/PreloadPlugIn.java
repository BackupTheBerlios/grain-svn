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
 * Created on 2005/08/26 3:21:09
 * 
 */
package jp.grain.samples.membermgr.struts;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import jp.grain.samples.membermgr.model.Job;
import jp.grain.samples.membermgr.model.PostalAddress;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * PreloadPlugIn
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class PreloadPlugIn implements PlugIn {

    private static final Logger log = Logger.getLogger(PreloadPlugIn.class);
    
    private String postalDataPath;
    private String jobDataPath;
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet, org.apache.struts.config.ModuleConfig)
     */
    public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
        try {
            log.info("loading postal data..");
            PostalAddress.init(new File(servlet.getServletContext().getRealPath(this.postalDataPath)));
            log.info("loading job data..");
            Job.init(new File(servlet.getServletContext().getRealPath(this.jobDataPath)));
            log.info("pre-load done.");
        } catch (IOException e) {
            log.error(e);
            throw new ServletException(e);
        }
    }    
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.PlugIn#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void setPostalDataPath(String postalDataPath) {
        this.postalDataPath = postalDataPath;
    }
    
    public void setJobDataPath(String jobDataPath) {
        this.jobDataPath = jobDataPath;
    }
}
