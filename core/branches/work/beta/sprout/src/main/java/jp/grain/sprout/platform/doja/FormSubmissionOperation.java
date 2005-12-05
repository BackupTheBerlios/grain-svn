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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import jp.grain.sprout.FormDocumentSerializeOperation;
import jp.grain.sprout.xml.BinaryXMLParser;
import jp.grain.sprout.xml.BinaryXMLSerializer;
import jp.grain.xforms.FormDocument;
import jp.grain.xforms.FormDocumentBuilder;

import org.xmlpull.v1.XmlPullParserException;

import com.hp.hpl.sparta.Node;
import com.nttdocomo.io.HttpConnection;

/**
 * HTTP経由のフォーム送信オペレーション
 * 
 * @version $Id$
 * @author Go Takahashi
 */
class FormSubmissionOperation implements FormDocumentSerializeOperation {

	private String _uri;
	private Node _node;
	private String _contentType;
	private FormDocument _doc;

	FormSubmissionOperation(String uri, String contentType) {
		this(uri, contentType, null);
	}
	
	FormSubmissionOperation(String uri, String contentType, Node node) {
		_uri = uri;
		_contentType = contentType;
		_node = node;
	}
	
	public void exec(Connection conn) throws IOException {
		System.out.println("submit uri : " + _uri);
		System.out.println("submit node : " + _node);
		System.out.println("opening connection");
		HttpConnection hc = (HttpConnection)conn;
		hc.setRequestMethod(_node == null ? HttpConnection.GET : HttpConnection.POST);
		if (_node != null) {
			System.out.println("writing to stream");
			OutputStream os = null;
			try {
				hc.setRequestProperty("Content-Type", _contentType);
				os = hc.openOutputStream();
                BinaryXMLSerializer bxs = new BinaryXMLSerializer(_node);
				bxs.serializeTo(os);
			} finally {
				if (os != null) os.close();
			}
		}
		System.out.println("reading from stream");
		InputStream is = null;
		try {
			hc.connect();
			System.out.println("connected : " + hc.getResponseCode());
			if (hc.getResponseCode() != HttpConnection.HTTP_OK) {
				 throw new RuntimeException("HTTP ERROR [" + hc.getResponseCode() + "] : " + hc.getResponseMessage());
			}
			is = hc.openInputStream();
			FormDocumentBuilder builder = new FormDocumentBuilder(BinaryXMLParser.newInstance(is, "SJIS"), _uri);
			builder.build();
			_doc = builder.getDocument();
			_doc.toXml(new OutputStreamWriter(System.out));
			System.out.println();
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
		return _doc != null;
	}
	
}