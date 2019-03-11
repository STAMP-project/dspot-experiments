package org.jabref.gui.autocompleter;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class DefaultAutoCompleterTest {
    private WordSuggestionProvider autoCompleter;

    @Test
    public void initAutoCompleterWithNullFieldThrowsException() {
        Assertions.assertThrows(NullPointerException.class, () -> new WordSuggestionProvider(null));
    }

    @Test
    public void completeWithoutAddingAnythingReturnsNothing() {
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingNullReturnsNothing() {
        autoCompleter.indexEntry(null);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingEmptyEntryReturnsNothing() {
        BibEntry entry = new BibEntry();
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeAfterAddingEntryWithoutFieldReturnsNothing() {
        BibEntry entry = new BibEntry();
        entry.setField("title", "testTitle");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeValueReturnsValue() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "testValue");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("testValue"));
        Assertions.assertEquals(Arrays.asList("testValue"), result);
    }

    @Test
    public void completeBeginnigOfValueReturnsValue() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "testValue");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("test"));
        Assertions.assertEquals(Arrays.asList("testValue"), result);
    }

    @Test
    public void completeLowercaseValueReturnsValue() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "testValue");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("testvalue"));
        Assertions.assertEquals(Arrays.asList("testValue"), result);
    }

    @Test
    public void completeNullThrowsException() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "testKey");
        autoCompleter.indexEntry(entry);
        Assertions.assertThrows(NullPointerException.class, () -> autoCompleter.call(AutoCompleterUtil.getRequest(null)));
    }

    @Test
    public void completeEmptyStringReturnsNothing() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "testKey");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest(""));
        Assertions.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void completeReturnsMultipleResults() {
        BibEntry entryOne = new BibEntry();
        entryOne.setField("field", "testValueOne");
        autoCompleter.indexEntry(entryOne);
        BibEntry entryTwo = new BibEntry();
        entryTwo.setField("field", "testValueTwo");
        autoCompleter.indexEntry(entryTwo);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("testValue"));
        Assertions.assertEquals(Arrays.asList("testValueOne", "testValueTwo"), result);
    }

    @Test
    public void completeShortStringReturnsValue() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "val");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("va"));
        Assertions.assertEquals(Collections.singletonList("val"), result);
    }

    @Test
    public void completeBeginnigOfSecondWordReturnsWord() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "test value");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("val"));
        Assertions.assertEquals(Collections.singletonList("value"), result);
    }

    @Test
    public void completePartOfWordReturnsValue() {
        BibEntry entry = new BibEntry();
        entry.setField("field", "test value");
        autoCompleter.indexEntry(entry);
        Collection<String> result = autoCompleter.call(AutoCompleterUtil.getRequest("lue"));
        Assertions.assertEquals(Collections.singletonList("value"), result);
    }
}

