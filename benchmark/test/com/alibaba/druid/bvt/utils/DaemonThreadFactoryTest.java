package com.alibaba.druid.bvt.utils;


import com.alibaba.druid.util.DaemonThreadFactory;
import junit.framework.TestCase;
import org.junit.Assert;


public class DaemonThreadFactoryTest extends TestCase {
    public void test_0() throws Exception {
        Runnable task = new Runnable() {
            public void run() {
            }
        };
        DaemonThreadFactory factory = new DaemonThreadFactory("test");
        Assert.assertEquals("[test-1]", factory.newThread(task).getName());
        Assert.assertEquals("[test-2]", factory.newThread(task).getName());
    }
}

