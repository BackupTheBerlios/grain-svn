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
 * Created on 2005/07/30 14:06:22
 * 
 */
package jp.grain.sprout;


/**
 * 物理レイアウトの基本要素
 * 
 * @version $Id$
 * @author Go Takahashi
 */
abstract public class Box extends Renderer {
    
    abstract public void append(InlineElement e);
    abstract public void addChildBox(Box child);
    abstract public int getChildCount();
    abstract public Box getChildBox(int index);
    
}