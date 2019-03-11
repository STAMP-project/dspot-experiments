package org.jabref.logic.util;


import FieldName.AUTHOR;
import FieldName.CROSSREF;
import FieldName.EDITOR;
import org.jabref.logic.bibtexkeypattern.BracketedPattern;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class BracketedPatternTest {
    private BibEntry bibentry;

    private BibDatabase database;

    private BibEntry dbentry;

    @Test
    public void bibentryExpansionTest() {
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage]");
        Assertions.assertEquals("2017_Kitsune_213", pattern.expand(bibentry));
    }

    @Test
    public void nullDatabaseExpansionTest() {
        BibDatabase another_database = null;
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage]");
        Assertions.assertEquals("2017_Kitsune_213", pattern.expand(bibentry, another_database));
    }

    @Test
    public void pureauthReturnsAuthorIfEditorIsAbsent() {
        BibDatabase emptyDatabase = new BibDatabase();
        BracketedPattern pattern = new BracketedPattern("[pureauth]");
        Assertions.assertEquals("Kitsune", pattern.expand(bibentry, emptyDatabase));
    }

    @Test
    public void pureauthReturnsAuthorIfEditorIsPresent() {
        BibDatabase emptyDatabase = new BibDatabase();
        BracketedPattern pattern = new BracketedPattern("[pureauth]");
        bibentry.setField(EDITOR, "Editorlastname, Editorfirstname");
        Assertions.assertEquals("Kitsune", pattern.expand(bibentry, emptyDatabase));
    }

    @Test
    public void pureauthReturnsEmptyStringIfAuthorIsAbsent() {
        BibDatabase emptyDatabase = new BibDatabase();
        BracketedPattern pattern = new BracketedPattern("[pureauth]");
        bibentry.clearField(AUTHOR);
        Assertions.assertEquals("", pattern.expand(bibentry, emptyDatabase));
    }

    @Test
    public void pureauthReturnsEmptyStringIfAuthorIsAbsentAndEditorIsPresent() {
        BibDatabase emptyDatabase = new BibDatabase();
        BracketedPattern pattern = new BracketedPattern("[pureauth]");
        bibentry.clearField(AUTHOR);
        bibentry.setField(EDITOR, "Editorlastname, Editorfirstname");
        Assertions.assertEquals("", pattern.expand(bibentry, emptyDatabase));
    }

    @Test
    public void emptyDatabaseExpansionTest() {
        BibDatabase another_database = new BibDatabase();
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage]");
        Assertions.assertEquals("2017_Kitsune_213", pattern.expand(bibentry, another_database));
    }

    @Test
    public void databaseWithStringsExpansionTest() {
        BibDatabase another_database = new BibDatabase();
        BibtexString string = new BibtexString("sgr", "Saulius Gra?ulis");
        another_database.addString(string);
        bibentry = new BibEntry();
        bibentry.setField("author", "#sgr#");
        bibentry.setField("year", "2017");
        bibentry.setField("pages", "213--216");
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage]");
        Assertions.assertEquals("2017_Gra?ulis_213", pattern.expand(bibentry, another_database));
    }

    @Test
    public void unbalancedBracketsExpandToSomething() {
        BracketedPattern pattern = new BracketedPattern("[year]_[auth_[firstpage]");
        Assertions.assertNotEquals("", pattern.expand(bibentry));
    }

    @Test
    public void unbalancedLastBracketExpandsToSomething() {
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage");
        Assertions.assertNotEquals("", pattern.expand(bibentry));
    }

    @Test
    public void entryTypeExpansionTest() {
        BracketedPattern pattern = new BracketedPattern("[entrytype]:[year]_[auth]_[pages]");
        Assertions.assertEquals("Misc:2017_Kitsune_213--216", pattern.expand(bibentry));
    }

    @Test
    public void entryTypeExpansionLowercaseTest() {
        BracketedPattern pattern = new BracketedPattern("[entrytype:lower]:[year]_[auth]_[firstpage]");
        Assertions.assertEquals("misc:2017_Kitsune_213", pattern.expand(bibentry));
    }

    @Test
    public void suppliedBibentryBracketExpansionTest() {
        BibDatabase another_database = null;
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage]");
        BibEntry another_bibentry = new BibEntry();
        another_bibentry.setField("author", "Gra?ulis, Saulius");
        another_bibentry.setField("year", "2017");
        another_bibentry.setField("pages", "213--216");
        Assertions.assertEquals("2017_Gra?ulis_213", pattern.expand(another_bibentry, ';', another_database));
    }

    @Test
    public void nullBibentryBracketExpansionTest() {
        BibDatabase another_database = null;
        BibEntry another_bibentry = null;
        BracketedPattern pattern = new BracketedPattern("[year]_[auth]_[firstpage]");
        Assertions.assertThrows(NullPointerException.class, () -> pattern.expand(another_bibentry, ';', another_database));
    }

    @Test
    public void bracketedExpressionDefaultConstructorTest() {
        BibDatabase another_database = null;
        BracketedPattern pattern = new BracketedPattern();
        Assertions.assertThrows(NullPointerException.class, () -> pattern.expand(bibentry, ';', another_database));
    }

    @Test
    public void unknownKeyExpandsToEmptyString() {
        Character separator = ';';
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[unknownkey]", separator, dbentry, database));
    }

    @Test
    public void emptyPatternAndEmptyModifierExpandsToEmptyString() {
        Character separator = ';';
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[:]", separator, dbentry, database));
    }

    @Test
    public void emptyPatternAndValidModifierExpandsToEmptyString() {
        Character separator = ';';
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[:lower]", separator, dbentry, database));
    }

    @Test
    public void bibtexkeyPatternExpandsToBibTeXKey() {
        Character separator = ';';
        Assertions.assertEquals("HipKro03", BracketedPattern.expandBrackets("[bibtexkey]", separator, dbentry, database));
    }

    @Test
    public void bibtexkeyPatternWithEmptyModifierExpandsToBibTeXKey() {
        Character separator = ';';
        Assertions.assertEquals("HipKro03", BracketedPattern.expandBrackets("[bibtexkey:]", separator, dbentry, database));
    }

    @Test
    public void authorPatternTreatsVonNamePrefixCorrectly() {
        Character separator = ';';
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh", BracketedPattern.expandBrackets("[author]", separator, dbentry, database));
    }

    @Test
    public void lowerFormatterWorksOnVonNamePrefixes() {
        Character separator = ';';
        Assertions.assertEquals("eric von hippel and georg von krogh", BracketedPattern.expandBrackets("[author:lower]", separator, dbentry, database));
    }

    @Test
    public void testResolvedFieldAndFormat() {
        BibEntry child = new BibEntry();
        child.setField(CROSSREF, "HipKro03");
        database.insertEntry(child);
        Character separator = ';';
        Assertions.assertEquals("Eric von Hippel and Georg von Krogh", BracketedPattern.expandBrackets("[author]", separator, child, database));
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[unknownkey]", separator, child, database));
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[:]", separator, child, database));
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[:lower]", separator, child, database));
        Assertions.assertEquals("eric von hippel and georg von krogh", BracketedPattern.expandBrackets("[author:lower]", separator, child, database));
        // the bibtexkey is not inherited
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[bibtexkey]", separator, child, database));
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[bibtexkey:]", separator, child, database));
    }

    @Test
    public void testResolvedParentNotInDatabase() {
        BibEntry child = new BibEntry();
        child.setField(CROSSREF, "HipKro03");
        database.removeEntry(dbentry);
        database.insertEntry(child);
        Assertions.assertEquals("", BracketedPattern.expandBrackets("[author]", ';', child, database));
    }
}

