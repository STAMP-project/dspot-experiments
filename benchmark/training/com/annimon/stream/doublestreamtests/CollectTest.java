package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class CollectTest {
    @Test
    public void testCollect() {
        String result = DoubleStream.of(1.0, 2.0, 3.0).collect(Functions.stringBuilderSupplier(), new com.annimon.stream.function.ObjDoubleConsumer<StringBuilder>() {
            @Override
            public void accept(StringBuilder t, double value) {
                t.append(value);
            }
        }).toString();
        Assert.assertThat(result, Matchers.is("1.02.03.0"));
    }
}

