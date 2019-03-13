package com.alibaba.druid.bvt.filter.wall;


import com.alibaba.druid.wall.WallSQLException;
import junit.framework.TestCase;
import org.junit.Assert;


public class WallSQLExceptionTest extends TestCase {
    public void test_wall() throws Exception {
        WallSQLException ex = new WallSQLException("", new RuntimeException());
        Assert.assertEquals("", ex.getMessage());
        Assert.assertNotNull(ex.getCause());
    }
}

