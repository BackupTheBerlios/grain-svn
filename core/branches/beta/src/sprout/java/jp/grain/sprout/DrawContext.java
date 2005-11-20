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
 * Created on 2005/08/07 18:07:01
 * 
 */
package jp.grain.sprout;


/**
 * Context to draw something.
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public interface DrawContext {
    
    public static final int COLOR_BLACK = 0x000000;

    
    int getColorByHex(String hexValue);

    /**
     * @param bgColor
     */
    void setColor(int bgColor);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     */
    void fillRect(int x, int y, int width, int height);

    /**
     * @param x
     * @param y
     * @return 
     */
    DrawContext moveTo(int x, int y);

    /**
     * @param sub
     * @param i
     * @param j
     */
    void drawString(String sub, int i, int j);

    /**
     * @param i
     * @param j
     * @param k
     * @param l
     */
    void drawRect(int i, int j, int k, int l);

    /**
     * @param impl
     */
    void setFormView(FormView view);
    
    FormView getFormView();
    
    public void saveOrigin(int[] origin);
    
    public void restoreOrigin(int[] origin);

    /**
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    void drawLine(int sx, int sy, int ex, int ey);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     */
    void clipRect(int x, int y, int width, int height);

    /**
     * @param font
     */
    void setFont(Font font);

}
