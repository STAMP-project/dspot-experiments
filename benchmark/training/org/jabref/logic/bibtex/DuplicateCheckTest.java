package org.jabref.logic.bibtex;


import BibDatabaseMode.BIBTEX;
import BibtexEntryTypes.ARTICLE;
import BibtexEntryTypes.BOOK;
import BibtexEntryTypes.INCOLLECTION;
import FieldName.ADDRESS;
import FieldName.AUTHOR;
import FieldName.CHAPTER;
import FieldName.DATE;
import FieldName.DOI;
import FieldName.EDITION;
import FieldName.EPRINT;
import FieldName.JOURNAL;
import FieldName.NUMBER;
import FieldName.PAGES;
import FieldName.PMID;
import FieldName.PUBLISHER;
import FieldName.TITLE;
import FieldName.URL;
import FieldName.VOLUME;
import FieldName.YEAR;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexEntryTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DuplicateCheckTest {
    private BibEntry simpleArticle;

    private BibEntry unrelatedArticle;

    private BibEntry simpleInbook;

    private BibEntry simpleIncollection;

    @Test
    public void testDuplicateDetection() {
        BibEntry one = new BibEntry(BibtexEntryTypes.ARTICLE);
        BibEntry two = new BibEntry(BibtexEntryTypes.ARTICLE);
        one.setField("author", "Billy Bob");
        two.setField("author", "Billy Bob");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setField("author", "James Joyce");
        Assertions.assertFalse(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setField("author", "Billy Bob");
        two.setType(BOOK);
        Assertions.assertFalse(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setType(ARTICLE);
        one.setField("year", "2005");
        two.setField("year", "2005");
        one.setField("title", "A title");
        two.setField("title", "A title");
        one.setField("journal", "A");
        two.setField("journal", "A");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        Assertions.assertEquals(1.01, DuplicateCheck.compareEntriesStrictly(one, two), 0.01);
        two.setField("journal", "B");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        Assertions.assertEquals(0.75, DuplicateCheck.compareEntriesStrictly(one, two), 0.01);
        two.setField("journal", "A");
        one.setField("number", "1");
        two.setField("volume", "21");
        one.setField("pages", "334--337");
        two.setField("pages", "334--337");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setField("number", "1");
        one.setField("volume", "21");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setField("volume", "22");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setField("journal", "B");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        one.setField("journal", "");
        two.setField("journal", "");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(one, two, BIBTEX));
        two.setField("title", "Another title");
        Assertions.assertFalse(DuplicateCheck.isDuplicate(one, two, BIBTEX));
    }

    @Test
    public void testWordCorrelation() {
        String d1 = "Characterization of Calanus finmarchicus habitat in the North Sea";
        String d2 = "Characterization of Calunus finmarchicus habitat in the North Sea";
        String d3 = "Characterization of Calanus glacialissss habitat in the South Sea";
        Assertions.assertEquals(1.0, DuplicateCheck.correlateByWords(d1, d2), 0.01);
        Assertions.assertEquals(0.78, DuplicateCheck.correlateByWords(d1, d3), 0.01);
        Assertions.assertEquals(0.78, DuplicateCheck.correlateByWords(d2, d3), 0.01);
    }

    @Test
    public void twoUnrelatedEntriesAreNoDuplicates() {
        Assertions.assertFalse(DuplicateCheck.isDuplicate(simpleArticle, unrelatedArticle, BIBTEX));
    }

    @Test
    public void twoUnrelatedEntriesWithDifferentDoisAreNoDuplicates() {
        simpleArticle.setField(DOI, "10.1016/j.is.2004.02.002");
        unrelatedArticle.setField(DOI, "10.1016/j.is.2004.02.00X");
        Assertions.assertFalse(DuplicateCheck.isDuplicate(simpleArticle, unrelatedArticle, BIBTEX));
    }

    @Test
    public void twoUnrelatedEntriesWithEqualDoisAreDuplicates() {
        simpleArticle.setField(DOI, "10.1016/j.is.2004.02.002");
        unrelatedArticle.setField(DOI, "10.1016/j.is.2004.02.002");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(simpleArticle, unrelatedArticle, BIBTEX));
    }

    @Test
    public void twoUnrelatedEntriesWithEqualPmidAreDuplicates() {
        simpleArticle.setField(PMID, "12345678");
        unrelatedArticle.setField(PMID, "12345678");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(simpleArticle, unrelatedArticle, BIBTEX));
    }

    @Test
    public void twoUnrelatedEntriesWithEqualEprintAreDuplicates() {
        simpleArticle.setField(EPRINT, "12345678");
        unrelatedArticle.setField(EPRINT, "12345678");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(simpleArticle, unrelatedArticle, BIBTEX));
    }

    @Test
    public void twoEntriesWithSameDoiButDifferentTypesAreDuplicates() {
        simpleArticle.setField(DOI, "10.1016/j.is.2004.02.002");
        BibEntry duplicateWithDifferentType = ((BibEntry) (simpleArticle.clone()));
        duplicateWithDifferentType.setType(INCOLLECTION);
        Assertions.assertTrue(DuplicateCheck.isDuplicate(simpleArticle, duplicateWithDifferentType, BIBTEX));
    }

    @Test
    public void twoInbooksWithDifferentChaptersAreNotDuplicates() {
        twoEntriesWithDifferentSpecificFieldsAreNotDuplicates(simpleInbook, CHAPTER, "Chapter One ? Down the Rabbit Hole", "Chapter Two ? The Pool of Tears");
    }

    @Test
    public void twoInbooksWithDifferentPagesAreNotDuplicates() {
        twoEntriesWithDifferentSpecificFieldsAreNotDuplicates(simpleInbook, PAGES, "1-20", "21-40");
    }

    @Test
    public void twoIncollectionsWithDifferentChaptersAreNotDuplicates() {
        twoEntriesWithDifferentSpecificFieldsAreNotDuplicates(simpleIncollection, CHAPTER, "10", "9");
    }

    @Test
    public void twoIncollectionsWithDifferentPagesAreNotDuplicates() {
        twoEntriesWithDifferentSpecificFieldsAreNotDuplicates(simpleIncollection, PAGES, "1-20", "21-40");
    }

    @Test
    public void inbookWithoutChapterCouldBeDuplicateOfInbookWithChapter() {
        final BibEntry inbook1 = ((BibEntry) (simpleInbook.clone()));
        final BibEntry inbook2 = ((BibEntry) (simpleInbook.clone()));
        inbook2.setField(CHAPTER, "");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(inbook1, inbook2, BIBTEX));
        Assertions.assertTrue(DuplicateCheck.isDuplicate(inbook2, inbook1, BIBTEX));
    }

    @Test
    public void twoBooksWithDifferentEditionsAreNotDuplicates() {
        BibEntry editionOne = new BibEntry(BibtexEntryTypes.BOOK);
        editionOne.setField(TITLE, "Effective Java");
        editionOne.setField(AUTHOR, "Bloch, Joshua");
        editionOne.setField(PUBLISHER, "Prentice Hall");
        editionOne.setField(DATE, "2001");
        editionOne.setField(EDITION, "1");
        BibEntry editionTwo = new BibEntry(BibtexEntryTypes.BOOK);
        editionTwo.setField(TITLE, "Effective Java");
        editionTwo.setField(AUTHOR, "Bloch, Joshua");
        editionTwo.setField(PUBLISHER, "Prentice Hall");
        editionTwo.setField(DATE, "2008");
        editionTwo.setField(EDITION, "2");
        Assertions.assertFalse(DuplicateCheck.isDuplicate(editionOne, editionTwo, BIBTEX));
    }

    @Test
    public void sameBooksWithMissingEditionAreDuplicates() {
        BibEntry editionOne = new BibEntry(BibtexEntryTypes.BOOK);
        editionOne.setField(TITLE, "Effective Java");
        editionOne.setField(AUTHOR, "Bloch, Joshua");
        editionOne.setField(PUBLISHER, "Prentice Hall");
        editionOne.setField(DATE, "2001");
        BibEntry editionTwo = new BibEntry(BibtexEntryTypes.BOOK);
        editionTwo.setField(TITLE, "Effective Java");
        editionTwo.setField(AUTHOR, "Bloch, Joshua");
        editionTwo.setField(PUBLISHER, "Prentice Hall");
        editionTwo.setField(DATE, "2008");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(editionOne, editionTwo, BIBTEX));
    }

    @Test
    public void sameBooksWithPartiallyMissingEditionAreDuplicates() {
        BibEntry editionOne = new BibEntry(BibtexEntryTypes.BOOK);
        editionOne.setField(TITLE, "Effective Java");
        editionOne.setField(AUTHOR, "Bloch, Joshua");
        editionOne.setField(PUBLISHER, "Prentice Hall");
        editionOne.setField(DATE, "2001");
        BibEntry editionTwo = new BibEntry(BibtexEntryTypes.BOOK);
        editionTwo.setField(TITLE, "Effective Java");
        editionTwo.setField(AUTHOR, "Bloch, Joshua");
        editionTwo.setField(PUBLISHER, "Prentice Hall");
        editionTwo.setField(DATE, "2008");
        editionTwo.setField(EDITION, "2");
        Assertions.assertTrue(DuplicateCheck.isDuplicate(editionOne, editionTwo, BIBTEX));
    }

    @Test
    public void sameBooksWithDifferentEditionsAreNotDuplicates() {
        BibEntry editionTwo = new BibEntry(BibtexEntryTypes.BOOK);
        editionTwo.setCiteKey("Sutton17reinfLrnIntroBook");
        editionTwo.setField(TITLE, "Reinforcement learning:An introduction");
        editionTwo.setField(PUBLISHER, "MIT Press");
        editionTwo.setField(YEAR, "2017");
        editionTwo.setField(AUTHOR, "Sutton, Richard S and Barto, Andrew G");
        editionTwo.setField(ADDRESS, "Cambridge, MA.USA");
        editionTwo.setField(EDITION, "Second");
        editionTwo.setField(JOURNAL, "MIT Press");
        editionTwo.setField(URL, "https://webdocs.cs.ualberta.ca/~sutton/book/the-book-2nd.html");
        BibEntry editionOne = new BibEntry(BibtexEntryTypes.BOOK);
        editionOne.setCiteKey("Sutton98reinfLrnIntroBook");
        editionOne.setField(TITLE, "Reinforcement learning: An introduction");
        editionOne.setField(PUBLISHER, "MIT press Cambridge");
        editionOne.setField(YEAR, "1998");
        editionOne.setField(AUTHOR, "Sutton, Richard S and Barto, Andrew G");
        editionOne.setField(VOLUME, "1");
        editionOne.setField(NUMBER, "1");
        editionOne.setField(EDITION, "First");
        Assertions.assertFalse(DuplicateCheck.isDuplicate(editionOne, editionTwo, BIBTEX));
    }
}

