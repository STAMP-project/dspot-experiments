package org.jabref.gui.autocompleter;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jabref.model.entry.Author;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class PersonNameSuggestionProviderTest {
    private final Author vassilisKostakos = new Author("Vassilis", "V.", "", "Kostakos", "");

    private PersonNameSuggestionProvider autoCompleter;

    private BibEntry entry;

    @Test
    public void completeWithoutAddingAnythingReturnsNothing() {
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingNullReturnsNothing() {
        autoCompleter.indexEntry(null);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingEmptyEntryReturnsNothing() {
        BibEntry entry = new BibEntry();
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingEntryWithoutFieldReturnsNothing() {
        BibEntry entry = new BibEntry();
        entry.setField("title", "testTitle");
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeNameReturnsName() {
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Kostakos"));
        Assertions.assertEquals(Collections.singletonList(vassilisKostakos), result);
    }

    @Test
    public void completeBeginningOfNameReturnsName() {
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Kosta"));
        Assertions.assertEquals(Collections.singletonList(vassilisKostakos), result);
    }

    @Test
    public void completeLowercaseBeginningOfNameReturnsName() {
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("kosta"));
        Assertions.assertEquals(Collections.singletonList(vassilisKostakos), result);
    }

    @Test
    public void completeNullThrowsException() {
        Assertions.assertThrows(NullPointerException.class, () -> autoCompleter.call(AutoCompleterUtil.getRequest(null)));
    }

    @Test
    public void completeEmptyStringReturnsNothing() {
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest(""));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeReturnsMultipleResults() {
        autoCompleter.indexEntry(entry);
        BibEntry entryTwo = new BibEntry();
        entryTwo.setField("field", "Kosta");
        autoCompleter.indexEntry(entryTwo);
        Author authorTwo = new Author("", "", "", "Kosta", "");
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Ko"));
        Assertions.assertEquals(Arrays.asList(authorTwo, vassilisKostakos), result);
    }

    @Test
    public void completePartOfNameReturnsName() {
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("osta"));
        Assertions.assertEquals(Collections.singletonList(vassilisKostakos), result);
    }

    @Test
    public void completeBeginningOfFirstNameReturnsName() {
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Vas"));
        Assertions.assertEquals(Collections.singletonList(vassilisKostakos), result);
    }

    @Test
    public void completeBeginningOfFirstNameReturnsNameWithJr() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "Reagle, Jr., Joseph M.");
        autoCompleter.indexEntry(entry);
        Author author = new Author("Joseph M.", "J. M.", "", "Reagle", "Jr.");
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Jos"));
        Assertions.assertEquals(Collections.singletonList(author), result);
    }

    @Test
    public void completeBeginningOfFirstNameReturnsNameWithVon() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "Eric von Hippel");
        autoCompleter.indexEntry(entry);
        Author author = new Author("Eric", "E.", "von", "Hippel", "");
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Eric"));
        Assertions.assertEquals(Collections.singletonList(author), result);
    }

    @Test
    public void completeBeginningOfLastNameReturnsNameWithUmlauts() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "Honig B?r");
        autoCompleter.indexEntry(entry);
        Author author = new Author("Honig", "H.", "", "B?r", "");
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("B\u00e4"));
        Assertions.assertEquals(Collections.singletonList(author), result);
    }

    @Test
    public void completeVonReturnsName() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "Eric von Hippel");
        autoCompleter.indexEntry(entry);
        Author author = new Author("Eric", "E.", "von", "Hippel", "");
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("von"));
        Assertions.assertEquals(Collections.singletonList(author), result);
    }

    @Test
    public void completeBeginningOfFullNameReturnsName() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "Vassilis Kostakos");
        autoCompleter.indexEntry(entry);
        Collection<Author> result = autoCompleter.call(AutoCompleterUtil.getRequest("Kostakos, Va"));
        Assertions.assertEquals(Collections.singletonList(vassilisKostakos), result);
    }
}

