package org.jabref.logic.importer.fetcher;


import FieldName.ABSTRACT;
import FieldName.DATE;
import IacrEprintFetcher.NAME;
import java.util.Optional;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


@FetcherTest
public class IacrEprintFetcherTest {
    private IacrEprintFetcher fetcher;

    private BibEntry abram2017;

    private BibEntry beierle2016;

    private BibEntry delgado2017;

    @Test
    public void searchByIdWithValidId1() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("Report 2017/1118 ");
        Assertions.assertFalse(fetchedEntry.get().getField(ABSTRACT).get().isEmpty());
        fetchedEntry.get().setField(ABSTRACT, "dummy");
        Assertions.assertEquals(Optional.of(abram2017), fetchedEntry);
    }

    @Test
    public void searchByIdWithValidId2() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("iacr ePrint 2016/119");
        Assertions.assertFalse(fetchedEntry.get().getField(ABSTRACT).get().isEmpty());
        fetchedEntry.get().setField(ABSTRACT, "dummy");
        Assertions.assertEquals(Optional.of(beierle2016), fetchedEntry);
    }

    @Test
    public void searchByIdWithValidIdAndNonAsciiChars() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("some random 2017/1095 stuff around the id");
        Assertions.assertFalse(fetchedEntry.get().getField(ABSTRACT).get().isEmpty());
        fetchedEntry.get().setField(ABSTRACT, "dummy");
        Assertions.assertEquals(Optional.of(delgado2017), fetchedEntry);
    }

    @Test
    public void searchByIdWithEmptyIdFails() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById(""));
    }

    @Test
    public void searchByIdWithInvalidReportNumberFails() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("2016/1"));
    }

    @Test
    public void searchByIdWithInvalidYearFails() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("16/115"));
    }

    @Test
    public void searchByIdWithInvalidIdFails() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("asdf"));
    }

    @Test
    public void searchForNonexistentIdFails() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("2016/6425"));
    }

    @Test
    public void testGetName() {
        Assertions.assertEquals(NAME, fetcher.getName());
    }

    @Test
    public void searchByIdForWithdrawnPaperFails() {
        Assertions.assertThrows(FetcherException.class, () -> fetcher.performSearchById("1998/016"));
    }

    @Test
    public void searchByIdWithOldHtmlFormatAndCheckDate() throws FetcherException {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("1997/006");
        Assertions.assertEquals(Optional.of("1997-05-04"), fetchedEntry.get().getField(DATE));
    }
}

