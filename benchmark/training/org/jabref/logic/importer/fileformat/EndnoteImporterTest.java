package org.jabref.logic.importer.fileformat;


import StandardFileType.ENDNOTE;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EndnoteImporterTest {
    private EndnoteImporter importer;

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("Refer/Endnote", importer.getName());
    }

    @Test
    public void testGetCLIId() {
        Assertions.assertEquals("refer", importer.getId());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(ENDNOTE, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals(("Importer for the Refer/Endnote format." + " Modified to use article number for pages if pages are missing."), importer.getDescription());
    }

    @Test
    public void testIsRecognizedFormat() throws IOException, URISyntaxException {
        List<String> list = Arrays.asList("Endnote.pattern.A.enw", "Endnote.pattern.E.enw", "Endnote.book.example.enw");
        for (String string : list) {
            Path file = Paths.get(EndnoteImporterTest.class.getResource(string).toURI());
            Assertions.assertTrue(importer.isRecognizedFormat(file, StandardCharsets.UTF_8));
        }
    }

    @Test
    public void testIsRecognizedFormatReject() throws IOException, URISyntaxException {
        List<String> list = Arrays.asList("IEEEImport1.txt", "IsiImporterTest1.isi", "IsiImporterTestInspec.isi", "IsiImporterTestWOS.isi", "IsiImporterTestMedline.isi", "RisImporterTest1.ris", "Endnote.pattern.no_enw", "empty.pdf", "annotated.pdf");
        for (String string : list) {
            Path file = Paths.get(EndnoteImporterTest.class.getResource(string).toURI());
            Assertions.assertFalse(importer.isRecognizedFormat(file, Charset.defaultCharset()));
        }
    }

    @Test
    public void testImportEntries0() throws IOException, URISyntaxException {
        Path file = Paths.get(EndnoteImporterTest.class.getResource("Endnote.entries.enw").toURI());
        List<BibEntry> bibEntries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        Assertions.assertEquals(5, bibEntries.size());
        BibEntry first = bibEntries.get(0);
        Assertions.assertEquals("misc", first.getType());
        Assertions.assertEquals(Optional.of("testA0 and testA1"), first.getField("author"));
        Assertions.assertEquals(Optional.of("testE0 and testE1"), first.getField("editor"));
        Assertions.assertEquals(Optional.of("testT"), first.getField("title"));
        BibEntry second = bibEntries.get(1);
        Assertions.assertEquals("misc", second.getType());
        Assertions.assertEquals(Optional.of("testC"), second.getField("address"));
        Assertions.assertEquals(Optional.of("testB2"), second.getField("booktitle"));
        Assertions.assertEquals(Optional.of("test8"), second.getField("date"));
        Assertions.assertEquals(Optional.of("test7"), second.getField("edition"));
        Assertions.assertEquals(Optional.of("testJ"), second.getField("journal"));
        Assertions.assertEquals(Optional.of("testD"), second.getField("year"));
        BibEntry third = bibEntries.get(2);
        Assertions.assertEquals("article", third.getType());
        Assertions.assertEquals(Optional.of("testB0"), third.getField("journal"));
        BibEntry fourth = bibEntries.get(3);
        Assertions.assertEquals("book", fourth.getType());
        Assertions.assertEquals(Optional.of("testI0"), fourth.getField("publisher"));
        Assertions.assertEquals(Optional.of("testB1"), fourth.getField("series"));
        BibEntry fifth = bibEntries.get(4);
        Assertions.assertEquals("mastersthesis", fifth.getType());
        Assertions.assertEquals(Optional.of("testX"), fifth.getField("abstract"));
        Assertions.assertEquals(Optional.of("testF"), fifth.getField("bibtexkey"));
        Assertions.assertEquals(Optional.of("testR"), fifth.getField("doi"));
        Assertions.assertEquals(Optional.of("testK"), fifth.getField("keywords"));
        Assertions.assertEquals(Optional.of("testO1"), fifth.getField("note"));
        Assertions.assertEquals(Optional.of("testN"), fifth.getField("number"));
        Assertions.assertEquals(Optional.of("testP"), fifth.getField("pages"));
        Assertions.assertEquals(Optional.of("testI1"), fifth.getField("school"));
        Assertions.assertEquals(Optional.of("testU"), fifth.getField("url"));
        Assertions.assertEquals(Optional.of("testV"), fifth.getField("volume"));
    }

    @Test
    public void testImportEntries1() throws IOException {
        String medlineString = "%O Artn\\\\s testO\n%A testA,\n%E testE0, testE1";
        List<BibEntry> bibEntries = importer.importDatabase(new BufferedReader(new StringReader(medlineString))).getDatabase().getEntries();
        BibEntry entry = bibEntries.get(0);
        Assertions.assertEquals(1, bibEntries.size());
        Assertions.assertEquals("misc", entry.getType());
        Assertions.assertEquals(Optional.of("testA"), entry.getField("author"));
        Assertions.assertEquals(Optional.of("testE0, testE1"), entry.getField("editor"));
        Assertions.assertEquals(Optional.of("testO"), entry.getField("pages"));
    }

    @Test
    public void testImportEntriesBookExample() throws IOException, URISyntaxException {
        Path file = Paths.get(EndnoteImporterTest.class.getResource("Endnote.book.example.enw").toURI());
        List<BibEntry> bibEntries = importer.importDatabase(file, StandardCharsets.UTF_8).getDatabase().getEntries();
        BibEntry entry = bibEntries.get(0);
        Assertions.assertEquals(1, bibEntries.size());
        Assertions.assertEquals("book", entry.getType());
        Assertions.assertEquals(Optional.of("Heidelberg"), entry.getField("address"));
        Assertions.assertEquals(Optional.of("Prei?el, Ren? and Stachmann, Bj?rn"), entry.getField("author"));
        Assertions.assertEquals(Optional.of("3., aktualisierte und erweiterte Auflage"), entry.getField("edition"));
        Assertions.assertEquals(Optional.of("Versionsverwaltung"), entry.getField("keywords"));
        Assertions.assertEquals(Optional.of("XX, 327"), entry.getField("pages"));
        Assertions.assertEquals(Optional.of("dpunkt.verlag"), entry.getField("publisher"));
        Assertions.assertEquals(Optional.of("Git : dezentrale Versionsverwaltung im Team : Grundlagen und Workflows"), entry.getField("title"));
        Assertions.assertEquals(Optional.of("http://d-nb.info/107601965X"), entry.getField("url"));
        Assertions.assertEquals(Optional.of("2016"), entry.getField("year"));
    }
}

