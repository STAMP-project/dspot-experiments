package com.baeldung.eclipsecollections;


import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.junit.Assert;
import org.junit.Test;


public class AnySatisfyPatternUnitTest {
    MutableList<Integer> list;

    @Test
    public void whenAnySatisfiesCondition_thenCorrect() {
        boolean result = list.anySatisfy(Predicates.greaterThan(30));
        Assert.assertTrue(result);
    }
}

