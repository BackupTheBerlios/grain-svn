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
 * Created on 2004/12/18
 *
 */
package jp.haw.grain.xforms;


/**
 * 
 * @version $Id: XHTMLElement.java 3385 2005-08-18 22:12:13Z go $
 * @author go
 */
public class XHTMLElement extends RenderableElement {

	public static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";
	
	/**
	 * @param tagName
	 */
	public XHTMLElement(String tagName) {
		super(tagName);
	}

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.Renderable#isBlockLevel()
     */
    public boolean isBlockLevel() {
        if ("body".equals(this.getTagName()) || "div".equals(this.getTagName())) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see jp.haw.grain.xforms.RenderableElement#accept(jp.haw.grain.sprout.LayoutManager)
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
