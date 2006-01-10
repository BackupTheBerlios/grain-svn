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
 * Created on 2005/12/31 14:22:34
 * 
 */
package jp.grain.cfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * TODO FileAccessor
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FileAccessor {
    
    public static final int ARCHIVE   = 0x20;
    public static final int DIRECTORY = 0x10;
    public static final int VOLUME    = 0x08;
    public static final int SYSTEM    = 0x04;
    public static final int HIDDEN    = 0x02;
    public static final int READ_ONLY = 0x01;
    
    int entryIndex = -1;
    FileEntry entry;    
    Partition partition;
    private CloseListener closeListener;
    
    FileAccessor(Partition part) throws IOException {
        this.partition = part;
    }    

    void init(byte[] entry) {
        this.entry = new FileEntry(entry);
    }
    
    /**
     * @param filename
     */
    void init(String filename) {
        this.entry = new FileEntry();
        this.entry.name = filename;
    }
    
    public String getName() {
        return this.entry.name;
    }
    
    public String getPath() {
        //TODO should return real path 
        return "/" + getName();
    }
    
    public boolean isDirectory() {
        return (this.entry.attribute & DIRECTORY) != 0;
    }
    
    public boolean isHidden() {
        return (this.entry.attribute & HIDDEN) != 0;
    }
    
    public boolean isOpen() {
        return this.partition != null;
    }
    
    public long fileSize() {
        return this.entry.fileSize;
    }
    
    public boolean exsists() {
        return this.entryIndex != -1;
    }
    
    public void delete() throws IOException {
        this.partition.removeFileEntry(this.entryIndex);
        this.entryIndex = -1;
    }
    
    public void create() throws IOException {
        this.entryIndex = this.partition.seekEmptyFileEntry();
        if (this.entryIndex < 0) throw new IOException("no more file entry");
        FileEntry entry = this.entry.clone();
        entry.attribute |= ARCHIVE;
        entry.createTimeMillis = new Date().getTime();
        entry.updateTimeMillis = entry.createTimeMillis;
        this.partition.saveFileEntry(this.entryIndex, entry);
        this.entry = entry;
    }
    
    public boolean canWrite() {
        // always returns true
        return true;
    }
    
    /**
     * 
     * @return always returns true at KFAT12 partition
     */
    public boolean canRead() {
        // always returns true
        return true;
    }

    public long lastModified() {
        return this.entry.updateTimeMillis;
    }
    
    public long availableSize() {
        //TODO
        return 0; 
    }
    
    public long totalSize() {
        //TODO
        return 0;
    }
    
    public long usedSize() {
        //TODO
        return 0;
    }
    
    public void truncate(long byteOffset) throws IOException {
        int cluster = entry.startCluster;
        long newFileSize = entry.fileSize - byteOffset;
        long numerOfOldAlloc = entry.fileSize / Partition.DEFAULT_BYTES_PER_SECTOR;
        long numberOfNewAlloc = newFileSize / Partition.DEFAULT_BYTES_PER_SECTOR;
        long delAlloc = numerOfOldAlloc - (numerOfOldAlloc - numberOfNewAlloc);
        if(newFileSize < 0)
        {
            return;
        }
        //fat area
        partition.truncateClusterEntry(cluster, (numerOfOldAlloc - numberOfNewAlloc), delAlloc);
        //data area
        entry.fileSize = byteOffset;
    }
    
    public Enumeration list() {
        //TODO
        Vector list = new Vector();
        return list.elements();
    }

    public Enumeration list(String filter, boolean includeHidden) {
        //TODO
        Vector list = new Vector();
        return list.elements();
    }
    
    public void mkdir() {
        //TODO
    }

    public InputStream openInputStream() throws IOException {
        FileInputStream fis = new FileInputStream(this);
        this.closeListener = fis;
        return fis;
    }
    
    public OutputStream openOutputStream() throws IOException {
        return openOutputStream(0);
    }

    public OutputStream openOutputStream(long byteOffset) throws IOException {
        FileOutputStream fos = new FileOutputStream(this, byteOffset);
        this.closeListener = fos;
        return fos;
    }
    
    public void rename(String newName) throws IOException {
        FileEntry newEntry = this.entry.clone();
        newEntry.name = newName;
        newEntry.updateTimeMillis = new Date().getTime();
        save(newEntry);
    }
        
    public void setFileEntry(String fileName) {
        //TODO
    }
    
    public void setHidden(boolean hidden) throws IOException {
        FileEntry entry = this.entry.clone();
        if (hidden) {
            this.entry.attribute |= HIDDEN;
        } else {
            this.entry.attribute &= ~HIDDEN;            
        }
        save(entry);
    }

    public void setReadable(boolean readable) {
        //nothing to do
    }

    public void setWritable(boolean writable) throws IOException {
        //nothing to do
    }

    /**
     * @throws IOException 
     * 
     */
    public void close() throws IOException {
        if (this.closeListener != null) this.closeListener.close();
        this.closeListener = null;
        this.partition = null;
    }
    
    /**
     * @param stream
     */
    public void removeClosedListener(CloseListener listener) {
        this.closeListener = null;
    }
    
    /**
     * @throws IOException 
     * 
     */
    void save() throws IOException {
        this.partition.saveFileEntry(this.entryIndex, this.entry);
    }

    void save(FileEntry newEntry) throws IOException {
        this.partition.saveFileEntry(this.entryIndex, newEntry);
        this.entry = newEntry;
    }

    
}
