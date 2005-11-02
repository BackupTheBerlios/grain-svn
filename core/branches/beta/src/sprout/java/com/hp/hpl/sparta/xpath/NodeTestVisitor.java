package com.hp.hpl.sparta.xpath;

/**
 * Add functionality to ubclasses of NodeTest.  This is a participant
 * in the Visitor Pattern [Gamma et al, #331].  You pass a visitor to
 * the XPath.accept method which then passes it to all the nodes on
 * the parse tree, each one of which calls back one of the visitor's
 * visit methods.

   <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
   This file is part of Sparta, an XML Parser, DOM, and XPath library.
   This library is free software; you can redistribute it and/or
   modify it under the terms of the <a href="doc-files/LGPL.txt">GNU
   Lesser General Public License</a> as published by the Free Software
   Foundation; either version 2.1 of the License, or (at your option)
   any later version.  This library is distributed in the hope that it
   will be useful, but WITHOUT ANY WARRANTY; without even the implied
   warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
   PURPOSE. </small></blockquote>
   @version  $Date: 2005-01-24 15:42:53 +0900 $  $Revision: 2782 $
   @author Eamonn O'Brien-Strain
 * @stereotype visitor
 */
public interface NodeTestVisitor {

    void visit(AllElementTest a);
    void visit(ThisNodeTest a);
	// 2004.12.27 HAW-Ryan commented throws XPathException    
    void visit(ParentNodeTest a)  /*throws XPathException*/;
    void visit(ElementTest a);
    void visit(AttrTest a);
    void visit(TextTest a);

}
