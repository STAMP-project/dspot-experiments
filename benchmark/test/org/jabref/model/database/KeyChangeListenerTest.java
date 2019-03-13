package org.jabref.model.database;


import BibEntry.KEY_FIELD;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class KeyChangeListenerTest {
    private BibDatabase db;

    private BibEntry entry1;

    private BibEntry entry2;

    private BibEntry entry3;

    private BibEntry entry4;

    @Test
    public void testCrossrefChanged() {
        Assertions.assertEquals(Optional.of("Entry4"), entry1.getField("crossref"));
        entry4.setCiteKey("Banana");
        Assertions.assertEquals(Optional.of("Banana"), entry1.getField("crossref"));
    }

    @Test
    public void testRelatedChanged() {
        Assertions.assertEquals(Optional.of("Entry1,Entry3"), entry2.getField("related"));
        entry1.setCiteKey("Banana");
        Assertions.assertEquals(Optional.of("Banana,Entry3"), entry2.getField("related"));
    }

    @Test
    public void testRelatedChangedInSameEntry() {
        Assertions.assertEquals(Optional.of("Entry1,Entry2,Entry3"), entry3.getField("related"));
        entry3.setCiteKey("Banana");
        Assertions.assertEquals(Optional.of("Entry1,Entry2,Banana"), entry3.getField("related"));
    }

    @Test
    public void testCrossrefRemoved() {
        entry4.clearField(KEY_FIELD);
        Assertions.assertEquals(Optional.empty(), entry1.getField("crossref"));
    }

    @Test
    public void testCrossrefEntryRemoved() {
        db.removeEntry(entry4);
        Assertions.assertEquals(Optional.empty(), entry1.getField("crossref"));
    }

    @Test
    public void testRelatedEntryRemoved() {
        db.removeEntry(entry1);
        Assertions.assertEquals(Optional.of("Entry3"), entry2.getField("related"));
    }

    @Test
    public void testRelatedAllEntriesRemoved() {
        db.removeEntry(entry1);
        db.removeEntry(entry3);
        Assertions.assertEquals(Optional.empty(), entry2.getField("related"));
    }
}

