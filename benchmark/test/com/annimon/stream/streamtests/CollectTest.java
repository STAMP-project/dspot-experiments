package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class CollectTest {
    @Test
    public void testCollectWithCollector() {
        String text = Stream.range(0, 10).map(Functions.<Integer>convertToString()).collect(Functions.joiningCollector());
        Assert.assertEquals("0123456789", text);
    }

    @Test
    public void testCollectWithSupplierAndAccumulator() {
        String text = Stream.of("a", "b", "c", "def", "", "g").collect(Functions.stringBuilderSupplier(), Functions.joiningAccumulator()).toString();
        Assert.assertEquals("abcdefg", text);
    }

    @Test
    public void testCollect123() {
        String string123 = Stream.of("1", "2", "3").collect(new com.annimon.stream.function.Supplier<StringBuilder>() {
            @Override
            public StringBuilder get() {
                return new StringBuilder();
            }
        }, new com.annimon.stream.function.BiConsumer<StringBuilder, String>() {
            @Override
            public void accept(StringBuilder value1, String value2) {
                value1.append(value2);
            }
        }).toString();
        Assert.assertEquals("123", string123);
    }
}

