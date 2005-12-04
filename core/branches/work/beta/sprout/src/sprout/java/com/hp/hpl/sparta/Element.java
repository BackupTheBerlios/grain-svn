/*
 * Element.java : based on Sparta by Hewlett-Packard Company.
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

 A type of Node with a particular tagName that has a set of attributes and
   can contain other nodes as children.  An example of its form in XML in the form <PRE>
     &lt;tagName attr1="value1" attr2="value2">
       text
       &lt;childTag...>
       &lt;childTag...>
       text
       &lt;childTag...>
       text
     &lt;/tagName>
   </PRE>

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

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.hp.hpl.sparta.xpath.*;

/**
  * Element.java : based on Sparta by Hewlett-Packard Company.
  * 
  * @version  $Date: 2005-06-08 13:47:38 +0900 $  $Revision: 3192 $
  * @author Go Takahashi
  * @author Ryan Bayhonan
  */
public class Element extends Node implements EventListener, EventTarget, ActionObserver  {

    private String nsUri_;
	private String prefix_;
	private Vector eventListeners_;
	private ActionListener actionListener_;

	//static private final boolean DEBUG = false;    // 2004.12.24 HAW-Ryan

    public Element(String tagName) {
        tagName_ = Sparta.intern(tagName);
    }

	Element() {
	}

    /** Create a deep clone of this Element.  It will have the tagname
     *  and attributes as this node.  This method will be called
     *  recursively to copy the while subtree of child Elements and
     *  Text nodes. */
    public Object clone() {
        return cloneElement(true);
    }

    /** Create a shallow clone of this Element.  It will have the
     *  tagname and attributes as this Element but will not have child
     *  Elements or Nodes. */
    // 2004.12.26 HAW-Ryan will not support functionality
    //public Element cloneShallow() {
    //   return cloneElement(false);
    //}

    /** Create a clone of this node.  It will
     *  have the tagname and attributes as this node.  If deep is
     *  true, this method will be called recursively to copy the while
     *  subtree of child Elements and text nodes. */
    public Element cloneElement(boolean deep) {
        Element result = new Element(tagName_);
        if (attributes_ != null)
            for (int i = 0; i < attributes_.size(); ++i) {
                String[] attr = (String[])attributes_.elementAt(i);
                result.setAttribute(attr[0], attr[1], attr[2]);
            }
        if (deep)
            for (Node n = firstChild_; n != null; n = n.getNextSibling())
                result.appendChild((Node) n.clone());
        return result;

    }

    /** @return tag as interned string.*/
    public String getTagName() {
        return tagName_;
    }

    public void setTagName(String tagName) {
        tagName_ = Sparta.intern(tagName);
        notifyObservers();
    }

    /**
     * @return either an Element or a Text node
     */
    public Node getFirstChild() {
        return firstChild_;
    }

    /**
     * @return either an Element or a Text node
     */
    public Node getLastChild() {
        return lastChild_;
    }

    /** Return enumeration of entry */
    public Enumeration getAttributes() {
        if (attributes_ == null)
            return Document.EMPTY;
        else
            return attributes_.elements();
    }

    /**
     * @return value of attribute that has this name or null if no such attribute.
     * WARNING! Unlike in a previous version of this API, this string is not interned
     * therefore you must do a.equals(b) instead of a==b when comparing 
     * attribute values.
     */
    public String getAttribute(String name) {
    	return getAttribute(null, name);
    }

    /** @param name attribute name which must be non-null, non empty
        @param value attribue value.
        @precondition non zero-length name
    */
    // Begin commented by HAW-Ryan 2004.12.22
    //public void setAttribute(String name, String value) {
	//	setAttribute(null, name, value);
    //}
    // End commented by HAW-Ryan 2004.12.22

    /** remove this attribute if it exists, otherwise silently do nothing. */
    public void removeAttribute(String name) {
		removeAttribute(null, name);
    }

    void appendChildNoChecking(Node addedChild) {
        // 2004.12.24 HAW-Ryan
        //if (DEBUG)
        //    checkInvariant();
        // 2004.12.24
        Element oldParent = addedChild.getParentNode();
        if (oldParent != null)
            oldParent.removeChildNoChecking(addedChild);
        addedChild.insertAtEndOfLinkedList(lastChild_);
        if (firstChild_ == null)
            firstChild_ = addedChild;
        addedChild.setParentNode(this);
        //!children_.add/*Element*/( addedChild );
        lastChild_ = addedChild;
        addedChild.setOwnerDocument(getOwnerDocument());
        // 2004.12.24
        //if (DEBUG)
        //    checkInvariant();
        // 2004.21.24
    }

    /** Add node as child of this element, cloning node if it is this element or
     *  an ancestor.
     */
    public void appendChild(Node addedChild) {
        // 2004.12.24 HAW-Ryan 2004.12.24
        //if (DEBUG)
        //    checkInvariant();
        // 2004.12.24
        if (!canHaveAsDescendent(addedChild))
            addedChild = (Element) addedChild.clone();
        appendChildNoChecking(addedChild);
        notifyObservers();
        // 2004.12.24.HAW-Ryan 2004.12.24
        //if (DEBUG)
        //    checkInvariant();
        // 2004.12.24
    }

    boolean canHaveAsDescendent(Node node) {
        if (node == this)
            return false;
        Element parent = getParentNode();
        if (parent == null)
            return true;
        return parent.canHaveAsDescendent(node);
    }

    private boolean removeChildNoChecking(Node childToRemove) {
        // 2004.12.24 HAW-Ryan 2004.12.24
        //if (DEBUG)
        //    checkInvariant();
        // 2004.12.24
        int i = 0;
        for (Node child = firstChild_;
            child != null;
            child = child.getNextSibling()) {
            if (child.equals(childToRemove)) {

                //Fix up list endpoints if necessary
                if (firstChild_ == child)
                    firstChild_ = child.getNextSibling();
                if (lastChild_ == child)
                    lastChild_ = child.getPreviousSibling();

                child.removeFromLinkedList();
                child.setParentNode(null);

                child.setOwnerDocument(null);

                // 2004.12.24 HAW-Ryan 2004.12.24
                //if (DEBUG)
                //    checkInvariant();
                // 2004.12.24
                return true;
            }
            ++i;
        }
        // 2004.12.24 HAW-Ryan
        //if (DEBUG)
        //    checkInvariant();
        // 2004.12.24
        return false;
    }

	// 2004.12.28 HAW-Ryan commented throws DOMException
    public void removeChild(Node childToRemove) /*throws DOMException*/ {
        boolean found = removeChildNoChecking(childToRemove);
        if (!found)
            //throw new DOMException(
            //    DOMException.NOT_FOUND_ERR,
            //    "Cannot find " + childToRemove + " in " + this);
		    throw new ParseException(
		        ParseException.NOT_FOUND_ERR,
		        "Cannot find " + childToRemove + " in " + this);
            
        notifyObservers();
        // 2004.12.24 HAW-Ryan
        //if (DEBUG)
        //    checkInvariant();
        // 2004.12.24
    }

    /** Replace oldChild with newChild.
     *  @throws DOMException if oldChild object is not a child.
     *    */
	// 2004.12.28 HAW-Ryan commented throws DOMException    
    public void replaceChild(Element newChild, Node oldChild)
        /*throws DOMException*/ {
        replaceChild_(newChild, oldChild);
        notifyObservers();
    }

    /** Replace oldChild with newChild.
     *  @throws DOMException if oldChild object is not a child.
     *    */
	// 2004.12.28 HAW-Ryan commented throws DOMException    
    public void replaceChild(Text newChild, Node oldChild)
        /*throws DOMException*/ {
        replaceChild_(newChild, oldChild);
        notifyObservers();
    }

	// 2004.12.28 HAW-Ryan commented throws DOMException
    private void replaceChild_(Node newChild, Node oldChild)
        /*throws DOMException*/ {
        int i = 0;
        for (Node child = firstChild_;
            child != null;
            child = child.getNextSibling()) {
            if (child == oldChild) {

                //Fix up list endpoints if necessary
                if (firstChild_ == oldChild)
                    firstChild_ = newChild;
                if (lastChild_ == oldChild)
                    lastChild_ = newChild;

                //Make oldChild's neighbouring siblings point to newChild
                oldChild.replaceInLinkedList(newChild);

                //Fix parent pointers
                newChild.setParentNode(this);
                oldChild.setParentNode(null);

                return;
            }
            ++i;
        }
        //throw new DOMException(
        //    DOMException.NOT_FOUND_ERR,
        //    "Cannot find " + oldChild + " in " + this);
		throw new ParseException(
		    ParseException.NOT_FOUND_ERR,
		    "Cannot find " + oldChild + " in " + this);    
    }

    /** Accumlate text nodes hierarchically. */
    void toString(Writer writer) throws IOException {
        for (Node i = firstChild_; i != null; i = i.getNextSibling())
            i.toString(writer);
    }

    /** Write XML representation to character stream. */
    public void toXml(Writer writer) throws IOException {
        writer.write("<");
        if (prefix_ != null && prefix_.length() != 0) writer.write(prefix_ + ":");
        writer.write(tagName_);
        
        if (namespaces_ != null) {
        	for (Enumeration e = namespaces_.keys(); e.hasMoreElements();) {
        		String prefix = (String)e.nextElement();
        		String nsUri = (String)namespaces_.get(prefix);
        		writer.write(" xmlns");
        		if (prefix.length() != 0) writer.write(":" + prefix);
        		writer.write("=\"");
        		htmlEncode(writer, nsUri);
        		writer.write("\"");
        	}
        }
        if (attributes_ != null)
        {        	
            for (int i = 0; i < attributes_.size(); ++i) {
                String[] attr = (String[])attributes_.elementAt(i);
                String name;
                //if (attr[0].length() == 0) {  // 2004.12.27 HAW-Ryan will throw NullPointerException
				if (attr[0] == null || attr[0].length() == 0) {          // if attr[0] is null
                    System.out.println("\n** toXML: attr[0]=" + attr[0] + ", attr[1]=" + attr[1] + ", attr[2]=" + attr[2]);
                	name = attr[1];                	
                } else {
                	// 2004.12.27 HAW-Ryan corrected "attr[1]" to attr[1] 
                	//name = new StringBuffer(attr[0]).append(":").append("attr[1]").toString();
                    System.out.println("\n** toXML: attr[0]=" + attr[0] + ", attr[1]=" + attr[1] + ", attr[2]=" + attr[2]);
                    System.out.println("findNamespacePrefix(): " + findNamespacePrefix(attr[0]));
                    name = new StringBuffer(findNamespacePrefix(attr[0])).append(":").append(attr[1]).toString();
                }
                System.out.println("** write attr name=" + name);
                writer.write(" " + name + "=\"");
                htmlEncode(writer, attr[2]);
                writer.write("\"");
            }
        }
        if (firstChild_ == null)
            writer.write("/>");
        else {
            writer.write(">");
            for (Node i = firstChild_; i != null; i = i.getNextSibling())
                i.toXml(writer);
            writer.write("</");
        	if (prefix_ != null && prefix_.length() != 0) writer.write(prefix_ + ":");
            writer.write(tagName_ + ">");
        }
    }

	// 2004.12.27 HAW-Ryan commented throws XPathException
    private XPathVisitor visitor(String xpath, boolean expectStringValue)
        /*throws XPathException*/ {
        XPath parseTree = XPath.get(xpath);
        if (parseTree.isStringValue() != expectStringValue) {
            String msg =
                expectStringValue
                    ? "evaluates to element not string"
                    : "evaluates to string not element";
            throw new XPathException(
                parseTree,
                "\"" + parseTree + "\" evaluates to " + msg);
        }
        return new XPathVisitor(this, parseTree);
    }

    /** Select all the elements that match the relative XPath
        expression with respect to this element. */
    // 2004.12.26 HAW-Ryan commented throws ParseException
    public Enumeration xpathSelectElements(String xpath)
        /*throws ParseException*/ {
        try {
            // 2004.12.24 HAW-Ryan will not be called
            //if (DEBUG) {
            //    Document doc = getOwnerDocument();
            //    if (doc != null && this == doc.getDocumentElement()) {
            //        XPath parseTree = XPath.get(xpath);
            //        //doc.monitor(parseTree);    // commented by HAW-Ryan 2004.12.23 (??)
            //    }
            //}
            // 2004.12.24

            return visitor(xpath, false).getResultEnumeration();

        } catch (XPathException e) {
			//throw new ("XPath problem", e);    // 2004.12.26 HAW-Ryan commented
			throw new ParseException( "XPath problem" + e.getMessage() ); 
        }
    }

    /** Select all the strings that match the relative XPath
        expression with respect to this element. */
    // 2004.12.26 HAW-Ryan commented throws ParseException
    public Enumeration xpathSelectStrings(String xpath) /*throws ParseException*/ {
        try {

            return visitor(xpath, true).getResultEnumeration();

        } catch (XPathException e) {
			//throw new ("XPath problem", e);    // 2004.12.26 HAW-Ryan commented
			throw new ParseException( "XPath problem" + e.getMessage() ); 
        }
    }

    /** Make sure this XPath exists, creating nodes if necessary,
     *  returning true if any nodes created.  Xpath must of the type that
     *  returns an element (not a string).
     *  */
	// 2004.12.26 HAW-Ryan commented throws ParseException    
    public boolean xpathEnsure(String xpath) /*throws ParseException*/ {
        try {

            //Quick exit for common case
            if (xpathSelectElement(xpath) != null)
                return false;

            //Split XPath into parent steps and last step
            final XPath parseTree = XPath.get(xpath);
            int stepCount = 0;
            for (Enumeration i = parseTree.getSteps(); i.hasMoreElements();) {
                i.nextElement();
                ++stepCount;
            }
            Step[] parentSteps = new Step[stepCount - 1];
            Enumeration i = parseTree.getSteps();
            for (int j = 0; j < parentSteps.length; ++j)
                parentSteps[j] = (Step) i.nextElement();
            Step lastStep = (Step) i.nextElement();

            Element parent;
            if (parentSteps.length == 0) {
                parent = this;
            } else {
                String parentXPath =
                    XPath.get(parseTree.isAbsolute(), parentSteps).toString();
                xpathEnsure(parentXPath.toString()); //recursion
                parent = xpathSelectElement(parentXPath);
            }

            Element newChild = makeMatching(parent, lastStep, xpath);
            parent.appendChildNoChecking(newChild);
            return true;

        } catch (XPathException e) {
            //throw new ParseException(xpath, e);    // 2004.12.26 HAW-Ryan commented
            throw new ParseException( xpath + e.getMessage() );
        }
    }

    /** Select the first element that matches the relative XPath
        expression with respect to this element, or null if
        there is no match.
    
        @todo make more efficient by short-circuiting the search.*/
	// 2004.12.26 HAW-Ryan commented throws ParseException    
    public Element xpathSelectElement(String xpath) /*throws ParseException*/ {
        try {
            // 2004.12.24 HAW-Ryan 2004.12.24 will not be called 
            //if (DEBUG) {
            //    Document doc = getOwnerDocument();
            //    if (doc != null && this == doc.getDocumentElement()) {
            //        XPath parseTree = XPath.get(xpath);
            //        //doc.monitor(parseTree);    // commented by HAW-Ryan 2004.12.23 (??)
            //   }
            //}
            // 2004.12.24
            return visitor(xpath, false).getFirstResultElement();

        } catch (XPathException e) {
			//throw new ("XPath problem", e);    // 2004.12.26 HAW-Ryan commented
			throw new ParseException( "XPath problem" + e.getMessage() );            
        }
    }

    /** Select the first element that matches the relative XPath
        expression with respect to this element, or null if
        there is no match. */
	// 2004.12.26 HAW-Ryan commented throws ParseException    
    public String xpathSelectString(String xpath) /*throws ParseException*/ {
        try {
  
            return visitor(xpath, true).getFirstResultString();
    
        } catch (XPathException e) {
			//throw new ("XPath problem", e);    // 2004.12.26 HAW-Ryan commented
			throw new ParseException( "XPath problem" + e.getMessage() ); 
        }
    }

    /** To be equal elements must have the same tagname, they must
     *  have the same children (applying equals recursivly) in the
     *  same order and they must have the same attributes in any
     *  order.  Elements can be equal even if they are in different
     *  documents, have different parents, have different siblings, or
     *  have different annotations.
     *   */
    public boolean equals(Object thatO) {

        //Do cheap tests first
        if (this == thatO)
            return true;
        if (!(thatO instanceof Element))
            return false;
        Element that = (Element) thatO;
        if (!this.tagName_.equals(that.tagName_))
            return false;
        //!if( this.children_.size() != that.children_.size() )
        //!    return false;
        int thisAttrCount =
            this.attributes_ == null ? 0 : this.attributes_.size();
        int thatAttrCount =
            that.attributes_ == null ? 0 : that.attributes_.size();
        if (thisAttrCount != thatAttrCount)
            return false;

        //Compare attributes ignoring order (we already know the
        //number is the same)
        if (attributes_ != null)
            for (int i = 0; i < attributes_.size(); ++i) {
                String[] thisValue = (String[]) this.attributes_.elementAt(i);
                //non-null
                String[] thatValue = (String[]) that.attributes_.elementAt(i);
                //maybe null
                // Begin modified by HAW-Ryan 2004.12.21				
				//if (!thisValue[0].equals(thatValue[0])) return false;
				//if (!thisValue[1].equals(thatValue[1])) return false;
				//if (!thisValue[2].equals(thatValue[2])) return false;
				if( (!thisValue[0].equals(thatValue[0])) ||
				    (!thisValue[1].equals(thatValue[1])) ||
					(!thisValue[2].equals(thatValue[2])) )
					return false;
				// End modified by HAW-Ryan 2004.12.21
            }

        //Compare children in order (we already know the number is the same)
        Node thisChild = this.firstChild_;
        Node thatChild = that.firstChild_;
        while (thisChild != null) {
            if (!thisChild.equals(thatChild))
                return false;
            thisChild = thisChild.getNextSibling();
            thatChild = thatChild.getNextSibling();
        }

        return true;
    }

    /** Called whenever cached version of hashCode needs to be regenerated. */
    protected int computeHashCode() {
        int hash = tagName_.hashCode();

        if (attributes_ != null) {
            for (int i = 0; i < attributes_.size(); ++i) {
                String[] attr = (String[])attributes_.elementAt(i);
				for (int n = 0;  n < 3; n++) {
					if (attr[i] == null) continue;
					hash = 31 * hash + attr[i].hashCode();
				}
            }
    	}

        for (Node i = firstChild_; i != null; i = i.getNextSibling())
            hash = 31 * hash + i.hashCode();
        return hash;
    }

    // 2004.12.24 HAW-Ryan useless since DEBUG == false
    //private void checkInvariant() {
    //    if (DEBUG) {
    //        if (tagName_ != Sparta.intern(tagName_))
    //            throw new Error("tagname not interned");
    //        if (attributes_ != null) {
	//			for (int i = 0; i < attributes_.size(); ++i) {                   
    //                String[] attr = (String[])attributes_.elementAt(i);
    //                if (attr[1].indexOf(' ') != -1)
    //                    throw new Error("Bad attribute " + attr[1]);
    //            }
    //        }
    //    }
    //}

    /**
     * @link aggregation
     * @label firstChild
     */
    private Node firstChild_ = null;

    /**
     * @link aggregation
     * @label lastChild
     */
    private Node lastChild_ = null;

    //Profiling found that these hashtables were using a lot of memory, so create them lazily

//    private Hashtable attributes_ = null; //create on first setAttribute
//    private Vector attributeNames_ = null; //create on first setAttribute
    private String tagName_ = null;

	/* (non-Javadoc)
	 */
	public void handleEvent(Event event) {
		if (actionListener_ == null) return;
		// Begin modified by HAW-Ryan 2004.12.22
		//switch (event.getEventPhase()) {
		//	case Event.CAPTURING_PHASE:
		//		actionListener_.performCapturingAction(event.getType());
		//		break;
		//	case Event.AT_TARGET:
		//		actionListener_.performTargetAction(event.getType());
		//		break;
		//	case Event.BUBBLING_PHASE:
		//		actionListener_.performBubblingAction(event.getType());
		//		break;
		//	default:
		//		// noting to do
		//}
		int phase = event.gsEventPhase( Event.IGNORE );
		if( phase == Event.CAPTURING_PHASE )
		    actionListener_.performCapturingAction(event.getType());
		else if( phase == Event.AT_TARGET )
		    actionListener_.performTargetAction(event.getType());
		else if( phase == Event.BUBBLING_PHASE )
		    actionListener_.performBubblingAction(event.getType());
		// End modified by HAW-Ryan 2004.12.22
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.sparta.EventTarget#addEventListener(java.lang.String, com.hp.hpl.sparta.EventListener, boolean)
	 */
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		if (eventListeners_ == null) eventListeners_ = new Vector();
		EventListenerEntry entry = new EventListenerEntry(type, listener, useCapture);
		eventListeners_.addElement(entry);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.sparta.EventTarget#removeEventListener(java.lang.String, com.hp.hpl.sparta.EventListener, boolean)
	 */
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		if (eventListeners_ == null) return;
		for (int i = 0; i < eventListeners_.size(); ++i) {
			EventListenerEntry entry = (EventListenerEntry)eventListeners_.elementAt(i);			
			// Begin modified by HAW-Ryan 2004.12.21
			//if (!entry.getType().equals(type)) continue; 
			//if (entry.getListener() != listener) continue;
			//if (entry.isUseCapture() != useCapture) continue;
			if( (!entry.getType().equals(type)) ||
				(entry.getListener() != listener) ||
				(entry.isUseCapture() != useCapture) )
					continue;
			// End modified by HAW-Ryan 2004.12.21			
			eventListeners_.removeElementAt(i);
			return;
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.sparta.EventTarget#dispatchEvent(com.hp.hpl.sparta.Event)
	 */
	// 2004.12.28 HAW-Ryan commented throws DOMException 
	public boolean dispatchEvent(Event event) /*throws DOMException*/ {
		event.setTarget(this);
		//event.setEventPhase(Event.CAPTURING_PHASE);    // commented by HAW-Ryan 2004.12.22
		event.gsEventPhase(Event.CAPTURING_PHASE);
		fireCapturing(event);
		//event.setEventPhase(Event.AT_TARGET);    // commented by HAW-Ryan 2004.12.22
		event.gsEventPhase(Event.AT_TARGET);
		fireHandling(event);
		//event.setEventPhase(Event.BUBBLING_PHASE);   // commented by HAW-Ryan 2004.12.22
		event.gsEventPhase(Event.BUBBLING_PHASE);
		fireBubbling(event);
		return event.isPreventDefault() ? false : true;
	}

	// 2004.12.28 HAW-Ryan commented throws DOMException
	private void fireCapturing(Event event) /*throws DOMException*/ {
		if (getParentNode() == null) return;
		getParentNode().captureEvent(event);
	}

	// 2004.12.28 HAW-Ryan commented throws DOMException
	private void fireBubbling(Event event) /*throws DOMException*/ {
		if (getParentNode() == null) return;
		getParentNode().bubblesEvent(event);
	}

	// 2004.12.28 HAW-Ryan commented throws DOMException
	private void fireHandling(Event event) /*throws DOMException*/ {
		event.setCurrentTarget(this);
		if (eventListeners_ != null) {
			for (int i = 0; i < eventListeners_.size(); ++i) {
				EventListenerEntry entry = (EventListenerEntry)eventListeners_.elementAt(i);
				System.out.println("fire handling : event listener / type = " + entry.getType() + " useCapture = " + entry.isUseCapture());
				// Begin modified by HAW-Ryan 2004.12.22
				//if (!entry.getType().equals(event.getType())) continue;
				//if (entry.isUseCapture() && event.getEventPhase() != Event.CAPTURING_PHASE) continue;
				//if (!entry.isUseCapture() && event.getEventPhase() == Event.CAPTURING_PHASE) continue;
				if( (!entry.getType().equals(event.getType())) ||
				    (entry.isUseCapture() && event.gsEventPhase( Event.IGNORE ) != Event.CAPTURING_PHASE) ||
				    (!entry.isUseCapture() && event.gsEventPhase( Event.IGNORE ) == Event.CAPTURING_PHASE)    
				   )
				   continue;
				// End modified by HAW-Ryan 2004.12.22
				entry.getListener().handleEvent(event);
			}
		// 2005.01.09 added by Takahashi
		}
		if (event.isPreventDefault()) return;
		performDefaultAction(event.getType());
	}

	// 2005.01.09 added by Takahashi
	protected void performDefaultAction(String eventType) {
		// nothing to do.
	}
	
	// 2004.12.28 HAW-Ryan commented throws DOMException
	private void captureEvent(Event event) /*throws DOMException*/ {
		fireCapturing(event);
		fireHandling(event);
	}

	// 2004.12.28 HAW-Ryan commented throws DOMException
	private void bubblesEvent(Event event) /*throws DOMException*/ {	
		fireHandling(event);
		fireBubbling(event);
	}

	/* (non-Javadoc)
	 */
	public void setActionListener(ActionListener listener) {
		actionListener_ = listener;
	}

	private class EventListenerEntry {
	
		String type_;
		EventListener listener_;
		boolean useCapture_;
	
		EventListenerEntry(String type, EventListener listener, boolean useCapture) {
			type_ = type;
			listener_ = listener;
			useCapture_ = useCapture;
		}
		
		/**
		 * @return
		 */
		public EventListener getListener() {
			return listener_;
		}

		/**
		 * @return
		 */
		public String getType() {
			return type_;
		}

		/**
		 * @return
		 */
		public boolean isUseCapture() {
			return useCapture_;
		}

	}
	
	private class AttributeKey {
	
		String namespace_;
		String name_;
	
		AttributeKey(String namespace, String name) {
			namespace_ = namespace;
			name_ = name;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof AttributeKey)) return false;
			AttributeKey key = (AttributeKey)obj;
			if (namespace_ == null) return key.namespace_ == null;
			if (name_ == null) return key.name_ == null;
			// Begin modified by HAW-Ryan 2004.12.21
			//if (!namespace_.equals(key.namespace_)) return false;
			//if (!name_.equals(name_)) return false;
			if( (!namespace_.equals(key.namespace_)) ||
				(!name_.equals(name_)) )
					return false;
			// End modified by HAW-Ryan 2004.12.21			
			return true;
		}

		public int hashCode() {
			return 31 * namespace_.hashCode() + name_.hashCode();
		}

	}
	
	/**
	 * @return
	 */
	// Begin modified by HAW-Ryan 2004.12.20
	// Note: merge getNamespaceUri/setNamespaceUri
	//public String getNamespaceUri() {
	//	return nsUri_;
	//}
	//
	//public void setNamespaceUri(String nsUri) {
	//	nsUri_ = nsUri;
	//}	
	public String gsNamespaceUri( String nsUri )
	{
		return ( nsUri != null ) ? nsUri_ = nsUri : nsUri_;
	}
	// End modified by HAW-Ryan 2004.12.20

	/**
	 * @param ns
	 * @param name
	 * @param string
	 */
	public void setAttribute(String namespace, String name, String value) {		
		if (attributes_ == null) {
			attributes_ = new Vector();
		}
		int index = indexOf(namespace, name);
		if (index == -1) { 
			String[] attr = { namespace, name, value };
			attributes_.addElement(attr);
		} else {
			String[] attr = (String[])attributes_.elementAt(index);
			attr[2] = value;
		}
		notifyObservers();
		// 2004.12.24 HAW-Ryan
		//if (DEBUG)
		//	checkInvariant();
		// 2004.12.24				
	}

	public String getAttribute(String namespace, String name) {
		if (attributes_ == null) return null;
		int index = indexOf(namespace, name);
		if (index == -1) return null;
		String[] attr = (String[])attributes_.elementAt(index);
		return attr[2];
	}

	private int indexOf(String namespace, String attributeName) {
		for (int i = 0; i < attributes_.size(); ++i) {
			String[] attr = (String[])attributes_.elementAt(i);
			if (namespace == null) {
				if (attr[1].equals(attributeName)) return i;
			} else {
				if (attr[0] == null) continue;
				if (attr[0].equals(namespace) && attr[1].equals(attributeName)) {
					return i;
				}
			}
		}
		return -1;
	}

	public void removeAttribute(String namespace, String name) {		
		if (attributes_ == null) return;		
		boolean attrRemoved = false;
		for(;;) {
			int index = indexOf(null, name);
			if (index == -1) break;			
			attributes_.removeElementAt(index);
			attrRemoved = true;			
			// 2004.12.28 HAW-Ryan modified
			//if (attributes_.size() == 0) attributes_ = null;
			if( attributes_.size() == 0 ) {
				attributes_ = null;
				break;
			}					
		}
		if (attrRemoved) notifyObservers();
	}

	Hashtable namespaces_ = null;
	Vector attributes_ = null;
	
	/**
	 * @param prefix
	 * @param uri
	 */
	public void setNamespace(String prefix, String uri) {
		if (namespaces_ == null) namespaces_ = new Hashtable();
		namespaces_.put(prefix, uri); 
	}

	/**
	 * @param prefix
	 * @return
	 */
	public String getNamespace(String prefix) {
		if (prefix == null) return null;
		String uri = null;
		if (namespaces_ != null) {
			uri = (String)namespaces_.get(prefix);
		} 
		if (uri == null && getParentNode() != null) {
			uri = getParentNode().getNamespace(prefix);
		}
		return uri;
	}

	/**
	 * @param prefix
	 * @return
	 */
	public String findNamespacePrefix(String namespaceUri) {
		if (namespaceUri == null) return null;
		if (namespaces_ != null) {
			for (Enumeration e = namespaces_.keys(); e.hasMoreElements();) {
				String prefix = (String)e.nextElement();
				String uri = (String)namespaces_.get(prefix);
				if (namespaceUri.equals(uri)) return prefix;
			}
		} 
		if (getParentNode() != null) {
			return getParentNode().findNamespacePrefix(namespaceUri);
		}
		return null;
	}	
//	added by Phuong 2005/08/01
	public Vector getPrefixList(){		
		if (namespaces_ ==null) return null;
		Vector prefix_map=new Vector();
		for (Enumeration e = namespaces_.keys(); e.hasMoreElements();) {
			String[] prefix_mapping=new String[2];
			prefix_mapping[0] = (String)e.nextElement();
			prefix_mapping[1] = (String)namespaces_.get(prefix_mapping[0]);
			prefix_map.addElement(prefix_mapping);
		}
		return prefix_map;
	}
	/**
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		prefix_ = prefix;
	}
	
//	added by Phuong 2005/08/01
	public String getPrefix(){
		return prefix_;
	}
	/**
	 * @return
	 */
	public ActionListener getActionListener() {
		return actionListener_;
	}
}

// $Log: Element.java,v $
// Revision 1.3  2005/02/06 11:28:41  go
// タスク完了:2005/2/6 20:28
// ページの保存と保存ページからの起動
//
// Revision 1.2  2005/02/06 08:32:05  go
// BasicPrefPanelを追加し、基本設定ダイアログの機能を実装
//
// Revision 1.1  2005/01/24 06:42:52  go
// 新規登録
//
// Revision 1.1  2005/01/24 04:16:28  go
// 新規登録
//
// Revision 1.1  2005/01/11 18:29:34  go
// 実機稼動対応
//
// Revision 1.3  2004/12/29 02:24:12  ryan
// modifications on 2004.12.28
//
// Revision 1.2  2004/12/27 09:10:09  ryan
// code modification for bytecode reduction
//
// Revision 1.1.1.1  2004/12/16 08:46:44  go
// import to proper
//
// Revision 1.3  2004/12/05 01:57:01  go
// support namespaces
//
// Revision 1.2  2004/12/02 23:26:32  go
// support dom level 2 events interfaces
//
// Revision 1.2  2004/12/02 21:35:21  go
// dom2 event ｴﾘﾏ｢･､･ｿ｡ｼ･ﾕ･ｧ｡ｼ･ｹ､ﾘ､ﾎﾂﾐｱ?
//
// Revision 1.1.1.1  2004/12/02 00:37:57  go
// no message
//
// Revision 1.10  2003/10/28 00:25:11  eobrain
// Whitespace change only.
//
// Revision 1.9  2003/07/17 23:53:19  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.8  2003/06/24 23:54:32  eobrain
// Fix bug that was causing duplicate identical text-node children to be deleted.
// Remove unnecessary hashset of children.
//
// Revision 1.7  2003/06/19 20:28:20  eobrain
// Hash code optimization.
// Add monitoring (in debug mode) to detect when indexing could optimize.
//
// Revision 1.6  2003/05/12 19:56:36  eobrain
// Performance improvements including interning of tagname and attribute values.
//
// Revision 1.5  2003/01/27 23:30:58  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.4  2002/12/13 23:09:24  eobrain
// Fix javadoc.
//
// Revision 1.3  2002/12/13 18:12:17  eobrain
// Fix xpathEnsure to handle case when the XPath given specifies a root node tagname that conflicts with the existing root node.  Extend xpathEnsure to work with any type of predicate.  Replace hacky string manipulation code with code that works on the XPath parse tree.
//
// Revision 1.2  2002/10/30 16:37:25  eobrain
// Fix Element.appendChild so that it cannot create invalid loop by
// adding ancestor.  No longer throw DOMException.  (WARNING: This breaks
// backwards compatibility. Client code that catches or propagates
// DOMException because of this call will now give a compile error.)
//
// Revision 1.1.1.1  2002/08/19 05:03:55  eobrain
// import from HP Labs internal CVS
//
// Revision 1.23  2002/08/18 04:20:52  eob
// Sparta no longer throws XPathException -- it throws ParseException
// instead.
//
// Revision 1.22  2002/08/15 23:40:22  sermarti
//
// Revision 1.21  2002/08/15 21:29:00  eob
// Constructor no longer needs documenent.
//
// Revision 1.20  2002/08/15 05:08:21  eob
// Notify observers.
//
// Revision 1.19  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.18  2002/06/21 00:25:47  eob
// Make work with old JDK 1.1.*
//
// Revision 1.17  2002/06/15 22:16:49  eob
// Comment change only.  Fix javadoc problem.
//
// Revision 1.16  2002/06/14 19:37:15  eob
// Make toString of Node do the same as in XSLT -- recursive
// concatenation of all text in text nodes.
//
// Revision 1.15  2002/05/23 21:22:44  eob
// Better error reporting.
//
// Revision 1.14  2002/05/11 00:10:18  eob
// Remove stub method that is now implemented with a slightly different
// name in Node.
//
// Revision 1.13  2002/05/10 21:03:19  eob
// Add equals method.
//
// Revision 1.12  2002/05/09 16:48:40  eob
// Add replaceChild.  Fix cloneChild.
//
// Revision 1.11  2002/03/28 01:23:18  jrowson
// fixed bugs related to client side caching
//
// Revision 1.10  2002/03/26 01:45:39  eob
// Deprecate XPathAPI
//
// Revision 1.9  2002/02/23 02:04:44  eob
// Add clone method.  Tweak toXml API.
//
// Revision 1.8  2002/02/15 21:20:10  eob
// Rename xpath* methods to xpathSelect* to make more obvious.
//
// Revision 1.7  2002/02/15 21:05:55  eob
// Add convenient xpath* methods, allowing a more object-oriented use than
// XPathAPI.
//
// Revision 1.6  2002/02/01 21:50:27  eob
// Move toXml up to Node
//
// Revision 1.5  2002/01/05 07:53:28  eob
// Factor out some functionality into Node.
//
// Revision 1.4  2002/01/04 00:37:50  eob
// add annotation
//
// Revision 1.3  2002/01/04 16:49:17  eob
// Fix indentation.
//
// Revision 1.2  2002/01/04 16:47:59  eob
// Move parse functionality functionality to ParseSource.
//
// Revision 1.1  2001/12/19 05:52:38  eob
// initial
