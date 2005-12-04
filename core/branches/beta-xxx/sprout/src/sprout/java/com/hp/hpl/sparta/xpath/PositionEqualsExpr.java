package com.hp.hpl.sparta.xpath;

/** Test position within element list.

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
   @version  $Date: 2005-06-08 13:47:38 +0900 $  $Revision: 3192 $
   @author Eamonn O'Brien-Strain
 */

public class PositionEqualsExpr extends BooleanExpr {

    public PositionEqualsExpr(int position) {
        position_ = position;
    }

	// 2004.12.27 HAW-Ryan commented throws XPathException
    public void accept(BooleanExprVisitor visitor) /*throws XPathException*/ {
        visitor.visit(this);
    }

    public int getPosition(){
        return position_;
    }
    
    public String toString(){
        return "["+position_+"]";
    }

    private final int position_;
}

// $Log: PositionEqualsExpr.java,v $
// Revision 1.1  2005/01/24 06:42:52  go
// êVãKìoò^
//
// Revision 1.1  2005/01/24 04:16:28  go
// êVãKìoò^
//
// Revision 1.1  2005/01/11 18:29:35  go
// é¿ã@â“ìÆëŒâû
//
// Revision 1.2  2004/12/27 09:13:34  ryan
// code modification for bytecode reduction
//
// Revision 1.1.1.1  2004/12/16 08:46:46  go
// import to proper
//
// Revision 1.1.1.1  2004/12/02 23:20:44  go
// import to haw cvs
//
// Revision 1.3  2002/12/13 18:08:30  eobrain
// Factor Visitor out into separate visitors for node tests and predicates.
//
// Revision 1.2  2002/12/06 23:41:49  eobrain
// Add toString() which returns the original XPath.
//
// Revision 1.1  2002/09/18 05:22:06  eobrain
// Support xpath predicates of the for [1], [2], ...
//
