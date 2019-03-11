package io.dropwizard.metrics;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class LongAdderFactoryTest {
    @Test
    public void test() {
        Assume.assumeThat(Runtime.class.getPackage().getImplementationVersion(), CoreMatchers.startsWith("1.7"));
        LongAdder longAdder = LongAdderFactory.create();
        Assert.assertThat(longAdder, CoreMatchers.instanceOf(UnsafeLongAdderImpl.class));
        Assert.assertThat(longAdder, CoreMatchers.instanceOf(UnsafeStriped64.class));
    }
}

