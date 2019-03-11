package com.baeldung.eclipsecollections;


import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.junit.Test;


public class DetectPatternUnitTest {
    MutableList<Integer> list;

    @Test
    public void whenDetect_thenCorrect() {
        Integer result = list.detect(Predicates.greaterThan(30));
        Assertions.assertThat(result).isEqualTo(41);
    }
}

