package org.jabref.logic.importer.fileformat;


import StandardFileType.TXT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.jabref.logic.bibtex.BibEntryAssert;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class InspecImporterTest {
    private static final String FILE_ENDING = ".txt";

    private InspecImporter importer;

    @Test
    public void testCompleteBibtexEntryOnJournalPaperImport() throws IOException, URISyntaxException {
        BibEntry expectedEntry = new BibEntry();
        expectedEntry.setType("article");
        expectedEntry.setField("title", "The SIS project : software reuse with a natural language approach");
        expectedEntry.setField("author", "Prechelt, Lutz");
        expectedEntry.setField("year", "1992");
        expectedEntry.setField("abstract", "Abstrakt");
        expectedEntry.setField("keywords", "key");
        expectedEntry.setField("journal", "10000");
        expectedEntry.setField("pages", "20");
        expectedEntry.setField("volume", "19");
        BibEntryAssert.assertEquals(Collections.singletonList(expectedEntry), InspecImporterTest.class.getResource("InspecImportTest2.txt"), importer);
    }

    @Test
    public void importConferencePaperGivesInproceedings() throws IOException {
        String testInput = "Record.*INSPEC.*\n" + (("\n" + "RT ~ Conference-Paper\n") + "AU ~ Prechelt, Lutz");
        BibEntry expectedEntry = new BibEntry();
        expectedEntry.setType("Inproceedings");
        expectedEntry.setField("author", "Prechelt, Lutz");
        try (BufferedReader reader = new BufferedReader(new StringReader(testInput))) {
            List<BibEntry> entries = importer.importDatabase(reader).getDatabase().getEntries();
            Assertions.assertEquals(Collections.singletonList(expectedEntry), entries);
        }
    }

    @Test
    public void importMiscGivesMisc() throws IOException {
        String testInput = "Record.*INSPEC.*\n" + (("\n" + "AU ~ Prechelt, Lutz \n") + "RT ~ Misc");
        BibEntry expectedEntry = new BibEntry();
        expectedEntry.setType("Misc");
        expectedEntry.setField("author", "Prechelt, Lutz");
        try (BufferedReader reader = new BufferedReader(new StringReader(testInput))) {
            List<BibEntry> entries = importer.importDatabase(reader).getDatabase().getEntries();
            Assertions.assertEquals(1, entries.size());
            BibEntry entry = entries.get(0);
            Assertions.assertEquals(expectedEntry, entry);
        }
    }

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("INSPEC", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("inspec", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(TXT, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("INSPEC format importer.", importer.getDescription());
    }
}

