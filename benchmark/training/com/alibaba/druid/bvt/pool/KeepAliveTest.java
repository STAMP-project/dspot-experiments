package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


/**
 * Created by wenshao on 21/01/2017.
 */
public class KeepAliveTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_keepAlive() throws Exception {
        dataSource.init();
        for (int i = 0; i < 1000; ++i) {
            if ((dataSource.getMinIdle()) == (dataSource.getPoolingCount())) {
                break;
            }
            Thread.sleep((10 * 1));
        }
        TestCase.assertEquals(dataSource.getMinIdle(), dataSource.getPoolingCount());
        TestCase.assertTrue(dataSource.isKeepAlive());
        Connection[] connections = new Connection[dataSource.getMaxActive()];
        for (int i = 0; i < (connections.length); ++i) {
            connections[i] = dataSource.getConnection();
        }
        for (int i = 0; i < (connections.length); ++i) {
            connections[i].close();
        }
        // assertEquals(dataSource.getMaxActive(), dataSource.getPoolingCount());
        // Thread.sleep(1000 * 1000);
    }
}

