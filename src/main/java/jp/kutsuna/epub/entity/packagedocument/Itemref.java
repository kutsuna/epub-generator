package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(namespace = "http://www.idpf.org/2007/opf")
public class Itemref {
    @JacksonXmlProperty(localName="idref", isAttribute = true)
    public String idref;

}
