package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "package", namespace = "http://www.idpf.org/2007/opf")
@JsonPropertyOrder({"metadata", "manifest", "spine"})
public class Package {

    @JacksonXmlProperty(localName="unique-identifier", isAttribute = true)
    public String uniqueIdentifier;

    @JacksonXmlProperty(localName="version", isAttribute = true)
    public String version;

    @JacksonXmlProperty(localName="xml:lang", isAttribute = true)
    public String language;

    @JacksonXmlProperty(localName="metadata", isAttribute = false, namespace = "http://www.idpf.org/2007/opf")
    public Metadata metadata;

    @JacksonXmlElementWrapper(localName = "manifest", namespace = "http://www.idpf.org/2007/opf")
    @JacksonXmlProperty(localName="item", namespace = "http://www.idpf.org/2007/opf")
    public List<Item> manifest;

    @JacksonXmlElementWrapper(useWrapping = false, namespace = "http://www.idpf.org/2007/opf")
    @JacksonXmlProperty(localName="spine", namespace = "http://www.idpf.org/2007/opf")
    public Spine spine;
}
