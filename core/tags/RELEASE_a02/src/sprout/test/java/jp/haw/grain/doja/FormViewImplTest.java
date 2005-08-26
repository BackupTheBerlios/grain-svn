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
 * Created on 2005/07/09 15:30:31
 * 
 */
package jp.haw.grain.doja;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;

import jp.haw.grain.sprout.Block;
import jp.haw.grain.sprout.Column;
import jp.haw.grain.sprout.FormDocumentSerializeOperation;
import jp.haw.grain.sprout.InlineElement;
import jp.haw.grain.sprout.xml.BinaryXMLParser;
import jp.haw.grain.xforms.FormControlElement;
import jp.haw.grain.xforms.FormDocument;
import jp.haw.grain.xforms.FormDocumentBuilder;
import jp.haw.grain.xforms.InstanceElement;
import jp.haw.grain.xforms.Processor;
import jp.haw.grain.xforms.SubmissionElement;

import org.xmlpull.v1.XmlPullParserException;

import com.nttdocomo.ui.Display;

/**
 * A test of LayoutManager
 * @version $Id: FormViewImplTest.java 3389 2005-08-19 00:53:01Z go $
 * @author Go Takahashi
 */
public class FormViewImplTest extends TestCase {

    private String name;
    private Processor processor = Processor.getInstance();
    
    public FormViewImplTest() {
    }

    public FormViewImplTest(String name, TestMethod method) {
        super(name, method);
    }
    
    protected void setUp() throws Exception {
        this.name = null;
    }

    public void testPaintCanvas() {
//        FormDocument doc = new FormDocument("form");
//        Element html = new XHTMLElement("html");
//        doc.setDocumentElement(html);
//        Element head = new XHTMLElement("head");
//        html.appendChild(head);
//        Element title = new XHTMLElement("title");
//        head.appendChild(title);
//        title.appendChild(new Text("title"));        
//        XHTMLElement body = new XHTMLElement("body");
//        body.setStyle("line-height", "20px");
//        body.setStyle("background-color", "#9966cc");
//        html.appendChild(body);
//        XHTMLElement p = new XHTMLElement("p");
//        p.appendChild(new Text("VSCL 2.0 Bar Code Capture API は、BarcodeControl インタフェースによって定義されています。この API は、MMAPI (JSR-135) のメディアフレームワークを使用します。"));
//        p.appendChild(new XHTMLElement("br"));
//        p.appendChild(new Text("低レベルAPIのための表示面を定義します。 キャンバス クラスは、 低レベル API で使用するフレームクラスです。 キャンバスは、スクロール機能を持ちません。"));
//        p.setStyle("line-height", "15px");
//        p.setStyle("background-color", "#ccffcc");
//        body.appendChild(p);
//        FormControlElement trigger1 = new FormControlElement("trigger");
//        trigger1.setStyle("width", "100px");
//        trigger1.setStyle("height", "20px");
//        body.appendChild(trigger1);
//        body.appendChild(new XHTMLElement("br"));
//        FormControlElement label1 = new FormControlElement("label");
//        label1.appendChild(new Text("trigger 1"));
//        trigger1.appendChild(label1);
//        FormControlElement trigger2 = new FormControlElement("trigger");
//        body.appendChild(trigger2);
//        body.appendChild(new XHTMLElement("br"));
//        body.appendChild(new Text("タイマ クラスです。 ワンショットタイマとインターバルタイマをサポートしています。 タイマイベントを受け取るリスナとして TimerListener オブジェクトを登録することができます。 タイマイベントが発生すると、リスナオブジェクトの timerExpired メソッドが呼び出されます。"));        
//        body.appendChild(new Text("x-www-form-urlencoded 形式の文字列をデコードした後、 デフォルトエンコーディングの文字列とみなして Unicode 文字列に変換します。"));
//        
//        FormControlElement label2 = new FormControlElement("label");
//        label2.appendChild(new Text("trigger 2"));
//        trigger2.appendChild(label2);
//        FormControlElement trigger3 = new FormControlElement("trigger");
//        trigger3.setStyle("width", "100px");
//        trigger3.setStyle("height", "20px");
//        body.appendChild(trigger3);
//        body.appendChild(new Text("x-www-form-urlencoded 形式の文字列をデコードした後、 デフォルトエンコーディングの文字列とみなして Unicode 文字列に変換します。"));
//        body.appendChild(new Text("低レベルAPIのための表示面を定義します。 キャンバス クラスは、 低レベル API で使用するフレームクラスです。 キャンバスは、スクロール機能を持ちません。"));
//        body.appendChild(new XHTMLElement("br"));
//        FormControlElement input1 = new MockBindedElement("input");
//        body.appendChild(input1);
//        FormControlElement labelInput1 = new FormControlElement("label");
//        input1.appendChild(labelInput1);
//        labelInput1.appendChild(new Text("氏名"));
//        input1.setStyle("width", "100px");
//        body.appendChild(new XHTMLElement("br"));
//        body.appendChild(new Text("VSCL 2.0 Bar Code Capture API は、BarcodeControl インタフェースによって定義されています。この API は、MMAPI (JSR-135) のメディアフレームワークを使用します。"));
//        body.appendChild(new XHTMLElement("br"));
//        FormControlElement output1 = new MockBindedElement("output");
//        body.appendChild(output1);
//        FormControlElement labelOutput1 = new FormControlElement("label");
//        output1.appendChild(labelOutput1);
//        labelOutput1.appendChild(new Text("名前"));
//        output1.setStyle("width", "100px");
//        body.appendChild(new XHTMLElement("br"));
//        FormControlElement input2 = new MockBindedElement("input");
//        body.appendChild(input2);
//        FormControlElement labelInput2 = new FormControlElement("label");
//        input2.appendChild(labelInput2);
//        labelInput2.appendChild(new Text("Age"));
//        body.appendChild(new Text("低レベルAPIのための表示面を定義します。 キャンバス クラスは、 低レベル API で使用するフレームクラスです。 キャンバスは、スクロール機能を持ちません。"));
          
        MockSproutApp app = new MockSproutApp("resource:///index.gbxml");
        app.start();
        FormViewImpl view = (FormViewImpl)Processor.getInstance().getCurrentFormView();
        Block root = view.getRootBlock();
        InlineElement element = ((Column)root.getChildBox(0)).getRow(1).getChildElement(1);
        assertEquals("focus element class name: " + element.getClass().getName(), "jp.haw.grain.sprout.TextBox", element.getClass().getName());
        assertSame("focus element", element, view.getFocused());
        
        Display.setCurrent(view);
    }
    
    public Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new FormViewImplTest("testPaintCanvas", new TestMethod() {
            public void run(TestCase tc) { ((FormViewImplTest)tc).testPaintCanvas(); }
        }));
        return suite;
    }
    
    class MockBindedElement extends FormControlElement { 
        
        public MockBindedElement(String tagName) {
            super(tagName);
        }
        
        public String getBindingSimpleContent() {
            return FormViewImplTest.this.name;
        }
        
        public void setBindingSimpleContent(String text) {
            FormViewImplTest.this.name = text;
        }
    }
    
    
    class MockSproutApp extends GrainApp {

        Throwable error;
        String defaultUri;
        
        MockSproutApp(String defaultUri) {
            setDefaultFormUrl(defaultUri);
            setDefaultFormLocation(DEFAULT_FORM_LOC_HTTP);
        }
        
        public void loadBasicPref() {
        }
        
        public FormDocumentSerializeOperation createSubmissionOperation(SubmissionElement element) {
            if (element == null) {
                return new MockSubmissionOperation(getDefaultFormUrl());
            } else {
                return new MockSubmissionOperation(element.getCanonicalActionUri());
            }
        }
        
        public FormDocumentSerializeOperation createExternalInstanceLoadOperation(InstanceElement element) {
            return super.createExternalInstanceLoadOperation(element);
        }
        
    }
    
    class MockSubmissionOperation implements FormDocumentSerializeOperation {

        private FormDocument doc;
        private String url;

        MockSubmissionOperation(String url) {
            this.url = url;
        }
        
        public FormDocument getFormDocuemnt() {
            return this.doc;
        }

        public boolean hasResponseBody() {
            return true;
        }

        public String getConnectionString() {
            return this.url;
        }

        public int getMode() {
            return Connector.READ;
        }

        public void exec(Connection c) throws IOException {
            InputStream is = null;
            try {
                is = ((InputConnection)c).openInputStream();
                BinaryXMLParser parser = BinaryXMLParser.newInstance(is, "SJIS");
                FormDocumentBuilder builder = new FormDocumentBuilder(parser, getConnectionString());
                builder.build();
                this.doc = builder.getDocument();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } finally {
                if (is != null) is.close();
            }
        }   
        
    }
}
