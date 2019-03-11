package com.baeldung.eclipsecollections;


import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.Test;


public class ZipWithIndexUnitTest {
    MutableList<Pair<String, Integer>> expectedPairs;

    @Test
    public void whenZip_thenCorrect() {
        MutableList<String> cars = FastList.newListWith("Porsche", "Volvo", "Toyota");
        MutableList<Pair<String, Integer>> pairs = cars.zipWithIndex();
        Assertions.assertThat(pairs).containsExactlyElementsOf(this.expectedPairs);
    }
}

