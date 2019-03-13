package org.jabref.logic.importer.fetcher;


import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class RfcFetcherTest {
    private RfcFetcher fetcher;

    private BibEntry bibEntry;

    @Test
    public void getNameReturnsEqualIdName() {
        Assertions.assertEquals("RFC", fetcher.getName());
    }

    @Test
    public void getHelpPageReturnsEqualHelpPage() {
        Assertions.assertEquals("RFCtoBibTeX", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    public void performSearchByIdFindsEntryWithRfcPrefix() throws Exception {
        Assertions.assertEquals(Optional.of(bibEntry), fetcher.performSearchById("RFC1945"));
    }

    @Test
    public void performSearchByIdFindsEntryWithoutRfcPrefix() throws Exception {
        Assertions.assertEquals(Optional.of(bibEntry), fetcher.performSearchById("1945"));
    }

    @Test
    public void performSearchByIdFindsNothingWithoutIdentifier() throws Exception {
        Assertions.assertEquals(Optional.empty(), fetcher.performSearchById(""));
    }

    @Test
    public void performSearchByIdFindsNothingWithValidIdentifier() throws Exception {
        Assertions.assertEquals(Optional.empty(), fetcher.performSearchById("RFC9999"));
    }

    @Test
    public void performSearchByIdFindsNothingWithInvalidIdentifier() throws Exception {
        Assertions.assertEquals(Optional.empty(), fetcher.performSearchById("banana"));
    }
}

