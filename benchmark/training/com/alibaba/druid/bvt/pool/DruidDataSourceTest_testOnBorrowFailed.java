package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.PooledConnection;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_testOnBorrowFailed extends TestCase {
    private DruidDataSource dataSource;

    private AtomicInteger validCount = new AtomicInteger();

    public void test_conn() throws Exception {
        PooledConnection conn = dataSource.getPooledConnection();
        conn.close();
        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(2, dataSource.getCreateCount());
        Assert.assertEquals(1, dataSource.getDiscardCount());
        Assert.assertEquals(2, dataSource.getConnectCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
    }
}

