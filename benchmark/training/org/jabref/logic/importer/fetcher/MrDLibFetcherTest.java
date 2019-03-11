package org.jabref.logic.importer.fetcher;


import FieldName.ADDRESS;
import FieldName.AUTHOR;
import FieldName.BOOKTITLE;
import FieldName.DOI;
import FieldName.ISBN;
import FieldName.KEYWORDS;
import FieldName.PAGES;
import FieldName.PUBLISHER;
import FieldName.SERIES;
import FieldName.TITLE;
import FieldName.URL;
import FieldName.YEAR;
import java.util.List;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class MrDLibFetcherTest {
    private MrDLibFetcher fetcher;

    @Test
    public void testPerformSearch() throws FetcherException {
        BibEntry bibEntry = new BibEntry();
        bibEntry.setField(TITLE, "lernen");
        List<BibEntry> bibEntrys = fetcher.performSearch(bibEntry);
        Assertions.assertFalse(bibEntrys.isEmpty());
    }

    @Test
    public void testPerformSearchForHornecker2006() throws FetcherException {
        BibEntry bibEntry = new BibEntry();
        bibEntry.setCiteKey("Hornecker:2006:GGT:1124772.1124838");
        bibEntry.setField(ADDRESS, "New York, NY, USA");
        bibEntry.setField(AUTHOR, "Hornecker, Eva and Buur, Jacob");
        bibEntry.setField(BOOKTITLE, "Proceedings of the SIGCHI Conference on Human Factors in Computing Systems");
        bibEntry.setField(DOI, "10.1145/1124772.1124838");
        bibEntry.setField(ISBN, "1-59593-372-7");
        bibEntry.setField(KEYWORDS, "CSCW,analysis,collaboration,design,framework,social interaction,tangible interaction,tangible interface");
        bibEntry.setField(PAGES, "437--446");
        bibEntry.setField(PUBLISHER, "ACM");
        bibEntry.setField(SERIES, "CHI '06");
        bibEntry.setField(TITLE, "{Getting a Grip on Tangible Interaction: A Framework on Physical Space and Social Interaction}");
        bibEntry.setField(URL, "http://doi.acm.org/10.1145/1124772.1124838");
        bibEntry.setField(YEAR, "2006");
        List<BibEntry> bibEntrys = fetcher.performSearch(bibEntry);
        Assertions.assertFalse(bibEntrys.isEmpty());
    }

    @Test
    public void testGetName() {
        Assertions.assertEquals("MDL_FETCHER", fetcher.getName());
    }
}

