package org.jabref.gui.autocompleter;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class BibEntrySuggestionProviderTest {
    private BibEntrySuggestionProvider autoCompleter;

    @Test
    public void completeWithoutAddingAnythingReturnsNothing() {
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingNullReturnsNothing() {
        autoCompleter.indexEntry(null);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingEmptyEntryReturnsNothing() {
        BibEntry entry = new BibEntry();
        autoCompleter.indexEntry(entry);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeKeyReturnsKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("testKey");
        autoCompleter.indexEntry(entry);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("testKey"));
        Assertions.assertEquals(Collections.singletonList(entry), result);
    }

    @Test
    public void completeBeginnigOfKeyReturnsKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("testKey");
        autoCompleter.indexEntry(entry);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.singletonList(entry), result);
    }

    @Test
    public void completeLowercaseKeyReturnsKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("testKey");
        autoCompleter.indexEntry(entry);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("testkey"));
        Assertions.assertEquals(Collections.singletonList(entry), result);
    }

    @Test
    public void completeNullThrowsException() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("testKey");
        autoCompleter.indexEntry(entry);
        Assertions.assertThrows(NullPointerException.class, () -> autoCompleter.call(AutoCompleterUtil.getRequest(null)));
    }

    @Test
    public void completeEmptyStringReturnsNothing() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("testKey");
        autoCompleter.indexEntry(entry);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest(""));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeReturnsMultipleResults() {
        BibEntry entryOne = new BibEntry();
        entryOne.setCiteKey("testKeyOne");
        autoCompleter.indexEntry(entryOne);
        BibEntry entryTwo = new BibEntry();
        entryTwo.setCiteKey("testKeyTwo");
        autoCompleter.indexEntry(entryTwo);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("testKey"));
        Assertions.assertEquals(Arrays.asList(entryTwo, entryOne), result);
    }

    @Test
    public void completeShortKeyReturnsKey() {
        BibEntry entry = new BibEntry();
        entry.setCiteKey("key");
        autoCompleter.indexEntry(entry);
        Collection<BibEntry> result = autoCompleter.call(AutoCompleterUtil.getRequest("k"));
        Assertions.assertEquals(Collections.singletonList(entry), result);
    }
}

