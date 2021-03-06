/* Generated by Together */

package com.hp.hpl.sparta.xpath;

/** A node test for an element with a particular tagname.

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
<<<<<<< AttrTest.java
   @version  $Date: 2005-06-08 13:47:38 +0900 $  $Revision: 3192 $
=======
   @version  $Date: 2005-06-08 13:47:38 +0900 $  $Revision: 3192 $
>>>>>>> 1.2
   @author Eamonn O'Brien-Strain
 */
public class AttrTest extends NodeTest {

    AttrTest(String attrName){
        attrName_ = attrName;
    }

    public void accept(Visitor visitor){
        visitor.visit(this);
    }

    /** Return true*/
    public boolean isStrVal(){
        return true;
    }

    public String getAttrName(){
        return attrName_;
    }

    public String toString(){
        return "@"+attrName_;
    }
    private final String attrName_;
}

// $Log: AttrTest.java,v $
// Revision 1.1  2005/01/24 06:42:52  go
// �V�K�o�^
//
// Revision 1.1  2005/01/24 04:16:28  go
// �V�K�o�^
//
// Revision 1.1  2005/01/11 18:29:34  go
// ���@�ғ��Ή�
//
//<<<<<<< AttrTest.java
//=======
// Revision 1.2  2005/02/07 08:48:10  ryan
// code reduction modification
//
// Revision 1.1.1.1  2004/12/16 08:46:46  go
// import to proper
//
//>>>>>>> 1.2
// Revision 1.1.1.1  2004/12/02 23:20:43  go
// import to haw cvs
//
// Revision 1.2  2002/12/06 23:41:49  eobrain
// Add toString() which returns the original XPath.
//
// Revision 1.1.1.1  2002/08/19 05:04:05  eobrain
// import from HP Labs internal CVS
//
// Revision 1.3  2002/08/18 23:38:24  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.2  2002/06/14 19:39:17  eob
// Make test for isStringValue more object-oriented.  Avoid "instanceof".
//
// Revision 1.1  2002/02/04 21:35:35  eob
// initial
