package org.jabref.logic.importer.fileformat;


import StandardFileType.PDF;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.jabref.logic.importer.ParserResult;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PdfXmpImporterTest {
    private PdfXmpImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("XMP-annotated PDF", importer.getName());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(PDF, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Wraps the XMPUtility function to be used as an Importer.", importer.getDescription());
    }

    @Test
    public void importEncryptedFileReturnsError() throws URISyntaxException {
        Path file = Paths.get(PdfXmpImporterTest.class.getResource("/pdfs/encrypted.pdf").toURI());
        ParserResult result = importer.importDatabase(file, StandardCharsets.UTF_8);
        Assertions.assertTrue(result.hasWarnings());
    }

    @Test
    public void testImportEntries() throws URISyntaxException {
        Path file = Paths.get(PdfXmpImporterTest.class.getResource("annotated.pdf").toURI());
        List<BibEntry> bibEntries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(1, bibEntries.size());
        BibEntry be0 = bibEntries.get(0);
        Assertions.assertEquals(Optional.of("how to annotate a pdf"), be0.getField("abstract"));
        Assertions.assertEquals(Optional.of("Chris"), be0.getField("author"));
        Assertions.assertEquals(Optional.of("pdf, annotation"), be0.getField("keywords"));
        Assertions.assertEquals(Optional.of("The best Pdf ever"), be0.getField("title"));
    }

    @Test
    public void testIsRecognizedFormat() throws IOException, URISyntaxException {
        Path file = Paths.get(PdfXmpImporterTest.class.getResource("annotated.pdf").toURI());
        Assertions.assertTrue(importer.isRecognizedFormat(file, StandardCharsets.UTF_8));
    }

    @Test
    public void testGetCommandLineId() {
        Assertions.assertEquals("xmp", importer.getId());
    }
}

