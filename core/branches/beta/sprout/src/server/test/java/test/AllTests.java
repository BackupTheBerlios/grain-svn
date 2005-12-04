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
 * Created on 2005/05/07
 *
 */
package test;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * サーバ通信を必要としない全テストを実施するテストスイート
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("All Test");
		suite.addTestSuite(jp.haw.grain.framework.xml.BinaryXMLEncoderTest.class);
        suite.addTestSuite(jp.haw.grain.framework.xml.BinaryXMLInputStreamTest.class);
        suite.addTestSuite(jp.haw.grain.framework.xml.BinaryXMLReaderTest.class);
		return suite;
	}
}
