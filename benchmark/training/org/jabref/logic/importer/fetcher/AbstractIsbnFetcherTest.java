package org.jabref.logic.importer.fetcher;


import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


@FetcherTest
public abstract class AbstractIsbnFetcherTest {
    protected AbstractIsbnFetcher fetcher;

    protected BibEntry bibEntry;

    @Test
    public void searchByIdSuccessfulWithLongISBN() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("978-0321356680");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    @Test
    public void searchByIdReturnsEmptyWithEmptyISBN() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("");
        Assertions.assertEquals(Optional.empty(), fetchedEntry);
    }

    @Test
    public void searchByIdThrowsExceptionForShortInvalidISBN() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("123456789"));
    }

    @Test
    public void searchByIdThrowsExceptionForLongInvalidISB() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("012345678910"));
    }

    @Test
    public void searchByIdThrowsExceptionForInvalidISBN() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("jabref-4-ever"));
    }
}

