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
 * �T�[�o�ʐM��K�v�Ƃ��Ȃ��S�e�X�g�����{����e�X�g�X�C�[�g
 * @author go
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("All Test");
		suite.addTestSuite(jp.haw.grain.framework.xml.BinaryXMLEncoderTest.class);
		return suite;
	}
}
