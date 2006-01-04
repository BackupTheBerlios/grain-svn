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
 * Created on 2006/01/04 2:46:55
 * 
 */
package jp.grain.cfs;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * TODO FileEntry
 * 
 * @version $Id$
 * @author Go Takahashi
 */
class FileEntry {
            
    public static final long INT_MASK = 0xffffffffL;
    
    String name;
    byte attribute; //[1]
    long createTimeMillis;
    long updateTimeMillis;
    int startCluster; //[2]
    long fileSize; //[4]
    
    FileEntry() {
    }
    
    FileEntry(byte[] data) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < 17; ++i) {
                byte b = dis.readByte();
                if (b == 0) continue;
                buf.append((char)(b & 0xff));
            }
            this.name = buf.toString();
            this.attribute = dis.readByte();
            this.createTimeMillis = (dis.readInt() & INT_MASK) << 10;
            this.updateTimeMillis = (dis.readInt() & INT_MASK) << 10;
            this.startCluster = dis.readUnsignedShort();
            this.fileSize = dis.readInt() & INT_MASK;
            dis.close();
        } catch (IOException e) {
            // will not reach here.
        }
    }
            
    FileEntry clone() {
        FileEntry entry = new FileEntry();
        entry.name = this.name;
        entry.attribute = this.attribute;
        entry.createTimeMillis = this.createTimeMillis;
        entry.updateTimeMillis = this.updateTimeMillis;
        entry.startCluster = this.startCluster;
        entry.fileSize = this.fileSize;
        return entry;
    }
    
    void writeTo(DataOutputStream dos) throws IOException {
        byte[] buf = new byte[17];
        byte[] name = this.name.getBytes();
        System.arraycopy(name, 0, buf, 0, name.length > 17 ? 17 : name.length);
        dos.write(buf);
        dos.write(this.attribute);
        dos.writeInt((int)this.createTimeMillis >>> 10);
        dos.writeInt((int)this.updateTimeMillis >>> 10);
        dos.writeShort(this.startCluster);
        dos.writeInt((int)this.fileSize);        
    }
}