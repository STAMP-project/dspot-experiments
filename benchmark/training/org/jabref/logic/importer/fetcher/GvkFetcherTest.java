package org.jabref.logic.importer.fetcher;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.jabref.logic.importer.FetcherException;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
public class GvkFetcherTest {
    private GvkFetcher fetcher;

    private BibEntry bibEntryPPN591166003;

    private BibEntry bibEntryPPN66391437X;

    @Test
    public void testGetName() {
        Assertions.assertEquals("GVK", fetcher.getName());
    }

    @Test
    public void testGetHelpPage() {
        Assertions.assertEquals("GVK", fetcher.getHelpPage().get().getPageName());
    }

    @Test
    public void simpleSearchQueryStringCorrect() {
        String query = "java jdk";
        String result = fetcher.getSearchQueryString(query);
        Assertions.assertEquals("pica.all=java jdk", result);
    }

    @Test
    public void simpleSearchQueryURLCorrect() throws MalformedURLException, URISyntaxException, FetcherException {
        String query = "java jdk";
        URL url = fetcher.getURLForQuery(query);
        Assertions.assertEquals("http://sru.gbv.de/gvk?version=1.1&operation=searchRetrieve&query=pica.all%3Djava+jdk&maximumRecords=50&recordSchema=picaxml&sortKeys=Year%2C%2C1", url.toString());
    }

    @Test
    public void complexSearchQueryStringCorrect() {
        String query = "kon java tit jdk";
        String result = fetcher.getSearchQueryString(query);
        Assertions.assertEquals("pica.kon=java and pica.tit=jdk", result);
    }

    @Test
    public void complexSearchQueryURLCorrect() throws MalformedURLException, URISyntaxException, FetcherException {
        String query = "kon java tit jdk";
        URL url = fetcher.getURLForQuery(query);
        Assertions.assertEquals("http://sru.gbv.de/gvk?version=1.1&operation=searchRetrieve&query=pica.kon%3Djava+and+pica.tit%3Djdk&maximumRecords=50&recordSchema=picaxml&sortKeys=Year%2C%2C1", url.toString());
    }

    @Test
    public void testPerformSearchMatchingMultipleEntries() throws FetcherException {
        List<BibEntry> searchResult = fetcher.performSearch("tit effective java");
        Assertions.assertTrue(searchResult.contains(bibEntryPPN591166003));
        Assertions.assertTrue(searchResult.contains(bibEntryPPN66391437X));
    }

    @Test
    public void testPerformSearch591166003() throws FetcherException {
        List<BibEntry> searchResult = fetcher.performSearch("ppn 591166003");
        Assertions.assertEquals(Collections.singletonList(bibEntryPPN591166003), searchResult);
    }

    @Test
    public void testPerformSearch66391437X() throws FetcherException {
        List<BibEntry> searchResult = fetcher.performSearch("ppn 66391437X");
        Assertions.assertEquals(Collections.singletonList(bibEntryPPN66391437X), searchResult);
    }

    @Test
    public void testPerformSearchEmpty() throws FetcherException {
        List<BibEntry> searchResult = fetcher.performSearch("");
        Assertions.assertEquals(Collections.emptyList(), searchResult);
    }
}

