package com.zendesk.maxwell.replication;


import com.zendesk.maxwell.TestWithNameLogging;
import org.junit.Assert;
import org.junit.Test;


public class MysqlVersionTest extends TestWithNameLogging {
    @Test
    public void testVersionComparison() {
        MysqlVersion version = new MysqlVersion(6, 5);
        Assert.assertTrue(version.atLeast(6, 5));
        Assert.assertTrue(version.atLeast(6, 4));
        Assert.assertTrue(version.atLeast(5, 9));
        Assert.assertFalse(version.atLeast(7, 2));
        Assert.assertFalse(version.atLeast(6, 6));
    }
}

