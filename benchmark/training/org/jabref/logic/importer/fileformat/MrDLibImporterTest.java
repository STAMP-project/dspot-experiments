package org.jabref.logic.importer.fileformat;


import FieldName.TITLE;
import FieldName.YEAR;
import StandardFileType.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import org.jabref.logic.importer.ParserResult;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MrDLibImporterTest {
    private MrDLibImporter importer;

    private BufferedReader input;

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("Takes valid JSON documents from the Mr. DLib API and parses them into a BibEntry", importer.getDescription());
    }

    @Test
    public void testGetName() {
        Assertions.assertEquals("MrDLibImporter", importer.getName());
    }

    @Test
    public void testGetFileExtention() {
        Assertions.assertEquals(JSON, importer.getFileType());
    }

    @Test
    public void testImportDatabaseIsYearSetCorrectly() throws IOException {
        ParserResult parserResult = importer.importDatabase(input);
        List<BibEntry> resultList = parserResult.getDatabase().getEntries();
        Assertions.assertEquals("2006", resultList.get(0).getLatexFreeField(YEAR).get());
    }

    @Test
    public void testImportDatabaseIsTitleSetCorrectly() throws IOException {
        ParserResult parserResult = importer.importDatabase(input);
        List<BibEntry> resultList = parserResult.getDatabase().getEntries();
        Assertions.assertEquals("The protection of rural lands with the spatial development strategy on the case of Hrastnik commune", resultList.get(0).getLatexFreeField(TITLE).get());
    }

    @Test
    public void testImportDatabaseMin() throws IOException {
        ParserResult parserResult = importer.importDatabase(input);
        List<BibEntry> resultList = parserResult.getDatabase().getEntries();
        Assertions.assertSame(5, resultList.size());
    }
}

