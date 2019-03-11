package org.jabref.model.database;


import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DuplicationCheckerTest {
    private BibDatabase database;

    @Test
    public void addEntry() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("AAA");
        database.insertEntry(entry);
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
    }

    @Test
    public void addAndRemoveEntry() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("AAA");
        database.insertEntry(entry);
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        database.removeEntry(entry);
        Assertions.assertEquals(0, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
    }

    @Test
    public void changeCiteKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("AAA");
        database.insertEntry(entry);
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        entry.setCiteKey("BBB");
        Assertions.assertEquals(0, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("BBB"));
    }

    @Test
    public void setCiteKeySameKeyDifferentEntries() {
        BibEntry entry0 = new BibEntry();
        entry0.setCiteKey("AAA");
        database.insertEntry(entry0);
        BibEntry entry1 = new BibEntry();
        entry1.setCiteKey("BBB");
        database.insertEntry(entry1);
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("BBB"));
        entry1.setCiteKey("AAA");
        Assertions.assertEquals(2, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        Assertions.assertEquals(0, database.getDuplicationChecker().getNumberOfKeyOccurrences("BBB"));
    }

    @Test
    public void removeMultipleCiteKeys() {
        BibEntry entry0 = new BibEntry();
        entry0.setCiteKey("AAA");
        database.insertEntry(entry0);
        BibEntry entry1 = new BibEntry();
        entry1.setCiteKey("AAA");
        database.insertEntry(entry1);
        BibEntry entry2 = new BibEntry();
        entry2.setCiteKey("AAA");
        database.insertEntry(entry2);
        Assertions.assertEquals(3, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        database.removeEntry(entry2);
        Assertions.assertEquals(2, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        database.removeEntry(entry1);
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        database.removeEntry(entry0);
        Assertions.assertEquals(0, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
    }

    @Test
    public void addEmptyCiteKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("");
        database.insertEntry(entry);
        Assertions.assertEquals(0, database.getDuplicationChecker().getNumberOfKeyOccurrences(""));
    }

    @Test
    public void removeEmptyCiteKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("AAA");
        database.insertEntry(entry);
        Assertions.assertEquals(1, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
        entry.setCiteKey("");
        database.removeEntry(entry);
        Assertions.assertEquals(0, database.getDuplicationChecker().getNumberOfKeyOccurrences("AAA"));
    }
}

