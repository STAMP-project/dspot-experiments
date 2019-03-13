package com.networknt.server;


import com.networknt.config.Config;
import com.networknt.status.Status;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class TestMergeStatusConfig extends TestCase {
    private Config config = null;

    final String homeDir = System.getProperty("user.home");

    @Test
    public void testAppStatus() {
        config.clear();
        Server.mergeStatusConfig();
        Status status = new Status("ERR99999");
        Assert.assertEquals(404, status.getStatusCode());
    }

    @Test
    public void testDuplicateStatus() {
        config.clear();
        try {
            Server.mergeStatusConfig();
            // second try to make sure duplication status appear
            Server.mergeStatusConfig();
            TestCase.fail();
        } catch (RuntimeException expected) {
            // pass
        }
    }
}

