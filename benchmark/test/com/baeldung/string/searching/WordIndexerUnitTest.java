package com.baeldung.string.searching;


import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class WordIndexerUnitTest {
    String theString;

    WordIndexer wordIndexer;

    @Test
    public void givenWord_whenSearching_thenFindAllIndexedLocations() {
        List<Integer> expectedResult = Arrays.asList(7, 122, 130, 221, 438);
        List<Integer> actualResult = wordIndexer.findWord(theString, "or");
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenWordWithNoRepeatCharacters_whenImprovedSearching_thenFindAllIndexedLocations() {
        List<Integer> expectedResult = Arrays.asList(7, 122, 130, 221, 438);
        List<Integer> actualResult = wordIndexer.findWordUpgrade(theString, "or");
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void givenWord_whenSearching_thenFindAtEndOfString() {
        List<Integer> expectedResult = Arrays.asList(480);
        List<Integer> actualResult = wordIndexer.findWordUpgrade(theString, "come,");
        Assertions.assertEquals(expectedResult, actualResult);
    }
}

