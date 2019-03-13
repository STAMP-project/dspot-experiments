package org.jabref.logic.importer.fetcher;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class AstrophysicsDataSystemTest {
    private AstrophysicsDataSystem fetcher;

    private BibEntry diezSliceTheoremEntry;

    private BibEntry famaeyMcGaughEntry;

    private BibEntry sunWelchEntry;

    private BibEntry xiongSunEntry;

    private BibEntry ingersollPollardEntry;

    private BibEntry luceyPaulEntry;

    @Test
    public void testHelpPage() {
        Assertions.assertEquals("ADS", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    public void testGetName() {
        Assertions.assertEquals("SAO/NASA Astrophysics Data System", fetcher.getName());
    }

    @Test
    public void searchByQueryFindsEntry() throws Exception {
        List<BibEntry> fetchedEntries = fetcher.performSearch("Diez slice theorem Lie");
        Assertions.assertEquals(Collections.singletonList(diezSliceTheoremEntry), fetchedEntries);
    }

    @Test
    public void searchByEntryFindsEntry() throws Exception {
        BibEntry searchEntry = new BibEntry();
        searchEntry.setField("title", "slice theorem");
        searchEntry.setField("author", "Diez");
        List<BibEntry> fetchedEntries = fetcher.performSearch(searchEntry);
        Assertions.assertFalse(fetchedEntries.isEmpty());
        Assertions.assertEquals(diezSliceTheoremEntry, fetchedEntries.get(0));
    }

    @Test
    public void testPerformSearchByFamaeyMcGaughEntry() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.12942/lrr-2012-10");
        fetchedEntry.ifPresent(( entry) -> entry.clearField(FieldName.ABSTRACT));// Remove abstract due to copyright

        Assertions.assertEquals(Optional.of(famaeyMcGaughEntry), fetchedEntry);
    }

    @Test
    public void testPerformSearchByIdEmptyDOI() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("");
        Assertions.assertEquals(Optional.empty(), fetchedEntry);
    }

    @Test
    public void testPerformSearchByIdInvalidDoi() throws Exception {
        Assertions.assertEquals(Optional.empty(), fetcher.performSearchById("this.doi.will.fail"));
    }

    @Test
    public void testPerformSearchBySunWelchEntry() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.1038/nmat3160");
        fetchedEntry.ifPresent(( entry) -> entry.clearField(FieldName.ABSTRACT));// Remove abstract due to copyright

        Assertions.assertEquals(Optional.of(sunWelchEntry), fetchedEntry);
    }

    @Test
    public void testPerformSearchByXiongSunEntry() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.1109/TGRS.2006.890567");
        Assertions.assertEquals(Optional.of(xiongSunEntry), fetchedEntry);
    }

    @Test
    public void testPerformSearchByIngersollPollardEntry() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.1016/0019-1035(82)90169-5");
        Assertions.assertEquals(Optional.of(ingersollPollardEntry), fetchedEntry);
    }

    @Test
    public void testPerformSearchByLuceyPaulEntry() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("10.1029/1999JE001117");
        Assertions.assertEquals(Optional.of(luceyPaulEntry), fetchedEntry);
    }
}

