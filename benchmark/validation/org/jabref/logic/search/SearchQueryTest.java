package org.jabref.logic.search;


import BibtexEntryTypes.ARTICLE;
import FieldName.BOOKTITLE;
import FieldName.TITLE;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexEntryTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SearchQueryTest {
    @Test
    public void testToString() {
        Assertions.assertEquals("\"asdf\" (case sensitive, regular expression)", new SearchQuery("asdf", true, true).toString());
        Assertions.assertEquals("\"asdf\" (case insensitive, plain text)", new SearchQuery("asdf", false, false).toString());
    }

    @Test
    public void testIsContainsBasedSearch() {
        Assertions.assertTrue(new SearchQuery("asdf", true, false).isContainsBasedSearch());
        Assertions.assertTrue(new SearchQuery("asdf", true, true).isContainsBasedSearch());
        Assertions.assertFalse(new SearchQuery("author=asdf", true, false).isContainsBasedSearch());
    }

    @Test
    public void testIsGrammarBasedSearch() {
        Assertions.assertFalse(new SearchQuery("asdf", true, false).isGrammarBasedSearch());
        Assertions.assertFalse(new SearchQuery("asdf", true, true).isGrammarBasedSearch());
        Assertions.assertTrue(new SearchQuery("author=asdf", true, false).isGrammarBasedSearch());
    }

    @Test
    public void testGrammarSearch() {
        BibEntry entry = new BibEntry();
        entry.addKeyword("one two", ',');
        SearchQuery searchQuery = new SearchQuery("keywords=\"one two\"", false, false);
        Assertions.assertTrue(searchQuery.isMatch(entry));
    }

    @Test
    public void testGrammarSearchFullEntryLastCharMissing() {
        BibEntry entry = new BibEntry();
        entry.setField(TITLE, "systematic revie");
        SearchQuery searchQuery = new SearchQuery("title=\"systematic review\"", false, false);
        Assertions.assertFalse(searchQuery.isMatch(entry));
    }

    @Test
    public void testGrammarSearchFullEntry() {
        BibEntry entry = new BibEntry();
        entry.setField(TITLE, "systematic review");
        SearchQuery searchQuery = new SearchQuery("title=\"systematic review\"", false, false);
        Assertions.assertTrue(searchQuery.isMatch(entry));
    }

    @Test
    public void testSearchingForOpenBraketInBooktitle() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        e.setField(BOOKTITLE, "Super Conference (SC)");
        SearchQuery searchQuery = new SearchQuery("booktitle=\"(\"", false, false);
        Assertions.assertTrue(searchQuery.isMatch(e));
    }

    @Test
    public void testSearchMatchesSingleKeywordNotPart() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        e.setField("keywords", "banana, pineapple, orange");
        SearchQuery searchQuery = new SearchQuery("anykeyword==apple", false, false);
        Assertions.assertFalse(searchQuery.isMatch(e));
    }

    @Test
    public void testSearchMatchesSingleKeyword() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        e.setField("keywords", "banana, pineapple, orange");
        SearchQuery searchQuery = new SearchQuery("anykeyword==pineapple", false, false);
        Assertions.assertTrue(searchQuery.isMatch(e));
    }

    @Test
    public void testSearchAllFields() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        e.setField("title", "Fruity features");
        e.setField("keywords", "banana, pineapple, orange");
        SearchQuery searchQuery = new SearchQuery("anyfield==\"fruity features\"", false, false);
        Assertions.assertTrue(searchQuery.isMatch(e));
    }

    @Test
    public void testSearchAllFieldsNotForSpecificField() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        e.setField("title", "Fruity features");
        e.setField("keywords", "banana, pineapple, orange");
        SearchQuery searchQuery = new SearchQuery("anyfield=fruit and keywords!=banana", false, false);
        Assertions.assertFalse(searchQuery.isMatch(e));
    }

    @Test
    public void testSearchAllFieldsAndSpecificField() {
        BibEntry e = new BibEntry(BibtexEntryTypes.INPROCEEDINGS);
        e.setField("title", "Fruity features");
        e.setField("keywords", "banana, pineapple, orange");
        SearchQuery searchQuery = new SearchQuery("anyfield=fruit and keywords=apple", false, false);
        Assertions.assertTrue(searchQuery.isMatch(e));
    }

    @Test
    public void testIsMatch() {
        BibEntry entry = new BibEntry();
        entry.setType(ARTICLE);
        entry.setField("author", "asdf");
        Assertions.assertFalse(new SearchQuery("qwer", true, true).isMatch(entry));
        Assertions.assertTrue(new SearchQuery("asdf", true, true).isMatch(entry));
        Assertions.assertTrue(new SearchQuery("author=asdf", true, true).isMatch(entry));
    }

    @Test
    public void testIsValidQueryNotAsRegEx() {
        Assertions.assertTrue(new SearchQuery("asdf", true, false).isValid());
    }

    @Test
    public void testIsValidQueryContainsBracketNotAsRegEx() {
        Assertions.assertTrue(new SearchQuery("asdf[", true, false).isValid());
    }

    @Test
    public void testIsNotValidQueryContainsBracketNotAsRegEx() {
        Assertions.assertTrue(new SearchQuery("asdf[", true, true).isValid());
    }

    @Test
    public void testIsValidQueryAsRegEx() {
        Assertions.assertTrue(new SearchQuery("asdf", true, true).isValid());
    }

    @Test
    public void testIsValidQueryWithNumbersAsRegEx() {
        Assertions.assertTrue(new SearchQuery("123", true, true).isValid());
    }

    @Test
    public void testIsValidQueryContainsBracketAsRegEx() {
        Assertions.assertTrue(new SearchQuery("asdf[", true, true).isValid());
    }

    @Test
    public void testIsValidQueryWithEqualSignAsRegEx() {
        Assertions.assertTrue(new SearchQuery("author=asdf", true, true).isValid());
    }

    @Test
    public void testIsValidQueryWithNumbersAndEqualSignAsRegEx() {
        Assertions.assertTrue(new SearchQuery("author=123", true, true).isValid());
    }

    @Test
    public void testIsValidQueryWithEqualSignNotAsRegEx() {
        Assertions.assertTrue(new SearchQuery("author=asdf", true, false).isValid());
    }

    @Test
    public void testIsValidQueryWithNumbersAndEqualSignNotAsRegEx() {
        Assertions.assertTrue(new SearchQuery("author=123", true, false).isValid());
    }

    @Test
    public void isMatchedForNormalAndFieldBasedSearchMixed() {
        BibEntry entry = new BibEntry();
        entry.setType(ARTICLE);
        entry.setField("author", "asdf");
        entry.setField("abstract", "text");
        Assertions.assertTrue(new SearchQuery("text AND author=asdf", true, true).isMatch(entry));
    }

    @Test
    public void testSimpleTerm() {
        String query = "progress";
        SearchQuery result = new SearchQuery(query, false, false);
        Assertions.assertFalse(result.isGrammarBasedSearch());
    }
}

