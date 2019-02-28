package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Identifier {
    @JacksonXmlProperty(localName="id", isAttribute = true)
    public String id;

    @JacksonXmlText
    public String value;
}
