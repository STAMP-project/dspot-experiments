package org.jabref.logic.importer.fetcher;


import java.util.Collections;
import org.jabref.testutils.category.FetcherTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@FetcherTest
class DOAJFetcherTest {
    DOAJFetcher fetcher;

    @Test
    public void searchByEmptyQuery() throws Exception {
        Assertions.assertEquals(Collections.emptyList(), fetcher.performSearch(""));
    }
}

