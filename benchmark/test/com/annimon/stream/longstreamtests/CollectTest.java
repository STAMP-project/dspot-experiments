package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class CollectTest {
    @Test
    public void testCollect() {
        String result = LongStream.of(10, 20, 30).collect(Functions.stringBuilderSupplier(), new com.annimon.stream.function.ObjLongConsumer<StringBuilder>() {
            @Override
            public void accept(StringBuilder t, long value) {
                t.append(value);
            }
        }).toString();
        Assert.assertThat(result, Matchers.is("102030"));
    }
}

