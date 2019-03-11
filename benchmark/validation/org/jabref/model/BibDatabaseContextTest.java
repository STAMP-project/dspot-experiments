package org.jabref.model;


import BibDatabaseMode.BIBLATEX;
import BibDatabaseMode.BIBTEX;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.IEEETranEntryTypes;
import org.jabref.model.metadata.MetaData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BibDatabaseContextTest {
    @Test
    public void testTypeBasedOnDefaultBibtex() {
        BibDatabaseContext bibDatabaseContext = new BibDatabaseContext(new BibDatabase(), new MetaData(), new Defaults(BibDatabaseMode.BIBTEX));
        Assertions.assertEquals(BIBTEX, bibDatabaseContext.getMode());
        bibDatabaseContext.setMode(BIBLATEX);
        Assertions.assertEquals(BIBLATEX, bibDatabaseContext.getMode());
    }

    @Test
    public void testTypeBasedOnDefaultBiblatex() {
        BibDatabaseContext bibDatabaseContext = new BibDatabaseContext(new BibDatabase(), new MetaData(), new Defaults(BibDatabaseMode.BIBLATEX));
        Assertions.assertEquals(BIBLATEX, bibDatabaseContext.getMode());
        bibDatabaseContext.setMode(BIBTEX);
        Assertions.assertEquals(BIBTEX, bibDatabaseContext.getMode());
    }

    @Test
    public void testTypeBasedOnInferredModeBibTeX() {
        BibDatabase db = new BibDatabase();
        BibEntry e1 = new BibEntry();
        db.insertEntry(e1);
        BibDatabaseContext bibDatabaseContext = new BibDatabaseContext(db);
        Assertions.assertEquals(BIBTEX, bibDatabaseContext.getMode());
    }

    @Test
    public void testTypeBasedOnInferredModeBiblatex() {
        BibDatabase db = new BibDatabase();
        BibEntry e1 = new BibEntry(IEEETranEntryTypes.ELECTRONIC);
        db.insertEntry(e1);
        BibDatabaseContext bibDatabaseContext = new BibDatabaseContext(db);
        Assertions.assertEquals(BIBLATEX, bibDatabaseContext.getMode());
    }
}

