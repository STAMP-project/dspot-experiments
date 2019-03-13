package com.taobao.metamorphosis.server.filter;


import com.taobao.metamorphosis.consumer.ConsumerMessageFilter;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerFilterManagerUnitTest {
    private ConsumerFilterManager consumerFilterManager;

    @Test
    public void testGetNullFilter() {
        Assert.assertNull(this.consumerFilterManager.findFilter("test", "not-exists"));
    }

    @Test
    public void testGetNullFilterWithClassLoader() {
        this.consumerFilterManager.setFilterClassLoader(Thread.currentThread().getContextClassLoader());
        Assert.assertNull(this.consumerFilterManager.findFilter("test", "not-exists"));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFilterNotFound() {
        this.consumerFilterManager.setFilterClassLoader(Thread.currentThread().getContextClassLoader());
        Assert.assertNull(this.consumerFilterManager.findFilter("test", "test-group1"));
    }

    @Test
    public void testGetFilter() {
        this.consumerFilterManager.setFilterClassLoader(Thread.currentThread().getContextClassLoader());
        ConsumerMessageFilter filter = this.consumerFilterManager.findFilter("test", "test-group2");
        Assert.assertNotNull(filter);
        Assert.assertSame(filter, this.consumerFilterManager.findFilter("test", "test-group2"));
        Assert.assertTrue(filter.accept(null, null));
        Assert.assertTrue(filter.accept(null, null));
    }
}

