/* Generated by Together */

package com.hp.hpl.sparta.xpath;

/** The test for a nodeset that appears before the optional
 * [predicate] in an xpath step.

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
public abstract class NodeTest {

	// 2004.12.27 HAW-Ryan commented throws XPathException
    public abstract void accept(Visitor visitor)  /*throws XPathException*/;
	//public abstract void accept(Visitor visitor);

    /** Does this nodetest evaluate to a string values (attribute values
        or text() nodes)*/
    //public abstract boolean isStringValue();
	public abstract boolean isStrVal();

}

// $Log: NodeTest.java,v $
// Revision 1.1  2005/01/24 06:42:52  go
// �V�K�o�^
//
// Revision 1.1  2005/01/24 04:16:28  go
// �V�K�o�^
//
// Revision 1.1  2005/01/11 18:29:35  go
// ���@�ғ��Ή�
//
// Revision 1.3  2005/02/07 08:47:28  ryan
// code reduction modification
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
// Revision 1.1.1.1  2002/08/19 05:04:04  eobrain
// import from HP Labs internal CVS
//
// Revision 1.3  2002/08/18 23:38:46  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.2  2002/06/14 19:40:19  eob
// Make test for isStringValue more object-oriented.  Avoid "instanceof".
//
// Revision 1.1  2002/02/01 01:25:00  eob
// initial