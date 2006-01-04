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
 * Created on 2005/12/28 15:43:35
 * 
 */
package jp.grain.cfs;

import java.io.IOException;

/**
 * TODO ScratchPadProvider
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class ScratchPadProvider implements Provider {
    
    public static final String FSTYPE_KFAT12 = "KFAT12  ";
    public static final String SUPPORTED_SCHEMA = "file://";
    private static ScratchPadProvider instance = new ScratchPadProvider();
    
    private Partition defaultPart;

    public static void initDefault(String defaultScratchPadUrl) throws IOException {
        FileSystem.registerProvider(instance);
        instance.setDefaultScratchPad(defaultScratchPadUrl);
    }
     
    private ScratchPadProvider() {
    }
    
    /* (non-Javadoc)
     * @see jp.grain.cfs.Provider#accept(java.lang.String)
     */
    public boolean accept(String url) {
        if (url.startsWith(SUPPORTED_SCHEMA)) return true;
        return false;
    }

    public FileAccessor open(String url) throws IOException {
        int rootSlash = url.indexOf('/', SUPPORTED_SCHEMA.length());
        if (rootSlash != SUPPORTED_SCHEMA.length()) throw new IOException("partition not supported yet");
        int lastIndex =  url.length() - (url.endsWith("/") ?  2 : 1);
        if (lastIndex < SUPPORTED_SCHEMA.length() - 1) throw new IOException("no root slash"); 
        int lastSlash = url.lastIndexOf('/', lastIndex);
        String filename = url.substring(lastSlash + 1, lastIndex + 1);
        FileAccessor fe = this.defaultPart.findFileEntry(filename);
        return fe;
    }
    
    private void setDefaultScratchPad(String defaultScratchPadUrl) throws IOException {
        this.defaultPart = new Partition(defaultScratchPadUrl);
        this.defaultPart.init();
    }

    /* (non-Javadoc)
     * @see jp.grain.cfs.Provider#format()
     */
    public void format(String url, boolean completely) throws IOException {
        this.defaultPart.bytesPerSector = Partition.DEFAULT_BYTES_PER_SECTOR;
        this.defaultPart.numberOfFATs = Partition.DEFAULT_NUMBER_OF_FATS;
        this.defaultPart.reservedSector = Partition.DEFAULT_RESERVED_SECTOR;
        this.defaultPart.rootEntries = Partition.DEFAULT_ROOT_ENTRIES;
        this.defaultPart.sectorsPerCluster = Partition.DEFAULT_SECTOR_PER_CLUSTER;
        this.defaultPart.sectorsPerFAT = Partition.DEFAULT_SECTOR_PER_FAT;
        this.defaultPart.totalSectors = Partition.DEFAULT_TOTAL_SECTORS;
        this.defaultPart.fileSystemType = FSTYPE_KFAT12;
        this.defaultPart.format(completely);
    }
    
    
}
