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
import java.io.OutputStream;

/**
 * 
 * TODO FileOutputStream
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FileOutputStream extends OutputStream implements CloseListener {
    
    private FileAccessor fileEntry;
    private long writtenBytes;
    private long writtenClusters;
    private int cluster;
    private OutputStream clusterOut;
    
    /**
     * @param entry
     * @param byteOffset
     */
    public FileOutputStream(FileAccessor entry, long byteOffset) {
        this.fileEntry = entry;
        this.writtenBytes = byteOffset % (this.fileEntry.partition.bytesPerSector * this.fileEntry.partition.sectorsPerCluster);
        this.cluster = entry.entry.startCluster;
    }

    public void write(int b) throws IOException {
        if (this.fileEntry == null) throw new IOException("already closed");
        Partition part = this.fileEntry.partition;
        if (this.writtenBytes >= part.bytesPerSector * part.sectorsPerCluster) {
            this.clusterOut.close();
            int next = part.processClusterEntry(this.cluster, Partition.PROCESS_FIND_NEXT_CLUSTER);
            if (next < 0) {
                next = part.processClusterEntry(this.cluster, Partition.PROCESS_FIND_EMPTY_CLUSTER);
                if (next < 0) throw new IOException("unable to write data : no more empty cluster");
            }
            this.clusterOut = null;
            this.cluster = next;
            this.writtenBytes = 0;
            this.writtenClusters++;
        }
        if (this.clusterOut == null) {
            this.clusterOut = part.getOutputStreamFor(this.cluster, this.writtenBytes);
        }
        this.clusterOut.write(b);
        this.writtenBytes++;
    }
    
    public void close() throws IOException {
        try {
            if (this.clusterOut != null) {
                flush();
                this.clusterOut.close();
            }
        } finally {
            this.fileEntry.removeClosedListener(this);
            this.fileEntry.save();
            this.fileEntry = null;
            this.clusterOut = null;
        }
    }

    public void flush() throws IOException {
        if (this.fileEntry == null) throw new IOException("already closed");
        if (this.clusterOut != null) this.clusterOut.flush();
        if (this.fileEntry.entry.fileSize < totalWrittenBytes()) {
            this.fileEntry.entry.fileSize = totalWrittenBytes();
        }
    }
    
    private long totalWrittenBytes() {
        long wrriten = writtenClusters * this.fileEntry.partition.bytesPerSector * this.fileEntry.partition.sectorsPerCluster;
        return wrriten + this.writtenBytes;
    }

    /* (non-Javadoc)
     * @see jp.grain.cfs.CloseListener#suspend()
     */
    public void suspend() throws IOException {
        try {
            if (this.clusterOut != null) this.clusterOut.close();
        } finally {
            this.clusterOut = null;
        }
    }
    
}
