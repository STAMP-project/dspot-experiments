package com.baeldung.eclipsecollections;


import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.junit.Test;


public class SelectPatternUnitTest {
    MutableList<Integer> list;

    @Test
    public void givenListwhenSelect_thenCorrect() {
        MutableList<Integer> greaterThanThirty = list.select(Predicates.greaterThan(30)).sortThis();
        Assertions.assertThat(greaterThanThirty).containsExactly(31, 38, 41);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void givenListwhenSelectUsingLambda_thenCorrect() {
        MutableList<Integer> greaterThanThirty = selectUsingLambda();
        Assertions.assertThat(greaterThanThirty).containsExactly(31, 38, 41);
    }
}

