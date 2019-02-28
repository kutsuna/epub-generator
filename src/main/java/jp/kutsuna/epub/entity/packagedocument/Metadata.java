package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Metadata {

    @JacksonXmlProperty(localName="identifier", isAttribute = false, namespace = "http://purl.org/dc/elements/1.1/")
    public Identifier identifier;

    @JacksonXmlProperty(localName="publisher", isAttribute = false, namespace = "http://purl.org/dc/elements/1.1/")
    public String publisher;

    @JacksonXmlProperty(localName="creator", isAttribute = false, namespace = "http://purl.org/dc/elements/1.1/")
    public String creator;

    @JacksonXmlProperty(localName="title", isAttribute = false, namespace = "http://purl.org/dc/elements/1.1/")
    public String title;

    @JacksonXmlProperty(localName="language", isAttribute = false, namespace = "http://purl.org/dc/elements/1.1/")
    public String language;

    @JacksonXmlProperty(localName="meta", isAttribute = false, namespace = "http://www.idpf.org/2007/opf")
    public Meta meta;
}
