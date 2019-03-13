package org.jabref.logic.importer.fetcher;


import FieldName.ABSTRACT;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class MedlineFetcherTest {
    private MedlineFetcher fetcher;

    private BibEntry entryWijedasa;

    private BibEntry entryEndharti;

    private BibEntry bibEntryIchikawa;

    private BibEntry bibEntrySari;

    @Test
    public void testGetName() {
        Assertions.assertEquals("Medline/PubMed", fetcher.getName());
    }

    @Test
    public void testGetHelpPage() {
        Assertions.assertEquals("Medline", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    public void testSearchByIDWijedasa() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("27670948");
        fetchedEntry.get().clearField(ABSTRACT);// Remove abstract due to copyright

        Assertions.assertEquals(Optional.of(entryWijedasa), fetchedEntry);
    }

    @Test
    public void testSearchByIDEndharti() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("27670445");
        fetchedEntry.get().clearField(ABSTRACT);// Remove abstract due to copyright

        Assertions.assertEquals(Optional.of(entryEndharti), fetchedEntry);
    }

    @Test
    public void testSearchByIDIchikawa() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("26197440");
        fetchedEntry.get().clearField(ABSTRACT);// Remove abstract due to copyright

        Assertions.assertEquals(Optional.of(bibEntryIchikawa), fetchedEntry);
    }

    @Test
    public void testSearchByIDSari() throws Exception {
        Optional<BibEntry> fetchedEntry = fetcher.performSearchById("26867355");
        fetchedEntry.get().clearField(ABSTRACT);// Remove abstract due to copyright

        Assertions.assertEquals(Optional.of(bibEntrySari), fetchedEntry);
    }

    @Test
    public void testMultipleEntries() throws Exception {
        List<BibEntry> entryList = fetcher.performSearch("java");
        entryList.forEach(( entry) -> entry.clearField(FieldName.ABSTRACT));// Remove abstract due to copyright);

        Assertions.assertEquals(50, entryList.size());
        Assertions.assertTrue(entryList.contains(bibEntryIchikawa));
        Assertions.assertTrue(entryList.contains(bibEntrySari));
    }

    @Test
    public void testInvalidSearchTerm() throws Exception {
        Assertions.assertEquals(Optional.empty(), fetcher.performSearchById("this.is.a.invalid.search.term.for.the.medline.fetcher"));
    }

    @Test
    public void testEmptyEntryList() throws Exception {
        List<BibEntry> entryList = fetcher.performSearch("java is fantastic and awesome ");
        Assertions.assertEquals(Collections.emptyList(), entryList);
    }

    @Test
    public void testEmptyInput() throws Exception {
        Assertions.assertEquals(Collections.emptyList(), fetcher.performSearch(""));
    }
}

