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
 * Created on 2005/06/16 14:44:20
 * 
 */
package jp.grain.sprout.test.doja;

import j2meunit.framework.Test;
import j2meunit.framework.TestResult;
import j2meunit.framework.TestSuite;
import j2meunit.util.StringUtil;

import com.nttdocomo.ui.IApplication;

/**
 * Runs the tests on doja.
 * 
 * @version  $Id$
 * @author Go Takahashi
 */
public class TestRunner extends IApplication {

    public void start() {
        TestSuite suite = new TestSuite();
        suite.addTest(new jp.grain.sprout.xml.BinaryXMLParserTest().suite());
        suite.addTest(new jp.grain.sprout.xml.BinaryXMLSerializerTest().suite());
        suite.addTest(new jp.grain.xforms.FormDocumentBuilderTest().suite());
        suite.addTest(new jp.grain.sprout.LayoutManagerTest().suite());      
        suite.addTest(new jp.grain.xforms.RowTest().suite());
        suite.addTest(new jp.grain.xforms.ColumnTest().suite());
        suite.addTest(new jp.grain.doja.CharactorSequenceTest().suite());
        suite.addTest(new jp.grain.doja.ButtonTest().suite());
        suite.addTest(new jp.grain.xforms.BlockTest().suite());
        suite.addTest(new jp.grain.doja.FormViewImplTest().suite());
        DojaTestRunner runner = new DojaTestRunner();
        runner.doRun(suite);
    }
    
    class DojaTestRunner extends j2meunit.textui.TestRunner {
        
        public final void doRun(Test suite) {
            
            TestResult result = createTestResult();
            result.addListener(this);

            long startTime = System.currentTimeMillis();
            suite.run(result);
            long endTime = System.currentTimeMillis();
            fWriter.println();
            fWriter.print("Total time: ");
            fWriter.println(StringUtil.elapsedTimeAsString(endTime - startTime));
            print(result);
            fWriter.println();

            if (result.wasSuccessful()) {
                fWriter.println("TEST SUCCESSFUL");
            } else {
                fWriter.println("TEST FAILED");
            }
       }
               
    }
}
