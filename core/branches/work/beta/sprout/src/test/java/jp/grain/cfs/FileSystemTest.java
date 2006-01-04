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
 * Created on 2005/12/28 22:23:42
 * 
 */
package jp.grain.cfs;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;

/**
 * TODO FileSystemTest
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class FileSystemTest extends TestCase {
    
    public FileSystemTest() {
    }

    public FileSystemTest(String name, TestMethod method) {
        super(name, method);
    }

    
    private void init() {
    }
    
    protected void setUp() throws Exception {
        try {
            ScratchPadProvider.initDefault("scratchpad:///0");
            FileSystem.formatCompletely("file:///");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void testOpen() {
        try {
            FileAccessor fe = FileSystem.open("file:///test.txt");
            assertNotNull("file entry not null", fe);
            assertTrue("not file exists", !fe.exsists());
            fe.close();
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testGetFileName() {
        try {
            FileAccessor fe = null;
            fe = FileSystem.open("file:///test0.txt");
            fe.close();
            assertEquals("flat file", "test0.txt", fe.getName());
            fe = FileSystem.open("file:///test/test1.txt");
            fe.close();
            assertEquals("dir file", "test1.txt", fe.getName());
            fe = FileSystem.open("file:///test/dir");
            fe.close();
            assertEquals("dir not end with slash", "dir", fe.getName());
            fe = FileSystem.open("file:///test/");
            fe.close();
            assertEquals("dir end with slash", "test", fe.getName());
            fe = FileSystem.open("file:///");
            fe.close();
            assertEquals("root dir", "", fe.getName());
            try {
                fe = FileSystem.open("file://");
                fail("detect root slash");
                fe.close();
            } catch (IOException e) {
            }
            try {
                fe = FileSystem.open("file://partition/test0.txt");
                fail("detect ghost partition...");
                fe.close();
            } catch (IOException ioe) {
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testCreateAndDeleteFile() {
        try {
            FileAccessor fe = null;
            fe = FileSystem.open("file:///test0.txt");
            assertTrue("before create : file not exists", !fe.exsists());
            fe.create();
            assertTrue("after create : file exists", fe.exsists());
            assertEquals("size = 0", 0L, fe.fileSize());
            fe.close();
            fe = FileSystem.open("file:///test0.txt");
            assertTrue("reopen : file exists", fe.exsists());
            assertEquals("size = 0", 0L, fe.fileSize());
            fe.delete();
            assertTrue("after delete : file not exists", !fe.exsists());
            fe.close();
            fe = FileSystem.open("file:///test0.txt");
            assertTrue("rereopen : not exists", !fe.exsists());
            fe.close();
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testWriteAndReadFileData() {
        try {
            FileAccessor fe = null;
            fe = FileSystem.open("file:///test0.txt");
            fe.create();
            assertEquals("before write : size = 0", 0L, fe.fileSize());
            OutputStream os = fe.openOutputStream();
            byte[] data = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 }; 
            for (int i = 0; i < 195; ++i) {
                os.write(data);
            }
            assertEquals("before flush : size = 0", 0L, fe.fileSize());
            os.flush();
            assertEquals("after flush : size = 5 * 195", 5L * 195, fe.fileSize());
            os.close();
            fe.close();
            fe = FileSystem.open("file:///test0.txt");
            InputStream is = fe.openInputStream();
            for (int i = 0; i < 195; ++i) {
                byte[] buf = new byte[5];
                is.read(buf);
                assertSame("series[" + i + "]", data, buf);
            }
            assertTrue("eof", is.read() == -1);
            is.close();
            fe.close();
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testRename() {
        try {
            FileAccessor fe = null;
            fe = FileSystem.open("file:///test0.txt");
            fe.create();
            assertEquals("before write : size = 0", 0L, fe.fileSize());
            OutputStream os = fe.openOutputStream();
            byte[] data = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04 }; 
            os.write(data);
            try { 
                fe.rename("rename0.jpg");
                fail("unable to rename while writing");
            } catch (IOException e) {
            }
            os.close();
            fe.rename("rename0.jpg");
            assertEquals("after rename : name = rename0.jpg", "rename0.jpg", fe.getName());
            fe.close();
            fe = FileSystem.open("file:///test0.txt");
            assertTrue("test0.txt not exists", !fe.exsists());
            fe.close();
            fe = FileSystem.open("file:///rename0.jpg");
            assertTrue("ename0.jpg exists", fe.exsists());
            InputStream is = fe.openInputStream();
            byte[] buf = new byte[5];
            is.read(buf);
            assertSame("read data", data, buf);
            assertTrue("eof", is.read() == -1);
            is.close();
            fe.close();
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    
    public void testFormat() {
        InputConnection conn = null;
        DataInputStream dis = null;
        try {
            conn = (InputConnection)Connector.open("scratchpad:///0");
            dis = conn.openDataInputStream();
            assertEquals("bytes per sector", 256, dis.readUnsignedShort());
            assertEquals("sector per cluster", 1, dis.readUnsignedByte());
            assertEquals("reserved sector", 1, dis.readUnsignedShort());
            assertEquals("number of fats", 1, dis.readUnsignedByte());
            assertEquals("root entries", 256, dis.readUnsignedShort());
            assertEquals("total sectors", 200, dis.readUnsignedShort());
            assertEquals("sector per fat", 100, dis.readUnsignedShort());
        } catch (IOException e) {
            fail(e.toString());
        } finally {
            if (dis != null) try { dis.close(); } catch (IOException e) {};
            if (conn != null) try { conn.close(); } catch (IOException e) {};
        }        
    }
    
    private void assertSame(String note, byte[] expected, byte[] target) {
        assertEquals(note, expected.length, target.length);
        for (int i = 0; i < expected.length; ++i) {
            assertEquals(note + " : data[" + i + "]", expected[i], target[i]);
        }        
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new FileSystemTest("testFormat", new TestMethod() {
            public void run(TestCase tc) { ((FileSystemTest)tc).testFormat(); }
        }));
        suite.addTest(new FileSystemTest("testOpen", new TestMethod() {
            public void run(TestCase tc) { ((FileSystemTest)tc).testOpen(); }
        }));
        suite.addTest(new FileSystemTest("testGetFileName", new TestMethod() {
            public void run(TestCase tc) { ((FileSystemTest)tc).testGetFileName(); }
        }));
        suite.addTest(new FileSystemTest("testCreateAndDeleteFile", new TestMethod() {
            public void run(TestCase tc) { ((FileSystemTest)tc).testCreateAndDeleteFile(); }
        }));
        suite.addTest(new FileSystemTest("testWriteAndReadFileData", new TestMethod() {
            public void run(TestCase tc) { ((FileSystemTest)tc).testWriteAndReadFileData(); }
        }));
        suite.addTest(new FileSystemTest("testRename", new TestMethod() {
            public void run(TestCase tc) { ((FileSystemTest)tc).testRename(); }
        }));
        return suite;
    }
}
