package com.baeldung.java9.language.stream;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;


@RunWith(JUnitPlatform.class)
public class CollectionFilterUnitTest {
    private static final Collection<Integer> BASE_INTEGER_COLLECTION = Arrays.asList(9, 12, 55, 56, 101, 115, 8002, 223, 2668, 19, 8);

    private static final Map<Integer, List<Integer>> EXPECTED_EVEN_FILTERED_AFTER_GROUPING_MAP = CollectionFilterUnitTest.createExpectedFilterAfterGroupingMap();

    private static final Map<Integer, List<Integer>> EXPECTED_EVEN_FILTERED_BEFORE_GROUPING_MAP = CollectionFilterUnitTest.createExpectedFilterBeforeGroupingMap();

    @Test
    public void givenAStringCollection_whenFilteringFourLetterWords_thenObtainTheFilteredCollection() {
        Map<Integer, List<Integer>> filteredAfterGroupingMap = StreamsGroupingCollectionFilter.findEvenNumbersAfterGroupingByQuantityOfDigits(CollectionFilterUnitTest.BASE_INTEGER_COLLECTION);
        Map<Integer, List<Integer>> filteredBeforeGroupingMap = StreamsGroupingCollectionFilter.findEvenNumbersBeforeGroupingByQuantityOfDigits(CollectionFilterUnitTest.BASE_INTEGER_COLLECTION);
        assertThat(filteredAfterGroupingMap).containsAllEntriesOf(CollectionFilterUnitTest.EXPECTED_EVEN_FILTERED_AFTER_GROUPING_MAP);
        assertThat(filteredBeforeGroupingMap).doesNotContainKey(3).containsAllEntriesOf(CollectionFilterUnitTest.EXPECTED_EVEN_FILTERED_BEFORE_GROUPING_MAP);
    }
}

