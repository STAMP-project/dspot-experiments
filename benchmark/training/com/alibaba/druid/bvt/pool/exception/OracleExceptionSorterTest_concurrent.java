package com.alibaba.druid.bvt.pool.exception;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.junit.Assert;


public class OracleExceptionSorterTest_concurrent extends TestCase {
    private DruidDataSource dataSource;

    public void test_connect() throws Exception {
        final CountDownLatch latch_0 = new CountDownLatch(1);
        Thread errorThread = new Thread() {
            public void run() {
                try {
                    Connection conn = dataSource.getConnection();
                    latch_0.countDown();
                    MockConnection mockConn = conn.unwrap(MockConnection.class);
                    Assert.assertNotNull(mockConn);
                    SQLException exception = new SQLException("xx", "xxx", 28);
                    mockConn.setError(exception);
                    try {
                        conn.createStatement();
                    } catch (SQLException ex) {
                        // ex.printStackTrace();
                    }
                    conn.close();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        };
        errorThread.start();
        final CountDownLatch workLatch = new CountDownLatch(2);
        final CountDownLatch workCompleteLatch = new CountDownLatch(2);
        for (int i = 0; i < 2; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        for (int i = 0; i < 1000; ++i) {
                            workLatch.countDown();
                            Connection conn = dataSource.getConnection();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        workCompleteLatch.countDown();
                    }
                }
            };
            thread.start();
        }
        workLatch.await();
        latch_0.countDown();
        workCompleteLatch.await();
        Assert.assertEquals(2001, dataSource.getConnectCount());
    }
}

