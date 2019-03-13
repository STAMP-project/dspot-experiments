package org.jabref.logic.importer.fileformat;


import StandardFileType.BIBTEXML;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BibTeXMLImporterTest {
    private BibTeXMLImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("BibTeXML", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("bibtexml", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(BIBTEXML, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Importer for the BibTeXML format.", importer.getDescription());
    }
}

