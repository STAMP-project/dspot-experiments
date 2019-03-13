package com.alibaba.druid.bvt.support.hibernate;


import com.alibaba.druid.support.hibernate.DruidConnectionProvider;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidConnectionProviderTest extends TestCase {
    private DruidConnectionProvider provider;

    public void test_hibernate() throws Exception {
        Connection conn = provider.getConnection();
        Assert.assertFalse(conn.isClosed());
        provider.closeConnection(conn);
        Assert.assertTrue(conn.isClosed());
    }
}

