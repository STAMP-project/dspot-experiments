package org.jabref.logic.bibtex.comparator;


import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexEntryTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FieldComparatorTest {
    @Test
    public void compareMonthFieldIdentity() throws Exception {
        FieldComparator comparator = new FieldComparator("month");
        BibEntry equal = new BibEntry();
        equal.setField("month", "1");
        Assertions.assertEquals(0, comparator.compare(equal, equal));
    }

    @Test
    public void compareMonthFieldEquality() throws Exception {
        FieldComparator comparator = new FieldComparator("month");
        BibEntry equal = new BibEntry();
        equal.setField("month", "1");
        BibEntry equal2 = new BibEntry();
        equal2.setField("month", "1");
        Assertions.assertEquals(0, comparator.compare(equal, equal2));
    }

    @Test
    public void compareMonthFieldBiggerAscending() throws Exception {
        FieldComparator comparator = new FieldComparator("month");
        BibEntry smaller = new BibEntry();
        smaller.setField("month", "jan");
        BibEntry bigger = new BibEntry();
        bigger.setField("month", "feb");
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareMonthFieldBiggerDescending() throws Exception {
        FieldComparator comparator = new FieldComparator("month", true);
        BibEntry smaller = new BibEntry();
        smaller.setField("month", "feb");
        BibEntry bigger = new BibEntry();
        bigger.setField("month", "jan");
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareYearFieldIdentity() throws Exception {
        FieldComparator comparator = new FieldComparator("year");
        BibEntry equal = new BibEntry();
        equal.setField("year", "2016");
        Assertions.assertEquals(0, comparator.compare(equal, equal));
    }

    @Test
    public void compareYearFieldEquality() throws Exception {
        FieldComparator comparator = new FieldComparator("year");
        BibEntry equal = new BibEntry();
        equal.setField("year", "2016");
        BibEntry equal2 = new BibEntry();
        equal2.setField("year", "2016");
        Assertions.assertEquals(0, comparator.compare(equal, equal2));
    }

    @Test
    public void compareYearFieldBiggerAscending() throws Exception {
        FieldComparator comparator = new FieldComparator("year");
        BibEntry smaller = new BibEntry();
        smaller.setField("year", "2000");
        BibEntry bigger = new BibEntry();
        bigger.setField("year", "2016");
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareYearFieldBiggerDescending() throws Exception {
        FieldComparator comparator = new FieldComparator("year", true);
        BibEntry smaller = new BibEntry();
        smaller.setField("year", "2016");
        BibEntry bigger = new BibEntry();
        bigger.setField("year", "2000");
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareTypeFieldIdentity() throws Exception {
        FieldComparator comparator = new FieldComparator("entrytype");
        BibEntry equal = new BibEntry(BibtexEntryTypes.ARTICLE);
        Assertions.assertEquals(0, comparator.compare(equal, equal));
    }

    @Test
    public void compareTypeFieldEquality() throws Exception {
        FieldComparator comparator = new FieldComparator("entrytype");
        BibEntry equal = new BibEntry(BibtexEntryTypes.ARTICLE);
        equal.setId("1");
        BibEntry equal2 = new BibEntry(BibtexEntryTypes.ARTICLE);
        equal2.setId("1");
        Assertions.assertEquals(0, comparator.compare(equal, equal2));
    }

    @Test
    public void compareTypeFieldBiggerAscending() throws Exception {
        FieldComparator comparator = new FieldComparator("entrytype");
        BibEntry smaller = new BibEntry(BibtexEntryTypes.ARTICLE);
        BibEntry bigger = new BibEntry(BibtexEntryTypes.BOOK);
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareTypeFieldBiggerDescending() throws Exception {
        FieldComparator comparator = new FieldComparator("entrytype", true);
        BibEntry bigger = new BibEntry(BibtexEntryTypes.ARTICLE);
        BibEntry smaller = new BibEntry(BibtexEntryTypes.BOOK);
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareStringFieldsIdentity() throws Exception {
        FieldComparator comparator = new FieldComparator("title");
        BibEntry equal = new BibEntry();
        equal.setField("title", "title");
        Assertions.assertEquals(0, comparator.compare(equal, equal));
    }

    @Test
    public void compareStringFieldsEquality() throws Exception {
        FieldComparator comparator = new FieldComparator("title");
        BibEntry equal = new BibEntry();
        equal.setField("title", "title");
        BibEntry equal2 = new BibEntry();
        equal2.setField("title", "title");
        Assertions.assertEquals(0, comparator.compare(equal, equal2));
    }

    @Test
    public void compareStringFieldsBiggerAscending() throws Exception {
        FieldComparator comparator = new FieldComparator("title");
        BibEntry bigger = new BibEntry();
        bigger.setField("title", "b");
        BibEntry smaller = new BibEntry();
        smaller.setField("title", "a");
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void compareStringFieldsBiggerDescending() throws Exception {
        FieldComparator comparator = new FieldComparator("title", true);
        BibEntry bigger = new BibEntry();
        bigger.setField("title", "a");
        BibEntry smaller = new BibEntry();
        smaller.setField("title", "b");
        Assertions.assertEquals(1, comparator.compare(bigger, smaller));
    }

    @Test
    public void nameOfComparisonField() throws Exception {
        FieldComparator comparator = new FieldComparator("title");
        Assertions.assertEquals("title", comparator.getFieldName());
    }

    @Test
    public void nameOfComparisonFieldAlias() throws Exception {
        FieldComparator comparator = new FieldComparator("author/editor");
        Assertions.assertEquals("author/editor", comparator.getFieldName());
    }
}

