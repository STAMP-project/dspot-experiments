package org.jabref.logic.importer.fileformat;


import StandardFileType.TXT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RepecNepImporterTest {
    private static final String FILE_ENDING = ".txt";

    private RepecNepImporter testImporter;

    @Test
    public final void testGetFormatName() {
        Assertions.assertEquals("REPEC New Economic Papers (NEP)", testImporter.getName());
    }

    @Test
    public final void testGetCliId() {
        Assertions.assertEquals("repecnep", testImporter.getId());
    }

    @Test
    public void testGetExtension() {
        Assertions.assertEquals(TXT, testImporter.getFileType());
    }

    @Test
    public final void testGetDescription() {
        Assertions.assertEquals("Imports a New Economics Papers-Message from the REPEC-NEP Service.", testImporter.getDescription());
    }
}

