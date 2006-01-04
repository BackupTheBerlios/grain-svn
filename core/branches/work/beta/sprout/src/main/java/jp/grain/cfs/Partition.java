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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

/**
 * 
 * TODO Partition
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class Partition {
    
    public static final int DEFAULT_BYTES_PER_SECTOR = 256;
    public static final short DEFAULT_SECTOR_PER_CLUSTER = 1;
    public static final int DEFAULT_RESERVED_SECTOR = 1;
    public static final short DEFAULT_NUMBER_OF_FATS = 1;
    public static final int DEFAULT_ROOT_ENTRIES = 256;
    public static final int DEFAULT_SECTOR_PER_FAT = 100;
    public static final int DEFAULT_TOTAL_SECTORS = 200;

    //
    public static final int ROOT_ENTRIES = 0;
    public static final int BYTES_PER_SECTOR = 0;
    
    public static final int PROCESS_FIND_NEXT_CLUSTER = -1;
    public static final int PROCESS_FIND_EMPTY_CLUSTER = -2;
    public static final int PROCESS_SEEK_EMPTY_CLUSTER = -3;
    
    //
    
    public static final int ROOT_ENTRY_LENGTH = 32;
    
    int bytesPerSector; //[2]
    short sectorsPerCluster; //[1]
    int reservedSector; //[2]
    short numberOfFATs; //[1]
    int rootEntries; //[2]
    int totalSectors; //[2]
    int sectorsPerFAT; //[2]
    String fileSystemType; //[8]

    private String scratchPadUrl;
    
    Partition(String scratchPadUrl) {
        this.scratchPadUrl = scratchPadUrl;
    }
    
    void loadInfo() throws IOException {
        
        DataInputStream dis = null;
        try {
            dis = Connector.openDataInputStream(this.scratchPadUrl);
            this.bytesPerSector = dis.readUnsignedShort();
            this.sectorsPerCluster = (short)dis.readUnsignedByte();
            this.reservedSector = dis.readUnsignedShort();
            this.numberOfFATs = (short)dis.readUnsignedByte();
            this.rootEntries = dis.readUnsignedShort();
            this.totalSectors = dis.readUnsignedShort();
            this.sectorsPerFAT = dis.readUnsignedShort();
            byte[] fsType = new byte[8];
            dis.read(fsType);
            this.fileSystemType = new String(fsType);
        } finally {
            if(dis != null) dis.close();
        }
    }
    
    public boolean isValidFormat() {
        return this.fileSystemType.equals(ScratchPadProvider.FSTYPE_KFAT12);
    }
        
    public String toString() {
        
        StringBuffer buf = new StringBuffer();
        buf.append("\n+ RESERVED AREA ");
        buf.append("\n  BYTES_PER_SECTOR    = " + bytesPerSector);
        buf.append("\n  SECTORS_PER_CLUSTER = " + sectorsPerCluster);
        buf.append("\n  RESERVED_SECTOR     = " + reservedSector);
        buf.append("\n  NUMBER_OF_FATS      = " + numberOfFATs);
        buf.append("\n  ROOT_ENTRIES        = " + rootEntries);
        buf.append("\n  TOTAL_SECTORS       = " + totalSectors);
        buf.append("\n  SECTORS_PER_FAT     = " + sectorsPerFAT);
        buf.append("\n  FILE_SYSTEM_TYPE    = " + fileSystemType);
        buf.append("\n- RESERVED AREA \n");
        
        return buf.toString();
    }

    /**
     * @param filename
     * @return
     * @throws IOException 
     */
    FileAccessor findFileEntry(String filename) throws IOException {
        DataInputStream dis = null;
        FileAccessor entry = new FileAccessor(this);
        byte[] filenameBytes = filename.getBytes();
        try {
            int offset = this.bytesPerSector * (this.reservedSector + this.sectorsPerFAT);
            dis = Connector.openDataInputStream(this.scratchPadUrl + ";pos=" + offset);
            byte[] buf = new byte[32];
            for (int i = 0; i < this.rootEntries; ++i) {
                dis.read(buf, 0, 17);
                if (buf[0] == 0x00 || buf[0] == 0xe5 || !sameName(buf, filenameBytes)) {
                    dis.skip(15); // = 1 + 4 + 4 + 2 + 4
                    continue;
                }
                entry.entryIndex = i;
                dis.read(buf, 17, 15);
                entry.init(buf);
                return entry;
            }
            entry.init(filename);
            return entry;
        } finally {
            if(dis != null) dis.close();
        }
    }
    
    boolean sameName(byte[] target, byte[] source) {
        for (int i = 0; i < 17; ++i) {
            if (i > source.length - 1) {
                if (target[i] != 0) return false;
                continue;
            }
            if (target[i] != source[i]) return false;
        }
        return true;
    }
    
//    /**
//     * @param entry
//     * @throws IOException 
//     */
//    void saveFileEntry(FileEntry entry) throws IOException {
//        DataOutputStream dos = null;
//        try {
//            int offset = this.bytesPerSector * (this.reservedSector + this.sectorsPerFAT);
//            if (entry.entryIndex == -1) {
//                entry.entryIndex = seekEmptyFileEntry();
//                if (entry.entryIndex == -1) throw new IOException("no more empty file entry");
//            }
//            if (entry.startCluster < 0) {
//                entry.startCluster = processClusterEntry(2, PROCESS_SEEK_EMPTY_CLUSTER);
//            }
//            offset += entry.entryIndex * ROOT_ENTRY_LENGTH;
//            dos = Connector.openDataOutputStream(this.scratchPadUrl + ";pos=" + offset);
//            byte[] buf = new byte[17];
//            byte[] name = entry.name.getBytes();
//            System.arraycopy(name, 0, buf, 0, name.length > 17 ? 17 : name.length);
//            dos.write(buf);
//            dos.write(entry.attribute);
//            dos.writeInt((int)entry.createTimeMillis >>> 10);
//            dos.writeInt((int)entry.updateTimeMillis >>> 10);
//            dos.writeShort(entry.startCluster);
//            dos.writeInt((int)entry.fileSize);
//        } finally {
//            if(dos != null) dos.close();
//        }
//    }
    
    int seekEmptyFileEntry() throws IOException {
        DataInputStream dis = null;
        try {
            int offset = this.bytesPerSector * (this.reservedSector + this.sectorsPerFAT);
            dis = Connector.openDataInputStream(this.scratchPadUrl + ";pos=" + offset);
            for (int i = 0; i < this.rootEntries; ++i) {          
                int first = dis.read();
                if (first == 0x00 || first == 0xe5) return i;
                dis.skip(ROOT_ENTRY_LENGTH - 1);
            }
            return -1;
        } finally {
            if(dis != null) dis.close();
        }
    }
 
    /**
     * @param i
     * @throws IOException 
     */
    void removeFileEntry(int entryIndex) throws IOException {
        if (entryIndex == -1) throw new IOException("unable to delete unexisting file");
        DataOutputStream dos = null;
        try {
            int offset = this.bytesPerSector * (this.reservedSector + this.sectorsPerFAT) + entryIndex * ROOT_ENTRY_LENGTH;
            dos = Connector.openDataOutputStream(this.scratchPadUrl + ";pos=" + offset);
            dos.write(0x00);
        } finally {
            if(dos != null) dos.close();
        }
    }
    
    /**
     * @throws IOException 
     * 
     */
    public void init() throws IOException {
        loadInfo();
    }

    /**
     * @throws IOException 
     * 
     */
    public void format(boolean completely) throws IOException {
        if (completely) clearAll();
        DataOutputStream dos = null;
        try {
            dos = Connector.openDataOutputStream(this.scratchPadUrl);
            dos.writeShort(this.bytesPerSector);
            dos.writeByte(this.sectorsPerCluster);
            dos.writeShort(this.reservedSector);
            dos.writeByte(this.numberOfFATs);
            dos.writeShort(this.rootEntries);
            dos.writeShort(this.totalSectors);
            dos.writeShort(this.sectorsPerFAT);
            dos.write(this.fileSystemType.getBytes());
        } finally {
            if(dos != null) dos.close();
        }
    }
    
    private void clearAll() throws IOException {
        DataOutputStream dos = null;
        try {
            dos = Connector.openDataOutputStream(this.scratchPadUrl);
            byte[] nullData = new byte[this.bytesPerSector]; 
            for (int i = 0; i < this.totalSectors; ++i) {
                dos.write(nullData);
            }
        } finally {
            if(dos != null) dos.close();
        }        
    }
    
    /**
     * @param i
     * @return
     * @throws IOException 
     */
    public int processClusterEntry(int cluster, int type) throws IOException {
        final byte[] startFatBlock = new byte[3];
        byte[] currentFatBlock = null;
        DataInputStream dis = null;
        int current = cluster;
        int entry = 0x000;
        try {
            final int maxFatBlock = this.sectorsPerFAT / 3;
            dis = Connector.openDataInputStream(createFatBlockUrlFor(cluster));       
            dis.read(startFatBlock);
            for (int i = (cluster >>> 1); i < maxFatBlock; ++i) {
                do {
                    entry = getEntryFrom(currentFatBlock == null ? startFatBlock : currentFatBlock, current);
                    if (type == PROCESS_FIND_NEXT_CLUSTER) {
                        if (entry == 0x000 || entry == 0x001) {
                            throw new IOException("broken fat entry : #" + cluster);
                        } else if (entry == 0xff7) {
                            continue;
                        } else if (entry >= 0xff8) {
                            return -1;
                        }
                        return entry;
                    } else {
                        if (entry == 0x000) return current;
                    }
                } while ((++current & 1) != 0);
                if (currentFatBlock == null) currentFatBlock = new byte[3];
                dis.read(currentFatBlock);
            }
            return -1;
        } finally {
            if (dis != null) dis.close();
            if (type == PROCESS_FIND_EMPTY_CLUSTER) {
                updateClusterEntry(startFatBlock, cluster, current);
            }
            if (type != PROCESS_FIND_NEXT_CLUSTER) {
                updateClusterEntry(currentFatBlock == null ? startFatBlock : currentFatBlock, current, 0xfff);
            }
        }
    }

    /**
     * @param i
     * @param l
     * @return
     * @throws IOException 
     */
    public OutputStream getOutputStreamFor(int cluster, long byteOffset) throws IOException {
        return Connector.openOutputStream(createDataAreaUrlFor(cluster, byteOffset));
    }
    
    /**
     * @param i
     * @return
     * @throws IOException 
     */
    public InputStream getInputStreamFor(int cluster, long byteOffset) throws IOException {
        return Connector.openInputStream(createDataAreaUrlFor(cluster, byteOffset));
    }
    
    /**
     * @param i
     * @param next
     * @throws IOException 
     */
    private void updateClusterEntry(byte[] fatBlock, int cluster, int entry) throws IOException {
        DataOutputStream dos = null;
        try {
            dos = Connector.openDataOutputStream(createFatBlockUrlFor(cluster));
            if ((cluster & 1) == 0) {
                fatBlock[0] = (byte)((entry >>> 4) & 0x0ff);
                fatBlock[1] = (byte)(((entry << 4) & 0x0f0) | (fatBlock[1] & 0x0f));
            } else {
                fatBlock[1] = (byte)((fatBlock[1] & 0xf0) | ((entry >>> 8) & 0x00f));
                fatBlock[2] = (byte)(entry & 0x0ff);
            }
            dos.write(fatBlock);
        } finally {
            if(dos != null) dos.close();
        }
    }
    
    private String createFatBlockUrlFor(int cluster) {
        final long reservedAreaOffsetBytes = this.bytesPerSector * this.reservedSector;
        final long fatOffsetBytes = (cluster >>> 1) * 3;
        final long totalOffsetBytes = reservedAreaOffsetBytes + fatOffsetBytes;
        final long length = this.sectorsPerFAT * this.bytesPerSector - fatOffsetBytes;
        return getUrl(totalOffsetBytes, length);
    }

    private String createDataAreaUrlFor(int cluster, long byteOffset) {
        final long bytesPerCluster = this.bytesPerSector * this.sectorsPerCluster;
        final long baseOffsetBytes = this.bytesPerSector * (this.reservedSector + this.sectorsPerFAT) + this.rootEntries * Partition.ROOT_ENTRY_LENGTH;
        final long totalOffsetBytes = baseOffsetBytes + cluster * bytesPerCluster + byteOffset;
        final long length = bytesPerCluster - byteOffset;
        return getUrl(totalOffsetBytes, length);
    }    

    private String getUrl(long pos, long length) {
        return this.scratchPadUrl + ";pos=" + pos + ",length=" + length;        
    }
    
    private static int getEntryFrom(byte[] fatBlock, int cluster) {
        if ((cluster & 1) == 0) { // even               
            return ((fatBlock[0] & 0xff) << 4) | ((fatBlock[1] & 0xf0) >>> 4);                 
        } else { // odd
            return ((fatBlock[1] & 0x0f) << 8) | (fatBlock[2] & 0xff);
        }
    }
    
    private static String trimToString(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] == 0) continue;
            buf.append((char)bytes[i]);
        }
        return buf.toString();
    }

    /**
     * @param newEntry
     * @throws IOException 
     */
    public void saveFileEntry(int entryIndex, FileEntry entry) throws IOException {
        DataOutputStream dos = null;
        try {
            if (entry.startCluster == 0) {
                entry.startCluster = processClusterEntry(2, PROCESS_SEEK_EMPTY_CLUSTER);
            }
            int offset = this.bytesPerSector * (this.reservedSector + this.sectorsPerFAT);
            offset += entryIndex * ROOT_ENTRY_LENGTH;
            dos = Connector.openDataOutputStream(this.scratchPadUrl + ";pos=" + offset);
            entry.writeTo(dos);
        } finally {
            if(dos != null) dos.close();
        }    
    }
}
