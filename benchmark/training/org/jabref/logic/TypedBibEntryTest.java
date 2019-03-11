package org.jabref.logic;


import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexEntryTypes;
import org.jabref.model.entry.CustomEntryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TypedBibEntryTest {
    @Test
    public void hasAllRequiredFieldsFail() {
        BibEntry e = new BibEntry(BibtexEntryTypes.ARTICLE);
        e.setField("author", "abc");
        e.setField("title", "abc");
        e.setField("journal", "abc");
        TypedBibEntry typedEntry = new TypedBibEntry(e, BibDatabaseMode.BIBTEX);
        Assertions.assertFalse(typedEntry.hasAllRequiredFields());
    }

    @Test
    public void hasAllRequiredFields() {
        BibEntry e = new BibEntry(BibtexEntryTypes.ARTICLE);
        e.setField("author", "abc");
        e.setField("title", "abc");
        e.setField("journal", "abc");
        e.setField("year", "2015");
        TypedBibEntry typedEntry = new TypedBibEntry(e, BibDatabaseMode.BIBTEX);
        Assertions.assertTrue(typedEntry.hasAllRequiredFields());
    }

    @Test
    public void hasAllRequiredFieldsForUnknownTypeReturnsTrue() {
        BibEntry e = new BibEntry(new CustomEntryType("articlllleeeee", "required", "optional"));
        TypedBibEntry typedEntry = new TypedBibEntry(e, BibDatabaseMode.BIBTEX);
        Assertions.assertTrue(typedEntry.hasAllRequiredFields());
    }

    @Test
    public void getTypeForDisplayReturnsTypeName() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        TypedBibEntry typedEntry = new TypedBibEntry(e, BibDatabaseMode.BIBTEX);
        Assertions.assertEquals("InProceedings", typedEntry.getTypeForDisplay());
    }

    @Test
    public void getTypeForDisplayForUnknownTypeCapitalizeFirstLetter() {
        BibEntry e = new BibEntry(new CustomEntryType("articlllleeeee", "required", "optional"));
        TypedBibEntry typedEntry = new TypedBibEntry(e, BibDatabaseMode.BIBTEX);
        Assertions.assertEquals("Articlllleeeee", typedEntry.getTypeForDisplay());
    }
}

