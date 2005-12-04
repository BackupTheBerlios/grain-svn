package com.hp.hpl.sparta.xpath;

/**
 * Add functionality to subclasses of BooleanExpr.  This is a participant
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
public interface BooleanExprVisitor {

    void visit(TrueExpr a);
	// 2004.12.27 HAW-Ryan commented throws XPathException
    //void visit(AttrExistsExpr a)  throws XPathException;
    //void visit(AttrEqualsExpr a)  throws XPathException;
    //void visit(AttrNotEqualsExpr a)  throws XPathException;
    //void visit(AttrLessExpr a)  throws XPathException;
    //void visit(AttrGreaterExpr a)  throws XPathException;
    //void visit(TextExistsExpr a)  throws XPathException;
    //void visit(TextEqualsExpr a)  throws XPathException;
    //void visit(TextNotEqualsExpr a)  throws XPathException;
    //void visit(PositionEqualsExpr a) throws XPathException;
    
	void visit(AttrExistsExpr a);
	void visit(AttrEqualsExpr a);
	void visit(AttrNotEqualsExpr a);
	void visit(AttrLessExpr a);
	void visit(AttrGreaterExpr a);
	void visit(TextExistsExpr a);
	void visit(TextEqualsExpr a);
	void visit(TextNotEqualsExpr a);
	void visit(PositionEqualsExpr a);    
}
