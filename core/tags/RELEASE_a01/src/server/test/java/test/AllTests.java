/*
 * $Header$
 * 
 * Created on 2005/05/07
 *
 */
package test;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * サーバ通信を必要としない全テストを実施するテストスイート
 * @author go
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("All Test");
		suite.addTestSuite(jp.haw.grain.framework.xml.BinaryXMLEncoderTest.class);
		return suite;
	}
}
