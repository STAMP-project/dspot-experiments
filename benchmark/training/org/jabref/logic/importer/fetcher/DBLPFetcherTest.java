package org.jabref.logic.importer.fetcher;


import java.util.Collections;
import java.util.List;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class DBLPFetcherTest {
    private DBLPFetcher dblpFetcher;

    private BibEntry entry;

    @Test
    public void findSingleEntry() throws FetcherException {
        String query = "Process Engine Benchmarking with Betsy in the Context of {ISO/IEC} Quality Standards";
        List<BibEntry> result = dblpFetcher.performSearch(query);
        Assertions.assertEquals(Collections.singletonList(entry), result);
    }

    @Test
    public void findSingleEntryUsingComplexOperators() throws FetcherException {
        String query = "geiger harrer betsy$ softw.trends";// -wirtz Negative operators do no longer work,  see issue https://github.com/JabRef/jabref/issues/2890

        List<BibEntry> result = dblpFetcher.performSearch(query);
        Assertions.assertEquals(Collections.singletonList(entry), result);
    }

    @Test
    public void findNothing() throws Exception {
        Assertions.assertEquals(Collections.emptyList(), dblpFetcher.performSearch(""));
    }
}

