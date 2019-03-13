package com.insightfullogic.java8.exercises.chapter3;


import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import org.junit.Test;


public class MapUsingReduceTest {
    @Test
    public void emptyList() {
        assertMapped(Function.<Object>identity(), Collections.<Object>emptyList(), Collections.<Object>emptyList());
    }

    @Test
    public void identityMapsToItself() {
        assertMapped((Integer x) -> x, Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void incrementingNumbers() {
        assertMapped((Integer x) -> x + 2, Arrays.asList(1, 2, 3), Arrays.asList(3, 4, 5));
    }
}

