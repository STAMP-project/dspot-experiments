package org.jabref.logic.importer.fileformat;


import StandardFileType.TXT;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BiblioscapeImporterTest {
    private BiblioscapeImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("Biblioscape", importer.getName());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(TXT, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals(("Imports a Biblioscape Tag File.\n" + "Several Biblioscape field types are ignored. Others are only included in the BibTeX field \"comment\"."), importer.getDescription());
    }

    @Test
    public void testGetCLIID() {
        Assertions.assertEquals("biblioscape", importer.getId());
    }

    @Test
    public void testImportEntriesAbortion() throws Throwable {
        Path file = Paths.get(BiblioscapeImporter.class.getResource("BiblioscapeImporterTestCorrupt.txt").toURI());
        Assertions.assertEquals(Collections.emptyList(), importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries());
    }
}

