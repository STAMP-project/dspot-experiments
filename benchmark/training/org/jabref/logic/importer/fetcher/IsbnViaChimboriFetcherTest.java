package org.jabref.logic.importer.fetcher;


import BiblatexEntryTypes.BOOK;
import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class IsbnViaChimboriFetcherTest extends AbstractIsbnFetcherTest {
    @Test
    @Override
    public void testName() {
        Assertions.assertEquals("ISBN (Chimbori/Amazon)", fetcher.getName());
    }

    @Test
    @Override
    public void testHelpPage() {
        Assertions.assertEquals("ISBNtoBibTeX", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    @Override
    public void searchByIdSuccessfulWithShortISBN() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("0321356683");
        bibEntry.setField("bibtexkey", "0321356683");
        bibEntry.setField("isbn", "0321356683");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    @Test
    @Override
    public void searchByIdSuccessfulWithLongISBN() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("9780321356680");
        bibEntry.setField("bibtexkey", "9780321356680");
        bibEntry.setField("isbn", "9780321356680");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    @Test
    @Override
    public void authorsAreCorrectlyFormatted() throws Exception {
        BibEntry bibEntry = new BibEntry();
        bibEntry.setType(BOOK);
        bibEntry.setField("bibtexkey", "3642434738");
        bibEntry.setField("title", "Fundamentals of Business Process Management");
        bibEntry.setField("publisher", "Springer");
        bibEntry.setField("year", "2015");
        bibEntry.setField("author", "Marlon Dumas and Marcello La Rosa and Jan Mendling and Hajo A. Reijers");
        bibEntry.setField("isbn", "3642434738");
        bibEntry.setField("url", "https://www.amazon.com/Fundamentals-Business-Process-Management-Marlon/dp/3642434738?SubscriptionId=AKIAIOBINVZYXZQZ2U3A&tag=chimbori05-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=3642434738");
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("3642434738");
        Assertions.assertEquals(Optional.of(bibEntry), fetchedEntry);
    }

    @Test
    public void searchForIsbnAvailableAtChimboriButNonOnEbookDe() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("3728128155");
        Assertions.assertNotEquals(Optional.empty(), fetchedEntry);
    }
}

