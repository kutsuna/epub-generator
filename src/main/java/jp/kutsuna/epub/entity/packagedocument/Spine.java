package jp.kutsuna.epub.entity.packagedocument;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Spine {

    @JacksonXmlProperty(localName="page-progression-direction", isAttribute = true)
    public String pageProgressionDirection;

    @JacksonXmlElementWrapper(useWrapping = false, namespace = "http://www.idpf.org/2007/opf")
    @JacksonXmlProperty(isAttribute = false, namespace = "http://www.idpf.org/2007/opf")
    public List<Itemref> itemref;
}
