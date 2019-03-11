package org.jabref.logic.search;


import BibtexEntryTypes.INCOLLECTION;
import java.util.Collections;
import java.util.List;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DatabaseSearcherTest {
    public static final SearchQuery INVALID_SEARCH_QUERY = new SearchQuery("\\asd123{}asdf", true, true);

    private BibDatabase database;

    @Test
    public void testNoMatchesFromEmptyDatabase() {
        List<BibEntry> matches = getMatches();
        Assertions.assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidSearchExpression() {
        List<BibEntry> matches = getMatches();
        Assertions.assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testGetDatabaseFromMatchesDatabaseWithEmptyEntries() {
        database.insertEntry(new BibEntry());
        List<BibEntry> matches = getMatches();
        Assertions.assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testNoMatchesFromDatabaseWithArticleTypeEntry() {
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "harrer");
        database.insertEntry(entry);
        List<BibEntry> matches = getMatches();
        Assertions.assertEquals(Collections.emptyList(), matches);
    }

    @Test
    public void testCorrectMatchFromDatabaseWithArticleTypeEntry() {
        BibEntry entry = new BibEntry();
        entry.setType("article");
        entry.setField("author", "harrer");
        database.insertEntry(entry);
        List<BibEntry> matches = getMatches();
        Assertions.assertEquals(Collections.singletonList(entry), matches);
    }

    @Test
    public void testNoMatchesFromEmptyDatabaseWithInvalidQuery() {
        SearchQuery query = new SearchQuery("asdf[", true, true);
        DatabaseSearcher databaseSearcher = new DatabaseSearcher(query, database);
        Assertions.assertEquals(Collections.emptyList(), databaseSearcher.getMatches());
    }

    @Test
    public void testCorrectMatchFromDatabaseWithIncollectionTypeEntry() {
        BibEntry entry = new BibEntry();
        entry.setType(INCOLLECTION);
        entry.setField("author", "tonho");
        database.insertEntry(entry);
        SearchQuery query = new SearchQuery("tonho", true, true);
        List<BibEntry> matches = getMatches();
        Assertions.assertEquals(Collections.singletonList(entry), matches);
    }

    @Test
    public void testNoMatchesFromDatabaseWithTwoEntries() {
        BibEntry entry = new BibEntry();
        database.insertEntry(entry);
        entry = new BibEntry();
        entry.setType(INCOLLECTION);
        entry.setField("author", "tonho");
        database.insertEntry(entry);
        SearchQuery query = new SearchQuery("tonho", true, true);
        DatabaseSearcher databaseSearcher = new DatabaseSearcher(query, database);
        Assertions.assertEquals(Collections.singletonList(entry), databaseSearcher.getMatches());
    }

    @Test
    public void testNoMatchesFromDabaseWithIncollectionTypeEntry() {
        BibEntry entry = new BibEntry();
        entry.setType(INCOLLECTION);
        entry.setField("author", "tonho");
        database.insertEntry(entry);
        SearchQuery query = new SearchQuery("asdf", true, true);
        DatabaseSearcher databaseSearcher = new DatabaseSearcher(query, database);
        Assertions.assertEquals(Collections.emptyList(), databaseSearcher.getMatches());
    }

    @Test
    public void testNoMatchFromDatabaseWithEmptyEntry() {
        BibEntry entry = new BibEntry();
        database.insertEntry(entry);
        SearchQuery query = new SearchQuery("tonho", true, true);
        DatabaseSearcher databaseSearcher = new DatabaseSearcher(query, database);
        Assertions.assertEquals(Collections.emptyList(), databaseSearcher.getMatches());
    }
}

