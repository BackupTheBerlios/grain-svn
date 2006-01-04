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
import java.io.InputStream;

/**
 * TODO FileInputStream
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FileInputStream extends InputStream implements CloseListener {

    private FileAccessor fileEntry;
    private InputStream clusterIn;
    private int totalReadedBytes;
    private int readedBytes;
    private int readedClusters;
    private int cluster;

    /**
     * @param entry
     */
    public FileInputStream(FileAccessor fileEntry) {
        this.fileEntry = fileEntry;
        this.cluster = fileEntry.entry.startCluster;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        if (this.fileEntry == null) throw new IOException("already closed");
        Partition part = this.fileEntry.partition;
        if (this.totalReadedBytes >= this.fileEntry.fileSize()) {
            return -1;
        } else if (this.readedBytes >= part.bytesPerSector * part.sectorsPerCluster) {
            this.clusterIn.close();
            int next = part.processClusterEntry(this.cluster, Partition.PROCESS_FIND_NEXT_CLUSTER);
            if (next < 0) throw new IOException("uable to find more data");
            this.clusterIn = null;
            this.cluster = next;
            this.readedBytes = 0;
            this.readedClusters++;
        }
        if (this.clusterIn == null) {
            this.clusterIn = part.getInputStreamFor(this.cluster, this.readedBytes);
        }
        int b = this.clusterIn.read();
        if (b == -1) throw new IOException("uable to find more data");
        this.readedBytes++;
        this.totalReadedBytes++;
        return b;
    }
    
    public void close() throws IOException {
        try {
            if (this.clusterIn != null) this.clusterIn.close();
        } finally {
            this.fileEntry.removeClosedListener(this);
            this.fileEntry = null;
            this.clusterIn = null;
        }
    }

    /* (non-Javadoc)
     * @see jp.grain.cfs.CloseListener#suspend()
     */
    public void suspend() throws IOException {
        try {
            if (this.clusterIn != null) this.clusterIn.close();
        } finally {
            this.clusterIn = null;
        }
    }
}
