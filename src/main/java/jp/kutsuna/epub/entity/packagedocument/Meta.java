package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Meta {
    @JacksonXmlProperty(localName="property", isAttribute = true)
    public String property;

    @JacksonXmlText
    public String value;
}
