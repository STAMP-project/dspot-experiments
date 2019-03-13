package org.jabref.logic.bibtex.comparator;


import FieldName.CROSSREF;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CrossRefEntryComparatorTest {
    private CrossRefEntryComparator comparator;

    @Test
    public void isEqualForEntriesWithoutCrossRef() {
        BibEntry e1 = new BibEntry();
        BibEntry e2 = new BibEntry();
        Assertions.assertEquals(0, comparator.compare(e1, e2));
    }

    @Test
    public void isEqualForEntriesWithCrossRef() {
        BibEntry e1 = new BibEntry();
        e1.setField(CROSSREF, "1");
        BibEntry e2 = new BibEntry();
        e2.setField(CROSSREF, "2");
        Assertions.assertEquals(0, comparator.compare(e1, e2));
    }

    @Test
    public void isGreaterForEntriesWithoutCrossRef() {
        BibEntry e1 = new BibEntry();
        BibEntry e2 = new BibEntry();
        e2.setField(CROSSREF, "1");
        Assertions.assertEquals(1, comparator.compare(e1, e2));
    }

    @Test
    public void isSmallerForEntriesWithCrossRef() {
        BibEntry e1 = new BibEntry();
        e1.setField(CROSSREF, "1");
        BibEntry e2 = new BibEntry();
        Assertions.assertEquals((-1), comparator.compare(e1, e2));
    }
}

