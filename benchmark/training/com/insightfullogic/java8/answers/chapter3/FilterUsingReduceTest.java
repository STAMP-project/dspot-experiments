package com.insightfullogic.java8.answers.chapter3;


import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import org.junit.Test;


public class FilterUsingReduceTest {
    @Test
    public void emptyList() {
        assertFiltered(( x) -> false, Collections.<Object>emptyList(), Collections.<Object>emptyList());
    }

    @Test
    public void trueReturnsEverything() {
        assertFiltered((Integer x) -> true, Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void falseRemovesEverything() {
        assertFiltered((Integer x) -> false, Arrays.asList(1, 2, 3), Arrays.asList());
    }

    @Test
    public void filterPartOfList() {
        assertFiltered((Integer x) -> x > 2, Arrays.asList(1, 2, 3), Arrays.asList(3));
    }
}

