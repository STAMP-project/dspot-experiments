package org.jabref.logic.importer.fileformat;


import StandardFileType.TXT;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jabref.logic.bibtex.BibEntryAssert;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class OvidImporterTest {
    private static final String FILE_ENDING = ".txt";

    private OvidImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("Ovid", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("ovid", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(TXT, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Imports an Ovid file.", importer.getDescription());
    }

    @Test
    public void testImportEmpty() throws IOException, URISyntaxException {
        Path file = Paths.get(OvidImporter.class.getResource("Empty.txt").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(Collections.emptyList(), entries);
    }

    @Test
    public void testImportEntries1() throws IOException, URISyntaxException {
        Path file = Paths.get(OvidImporter.class.getResource("OvidImporterTest1.txt").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(5, entries.size());
        BibEntry entry = entries.get(0);
        Assertions.assertEquals("misc", entry.getType());
        Assertions.assertEquals(Optional.of("Mustermann and Musterfrau"), entry.getField("author"));
        Assertions.assertEquals(Optional.of("Short abstract"), entry.getField("abstract"));
        Assertions.assertEquals(Optional.of("Musterbuch"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("Einleitung"), entry.getField("chaptertitle"));
        entry = entries.get(1);
        Assertions.assertEquals("inproceedings", entry.getType());
        Assertions.assertEquals(Optional.of("Max"), entry.getField("editor"));
        Assertions.assertEquals(Optional.of("Max the Editor"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("Very Long Title"), entry.getField("journal"));
        Assertions.assertEquals(Optional.of("28"), entry.getField("volume"));
        Assertions.assertEquals(Optional.of("2"), entry.getField("issue"));
        Assertions.assertEquals(Optional.of("2015"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("103--106"), entry.getField("pages"));
        entry = entries.get(2);
        Assertions.assertEquals("incollection", entry.getType());
        Assertions.assertEquals(Optional.of("Max"), entry.getField("author"));
        Assertions.assertEquals(Optional.of("Test"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("Very Long Title"), entry.getField("journal"));
        Assertions.assertEquals(Optional.of("28"), entry.getField("volume"));
        Assertions.assertEquals(Optional.of("2"), entry.getField("issue"));
        Assertions.assertEquals(Optional.of("April"), entry.getField("month"));
        Assertions.assertEquals(Optional.of("2015"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("103--106"), entry.getField("pages"));
        entry = entries.get(3);
        Assertions.assertEquals("book", entry.getType());
        Assertions.assertEquals(Optional.of("Max"), entry.getField("author"));
        Assertions.assertEquals(Optional.of("2015"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("Editor"), entry.getField("editor"));
        Assertions.assertEquals(Optional.of("Very Long Title"), entry.getField("booktitle"));
        Assertions.assertEquals(Optional.of("103--106"), entry.getField("pages"));
        Assertions.assertEquals(Optional.of("Address"), entry.getField("address"));
        Assertions.assertEquals(Optional.of("Publisher"), entry.getField("publisher"));
        entry = entries.get(4);
        Assertions.assertEquals("article", entry.getType());
        Assertions.assertEquals(Optional.of("2014"), entry.getField("year"));
        Assertions.assertEquals(Optional.of("58"), entry.getField("pages"));
        Assertions.assertEquals(Optional.of("Test"), entry.getField("address"));
        Assertions.assertEquals(Optional.empty(), entry.getField("title"));
        Assertions.assertEquals(Optional.of("TestPublisher"), entry.getField("publisher"));
    }

    @Test
    public void testImportEntries2() throws IOException, URISyntaxException {
        Path file = Paths.get(OvidImporter.class.getResource("OvidImporterTest2Invalid.txt").toURI());
        List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(Collections.emptyList(), entries);
    }

    @Test
    public void testImportSingleEntries() throws IOException, URISyntaxException {
        for (int n = 3; n <= 7; n++) {
            Path file = Paths.get(OvidImporter.class.getResource((("OvidImporterTest" + n) + ".txt")).toURI());
            try (InputStream nis = OvidImporter.class.getResourceAsStream((("OvidImporterTestBib" + n) + ".bib"))) {
                List<BibEntry> entries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
                Assertions.assertNotNull(entries);
                Assertions.assertEquals(1, entries.size());
                BibEntryAssert.assertEquals(nis, entries.get(0));
            }
        }
    }
}

