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
 * Created on 2005/07/19 16:21:04
 */
package jp.haw.grain.sprout;

import java.util.Vector;

import jp.haw.grain.xforms.RenderableElement;

import com.hp.hpl.sparta.Node;
import com.hp.hpl.sparta.Text;

/**
 * ï∂éöóÒÇÃïîï™óvëfÅB
 * 
 * @version $Id$
 * @author Go Takahashi
 */
public class CharactorSequence extends InlineElement {

    public static final String NOMAL_TEXT = "text";
    public static final String LINE_BREAK = "br";
    
    public static final int ROW = 0;
    public static final int RANGE = 1;
    
    public static final int START = 0;
    public static final int LENGTH = 1;
    
    protected Text text;
    protected Object[][] chunkz;
    protected Vector chunks = new Vector();
    private String type;
    private Font font;
    
    public CharactorSequence(String type) {
        this.type = type;
    }
    
    public CharactorSequence(Text text) {
        this.type = NOMAL_TEXT;
        this.text = text;
        init();
    }
    
    public void init() {
        if (this.text == null) return;
        this.chunkz = new Object[1][2];
        this.chunkz[0][RANGE] = new int[2];
    }
    
    public String toString() {
        if (this.text != null) return this.text.getData();
        return this.type;
    }
        
    public boolean isLineBreak() {
        if (this.type == LINE_BREAK) return true;
        return false;
    }

    public boolean isContinue() {
        if (this.type != NOMAL_TEXT) return false;
        if (this.chunks.size() == 0) return true;
        CharacterChunk lastChunk = (CharacterChunk)this.chunks.elementAt(this.chunks.size() - 1);
        if (lastChunk.end < this.text.getData().length()) return true;
        return false;
    }
    
    public InlineElement fitWidth(int width, boolean force) {
        if (this.text == null) return this;
        String src = this.text.getData();
        int start = 0;
        if (this.chunks.size() > 0) {
            CharacterChunk chunk = (CharacterChunk)this.chunks.elementAt(this.chunks.size() - 1);
            start = chunk.end;
        }
        int end = this.font.getLengthUntilLineBreak(src, start, src.length() - start, width);
        if (start == end) {
            if (!force) {
                return null;
            } else {
                end = start + 1;
            }
        }
        CharacterChunk chunk = new CharacterChunk(start, end);
        chunk.apply();
        this.chunks.addElement(chunk);
        return chunk;        
    }
    
    private Object[] createNewChunk() {
        Object[][] newChunks = new Object[this.chunkz.length + 1][2];
        System.arraycopy(this.chunkz, 0, newChunks, 0, this.chunkz.length);
        //newChunks[RANGE] = new int[2];
        this.chunkz = newChunks;
        return newChunks;
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.InlineElement#draw(jp.haw.grain.sprout.DrawContext)
     */
    public void draw(DrawContext dc) {
        // TODO if charsequence is not need to devide with chanks, drawself
    }
    
    public void apply() {
        if (this.text == null) return;
        this.font = Font.getFontOf(this.text);
    }
    
    /* (non-Javadoc)
     * @see jp.haw.grain.sprout.InlineElement#action(jp.haw.grain.sprout.FormView, int, int)
     */
    public boolean action(FormView view, int action, int selector) {
        // TODO Auto-generated method stub
        return false;
    }
    
    private class CharacterChunk extends InlineElement {
        
        private int start;
        private int end;

        CharacterChunk(int start, int end) {
            this.start = start;
            this.end = end;            
        }
        
        public int getLeading(int height) {
            return height - CharactorSequence.this.font.getHeight();
        }
        
        public void apply() {
            String src = CharactorSequence.this.text.getData().substring(this.start, this.end);
            this.width = font.getWidth(src);
            this.height = -1;
            for (Node node = CharactorSequence.this.text.getParentNode(); node != null; node = node.getParentNode()) {
                if (!(node instanceof RenderableElement)) continue;
                RenderableElement re = (RenderableElement)node;
                this.height = re.getStyleByPixel("line-height");
                if (this.height >= 0) break;
            }
            if (this.height < 0) this.height = CharactorSequence.this.font.getHeight();
        }

        /* (non-Javadoc)
         * @see jp.haw.grain.sprout.InlineElement#draw(jp.haw.grain.sprout.DrawContext, jp.haw.grain.sprout.Row)
         */
        public void draw(DrawContext dc) {
            dc.setColor(DrawContext.COLOR_BLACK);
            String txt = CharactorSequence.this.text.getData();
            String src = txt.substring(this.start, this.end);
            dc.setFont(CharactorSequence.this.font);
            dc.drawString(src, 0, 0);
        }

        /* (non-Javadoc)
         * @see jp.haw.grain.sprout.InlineElement#action(jp.haw.grain.sprout.FormView, int, int)
         */
        public boolean action(FormView view, int action, int selector) {
            // TODO Auto-generated method stub
            return false;
        }
        
    }

    
}
