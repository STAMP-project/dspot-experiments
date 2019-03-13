package io.aeron;


import BufferBuilderUtil.MAX_CAPACITY;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class BufferBuilderUtilTest {
    @Test
    public void shouldFindMaxCapacityWhenRequested() {
        Assert.assertThat(BufferBuilderUtil.findSuitableCapacity(0, MAX_CAPACITY), CoreMatchers.is(MAX_CAPACITY));
    }
}

