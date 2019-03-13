package com.stacknet.paramsearch;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestUtils {
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    @Test
    public void testPath() {
        String file = "manual_index/test.csv";
        String p = TestUtils.getResourceFileAbsolutePath(file);
        Assert.assertTrue(p.endsWith(file));
    }
}

