/*
 * ParseException.java : based on Sparta by Hewlett-Packard Company.
 * 
 * Copyright (C) 2004-2005 HAW International Inc.
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
 */

/* Here is the original copyright:

  Thrown when error parsing XML or XPath.

   <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
   This file is part of Sparta, an XML Parser, DOM, and XPath library.
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public License
   as published by the Free Software Foundation; either version 2.1 of
   the License, or (at your option) any later version.  This library
   is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or
   FITNESS FOR A PARTICULAR PURPOSE.</small></blockquote>
   @see <a "href="doc-files/LGPL.txt">GNU Lesser General Public License</a>
   @author Eamonn O'Brien-Strain

*/


package com.hp.hpl.sparta;

/** 
 * Thrown when error parsing XML or XPath.
 * @version  $Id$
 * @author Go Takahashi
 * @author Ryan Bayhonan
*/
public class ParseException extends RuntimeException {

    // 2004.12.28 HAW-Ryan data from DOMException
	public short   code;
	//public static final short           INDEX_SIZE_ERR       = 1;
	public static final short           DOMSTRING_SIZE_ERR   = 2;
	public static final short           HIERARCHY_REQUEST_ERR = 3;
	public static final short           WRONG_DOCUMENT_ERR   = 4;
	//public static final short           INVALID_CHARACTER_ERR = 5;
	//public static final short           NO_DATA_ALLOWED_ERR  = 6;
	//public static final short           NO_MODIFICATION_ALLOWED_ERR = 7;
	public static final short           NOT_FOUND_ERR        = 8;
	//public static final short           NOT_SUPPORTED_ERR    = 9;
	//public static final short           INUSE_ATTRIBUTE_ERR  = 10;    

	public ParseException(short code, String message) {
		super(message);
		this.code = code;
	}
    // End   

    public ParseException(String msg){
        super(msg);
    }

    /* For use by handlers */
    //public ParseException(String msg, Throwable cause) {
	//super(msg + " " + cause);
	//this.cause_ = cause;
    //}

    /* Using systemID */
    public ParseException(String systemId,
                          int lineNumber,
                          int lastCharRead,
                          //String history,   // commented by HAW-Ryan 2004.12.23
                          String msg)
    {
        // 2004.12.29 HAW-Ryan modified
        //// commented by HAW-Ryan 2004.12.23
        ////super(toMessage(systemId,lineNumber,lastCharRead,history,msg));
		//super(toMessage(systemId,lineNumber,lastCharRead,msg));
        ////lineNumber_ = lineNumber;   // 2004.12.26 HAW-Ryan comment
		//String str = (lastCharRead==-1) ? "EOF" : (""+(char)lastCharRead);
		//String mainmsg = systemId+"("+lineNumber+"): \n"+"\nLast character read was \'"+ str +"\'\n"+msg;    	
    	super( new String( systemId+"("+lineNumber+"): \n"+"\nLast character read was \'"+ ((lastCharRead==-1) ? "EOF" : (""+(char)lastCharRead)) +"\'\n"+msg ) );				        
    }

    // Begin commented by HAW-Ryan 2004.12.20
    // Note: will not use ParseLog object.
    //public ParseException(ParseLog log,
    //                      String systemId,
    //                      int lineNumber,
    //                      int lastCharRead,
    //                      String history,
    //                      String msg)
    //{
    //    this(systemId,lineNumber,lastCharRead,history,msg);
    //    log.error( msg, systemId, lineNumber );
    //}
    // End commented by HAW-Ryan 2004.12.20

//    public ParseException(ParseCharStream source, String msg) {
//        this( //source.getLog(),    // commented by HAW-Ryan 2004.12.20
//              source.getSystemId(),
//              source.getLineNumber(),
//              source.getLastCharRead(),
//              //source.getHistory(),    // commented by HAW-Ran 2004.12.23
//              msg );
//    }

    // 2004.12.24 HAW-Ryan 
    //public ParseException(ParseCharStream source, char actual, char expected) {
    //    this(source,"got \'"+actual+"\' instead of expected \'"+expected+"\'");
    //}
    // 2004.12.24

    /** Precondition: expected.length > 0 */
    //public ParseException(ParseCharStream source,
    //                      char actual,
    //                      char[] expected)
    //{
    //    this(
    //         source,
    //         "got \'"+actual+"\' instead of "+toString(expected)
    //         );
    //}

    // 2004.12.24 HAW-Ryan 
    //public ParseException(ParseCharStream source,
    //                      char actual,
    //                      String expected)
    //{
    //    this(source,"got \'"+actual+"\' instead of "+expected+" as expected");
    //}
    // 2004.12.24

//    public ParseException(ParseCharStream source,
//                          String actual,
//                          String expected)
//    {
//        this(source,"got \""+actual+"\" instead of \""+expected+"\" as expected");
//    }

    // 2004.12.24 HAW-Ryan 
    //static private String toString(char[] chars){
    //    StringBuffer result = new StringBuffer();
    //    result.append(chars[0]);
    //    for(int i=1; i<chars.length; ++i)
    //        result.append("or "+chars[i]);
    //    return result.toString();
    //}
    // 2004.12.24

    // 2004.12.24 HAW-Ryan
    //public ParseException(ParseCharStream source,
    //                      String actual,
    //                      char[] expected)
    //{
    //    this(source,actual,new String(expected));
    //}
    // 2004.12.24

    // 2004.12.26 HAW-Ryan this function may not be used
    //public int getLineNumber(){
    //    return lineNumber_;
    //}

    //private int lineNumber_ = -1;   //2004.12.26 HAW-Ryan comment

	// 2004.12.26 HAW-Ryan this function may not be used
    //public Throwable getCause() {
	//return cause_;
    //}

    //////////////////////////////////////////////////////////////////

    /*static private String toMessage(ParseCharStream source, String msg){
      return toMessage( source.getSystemId(),
      source.getLineNumber(),
      source.getLastCharRead(),
      source.getHistory(),
      msg );
      }*/

    //static private String toMessage(String systemId,
    //                                int lineNumber,
    //                                int lastCharRead,
    //                                //String history,    // commented by HAW-Ryan 2004.12.23
    //                                String msg)
    //{
    //    // 2004.12.26 HAW-Ryan added
    //	String str = (lastCharRead==-1) ? "EOF" : (""+(char)lastCharRead);
    //	// End
    //	
    //    return systemId+"("+lineNumber
    //        // Begin modified by HAW-Ryan 2004.12.23
    //        //+"): \n"+history+"\nLast character read was \'"
	//	    +"): \n"+"\nLast character read was \'"
	//	    // End modified by HAW-Ryan 2004.12.23
    //        //+charRepr(lastCharRead)+"\'\n"+msg;     // 2004.12.26 HAW-Ryan comment
	//	    + str +"\'\n"+msg;
    //}

    //static String charRepr(int ch){
    //    return (ch==-1) ? "EOF" : (""+(char)ch);
    //}

    // 2004.12.26 HAw-Ryan will not support
    //private Throwable cause_ = null;
    
	public static String declaredEncoding_;    // 2004.12.24 HAW-Ryan added

}

// $Log: ParseException.java,v $
// Revision 1.1  2005/01/24 06:42:52  go
// V‹K“o˜^
//
// Revision 1.1  2005/01/24 04:16:28  go
// V‹K“o˜^
//
// Revision 1.1  2005/01/11 18:29:34  go
// ŽÀ‹@‰Ò“®‘Î‰ž
//
// Revision 1.3  2004/12/29 02:31:42  ryan
// modifications on 2004.12.28
//
// Revision 1.2  2004/12/27 09:10:09  ryan
// code modification for bytecode reduction
//
// Revision 1.1.1.1  2004/12/16 08:46:45  go
// import to proper
//
// Revision 1.1.1.1  2004/12/02 23:20:43  go
// import to haw cvs
//
// Revision 1.1.1.1  2002/08/19 05:03:58  eobrain
// import from HP Labs internal CVS
//
// Revision 1.13  2002/08/18 04:37:56  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.12  2002/08/09 22:36:49  sermarti
//
// Revision 1.11  2002/08/05 20:04:32  sermarti
//
// Revision 1.10  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.9  2002/05/23 21:29:32  eob
// Tweaks.
//
// Revision 1.8  2002/05/09 20:58:30  eob
// Add protected constructor to allow sub-classes without supplying all
// other info.
//
// Revision 1.7  2002/05/09 16:50:21  eob
// Add history for better error reporting.
//
// Revision 1.6  2002/01/08 19:51:02  eob
// Factored out constructors for more flexibilty.
//
// Revision 1.5  2002/01/04 00:38:40  eob
// Improve logging.
//
// Revision 1.4  2002/01/04 16:52:40  eob
// Comment change only.
//
// Revision 1.3  2002/01/04 16:52:10  eob
// Add constructors.
//
// Revision 1.2  2001/12/20 20:07:49  eob
// Added constructor that takes 2 strings.  Add getLineNumber method.
//
// Revision 1.1  2001/12/19 05:52:38  eob
// initial
