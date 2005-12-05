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
 * Created on 2005/02/05
 * 
 */
package jp.grain.sprout.platform.doja;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import jp.grain.sprout.FormDocumentSerializeOperation;
import jp.grain.sprout.xml.BinaryXMLParser;
import jp.grain.sprout.xml.BinaryXMLSerializer;
import jp.grain.xforms.FormDocument;
import jp.grain.xforms.FormDocumentBuilder;

import org.xmlpull.v1.XmlPullParserException;

/**
 * スクラッチパッドへのフォーム書き出しオペレーション
 * 
 * @version $Id$
 * @author Go Takahashi
 */
class FormStoreOperation implements FormDocumentSerializeOperation {

	private String _uri;
	private FormDocument _doc;

	public static FormStoreOperation createForSave(String uri, FormDocument doc) {
		return new FormStoreOperation(uri, doc);
	}
	
	public static FormStoreOperation createForLoad(String uri) {
		return new FormStoreOperation(uri, null);
	}
	
	private FormStoreOperation(String uri, FormDocument doc) {
		_uri = uri;
		_doc = doc;
	}
	
	public void exec(Connection conn) throws IOException {
		System.out.println("submit uri : " + _uri);
		System.out.println("opening connection");
		if (_doc != null) {
			saveDocument(conn);
		} else {
			loadDocument(conn);
		}
	}
	
	/**
	 * saveFormat = [uri-length]:[uri][xml-data]
	 * @param conn
	 * @throws IOException
	 */
	private void saveDocument(Connection conn) throws IOException {
		OutputConnection oc = (OutputConnection)conn;
		System.out.println("writing to scratchpad");
		OutputStream os = null;
		try {
			os = oc.openOutputStream();
            byte[] uri = _doc.getUri().getBytes();
			os.write(String.valueOf(uri.length).getBytes());
			os.write(':');
			os.write(uri);
            BinaryXMLSerializer bxs = new BinaryXMLSerializer(_doc.getDocumentElement());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bxs.serializeTo(baos);
            byte[] data = baos.toByteArray();
            System.out.println("***** sava data");
            for (int i = 0; i < data.length; ++i) {
                String hex = Integer.toHexString(data[i]);
                if (hex.length() == 1) System.out.print("0");
                System.out.print(hex);
            }
            System.out.println();
            os.write(data);
		} finally {
			if (os != null) os.close();
		}
	}

	/**
	 * 
	 * @param conn
	 * @throws IOException
	 */
	private void loadDocument(Connection conn) throws IOException {
		InputConnection ic = (InputConnection)conn;
		System.out.println("reading from stream");
		InputStream is = null;
		try {
			is = ic.openInputStream();
			StringBuffer uriLen = new StringBuffer();
			int i = -1;
			for (;;) {
				i = is.read();
				if (i == -1) throw new RuntimeException("parse error while loading document");
				if (((char)i) == ':') break;
				uriLen.append((char)i);
			}
			byte[] uri = new byte[Integer.parseInt(uriLen.toString())];
			is.read(uri);
			System.out.println("read url : '" + new String(uri) + "'");
			FormDocumentBuilder builder = new FormDocumentBuilder(BinaryXMLParser.newInstance(is, "SJIS"), new String(uri));
			builder.build();
			_doc = builder.getDocument();
        } catch (XmlPullParserException e) {
            // FIXME maybe wrap in SproutComminucationException
            throw new IOException(e.toString());
        } finally {
			if (is != null) is.close();
		}			
	}
	
	public String getConnectionString() {
		return _uri;
	}

	public int getMode() {
		return Connector.READ_WRITE;
	}

	public FormDocument getFormDocuemnt() {
		return _doc;
	}

	/* (non-Javadoc)
	 * @see jp.haw.grain.sprout.FormDocumentSerializeOperation#hasResponseBody()
	 */
	public boolean hasResponseBody() {
		// FIXME implements me
		return false;
	}
	
}