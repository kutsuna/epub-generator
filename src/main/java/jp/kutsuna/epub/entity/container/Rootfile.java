package jp.kutsuna.epub.entity.container;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;

public class Rootfile {
    @JacksonXmlProperty(localName="full-path", isAttribute = true)
    public String fullPath;

    @JacksonXmlProperty(localName="media-type", isAttribute = true)
    public String mediaType;
}
