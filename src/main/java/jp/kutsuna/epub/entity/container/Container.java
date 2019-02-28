package jp.kutsuna.epub.entity.container;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "container", namespace = "urn:oasis:names:tc:opendocument:xmlns:container")
public class Container {

    @JacksonXmlProperty(localName="version", isAttribute = true)
    public String version;

    @JacksonXmlElementWrapper(localName = "rootfiles", namespace = "urn:oasis:names:tc:opendocument:xmlns:container")
    @JacksonXmlProperty(localName="rootfile", namespace = "urn:oasis:names:tc:opendocument:xmlns:container")
    public List<Rootfile> rootfiles;
}
