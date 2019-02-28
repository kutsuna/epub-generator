# epub-generator
EPUBファイルを生成するためのサービスです。

以下の内容をパラメータとして渡すと、EPUBファイルを生成します。

- 書籍ID
- 出版社名
- 著者名
- 書籍タイトル
- 言語
- 目次ファイル内容
- 本文ファイル内容

## build方法
レポジトリ直下にpom.xmlを置いています。そちらを利用してjarファイルを生成してください。

> mvn install

## 使い方
サーバーアプリケーションを実行します。
> java -jar epub-generator.jar

次にフォームをWebサーバに配置します。フォームのサンプルはwebフォルダに置いています。
接続先は適宜変更して下さい。

(pythonのワンライナーWebサーバーの場合)
> sudo python -m SimpleHTTPServer 80

