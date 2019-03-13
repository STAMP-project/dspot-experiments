package org.jabref.logic.importer.fetcher;


import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class TitleFetcherTest {
    private TitleFetcher fetcher;

    private BibEntry bibEntryBischof2009;

    @Test
    public void testGetName() {
        Assertions.assertEquals("Title", fetcher.getName());
    }

    @Test
    public void testGetHelpPage() {
        Assertions.assertEquals("TitleToBibTeX", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    public void testPerformSearchKopp2007() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("BPELscript: A simplified script syntax for WS-BPEL 2.0");
        Assertions.assertEquals(Optional.of(bibEntryBischof2009), fetchedEntry);
    }

    @Test
    public void testPerformSearchEmptyTitle() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("");
        Assertions.assertEquals(Optional.empty(), fetchedEntry);
    }

    @Test
    public void testPerformSearchInvalidTitle() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("An unknown title where noi DOI can be determined");
        Assertions.assertEquals(Optional.empty(), fetchedEntry);
    }
}

