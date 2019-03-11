package org.jabref.logic.openoffice;


import OOBibStyle.MULTI_CITE_CHRONOLOGICAL;
import StyleLoader.DEFAULT_AUTHORYEAR_STYLE_PATH;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.importer.Importer;
import org.jabref.logic.importer.ParserResult;
import org.jabref.logic.layout.Layout;
import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static OOBibStyle.UNDEFINED_CITATION_MARKER;
import static StyleLoader.DEFAULT_AUTHORYEAR_STYLE_PATH;
import static StyleLoader.DEFAULT_NUMERICAL_STYLE_PATH;


public class OOBibStyleTest {
    private LayoutFormatterPreferences layoutFormatterPreferences;

    private ImportFormatPreferences importFormatPreferences;

    @Test
    public void testAuthorYear() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_AUTHORYEAR_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertTrue(style.isValid());
        Assertions.assertTrue(style.isFromResource());
        Assertions.assertFalse(style.isBibtexKeyCiteMarkers());
        Assertions.assertFalse(style.isBoldCitations());
        Assertions.assertFalse(style.isFormatCitations());
        Assertions.assertFalse(style.isItalicCitations());
        Assertions.assertFalse(style.isNumberEntries());
        Assertions.assertFalse(style.isSortByPosition());
    }

    @Test
    public void testAuthorYearAsFile() throws IOException, URISyntaxException {
        File defFile = Paths.get(OOBibStyleTest.class.getResource(DEFAULT_AUTHORYEAR_STYLE_PATH).toURI()).toFile();
        OOBibStyle style = new OOBibStyle(defFile, layoutFormatterPreferences, StandardCharsets.UTF_8);
        Assertions.assertTrue(style.isValid());
        Assertions.assertFalse(style.isFromResource());
        Assertions.assertFalse(style.isBibtexKeyCiteMarkers());
        Assertions.assertFalse(style.isBoldCitations());
        Assertions.assertFalse(style.isFormatCitations());
        Assertions.assertFalse(style.isItalicCitations());
        Assertions.assertFalse(style.isNumberEntries());
        Assertions.assertFalse(style.isSortByPosition());
    }

    @Test
    public void testNumerical() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertTrue(style.isValid());
        Assertions.assertFalse(style.isBibtexKeyCiteMarkers());
        Assertions.assertFalse(style.isBoldCitations());
        Assertions.assertFalse(style.isFormatCitations());
        Assertions.assertFalse(style.isItalicCitations());
        Assertions.assertTrue(style.isNumberEntries());
        Assertions.assertTrue(style.isSortByPosition());
    }

    @Test
    public void testGetNumCitationMarker() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertEquals("[1] ", style.getNumCitationMarker(Arrays.asList(1), (-1), true));
        Assertions.assertEquals("[1]", style.getNumCitationMarker(Arrays.asList(1), (-1), false));
        Assertions.assertEquals("[1] ", style.getNumCitationMarker(Arrays.asList(1), 0, true));
        Assertions.assertEquals("[1-3] ", style.getNumCitationMarker(Arrays.asList(1, 2, 3), 1, true));
        Assertions.assertEquals("[1; 2; 3] ", style.getNumCitationMarker(Arrays.asList(1, 2, 3), 5, true));
        Assertions.assertEquals("[1; 2; 3] ", style.getNumCitationMarker(Arrays.asList(1, 2, 3), (-1), true));
        Assertions.assertEquals("[1; 3; 12] ", style.getNumCitationMarker(Arrays.asList(1, 12, 3), 1, true));
        Assertions.assertEquals("[3-5; 7; 10-12] ", style.getNumCitationMarker(Arrays.asList(12, 7, 3, 4, 11, 10, 5), 1, true));
        String citation = style.getNumCitationMarker(Arrays.asList(1), (-1), false);
        Assertions.assertEquals("[1; pp. 55-56]", style.insertPageInfo(citation, "pp. 55-56"));
    }

    @Test
    public void testGetNumCitationMarkerUndefined() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertEquals((("[" + (UNDEFINED_CITATION_MARKER)) + "; 2-4] "), style.getNumCitationMarker(Arrays.asList(4, 2, 3, 0), 1, true));
        Assertions.assertEquals((("[" + (UNDEFINED_CITATION_MARKER)) + "] "), style.getNumCitationMarker(Arrays.asList(0), 1, true));
        Assertions.assertEquals((("[" + (UNDEFINED_CITATION_MARKER)) + "; 1-3] "), style.getNumCitationMarker(Arrays.asList(1, 2, 3, 0), 1, true));
        Assertions.assertEquals((((((("[" + (UNDEFINED_CITATION_MARKER)) + "; ") + (UNDEFINED_CITATION_MARKER)) + "; ") + (UNDEFINED_CITATION_MARKER)) + "] "), style.getNumCitationMarker(Arrays.asList(0, 0, 0), 1, true));
    }

    @Test
    public void testGetCitProperty() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertEquals(", ", style.getStringCitProperty("AuthorSeparator"));
        Assertions.assertEquals(3, style.getIntCitProperty("MaxAuthors"));
        Assertions.assertTrue(style.getBooleanCitProperty(MULTI_CITE_CHRONOLOGICAL));
        Assertions.assertEquals("Default", style.getCitationCharacterFormat());
        Assertions.assertEquals("Default [number] style file.", style.getName());
        Set<String> journals = style.getJournals();
        Assertions.assertTrue(journals.contains("Journal name 1"));
    }

    /**
     * In IntelliJ: When running this test, ensure that the working directory is <code>%MODULE_WORKING_DIR%"</code>
     */
    @Test
    public void testGetCitationMarker() throws IOException {
        Path testBibtexFile = Paths.get("src/test/resources/testbib/complex.bib");
        ParserResult result = new org.jabref.logic.importer.fileformat.BibtexParser(importFormatPreferences, new DummyFileUpdateMonitor()).parse(Importer.getReader(testBibtexFile, StandardCharsets.UTF_8));
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        BibDatabase db = result.getDatabase();
        for (BibEntry entry : db.getEntries()) {
            entryDBMap.put(entry, db);
        }
        BibEntry entry = db.getEntryByKey("1137631").get();
        Assertions.assertEquals("[Bostr?m et al., 2006]", style.getCitationMarker(Arrays.asList(entry), entryDBMap, true, null, null));
        Assertions.assertEquals("Bostr?m et al. [2006]", style.getCitationMarker(Arrays.asList(entry), entryDBMap, false, null, new int[]{ 3 }));
        Assertions.assertEquals("[Bostr?m, W?yrynen, Bod?n, Beznosov & Kruchten, 2006]", style.getCitationMarker(Arrays.asList(entry), entryDBMap, true, null, new int[]{ 5 }));
    }

    /**
     * In IntelliJ: When running this test, ensure that the working directory is <code>%MODULE_WORKING_DIR%"</code>
     */
    @Test
    public void testLayout() throws IOException {
        Path testBibtexFile = Paths.get("src/test/resources/testbib/complex.bib");
        ParserResult result = new org.jabref.logic.importer.fileformat.BibtexParser(importFormatPreferences, new DummyFileUpdateMonitor()).parse(Importer.getReader(testBibtexFile, StandardCharsets.UTF_8));
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        BibDatabase db = result.getDatabase();
        Layout l = style.getReferenceFormat("default");
        l.setPostFormatter(new OOPreFormatter());
        BibEntry entry = db.getEntryByKey("1137631").get();
        Assertions.assertEquals("Bostr?m, G.; W?yrynen, J.; Bod?n, M.; Beznosov, K. and Kruchten, P. (<b>2006</b>). <i>Extending XP practices to support security requirements engineering</i>,   : 11-18.", l.doLayout(entry, db));
        l = style.getReferenceFormat("incollection");
        l.setPostFormatter(new OOPreFormatter());
        Assertions.assertEquals("Bostr?m, G.; W?yrynen, J.; Bod?n, M.; Beznosov, K. and Kruchten, P. (<b>2006</b>). <i>Extending XP practices to support security requirements engineering</i>. In:  (Ed.), <i>SESS '06: Proceedings of the 2006 international workshop on Software engineering for secure systems</i>, ACM.", l.doLayout(entry, db));
    }

    @Test
    public void testInstitutionAuthor() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        BibDatabase database = new BibDatabase();
        Layout l = style.getReferenceFormat("article");
        l.setPostFormatter(new OOPreFormatter());
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "{JabRef Development Team}");
        entry.setField("title", "JabRef Manual");
        entry.setField("year", "2016");
        database.insertEntry(entry);
        Assertions.assertEquals("<b>JabRef Development Team</b> (<b>2016</b>). <i>JabRef Manual</i>,  .", l.doLayout(entry, database));
    }

    @Test
    public void testVonAuthor() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        BibDatabase database = new BibDatabase();
        Layout l = style.getReferenceFormat("article");
        l.setPostFormatter(new OOPreFormatter());
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "Alpha von Beta");
        entry.setField("title", "JabRef Manual");
        entry.setField("year", "2016");
        database.insertEntry(entry);
        Assertions.assertEquals("<b>von Beta, A.</b> (<b>2016</b>). <i>JabRef Manual</i>,  .", l.doLayout(entry, database));
    }

    @Test
    public void testInstitutionAuthorMarker() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "{JabRef Development Team}");
        entry.setField("title", "JabRef Manual");
        entry.setField("year", "2016");
        database.insertEntry(entry);
        entries.add(entry);
        entryDBMap.put(entry, database);
        Assertions.assertEquals("[JabRef Development Team, 2016]", style.getCitationMarker(entries, entryDBMap, true, null, null));
    }

    @Test
    public void testVonAuthorMarker() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "Alpha von Beta");
        entry.setField("title", "JabRef Manual");
        entry.setField("year", "2016");
        database.insertEntry(entry);
        entries.add(entry);
        entryDBMap.put(entry, database);
        Assertions.assertEquals("[von Beta, 2016]", style.getCitationMarker(entries, entryDBMap, true, null, null));
    }

    @Test
    public void testNullAuthorMarker() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("year", "2016");
        database.insertEntry(entry);
        entries.add(entry);
        entryDBMap.put(entry, database);
        Assertions.assertEquals("[, 2016]", style.getCitationMarker(entries, entryDBMap, true, null, null));
    }

    @Test
    public void testNullYearMarker() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "Alpha von Beta");
        database.insertEntry(entry);
        entries.add(entry);
        entryDBMap.put(entry, database);
        Assertions.assertEquals("[von Beta, ]", style.getCitationMarker(entries, entryDBMap, true, null, null));
    }

    @Test
    public void testEmptyEntryMarker() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry = new BibEntry();
        entry.setType("article");
        database.insertEntry(entry);
        entries.add(entry);
        entryDBMap.put(entry, database);
        Assertions.assertEquals("[, ]", style.getCitationMarker(entries, entryDBMap, true, null, null));
    }

    @Test
    public void testGetCitationMarkerInParenthesisUniquefiers() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField("author", "Alpha Beta");
        entry1.setField("title", "Paper 1");
        entry1.setField("year", "2000");
        entries.add(entry1);
        database.insertEntry(entry1);
        BibEntry entry3 = new BibEntry();
        entry3.setField("author", "Alpha Beta");
        entry3.setField("title", "Paper 2");
        entry3.setField("year", "2000");
        entries.add(entry3);
        database.insertEntry(entry3);
        BibEntry entry2 = new BibEntry();
        entry2.setField("author", "Gamma Epsilon");
        entry2.setField("year", "2001");
        entries.add(entry2);
        database.insertEntry(entry2);
        for (BibEntry entry : database.getEntries()) {
            entryDBMap.put(entry, database);
        }
        Assertions.assertEquals("[Beta, 2000; Beta, 2000; Epsilon, 2001]", style.getCitationMarker(entries, entryDBMap, true, null, null));
        Assertions.assertEquals("[Beta, 2000a,b; Epsilon, 2001]", style.getCitationMarker(entries, entryDBMap, true, new String[]{ "a", "b", "" }, new int[]{ 1, 1, 1 }));
    }

    @Test
    public void testGetCitationMarkerInTextUniquefiers() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField("author", "Alpha Beta");
        entry1.setField("title", "Paper 1");
        entry1.setField("year", "2000");
        entries.add(entry1);
        database.insertEntry(entry1);
        BibEntry entry3 = new BibEntry();
        entry3.setField("author", "Alpha Beta");
        entry3.setField("title", "Paper 2");
        entry3.setField("year", "2000");
        entries.add(entry3);
        database.insertEntry(entry3);
        BibEntry entry2 = new BibEntry();
        entry2.setField("author", "Gamma Epsilon");
        entry2.setField("year", "2001");
        entries.add(entry2);
        database.insertEntry(entry2);
        for (BibEntry entry : database.getEntries()) {
            entryDBMap.put(entry, database);
        }
        Assertions.assertEquals("Beta [2000]; Beta [2000]; Epsilon [2001]", style.getCitationMarker(entries, entryDBMap, false, null, null));
        Assertions.assertEquals("Beta [2000a,b]; Epsilon [2001]", style.getCitationMarker(entries, entryDBMap, false, new String[]{ "a", "b", "" }, new int[]{ 1, 1, 1 }));
    }

    @Test
    public void testGetCitationMarkerInParenthesisUniquefiersThreeSameAuthor() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField("author", "Alpha Beta");
        entry1.setField("title", "Paper 1");
        entry1.setField("year", "2000");
        entries.add(entry1);
        database.insertEntry(entry1);
        BibEntry entry2 = new BibEntry();
        entry2.setField("author", "Alpha Beta");
        entry2.setField("title", "Paper 2");
        entry2.setField("year", "2000");
        entries.add(entry2);
        database.insertEntry(entry2);
        BibEntry entry3 = new BibEntry();
        entry3.setField("author", "Alpha Beta");
        entry3.setField("title", "Paper 3");
        entry3.setField("year", "2000");
        entries.add(entry3);
        database.insertEntry(entry3);
        for (BibEntry entry : database.getEntries()) {
            entryDBMap.put(entry, database);
        }
        Assertions.assertEquals("[Beta, 2000a,b,c]", style.getCitationMarker(entries, entryDBMap, true, new String[]{ "a", "b", "c" }, new int[]{ 1, 1, 1 }));
    }

    @Test
    public void testGetCitationMarkerInTextUniquefiersThreeSameAuthor() throws IOException {
        OOBibStyle style = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry1 = new BibEntry();
        entry1.setField("author", "Alpha Beta");
        entry1.setField("title", "Paper 1");
        entry1.setField("year", "2000");
        entries.add(entry1);
        database.insertEntry(entry1);
        BibEntry entry2 = new BibEntry();
        entry2.setField("author", "Alpha Beta");
        entry2.setField("title", "Paper 2");
        entry2.setField("year", "2000");
        entries.add(entry2);
        database.insertEntry(entry2);
        BibEntry entry3 = new BibEntry();
        entry3.setField("author", "Alpha Beta");
        entry3.setField("title", "Paper 3");
        entry3.setField("year", "2000");
        entries.add(entry3);
        database.insertEntry(entry3);
        for (BibEntry entry : database.getEntries()) {
            entryDBMap.put(entry, database);
        }
        Assertions.assertEquals("Beta [2000a,b,c]", style.getCitationMarker(entries, entryDBMap, false, new String[]{ "a", "b", "c" }, new int[]{ 1, 1, 1 }));
    }

    // TODO: equals only work when initialized from file, not from reader
    @Test
    public void testEquals() throws IOException {
        OOBibStyle style1 = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        OOBibStyle style2 = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertEquals(style1, style2);
    }

    // TODO: equals only work when initialized from file, not from reader
    @Test
    public void testNotEquals() throws IOException {
        OOBibStyle style1 = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        OOBibStyle style2 = new OOBibStyle(DEFAULT_AUTHORYEAR_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertNotEquals(style1, style2);
    }

    @Test
    public void testCompareToEqual() throws IOException {
        OOBibStyle style1 = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        OOBibStyle style2 = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertEquals(0, style1.compareTo(style2));
    }

    @Test
    public void testCompareToNotEqual() throws IOException {
        OOBibStyle style1 = new OOBibStyle(DEFAULT_NUMERICAL_STYLE_PATH, layoutFormatterPreferences);
        OOBibStyle style2 = new OOBibStyle(DEFAULT_AUTHORYEAR_STYLE_PATH, layoutFormatterPreferences);
        Assertions.assertTrue(((style1.compareTo(style2)) > 0));
        Assertions.assertFalse(((style2.compareTo(style1)) > 0));
    }

    @Test
    public void testEmptyStringPropertyAndOxfordComma() throws IOException, URISyntaxException {
        OOBibStyle style = new OOBibStyle("test.jstyle", layoutFormatterPreferences);
        Map<BibEntry, BibDatabase> entryDBMap = new HashMap<>();
        List<BibEntry> entries = new ArrayList<>();
        BibDatabase database = new BibDatabase();
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "Alpha von Beta and Gamma Epsilon and Ypsilon Tau");
        entry.setField("title", "JabRef Manual");
        entry.setField("year", "2016");
        database.insertEntry(entry);
        entries.add(entry);
        entryDBMap.put(entry, database);
        Assertions.assertEquals("von Beta, Epsilon, & Tau, 2016", style.getCitationMarker(entries, entryDBMap, true, null, null));
    }
}

