/*
 * Grain Core - A XForms processor for mobile terminals.
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
 * Created on 2005/12/31 11:36:48
 * 
 */
package jp.grain.cfs;

import java.io.IOException;

/**
 * 
 * TODO FileSystem
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FileSystem {
        
    private static Provider provider;
    
    public static void registerProvider(Provider provider) {
        FileSystem.provider = provider;
    }
    
    /**
     * default implementation suppoorts only scratchpad schema
     * @param url
     * @return
     * @throws IOException 
     */
    public static FileAccessor open(String url) throws IOException {
        if (provider == null || !provider.accept(url)) throw new IOException("unable to find provider");
        return provider.open(url);
    }

    /**
     * @param string
     * @throws IOException 
     */
    public static void format(String url) throws IOException {
        if (provider == null || !provider.accept(url)) throw new IOException("unable to find provider");
        provider.format(url, false);
    }

    /**
     * @param string
     * @throws IOException 
     */
    public static void formatCompletely(String url) throws IOException {
        if (provider == null || !provider.accept(url)) throw new IOException("unable to find provider");
        provider.format(url, true);
    }
}
