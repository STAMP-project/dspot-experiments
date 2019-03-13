package com.alibaba.druid.pvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.util.concurrent.ScheduledExecutorService;
import junit.framework.TestCase;


public class Large10KTest extends TestCase {
    private DruidDataSource[] dataSources;

    private ScheduledExecutorService scheduler;

    public void test_large() throws Exception {
        Connection[] connections = new Connection[(dataSources.length) * 8];
        for (int i = 0; i < (dataSources.length); ++i) {
            for (int j = 0; j < 8; ++j) {
                connections[((i * 8) + j)] = dataSources[i].getConnection();
            }
        }
        for (int i = 0; i < (dataSources.length); ++i) {
            for (int j = 0; j < 8; ++j) {
                connections[((i * 8) + j)].close();
            }
        }
    }
}

