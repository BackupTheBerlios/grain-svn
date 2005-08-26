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
 * Created on 2005/07/03 16:27:05
 * 
 */
package jp.haw.grain.sprout;

import jp.haw.grain.sprout.Block;
import jp.haw.grain.sprout.Box;
import jp.haw.grain.sprout.Column;
import jp.haw.grain.xforms.FormControlElement;
import jp.haw.grain.xforms.FormDocument;
import jp.haw.grain.xforms.Processor;
import jp.haw.grain.xforms.RenderableElement;
import jp.haw.grain.xforms.Visitor;
import jp.haw.grain.xforms.XFormsElement;
import jp.haw.grain.xforms.XHTMLElement;

import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Text;

/**
 * フォームドキュメントから物理レイアウトを構築する。
 * 
 * @version $Id: LayoutManager.java 3385 2005-08-18 22:12:13Z go $
 * @author Go Takahashi
 */
public class LayoutManager implements Visitor {

    private FormDocument doc;
    private FormView view;
    private Box box;
//    private LayoutElementFactory factory;
    
    /**
     * 
     */
    public LayoutManager(FormDocument doc, FormView view) {
        this.doc = doc;
        this.view = view;
//        this.factory = factory;
    }
    
    public void layout() {
        if (this.doc == null) {
            this.view.setTitle("指定されたフォームが見つかりませんでした。");
            Block root = new Block(new XHTMLElement("body"));
            this.view.setRootBlock(root);
            root.apply();
            return;
        }
        try {
            processHead();
            processBody();
            box.apply();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("rendered : " + this);
    }
    
    private void processHead() {
        String title = this.doc.xpathSelectString("/html/head/title/text()");
        if (title != null) this.view.setTitle(title);
    }
    
    private void processBody() {
        RenderableElement body = (RenderableElement)this.doc.xpathSelectElement("/html/body");
        Block block = new Block(body, this.view.getWidth());
        this.view.setRootBlock(block);
        this.box = block;
        traverseChildren(body);
    }

    private void processTag(Element tag) throws ParseException {
        String tagName = tag.getTagName();
        System.out.println("processing tag : " + tagName);
        
        if (Processor.XFORMS_NS.equals(tag.gsNamespaceUri(null))) {
            if (tag instanceof FormControlElement) {
                processFormControlTag((FormControlElement)tag);
            } else {
                processXFormsTag((XFormsElement)tag);
            }
        } else {
            processHtmlTag(tag);
        }
        // TODO process global attributes
        
    }
    
    private void traverseChildren(Element element) {
        for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
            Box saved = this.box;
            try {
                if (n instanceof RenderableElement) {
                    RenderableElement child = (RenderableElement)n;
                    child.accept(this);
                    traverseChildren(child);
                } else if (n instanceof Text) {
                    CharactorSequence cs = new CharactorSequence((Text)n);
                    this.box.append(cs);
                }
            } catch (RuntimeException e) {
                System.out.println(e.toString());
            } finally {
                this.box = saved;
            }
        }
    }
    
//    /**
//     * @param element
//     */
//    private void processInlineBlockElement(Element element) {
//        Renderable r = findImmediatelyEnclosingRendarable(element);
//        Renderer p = (r != null) ? r.getRenderer() : null;
//        int hPos = 0;
//        int vPos = 0;
//        int maxHeight = 0;
//        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
//        }
//        if (p != null && !p.isInline()) p.setHeight(vPos + maxHeight);       
//    }
//
//    private void processBlockLevelElement(Element element) {
//        Renderable r = findImmediatelyEnclosingRendarable(element);
//        Renderer p = (r != null) ? r.getRenderer() : null; 
//        int hPos = 0;
//        int vPos = 0;
//        int maxHeight = 0;
//        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
//            if (!(child instanceof Renderable)) {
//                processNode(child);
//                continue;
//            }
//            Renderer c = this.view.createRenderer(child);
//            if (c == null) {
//                processNode(child);
//                continue;
//            }
//            ((Renderable)child).setRenderer(c);
//            if (c.getWidth() < 0) {
//                c.setWidth(p.getWidth());
//            }
//            processNode(child);
//            int expectedWidth = hPos;
//            expectedWidth += c.getWidth();
//            if (expectedWidth > p.getWidth() || !c.isInline()) {
//                hPos = 0;
//                vPos += maxHeight;
//                maxHeight = 0;
//            }
//            c.setHPos(hPos);
//            c.setVPos(vPos);
//            hPos += c.getWidth();
//            if (c.getHeight() > maxHeight) maxHeight = c.getHeight();
//        }
//        if (p != null && !p.isInline()) p.setHeight(vPos + maxHeight);       
//    }

    private void processNode(Node node) {
        if (node instanceof Element) {
            System.out.println("element : '" + ((Element)node).getTagName() + "'");
            processTag((Element)node);
        } else {
            System.out.println("other ; '" + node.toString().trim() + "'");
            //processText((Text)node);
        }
    }
        
//    private void fireSizeCommited(Vector sizeUndefinedElements, int allocatableWidth) {
//        int average = allocatableWidth / sizeUndefinedElements.size();
//        for (int i = 0; i < sizeUndefinedElements.size(); ++i) {
//            int width;
//            if (allocatableWidth < average) {
//                width = allocatableWidth;
//            } else {
//                width = average;
//            }
//            Renderable r = (Renderable)sizeUndefinedElements.elementAt(i);
//            r.notifySizeCommited(width);
//            allocatableWidth -= r.getRenderer().getRealWidth();
//        }
//    }
    
    /**
     * @param element
     */
    private void processFormControlTag(FormControlElement element) throws ParseException {
//        FormControl ctrl = (FormControl)getMapping(element);
//        if (ctrl == null) {
//            System.out.println("create new form control : " + element.getTagName());    
//            ctrl = new FormControl(element);
//            System.out.println("soft key : " + ctrl.isSoftKey());
//            if (ctrl.isSoftKey()) {
//                System.out.println("set up soft key control : " + ctrl.getLabelName()); 
//                _softKey1Control = ctrl;
//                setSoftLabel(Panel.SOFT_KEY_1, ctrl.getLabelName());
//            } else {
//                System.out.println("set up nomal control"); 
//                if (ctrl.getLabel() != null) {
//                    add(ctrl.getLabel());
//                    layout_.br();
//                }
//                if (ctrl.getComponent() != null) add(ctrl.getComponent());
//                registMapping(element, ctrl);
//            }
//        }
//        if (_rendered) {
//            System.out.println("updating form control");
//            ctrl.update();
//        }
    }

//    private void registMapping(FormControlElement element, FormControl ctrl) {
//        Object[] mapping = new Object[]{ element, ctrl };
//        _controlMap.addElement(mapping);
//    }
//
//    private FormControl getMapping(FormControlElement element) {
//        for (int i = 0; i < _controlMap.size(); ++i) {
//            Object[] mapping = (Object[])_controlMap.elementAt(i);
//            if (mapping[0] == element) return (FormControl)mapping[1];
//        }
//        return null;
//    }
    
    private void processXFormsTag(XFormsElement element) throws ParseException {
        String tagName = element.getTagName();
        System.out.println("other xforms tag : " + tagName);
        if (tagName.equals("case")) {
            System.out.println("test render case : " + element.getAttribute("case"));
            if (!"true".equals(element.getAttribute("selected"))) return;
        }
        System.out.println("render other xforms tag");
        traverseChildren(element);
    }

    private void processHtmlTag(Element tag) throws ParseException {
        String tagName = tag.getTagName();  
        if (tagName.equals("body")) {
            traverseChildren(tag);
        } else {
            traverseChildren(tag);
        }
    }
    
    /**
     * @param element
     */
    public void visit(FormControlElement element) {
        String name = element.getTagName();
        if ("trigger".equals(name) || "submit".equals(name)) {
            Element labelElem = element.xpathSelectElement("label");
            Text label = (labelElem != null) ? (Text)labelElem.getFirstChild() : new Text("");
            InlineElement button = new Button(element, label);
            this.box.append(button);
            this.view.addNavigativeElement(button);
        } else if ("input".equals(name)) {
            Element labelElem = element.xpathSelectElement("label");
            if (labelElem != null) {
                Text label = (Text)labelElem.getFirstChild();
                this.box.append(new CharactorSequence(label));
            }
            InlineElement textBox = new TextBox(element);
            this.box.append(textBox);
            this.view.addNavigativeElement(textBox);
        } else if ("output".equals(name)) {
            Element labelElem = element.xpathSelectElement("label");
            if (labelElem != null) {
                Text label = (Text)labelElem.getFirstChild();
                this.box.append(new CharactorSequence(label));
            }
            InlineElement text = new jp.haw.grain.sprout.Label(element);
            this.box.append(text);
        } else {
            throw new RuntimeException("fce unexpected tag: " +  name);
        }
    }

    /**
     * @param element
     */
    public void visit(XFormsElement element) {
        String name = element.getTagName();
        if ("group".equals(name)) {
        } else {
            throw new RuntimeException("xfe unexpected tag: " +  name);
        }        
    }

    /**
     * @param element
     */
    public void visit(XHTMLElement element) {
        String name = element.getTagName();
        if ("div".equals(name)) {
            Block block = new Block(element);
            this.box.addChildBox(block);
            this.box = block;
        } else if ("p".equals(name)) {
            Column column = new Column(element);
            this.box.addChildBox(column);
            this.box = column;
        } else if ("br".equals(name)) {
            this.box.append(new CharactorSequence(CharactorSequence.LINE_BREAK));
        } else {
            throw new RuntimeException("xhe unexpected tag: " +  name);
        }
    }
    
}
