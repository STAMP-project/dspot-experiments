package org.jabref.logic.importer.fetcher;


import BiblatexEntryTypes.BOOK;
import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class IsbnViaEbookDeFetcherTest extends AbstractIsbnFetcherTest {
    @Test
    @Override
    public void testName() {
        Assertions.assertEquals("ISBN (ebook.de)", fetcher.getName());
    }

    @Test
    @Override
    public void testHelpPage() {
        Assertions.assertEquals("ISBNtoBibTeX", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    @Override
    public void searchByIdSuccessfulWithShortISBN() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("0134685997");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    @Test
    @Override
    public void searchByIdSuccessfulWithLongISBN() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("9780134685991");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    @Test
    @Override
    public void authorsAreCorrectlyFormatted() throws Exception {
        BibEntry bibEntry = new BibEntry();
        bibEntry.setType(BOOK);
        bibEntry.setField("bibtexkey", "9783662565094");
        bibEntry.setField("title", "Fundamentals of Business Process Management");
        bibEntry.setField("publisher", "Springer Berlin Heidelberg");
        bibEntry.setField("year", "2018");
        bibEntry.setField("author", "Dumas, Marlon and Rosa, Marcello La and Mendling, Jan and Reijers, Hajo A.");
        bibEntry.setField("date", "2018-03-23");
        bibEntry.setField("ean", "9783662565094");
        bibEntry.setField("url", "https://www.ebook.de/de/product/33399253/marlon_dumas_marcello_la_rosa_jan_mendling_hajo_a_reijers_fundamentals_of_business_process_management.html");
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("978-3-662-56509-4");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    /**
     * This test searches for a valid ISBN. See https://www.amazon.de/dp/3728128155/?tag=jabref-21 However, this ISBN is
     * not available on ebook.de. The fetcher should return nothing rather than throwing an exeption.
     */
    @Test
    public void searchForValidButNotFoundISBN() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("3728128155");
        Assertions.assertEquals(Optional.empty(), fetchedEntry);
    }
}

