package org.jabref.logic.util.io;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexEntryTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RegExpBasedFileFinderTests {
    private static final String FILES_DIRECTORY = "src/test/resources/org/jabref/logic/importer/unlinkedFilesTestFolder";

    private BibDatabase database;

    private BibEntry entry;

    @Test
    public void testFindFiles() throws Exception {
        // given
        BibEntry localEntry = new BibEntry(BibtexEntryTypes.ARTICLE);
        localEntry.setCiteKey("pdfInDatabase");
        localEntry.setField("year", "2001");
        List<String> extensions = Collections.singletonList("pdf");
        List<Path> dirs = Collections.singletonList(Paths.get(RegExpBasedFileFinderTests.FILES_DIRECTORY));
        RegExpBasedFileFinder fileFinder = new RegExpBasedFileFinder("**/[bibtexkey].*\\\\.[extension]", ',');
        // when
        List<Path> result = fileFinder.findAssociatedFiles(localEntry, dirs, extensions);
        // then
        Assertions.assertEquals(Collections.singletonList(Paths.get("src/test/resources/org/jabref/logic/importer/unlinkedFilesTestFolder/pdfInDatabase.pdf")), result);
    }

    @Test
    public void testYearAuthFirspageFindFiles() throws Exception {
        // given
        List<String> extensions = Collections.singletonList("pdf");
        List<Path> dirs = Collections.singletonList(Paths.get(RegExpBasedFileFinderTests.FILES_DIRECTORY));
        RegExpBasedFileFinder fileFinder = new RegExpBasedFileFinder("**/[year]_[auth]_[firstpage].*\\\\.[extension]", ',');
        // when
        List<Path> result = fileFinder.findAssociatedFiles(entry, dirs, extensions);
        // then
        Assertions.assertEquals(Collections.singletonList(Paths.get("src/test/resources/org/jabref/logic/importer/unlinkedFilesTestFolder/directory/subdirectory/2003_Hippel_209.pdf")), result);
    }

    @Test
    public void testAuthorWithDiacritics() throws Exception {
        // given
        BibEntry localEntry = new BibEntry(BibtexEntryTypes.ARTICLE);
        localEntry.setCiteKey("Grazulis2017");
        localEntry.setField("year", "2017");
        localEntry.setField("author", "Gra?ulis, Saulius and O. Kitsune");
        localEntry.setField("pages", "726--729");
        List<String> extensions = Collections.singletonList("pdf");
        List<Path> dirs = Collections.singletonList(Paths.get(RegExpBasedFileFinderTests.FILES_DIRECTORY));
        RegExpBasedFileFinder fileFinder = new RegExpBasedFileFinder("**/[year]_[auth]_[firstpage]\\\\.[extension]", ',');
        // when
        List<Path> result = fileFinder.findAssociatedFiles(localEntry, dirs, extensions);
        // then
        Assertions.assertEquals(Collections.singletonList(Paths.get("src/test/resources/org/jabref/logic/importer/unlinkedFilesTestFolder/directory/subdirectory/2017_Gra?ulis_726.pdf")), result);
    }

    @Test
    public void testFindFileInSubdirectory() throws Exception {
        // given
        BibEntry localEntry = new BibEntry(BibtexEntryTypes.ARTICLE);
        localEntry.setCiteKey("pdfInSubdirectory");
        localEntry.setField("year", "2017");
        List<String> extensions = Collections.singletonList("pdf");
        List<Path> dirs = Collections.singletonList(Paths.get(RegExpBasedFileFinderTests.FILES_DIRECTORY));
        RegExpBasedFileFinder fileFinder = new RegExpBasedFileFinder("**/[bibtexkey].*\\\\.[extension]", ',');
        // when
        List<Path> result = fileFinder.findAssociatedFiles(localEntry, dirs, extensions);
        // then
        Assertions.assertEquals(Collections.singletonList(Paths.get("src/test/resources/org/jabref/logic/importer/unlinkedFilesTestFolder/directory/subdirectory/pdfInSubdirectory.pdf")), result);
    }

    @Test
    public void testFindFileNonRecursive() throws Exception {
        // given
        BibEntry localEntry = new BibEntry(BibtexEntryTypes.ARTICLE);
        localEntry.setCiteKey("pdfInSubdirectory");
        localEntry.setField("year", "2017");
        List<String> extensions = Collections.singletonList("pdf");
        List<Path> dirs = Collections.singletonList(Paths.get(RegExpBasedFileFinderTests.FILES_DIRECTORY));
        RegExpBasedFileFinder fileFinder = new RegExpBasedFileFinder("*/[bibtexkey].*\\\\.[extension]", ',');
        // when
        List<Path> result = fileFinder.findAssociatedFiles(localEntry, dirs, extensions);
        // then
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testExpandBrackets() {
        Assertions.assertEquals("", RegExpBasedFileFinder.expandBrackets("", entry, database, ','));
        Assertions.assertEquals("dropped", RegExpBasedFileFinder.expandBrackets("drop[unknownkey]ped", entry, database, ','));
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh", RegExpBasedFileFinder.expandBrackets("[author]", entry, database, ','));
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh are two famous authors.", RegExpBasedFileFinder.expandBrackets("[author] are two famous authors.", entry, database, ','));
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh are two famous authors.", RegExpBasedFileFinder.expandBrackets("[author] are two famous authors.", entry, database, ','));
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh have published Open Source Software and the \"Private-Collective\" Innovation Model: Issues for Organization Science in Organization Science.", RegExpBasedFileFinder.expandBrackets("[author] have published [fulltitle] in [journal].", entry, database, ','));
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh have published Open Source Software and the \"Private Collective\" Innovation Model: Issues for Organization Science in Organization Science.", RegExpBasedFileFinder.expandBrackets("[author] have published [title] in [journal].", entry, database, ','));
    }
}

