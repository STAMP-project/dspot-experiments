package org.jabref.model.entry;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class KeywordListTest {
    private KeywordList keywords;

    @Test
    public void parseEmptyStringReturnsEmptyList() throws Exception {
        Assertions.assertEquals(new KeywordList(), KeywordList.parse("", ','));
    }

    @Test
    public void parseOneWordReturnsOneKeyword() throws Exception {
        Assertions.assertEquals(new KeywordList("keywordOne"), KeywordList.parse("keywordOne", ','));
    }

    @Test
    public void parseTwoWordReturnsTwoKeywords() throws Exception {
        Assertions.assertEquals(new KeywordList("keywordOne", "keywordTwo"), KeywordList.parse("keywordOne, keywordTwo", ','));
    }

    @Test
    public void parseTwoWordReturnsTwoKeywordsWithoutSpace() throws Exception {
        Assertions.assertEquals(new KeywordList("keywordOne", "keywordTwo"), KeywordList.parse("keywordOne,keywordTwo", ','));
    }

    @Test
    public void parseTwoWordReturnsTwoKeywordsWithDifferentDelimiter() throws Exception {
        Assertions.assertEquals(new KeywordList("keywordOne", "keywordTwo"), KeywordList.parse("keywordOne| keywordTwo", '|'));
    }

    @Test
    public void parseWordsWithWhitespaceReturnsOneKeyword() throws Exception {
        Assertions.assertEquals(new KeywordList("keyword and one"), KeywordList.parse("keyword and one", ','));
    }

    @Test
    public void parseWordsWithWhitespaceAndCommaReturnsTwoKeyword() throws Exception {
        Assertions.assertEquals(new KeywordList("keyword and one", "and two"), KeywordList.parse("keyword and one, and two", ','));
    }

    @Test
    public void parseIgnoresDuplicates() throws Exception {
        Assertions.assertEquals(new KeywordList("keywordOne", "keywordTwo"), KeywordList.parse("keywordOne, keywordTwo, keywordOne", ','));
    }

    @Test
    public void parseWordsWithBracketsReturnsOneKeyword() throws Exception {
        Assertions.assertEquals(new KeywordList("[a] keyword"), KeywordList.parse("[a] keyword", ','));
    }

    @Test
    public void asStringAddsSpaceAfterDelimiter() throws Exception {
        Assertions.assertEquals("keywordOne, keywordTwo", keywords.getAsString(','));
    }

    @Test
    public void parseHierarchicalChain() throws Exception {
        Keyword expected = Keyword.of("Parent", "Node", "Child");
        Assertions.assertEquals(new KeywordList(expected), KeywordList.parse("Parent > Node > Child", ',', '>'));
    }

    @Test
    public void parseTwoHierarchicalChains() throws Exception {
        Keyword expectedOne = Keyword.of("Parent1", "Node1", "Child1");
        Keyword expectedTwo = Keyword.of("Parent2", "Node2", "Child2");
        Assertions.assertEquals(new KeywordList(expectedOne, expectedTwo), KeywordList.parse("Parent1 > Node1 > Child1, Parent2 > Node2 > Child2", ',', '>'));
    }
}

