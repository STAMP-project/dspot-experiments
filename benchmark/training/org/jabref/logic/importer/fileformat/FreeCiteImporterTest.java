package org.jabref.logic.importer.fileformat;


import StandardFileType.FREECITE;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


// we mark this as fetcher test, because it depends on the avaiability of the FreeCite online library
@FetcherTest
public class FreeCiteImporterTest {
    private FreeCiteImporter importer;

    @Test
    public void freeCiteReturnsSomething() throws IOException {
        String entryText = "Kopp, O.; Martin, D.; Wutke, D. & Leymann, F. The Difference Between Graph-Based and Block-Structured Business Process Modelling Languages Enterprise Modelling and Information Systems, Gesellschaft f?r Informatik e.V. (GI), 2009, 4, 3-13";
        BufferedReader input = new BufferedReader(new StringReader(entryText));
        List<BibEntry> bibEntries = importer.importDatabase(input).getDatabase().getEntries();
        BibEntry bibEntry = bibEntries.get(0);
        Assertions.assertEquals(1, bibEntries.size());
        Assertions.assertEquals(bibEntry.getField("author"), Optional.of("O Kopp and D Martin and D Wutke and F Leymann"));
    }

    @Test
    public void testGetFormatName() {
        Assertions.assertEquals("text citations", importer.getName());
    }

    @Test
    public void testsGetExtensions() {
        Assertions.assertEquals(FREECITE, importer.getFileType());
    }

    @Test
    public void testGetDescription() {
        Assertions.assertEquals("This importer parses text format citations using the online API of FreeCite.", importer.getDescription());
    }
}

