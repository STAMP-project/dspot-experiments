package com.navercorp.pinpoint.bootstrap.config;


import org.junit.Assert;
import org.junit.Test;


public class ExcludeMethodFilterTest {
    @Test
    public void testFilter() throws Exception {
        Filter<String> filter = new ExcludeMethodFilter("get,post");
        boolean getResult = filter.filter("GET");
        Assert.assertTrue(getResult);
        boolean postResult = filter.filter("POST");
        Assert.assertTrue(postResult);
    }

    @Test
    public void testUnFilter() throws Exception {
        Filter<String> filter = new ExcludeMethodFilter("get,post");
        boolean putResult = filter.filter("PUT");
        Assert.assertFalse(putResult);
        boolean headResult = filter.filter("HEAD");
        Assert.assertFalse(headResult);
    }
}

