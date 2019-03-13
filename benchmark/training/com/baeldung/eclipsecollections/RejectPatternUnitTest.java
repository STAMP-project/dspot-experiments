package com.baeldung.eclipsecollections;


import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.junit.Test;


public class RejectPatternUnitTest {
    MutableList<Integer> list;

    MutableList<Integer> expectedList;

    @Test
    public void whenReject_thenCorrect() {
        MutableList<Integer> notGreaterThanThirty = list.reject(Predicates.greaterThan(30)).sortThis();
        Assertions.assertThat(notGreaterThanThirty).containsExactlyElementsOf(this.expectedList);
    }
}

