package org.jabref.model.entry;


import BibEntry.KEY_FIELD;
import FieldName.KEYWORDS;
import SpecialField.RANKING;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jabref.model.FieldChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static BibtexEntryTypes.ARTICLE;


public class BibEntryTests {
    private BibEntry keywordEntry;

    private BibEntry emptyEntry;

    @Test
    public void testDefaultConstructor() {
        BibEntry entry = new BibEntry();
        // we have to use `getType("misc")` in the case of biblatex mode
        Assertions.assertEquals("misc", entry.getType());
        Assertions.assertNotNull(entry.getId());
        Assertions.assertFalse(entry.getField("author").isPresent());
    }

    @Test
    public void allFieldsPresentDefault() {
        BibEntry e = new BibEntry(ARTICLE);
        e.setField("author", "abc");
        e.setField("title", "abc");
        e.setField("journal", "abc");
        List<String> requiredFields = new ArrayList<>();
        requiredFields.add("author");
        requiredFields.add("title");
        Assertions.assertTrue(e.allFieldsPresent(requiredFields, null));
        requiredFields.add("year");
        Assertions.assertFalse(e.allFieldsPresent(requiredFields, null));
    }

    @Test
    public void allFieldsPresentOr() {
        BibEntry e = new BibEntry(ARTICLE);
        e.setField("author", "abc");
        e.setField("title", "abc");
        e.setField("journal", "abc");
        List<String> requiredFields = new ArrayList<>();
        // XOR required
        requiredFields.add("journal/year");
        Assertions.assertTrue(e.allFieldsPresent(requiredFields, null));
        requiredFields.add("year/address");
        Assertions.assertFalse(e.allFieldsPresent(requiredFields, null));
    }

    @Test
    public void isNullCiteKeyThrowsNPE() {
        BibEntry e = new BibEntry(ARTICLE);
        Assertions.assertThrows(NullPointerException.class, () -> e.setCiteKey(null));
    }

    @Test
    public void isEmptyCiteKey() {
        BibEntry e = new BibEntry(ARTICLE);
        Assertions.assertFalse(e.hasCiteKey());
        e.setCiteKey("");
        Assertions.assertFalse(e.hasCiteKey());
        e.setCiteKey("key");
        Assertions.assertTrue(e.hasCiteKey());
        e.clearField(KEY_FIELD);
        Assertions.assertFalse(e.hasCiteKey());
    }

    @Test
    public void typeOfBibEntryIsMiscAfterSettingToNullString() {
        Assertions.assertEquals("article", keywordEntry.getType());
        keywordEntry.setType(((String) (null)));
        Assertions.assertEquals("misc", keywordEntry.getType());
    }

    @Test
    public void typeOfBibEntryIsMiscAfterSettingToEmptyString() {
        Assertions.assertEquals("article", keywordEntry.getType());
        keywordEntry.setType("");
        Assertions.assertEquals("misc", keywordEntry.getType());
    }

    @Test
    public void getFieldOrAliasDateWithYearNumericalMonthString() {
        emptyEntry.setField("year", "2003");
        emptyEntry.setField("month", "3");
        Assertions.assertEquals(Optional.of("2003-03"), emptyEntry.getFieldOrAlias("date"));
    }

    @Test
    public void getFieldOrAliasDateWithYearAbbreviatedMonth() {
        emptyEntry.setField("year", "2003");
        emptyEntry.setField("month", "#mar#");
        Assertions.assertEquals(Optional.of("2003-03"), emptyEntry.getFieldOrAlias("date"));
    }

    @Test
    public void getFieldOrAliasDateWithYearAbbreviatedMonthString() {
        emptyEntry.setField("year", "2003");
        emptyEntry.setField("month", "mar");
        Assertions.assertEquals(Optional.of("2003-03"), emptyEntry.getFieldOrAlias("date"));
    }

    @Test
    public void getFieldOrAliasDateWithOnlyYear() {
        emptyEntry.setField("year", "2003");
        Assertions.assertEquals(Optional.of("2003"), emptyEntry.getFieldOrAlias("date"));
    }

    @Test
    public void getFieldOrAliasYearWithDateYYYY() {
        emptyEntry.setField("date", "2003");
        Assertions.assertEquals(Optional.of("2003"), emptyEntry.getFieldOrAlias("year"));
    }

    @Test
    public void getFieldOrAliasYearWithDateYYYYMM() {
        emptyEntry.setField("date", "2003-03");
        Assertions.assertEquals(Optional.of("2003"), emptyEntry.getFieldOrAlias("year"));
    }

    @Test
    public void getFieldOrAliasYearWithDateYYYYMMDD() {
        emptyEntry.setField("date", "2003-03-30");
        Assertions.assertEquals(Optional.of("2003"), emptyEntry.getFieldOrAlias("year"));
    }

    @Test
    public void getFieldOrAliasMonthWithDateYYYYReturnsNull() {
        emptyEntry.setField("date", "2003");
        Assertions.assertEquals(Optional.empty(), emptyEntry.getFieldOrAlias("month"));
    }

    @Test
    public void getFieldOrAliasMonthWithDateYYYYMM() {
        emptyEntry.setField("date", "2003-03");
        Assertions.assertEquals(Optional.of("#mar#"), emptyEntry.getFieldOrAlias("month"));
    }

    @Test
    public void getFieldOrAliasMonthWithDateYYYYMMDD() {
        emptyEntry.setField("date", "2003-03-30");
        Assertions.assertEquals(Optional.of("#mar#"), emptyEntry.getFieldOrAlias("month"));
    }

    @Test
    public void getFieldOrAliasLatexFreeAlreadyFreeValueIsUnchanged() {
        emptyEntry.setField("title", "A Title Without any LaTeX commands");
        Assertions.assertEquals(Optional.of("A Title Without any LaTeX commands"), emptyEntry.getFieldOrAliasLatexFree("title"));
    }

    @Test
    public void getFieldOrAliasLatexFreeAlreadyFreeAliasValueIsUnchanged() {
        emptyEntry.setField("journal", "A Title Without any LaTeX commands");
        Assertions.assertEquals(Optional.of("A Title Without any LaTeX commands"), emptyEntry.getFieldOrAliasLatexFree("journaltitle"));
    }

    @Test
    public void getFieldOrAliasLatexFreeBracesAreRemoved() {
        emptyEntry.setField("title", "{A Title with some {B}ra{C}es}");
        Assertions.assertEquals(Optional.of("A Title with some BraCes"), emptyEntry.getFieldOrAliasLatexFree("title"));
    }

    @Test
    public void getFieldOrAliasLatexFreeBracesAreRemovedFromAlias() {
        emptyEntry.setField("journal", "{A Title with some {B}ra{C}es}");
        Assertions.assertEquals(Optional.of("A Title with some BraCes"), emptyEntry.getFieldOrAliasLatexFree("journaltitle"));
    }

    @Test
    public void getFieldOrAliasLatexFreeComplexConversionInAlias() {
        emptyEntry.setField("journal", "A 32~{mA} {$\\Sigma\\Delta$}-modulator");
        Assertions.assertEquals(Optional.of("A 32 mA ??-modulator"), emptyEntry.getFieldOrAliasLatexFree("journaltitle"));
    }

    @Test
    public void setNullField() {
        Assertions.assertThrows(NullPointerException.class, () -> emptyEntry.setField(null));
    }

    @Test
    public void addNullKeywordThrowsNPE() {
        Assertions.assertThrows(NullPointerException.class, () -> keywordEntry.addKeyword(((Keyword) (null)), ','));
    }

    @Test
    public void putNullKeywordListThrowsNPE() {
        Assertions.assertThrows(NullPointerException.class, () -> keywordEntry.putKeywords(((KeywordList) (null)), ','));
    }

    @Test
    public void putNullKeywordSeparatorThrowsNPE() {
        Assertions.assertThrows(NullPointerException.class, () -> keywordEntry.putKeywords(Arrays.asList("A", "B"), null));
    }

    @Test
    public void testGetSeparatedKeywordsAreCorrect() {
        Assertions.assertEquals(new KeywordList("Foo", "Bar"), keywordEntry.getKeywords(','));
    }

    @Test
    public void testAddKeywordIsCorrect() {
        keywordEntry.addKeyword("FooBar", ',');
        Assertions.assertEquals(new KeywordList("Foo", "Bar", "FooBar"), keywordEntry.getKeywords(','));
    }

    @Test
    public void testAddKeywordHasChanged() {
        keywordEntry.addKeyword("FooBar", ',');
        Assertions.assertTrue(keywordEntry.hasChanged());
    }

    @Test
    public void testAddKeywordTwiceYiedsOnlyOne() {
        keywordEntry.addKeyword("FooBar", ',');
        keywordEntry.addKeyword("FooBar", ',');
        Assertions.assertEquals(new KeywordList("Foo", "Bar", "FooBar"), keywordEntry.getKeywords(','));
    }

    @Test
    public void addKeywordIsCaseSensitive() {
        keywordEntry.addKeyword("FOO", ',');
        Assertions.assertEquals(new KeywordList("Foo", "Bar", "FOO"), keywordEntry.getKeywords(','));
    }

    @Test
    public void testAddKeywordWithDifferentCapitalizationChanges() {
        keywordEntry.addKeyword("FOO", ',');
        Assertions.assertTrue(keywordEntry.hasChanged());
    }

    @Test
    public void testAddKeywordEmptyKeywordIsNotAdded() {
        keywordEntry.addKeyword("", ',');
        Assertions.assertEquals(new KeywordList("Foo", "Bar"), keywordEntry.getKeywords(','));
    }

    @Test
    public void testAddKeywordEmptyKeywordNotChanged() {
        keywordEntry.addKeyword("", ',');
        Assertions.assertFalse(keywordEntry.hasChanged());
    }

    @Test
    public void texNewBibEntryHasNoKeywords() {
        Assertions.assertTrue(emptyEntry.getKeywords(',').isEmpty());
    }

    @Test
    public void texNewBibEntryHasNoKeywordsEvenAfterAddingEmptyKeyword() {
        emptyEntry.addKeyword("", ',');
        Assertions.assertTrue(emptyEntry.getKeywords(',').isEmpty());
    }

    @Test
    public void texNewBibEntryAfterAddingEmptyKeywordNotChanged() {
        emptyEntry.addKeyword("", ',');
        Assertions.assertFalse(emptyEntry.hasChanged());
    }

    @Test
    public void testAddKeywordsWorksAsExpected() {
        emptyEntry.addKeywords(Arrays.asList("Foo", "Bar"), ',');
        Assertions.assertEquals(new KeywordList("Foo", "Bar"), emptyEntry.getKeywords(','));
    }

    @Test
    public void testPutKeywordsOverwritesOldKeywords() {
        keywordEntry.putKeywords(Arrays.asList("Yin", "Yang"), ',');
        Assertions.assertEquals(new KeywordList("Yin", "Yang"), keywordEntry.getKeywords(','));
    }

    @Test
    public void testPutKeywordsHasChanged() {
        keywordEntry.putKeywords(Arrays.asList("Yin", "Yang"), ',');
        Assertions.assertTrue(keywordEntry.hasChanged());
    }

    @Test
    public void testPutKeywordsPutEmpyListErasesPreviousKeywords() {
        keywordEntry.putKeywords(Collections.emptyList(), ',');
        Assertions.assertTrue(keywordEntry.getKeywords(',').isEmpty());
    }

    @Test
    public void testPutKeywordsPutEmpyListHasChanged() {
        keywordEntry.putKeywords(Collections.emptyList(), ',');
        Assertions.assertTrue(keywordEntry.hasChanged());
    }

    @Test
    public void testPutKeywordsPutEmpyListToEmptyBibentry() {
        emptyEntry.putKeywords(Collections.emptyList(), ',');
        Assertions.assertTrue(emptyEntry.getKeywords(',').isEmpty());
    }

    @Test
    public void testPutKeywordsPutEmpyListToEmptyBibentryNotChanged() {
        emptyEntry.putKeywords(Collections.emptyList(), ',');
        Assertions.assertFalse(emptyEntry.hasChanged());
    }

    @Test
    public void putKeywordsToEmptyReturnsNoChange() {
        Optional<FieldChange> change = emptyEntry.putKeywords(Collections.emptyList(), ',');
        Assertions.assertEquals(Optional.empty(), change);
    }

    @Test
    public void clearKeywordsReturnsChange() {
        Optional<FieldChange> change = keywordEntry.putKeywords(Collections.emptyList(), ',');
        Assertions.assertEquals(Optional.of(new FieldChange(keywordEntry, "keywords", "Foo, Bar", null)), change);
    }

    @Test
    public void changeKeywordsReturnsChange() {
        Optional<FieldChange> change = keywordEntry.putKeywords(Arrays.asList("Test", "FooTest"), ',');
        Assertions.assertEquals(Optional.of(new FieldChange(keywordEntry, "keywords", "Foo, Bar", "Test, FooTest")), change);
    }

    @Test
    public void putKeywordsToSameReturnsNoChange() {
        Optional<FieldChange> change = keywordEntry.putKeywords(Arrays.asList("Foo", "Bar"), ',');
        Assertions.assertEquals(Optional.empty(), change);
    }

    @Test
    public void getKeywordsReturnsParsedKeywordListFromKeywordsField() {
        BibEntry entry = new BibEntry();
        entry.setField(KEYWORDS, "w1, w2a w2b, w3");
        Assertions.assertEquals(new KeywordList("w1", "w2a w2b", "w3"), entry.getKeywords(','));
    }

    @Test
    public void removeKeywordsOnEntryWithoutKeywordsDoesNothing() {
        BibEntry entry = new BibEntry();
        Optional<FieldChange> change = entry.removeKeywords(RANKING.getKeyWords(), ',');
        Assertions.assertEquals(Optional.empty(), change);
    }

    @Test
    public void removeKeywordsWithEmptyListDoesNothing() {
        keywordEntry.putKeywords(Arrays.asList("kw1", "kw2"), ',');
        Optional<FieldChange> change = keywordEntry.removeKeywords(new KeywordList(), ',');
        Assertions.assertEquals(Optional.empty(), change);
    }

    @Test
    public void removeKeywordsWithNonExistingKeywordsDoesNothing() {
        keywordEntry.putKeywords(Arrays.asList("kw1", "kw2"), ',');
        Optional<FieldChange> change = keywordEntry.removeKeywords(KeywordList.parse("kw3, kw4", ','), ',');
        Assertions.assertEquals(Optional.empty(), change);
        Assertions.assertEquals(Sets.newHashSet("kw1", "kw2"), keywordEntry.getKeywords(',').toStringList());
    }

    @Test
    public void removeKeywordsWithExistingKeywordsRemovesThem() {
        keywordEntry.putKeywords(Arrays.asList("kw1", "kw2", "kw3"), ',');
        Optional<FieldChange> change = keywordEntry.removeKeywords(KeywordList.parse("kw1, kw2", ','), ',');
        Assertions.assertTrue(change.isPresent());
        Assertions.assertEquals(KeywordList.parse("kw3", ','), keywordEntry.getKeywords(','));
    }

    @Test
    public void setCiteKey() {
        BibEntry be = new BibEntry();
        Assertions.assertFalse(be.hasCiteKey());
        be.setField("author", "Albert Einstein");
        be.setCiteKey("Einstein1931");
        Assertions.assertTrue(be.hasCiteKey());
        Assertions.assertEquals(Optional.of("Einstein1931"), be.getCiteKeyOptional());
        Assertions.assertEquals(Optional.of("Albert Einstein"), be.getField("author"));
        be.clearField("author");
        Assertions.assertEquals(Optional.empty(), be.getField("author"));
    }
}

