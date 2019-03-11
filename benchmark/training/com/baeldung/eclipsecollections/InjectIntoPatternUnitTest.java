package com.baeldung.eclipsecollections;


import Lists.mutable;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class InjectIntoPatternUnitTest {
    @Test
    public void whenInjectInto_thenCorrect() {
        List<Integer> list = mutable.of(1, 2, 3, 4);
        int result = 5;
        for (int i = 0; i < (list.size()); i++) {
            Integer v = list.get(i);
            result = result + (v.intValue());
        }
        Assert.assertEquals(15, result);
    }
}

