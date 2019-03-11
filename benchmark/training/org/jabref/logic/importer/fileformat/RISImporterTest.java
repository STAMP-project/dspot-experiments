package org.jabref.logic.importer.fileformat;


import StandardFileType.RIS;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RISImporterTest {
    private RisImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("RIS", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("ris", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(RIS, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Imports a Biblioscape Tag File.", importer.getDescription());
    }

    @Test
    public void testIfNotRecognizedFormat() throws IOException, URISyntaxException {
        Path file = Paths.get(RISImporterTest.class.getResource("RisImporterCorrupted.ris").toURI());
        Assertions.assertFalse(importer.isRecognizedFormat(file, StandardCharsets.UTF_8));
    }
}

