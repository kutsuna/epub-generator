package jp.kutsuna.epub;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import jp.kutsuna.epub.BookInformation;
import jp.kutsuna.epub.entity.container.Container;
import jp.kutsuna.epub.entity.container.Rootfile;
import jp.kutsuna.epub.entity.packagedocument.*;
import jp.kutsuna.epub.entity.packagedocument.Package;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EPubFileGenerator {
    private static final String OEBPS_DIR = "OEBPS/";
    private static final String META_INF_DIR = "META-INF/";
    private static final String MEDIA_TYPE_FILE = "mimetype";
    private static final String PACKAGE_DOCUMENT_FILENAME = "package.opf";
    private static final String CONTAINER_FILENAME = "container.xml";
    private static final String NAVIGATION_FILE = "toc.xhtml";
    private static final String BOOK_CONTENT_FILE = "main.xhtml";
    private static final String EPUB_FILE = "output.epub";

    private static final String MEDIA_TYPE_EPUB = "application/oebps-package+xml";

    private final Counter counter;

    @Autowired
    public EPubFileGenerator(MeterRegistry registry) {
        this.counter = registry.counter("service.invoked.generate");
    }

    private void createMimetypeFile(String targetDir) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(targetDir + MEDIA_TYPE_FILE))) {
            writer.write("application/epub+zip");
        }
    }

    private void createContainerFile(String targetDir) throws IOException, JsonMappingException {
        new File(targetDir + META_INF_DIR).mkdir();

        Container container = new Container();
        container.version = "1.0";

        List<Rootfile> rootfiles = new ArrayList<>();
        Rootfile rootfile = new Rootfile();
        rootfile.fullPath = OEBPS_DIR + PACKAGE_DOCUMENT_FILENAME;
        rootfile.mediaType = MEDIA_TYPE_EPUB;
        rootfiles.add(rootfile);
        container.rootfiles = rootfiles;

        ObjectMapper xmlMapper = new XmlMapper();
        ((XmlMapper) xmlMapper).enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        ((XmlMapper) xmlMapper).enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.writeValue(new File(targetDir + META_INF_DIR + CONTAINER_FILENAME), container);
    }

    private String getCurrentTime() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT).replaceAll("\\..*Z$", "Z");
    }

    private void createPackageDocumentFile(String targetDir, BookInformation bookInformation) throws IOException, JsonMappingException {
        ObjectMapper xmlMapper = new XmlMapper();
        ((XmlMapper) xmlMapper).enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        ((XmlMapper) xmlMapper).enable(SerializationFeature.INDENT_OUTPUT);

        // TODO Builderに置換
        Package packageElement = new Package();
        packageElement.uniqueIdentifier = "idName";
        packageElement.version = "3.0";
        packageElement.language = "ja";
        Metadata metadata = new Metadata();
        Identifier identifier = new Identifier();
        identifier.id = packageElement.uniqueIdentifier;
        identifier.value = bookInformation.getBook_id();
        metadata.identifier = identifier;
        metadata.publisher = bookInformation.getPublisher();
        metadata.creator = bookInformation.getCreator();
        metadata.title = bookInformation.getTitle();
        metadata.language = bookInformation.getLanguage();
        packageElement.metadata = metadata;
        Meta meta = new Meta();
        meta.property = "dcterms:modified";
        meta.value = getCurrentTime();
        metadata.meta = meta;
        List<Item> manifest = new ArrayList<>();
        Item nav = new Item();
        nav.id = "nav";
        nav.href = NAVIGATION_FILE;
        nav.mediaType = "application/xhtml+xml";
        nav.properties = "nav";
        manifest.add(nav);
        Item main = new Item();
        main.id = "main_xhtml";
        main.href = BOOK_CONTENT_FILE;
        main.mediaType = "application/xhtml+xml";
        manifest.add(main);
        packageElement.manifest = manifest;
        Spine spine = new Spine();
        spine.pageProgressionDirection = bookInformation.getPageProgressionDirection() == null ?  "default" : bookInformation.getPageProgressionDirection();
        List<Itemref> itemrefs = new ArrayList<>();
        Itemref navItemref = new Itemref();
        navItemref.idref = "nav";
        itemrefs.add(navItemref);
        Itemref mainItemref = new Itemref();
        mainItemref.idref = "main_xhtml";
        itemrefs.add(mainItemref);
        spine.itemref = itemrefs;
        packageElement.spine = spine;
        xmlMapper.writeValue(new File(targetDir + OEBPS_DIR + PACKAGE_DOCUMENT_FILENAME), packageElement);
    }

    private void createNavigationFile(String targetDir, String tocContent) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(targetDir + OEBPS_DIR + NAVIGATION_FILE))) {
            writer.write(tocContent);
        }
    }

    private void createBookContentFile(String targetDir, String bookContent) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(targetDir + OEBPS_DIR + BOOK_CONTENT_FILE))) {
            writer.write(bookContent);
        }
    }

    private void addEntryAndWrite(ZipOutputStream zos, File file, ZipEntry entry) throws IOException {
        zos.putNextEntry(entry);

        byte[] buf = new byte[1024];
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
        }
        zos.closeEntry();
    }

    private File compressAndDeleteWorkFile(String targetDir) throws FileNotFoundException, IOException {
        ZipOutputStream zos = null;
        File zipFile = Files.createTempFile("temp_", ".epub").toFile();
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile.getAbsolutePath())));
            zos.setLevel(0);
            File file = new File(targetDir + MEDIA_TYPE_FILE);
            addEntryAndWrite(zos, file, new ZipEntry(file.getName()));

            zos.setLevel(9);

            zos.putNextEntry(new ZipEntry(META_INF_DIR));
            addEntryAndWrite(zos, new File(targetDir + META_INF_DIR + CONTAINER_FILENAME), new ZipEntry(META_INF_DIR + CONTAINER_FILENAME));

            zos.putNextEntry(new ZipEntry(OEBPS_DIR));
            addEntryAndWrite(zos, new File(targetDir + OEBPS_DIR + PACKAGE_DOCUMENT_FILENAME), new ZipEntry(OEBPS_DIR + PACKAGE_DOCUMENT_FILENAME));
            addEntryAndWrite(zos, new File(targetDir + OEBPS_DIR + NAVIGATION_FILE), new ZipEntry(OEBPS_DIR + NAVIGATION_FILE));
            addEntryAndWrite(zos, new File(targetDir + OEBPS_DIR + BOOK_CONTENT_FILE), new ZipEntry(OEBPS_DIR + BOOK_CONTENT_FILE));

            // delete workfile
            new File(targetDir + MEDIA_TYPE_FILE).delete();
            new File(targetDir + META_INF_DIR + CONTAINER_FILENAME).delete();
            new File(targetDir + OEBPS_DIR + PACKAGE_DOCUMENT_FILENAME).delete();
            new File(targetDir + OEBPS_DIR + NAVIGATION_FILE).delete();
            new File(targetDir + OEBPS_DIR + BOOK_CONTENT_FILE).delete();

        } finally {
            if(zos != null) {
                zos.close();
            }
        }

        return zipFile;
    }

    public File generate(BookInformation bookInformation) throws IOException, JsonMappingException {
        counter.increment();

        String targetDir = "./output/" + UUID.randomUUID() + "/";
        new File(targetDir).mkdir();
        new File(targetDir + OEBPS_DIR).mkdir();

        createMimetypeFile(targetDir);
        createContainerFile(targetDir);
        createPackageDocumentFile(targetDir, bookInformation);
        createNavigationFile(targetDir, bookInformation.getTocContent());
        createBookContentFile(targetDir, bookInformation.getBookContent());
        return compressAndDeleteWorkFile(targetDir);
    }
}
