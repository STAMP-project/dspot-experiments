package com.baeldung.eclipsecollections;


import Lists.mutable;
import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.junit.Test;


public class ZipUnitTest {
    MutableList<Pair<String, String>> expectedPairs;

    @Test
    public void whenZip_thenCorrect() {
        MutableList<String> numbers = mutable.with("1", "2", "3", "Ignored");
        MutableList<String> cars = mutable.with("Porsche", "Volvo", "Toyota");
        MutableList<Pair<String, String>> pairs = numbers.zip(cars);
        Assertions.assertThat(pairs).containsExactlyElementsOf(this.expectedPairs);
    }
}

