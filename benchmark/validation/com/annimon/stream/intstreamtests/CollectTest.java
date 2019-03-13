package com.annimon.stream.intstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class CollectTest {
    @Test
    public void testCollect() {
        String result = IntStream.of(0, 1, 5, 10).collect(Functions.stringBuilderSupplier(), new com.annimon.stream.function.ObjIntConsumer<StringBuilder>() {
            @Override
            public void accept(StringBuilder t, int value) {
                t.append(value);
            }
        }).toString();
        Assert.assertThat(result, CoreMatchers.is("01510"));
    }
}

