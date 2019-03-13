package com.baeldung.java9.language;


import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class Java9ObjectsAPIUnitTest {
    @Test
    public void givenNullObject_whenRequireNonNullElse_thenElse() {
        List<String> aList = java.util.Objects.<List>requireNonNullElse(aMethodReturningNullList(), Collections.EMPTY_LIST);
        MatcherAssert.assertThat(aList, is(Collections.EMPTY_LIST));
    }

    @Test
    public void givenObject_whenRequireNonNullElse_thenObject() {
        List<String> aList = java.util.Objects.<List>requireNonNullElse(aMethodReturningNonNullList(), Collections.EMPTY_LIST);
        MatcherAssert.assertThat(aList, is(List.of(List, "item1", "item2")));
    }

    @Test(expected = NullPointerException.class)
    public void givenNull_whenRequireNonNullElse_thenException() {
        <List>requireNonNullElse(null, null);
    }

    @Test
    public void givenObject_whenRequireNonNullElseGet_thenObject() {
        List<String> aList = <List>requireNonNullElseGet(null, List::of);
        MatcherAssert.assertThat(aList, is(List.of(List)));
    }

    @Test
    public void givenNumber_whenInvokeCheckIndex_thenNumber() {
        int length = 5;
        MatcherAssert.assertThat(checkIndex(4, length), is(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void givenOutOfRangeNumber_whenInvokeCheckIndex_thenException() {
        int length = 5;
        checkIndex(5, length);
    }

    @Test
    public void givenSubRange_whenCheckFromToIndex_thenNumber() {
        int length = 6;
        MatcherAssert.assertThat(checkFromToIndex(2, length, length), is(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void givenInvalidSubRange_whenCheckFromToIndex_thenException() {
        int length = 6;
        checkFromToIndex(2, 7, length);
    }

    @Test
    public void givenSubRange_whenCheckFromIndexSize_thenNumber() {
        int length = 6;
        MatcherAssert.assertThat(checkFromIndexSize(2, 3, length), is(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void givenInvalidSubRange_whenCheckFromIndexSize_thenException() {
        int length = 6;
        checkFromIndexSize(2, 6, length);
    }
}

