
　Grain Core a03

　An XForms processor for mobile terminal

　Grain Core は XFormsプロセッサ搭載のXHTMLブラウザである Sprout とバイナリ形式の
　XMLパーサ／シリアライザから構成される、携帯端末向けのリッチクライアントフレーム
　ワークの中核をなすモジュールです。


　構成

　------------------------------------------------------------------------------
　Sprout - XFormsプロセッサ搭載のXHTMLブラウザ
　------------------------------------------------------------------------------

　　配布物：

　　　grain-core-[version]-sprout-doja.zip
　　　および grain-core-[version]-sprout-doja.tar.gz
　
　　利用方法：
　　
　　　これらは、Sprout の NTT DoCoMo の iアプリ対応端末向けの実行ファイルです。 
　　　これらのファイルを解凍すると、bin以下に次のファイルが展開されます。
　　
　　　sprout.jar
　　　　sprout の 実行ファイルです。

　　　sprout.jam.sample
　　　　jamファイルを作成する際のベースにするファイルです。
　　　　AppSize などは同梱の sprout.jar に合わせて設定されています。
　　　　PackageURLなどを、環境に合わせて書き換えて下さい。
　　　　　　
　　　iアプリの実行に関する詳細は NTT DoCoMo のホームページなどを参照して下さい。
　　　なお、Sprout はサーバとの通信をバイナリ化されたXML形式(gbXML)で行います
　　　ので、実際に利用するためにはgbXMLフィルタを設定した Servlet が必要です。


　------------------------------------------------------------------------------
　gbXMLフィルタ - XML と バイナリXML(gbXML)の相互変換を行うServletフィルタ
　------------------------------------------------------------------------------

　　配布物：

　　　grain-core-[version]-lib.zip および grain-core-[version]-lib.tar.gz
　　
　　利用方法：

　　　これらは、XML と Grain 独自のバイナリ形式XMLフォーマット(gbXML)の間の相互
　　　変換を行うためのライブラリと、これを Servlet/JSP から簡単に利用できるよう
　　　にするための Servlet フィルタから構成されています。
　　　これらのファイルを解凍すると、lib以下に次のファイルが展開されます。

　　　gbxml-filter.jar
　　　　XML <--> gbxXML の相互変換を行う Servletフィルタ

　　　フィルタをアプリケーションから利用するためには、この jar ファイルをアプリ
　　　ケーションコンテキストの WEB-INF/lib に配置した上で、web.xmlにフィルタの
　　　設定を行う必要があります。

　　　適切に設定を行うことで、サーブレットは gbXML を意識することなく、Sprout
　　　との XML データの通信を行うことができます。


　ホームページ

	Grain の 公式ホームページは以下の URL です。
	http://grain.sourceforge.jp/
	
	近日中に grain.jp ドメインでのサービスを開始する予定です。


　著作権

	Grain Core に関する全ての著作権は、原著作者である株式会社ハウインター
	ナショナルが所有します。Grain Core  は、GNU Lesser General Public License 
	(LGPL) に準ずるフリーソフトウェアです。個人的に使用する場合は、改変・複製に
	制限はありません。配布する場合は LGPL に従って下さい。


　無保証

　　Grain Core は無保証です。Grain Core を使って生じたいかなる損害に対しても、
　　原著作者は一切責任を負いません。詳しくは LGPL の各条項を参照して下さい。

