package org.jabref.logic.importer.fileformat;


import StandardFileType.MEDLINE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Articles in the medline format can be downloaded from http://www.ncbi.nlm.nih.gov/pubmed/. 1. Search for a term and
 * make sure you have selected the PubMed database 2. Select the results you want to export by checking their checkboxes
 * 3. Press on the 'Send to' drop down menu on top of the search results 4. Select 'File' as Destination and 'XML' as
 * Format 5. Press 'Create File' to download your search results in a medline xml file
 */
public class MedlineImporterTest {
    private MedlineImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("Medline/PubMed", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("medline", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(MEDLINE, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Importer for the Medline format.", importer.getDescription());
    }
}

