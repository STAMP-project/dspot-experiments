package org.jabref.logic.importer.fileformat;


import StandardFileType.TXT;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CopacImporterTest {
    private CopacImporter importer;

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(TXT, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Importer for COPAC format.", importer.getDescription());
    }

    @Test
    public void testImportEmptyEntries() throws Exception {
        Path path = Paths.get(CopacImporterTest.class.getResource("Empty.txt").toURI());
        List<BibEntry> entries = importer.importDatabase(path, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(Collections.emptyList(), entries);
    }
}

