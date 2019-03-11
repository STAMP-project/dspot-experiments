package com.baeldung.eclipsecollections;


import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.junit.Assert;
import org.junit.Test;


public class AllSatisfyPatternUnitTest {
    MutableList<Integer> list;

    @Test
    public void whenAnySatisfiesCondition_thenCorrect() {
        boolean result = list.allSatisfy(Predicates.greaterThan(0));
        Assert.assertTrue(result);
    }
}

