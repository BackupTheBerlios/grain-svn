/*
 * Grain Core - A XForms processor for mobile terminals.
 * Copyright (C) 2005 Go TAKAHASHI and HAW International Inc.
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
 * Created on 2004/12/19
 *
 */
package jp.haw.grain.sprout;

import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;


/**
 * シリアライズ操作の実行コンテキスト
 * 
 * @version $Id: DocumentManager.java 3385 2005-08-18 22:12:13Z go $
 * @author go
 */
public class DocumentManager {

	private DocumentManager() {
	}
	
	public static void execSerializeOperation(SerializeOperation operation) throws IOException {
		Connection conn = null;
		try {
			System.out.println("opening connection");
			conn = Connector.open(operation.getConnectionString(), operation.getMode());
			System.out.println("handling serialization");
			operation.exec(conn);
		} finally {
			try {
				System.out.println("closing connection");
				if (conn != null) conn.close();
			} catch (IOException e) {
				System.out.println("closing err");
				e.printStackTrace();
			}
		}				
		
	}
	
//	public FormDocument load(String uri) throws ParseException, IOException {
//		return submit(uri, null, null);
//	}
//	public FormDocument load(String uri) throws ParseException, IOException {
//
//		InputConnection ic = null;
//		try {
//			System.out.println(uri);
//			ic = (InputConnection)Connector.open(uri, Connector.READ);
//			Reader reader = null;
//			try {
//				if (ic instanceof HttpConnection) {
//					HttpConnection hc = (HttpConnection)ic;
//					hc.setRequestMethod(HttpConnection.GET);
//					hc.connect();
//					if (hc.getResponseCode() == HttpConnection.HTTP_OK) {
//						reader = new InputStreamReader(hc.openInputStream(), "SJIS");
//					}
//				} else {
//					reader = new InputStreamReader(ic.openInputStream());
//				}
//				return createDocument(uri, reader);
//			} finally {
//				if (reader != null) reader.close();
//			}
//		} finally {
//			if (ic != null) ic.close();
//		}
//	}


//	/* (non-Javadoc)
//	 * @see jp.haw.grain.xforms.DocumentLoader#submit(java.lang.String, jp.haw.grain.xforms.ModelElement)
//	 */
//	public FormDocument submit(String uri, String contentType, Node node) throws ParseException, IOException {
//		System.out.println("submit uri : " + uri);
//		System.out.println("submit node : " + node);
//		StreamConnection sc = null;
//		try {
//			System.out.println("opening connection");
//			sc = (StreamConnection)Connector.open(uri, node == null ? Connector.READ : Connector.READ_WRITE);
//			if (sc instanceof HttpConnection) {
//				HttpConnection hc = (HttpConnection)sc;
//				hc.setRequestMethod(node == null ? HttpConnection.GET : HttpConnection.POST);
//			}
//			if (node != null) {
//				System.out.println("writing to stream");
//				writeNode(sc, contentType, node);
//			}
//			System.out.println("reading from stream");
//			return readDocument(new FormDocumentBuilder(uri), sc);
//		} finally {
//			try {
//				System.out.println("closing connection");
//				if (sc != null) sc.close();
//			} catch (IOException e) {
//				System.out.println("closing err");
//				e.printStackTrace();
//			}
//		}				
//	}
//		
//	private static FormDocument readDocument(FormDocumentBuilder builder, Reader read) throws ParseException, IOException {
//		Reader reader = null;
//		try {
//			reader = new InputStreamReader(sc.openInputStream());
//			builder.build(reader);
//			return builder.getDocument();
//		} finally {
//			if (reader != null) reader.close();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see jp.haw.grain.xforms.FormDocumentManager#save(java.lang.String)
//	 */
//	public void store(String storeUri, FormDocument doc) throws ParseException, IOException {
//		StreamConnection sc = null;
//		try {
//			System.out.println("opening connection");
//			sc = (StreamConnection)Connector.open("scratchpad:///0;pos=0", Connector.WRITE);
//			writeUri(doc.getUri());
//			writeNode(sc, null, doc);
//		} finally {
//			try {
//				System.out.println("closing connection");
//				if (sc != null) sc.close();
//			} catch (IOException e) {
//				System.out.println("closing err");
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see jp.haw.grain.xforms.FormDocumentManager#restore(java.lang.String)
//	 */
//	public FormDocument restore(String restoreUri) throws ParseException, IOException {
//		StreamConnection sc = null;
//		try {
//			System.out.println("opening connection");
//			sc = (StreamConnection)Connector.open("scratchpad:///0;pos=0", Connector.READ);
//			String uri = readUri(sc);
//			return readDocument(new FormDocumentBuilder(uri), sc);
//		} finally {
//			try {
//				System.out.println("closing connection");
//				if (sc != null) sc.close();
//			} catch (IOException e) {
//				System.out.println("closing err");
//				e.printStackTrace();
//			}
//		}
//	}
}
