package com.alibaba.druid.bvt.pool.profile;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;


public class ProfileEnableTest extends TestCase {
    private DruidDataSource dataSource;

    public void testDefault() throws Exception {
        for (int i = 0; i < 10; ++i) {
            DruidPooledConnection conn = dataSource.getConnection();
            System.out.println(("physicalConnectNanoSpan : " + (conn.getPhysicalConnectNanoSpan())));
            System.out.println(("physicalConnectionUsedCount : " + (conn.getPhysicalConnectionUsedCount())));
            System.out.println(("connectNotEmptyWaitNanos : " + (conn.getConnectNotEmptyWaitNanos())));
            conn.close();
        }
    }
}

