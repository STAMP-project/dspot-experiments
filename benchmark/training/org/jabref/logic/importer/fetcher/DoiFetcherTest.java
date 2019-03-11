package org.jabref.logic.importer.fetcher;


import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


@FetcherTest
public class DoiFetcherTest {
    private DoiFetcher fetcher;

    private BibEntry bibEntryBurd2011;

    private BibEntry bibEntryDecker2007;

    @Test
    public void testGetName() {
        Assertions.assertEquals("DOI", fetcher.getName());
    }

    @Test
    public void testGetHelpPage() {
        Assertions.assertEquals("DOItoBibTeX", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    public void testPerformSearchBurd2011() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.1002/9781118257517");
        Assertions.assertEquals(Optional.of(bibEntryBurd2011), fetchedEntry);
    }

    @Test
    public void testPerformSearchDecker2007() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.1109/ICWS.2007.59");
        Assertions.assertEquals(Optional.of(bibEntryDecker2007), fetchedEntry);
    }

    @Test
    public void testPerformSearchEmptyDOI() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById(""));
    }

    @Test
    public void testPerformSearchInvalidDOI() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("10.1002/9781118257517F"));
    }

    @Test
    public void testPerformSearchNonTrimmedDOI() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("http s://doi.org/ 10.1109 /ICWS .2007.59 ");
        Assertions.assertEquals(Optional.of(bibEntryDecker2007), fetchedEntry);
    }
}

