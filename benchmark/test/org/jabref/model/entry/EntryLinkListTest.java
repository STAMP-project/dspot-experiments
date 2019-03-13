package org.jabref.model.entry;


import FieldName.CROSSREF;
import java.util.List;
import java.util.Optional;
import org.jabref.model.database.BibDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EntryLinkListTest {
    private static final String KEY = "test";

    private BibDatabase database;

    private List<ParsedEntryLink> links;

    private ParsedEntryLink link;

    private BibEntry source;

    private BibEntry target;

    @Test
    public void givenFieldValueAndDatabaseWhenParsingThenExpectKey() {
        Assertions.assertEquals(EntryLinkListTest.KEY, link.getKey());
    }

    @Test
    public void givenFieldValueAndDatabaseWhenParsingThenExpectDataBase() {
        Assertions.assertEquals(database, link.getDataBase());
    }

    @Test
    public void givenFieldValueAndDatabaseWhenParsingThenExpectEmptyLinkedEntry() {
        Assertions.assertEquals(Optional.empty(), link.getLinkedEntry());
    }

    @Test
    public void givenFieldValueAndDatabaseWhenParsingThenExpectLink() {
        ParsedEntryLink expected = new ParsedEntryLink(EntryLinkListTest.KEY, database);
        Assertions.assertEquals(expected, link);
    }

    @Test
    public void givenNullFieldValueAndDatabaseWhenParsingThenExpectLinksIsEmpty() {
        links = EntryLinkList.parse(null, database);
        Assertions.assertTrue(links.isEmpty());
    }

    @Test
    public void givenTargetAndSourceWhenSourceCrossrefTargetThenSourceCrossrefsTarget() {
        source.setField(CROSSREF, "target");
        assertSourceCrossrefsTarget(target, source);
    }
}

