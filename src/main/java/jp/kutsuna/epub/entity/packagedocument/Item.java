package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Item {
    @JacksonXmlProperty(localName="id", isAttribute = true)
    public String id;

    @JacksonXmlProperty(localName="href", isAttribute = true)
    public String href;

    @JacksonXmlProperty(localName="media-type", isAttribute = true)
    public String mediaType;

    @JacksonXmlProperty(localName="properties", isAttribute = true)
    public String properties;

}
