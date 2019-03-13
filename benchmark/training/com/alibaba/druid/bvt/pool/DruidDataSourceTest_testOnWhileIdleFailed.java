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
public class DruidDataSourceTest_testOnWhileIdleFailed extends TestCase {
    private DruidDataSource dataSource;

    private AtomicInteger validCount = new AtomicInteger();

    public void test_conn() throws Exception {
        {
            PooledConnection conn = dataSource.getPooledConnection();
            conn.close();
        }
        Assert.assertEquals(1, validCount.get());// createValidate

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(1, dataSource.getCreateCount());
        Assert.assertEquals(0, dataSource.getDiscardCount());
        Assert.assertEquals(1, dataSource.getConnectCount());
        Assert.assertEquals(1, dataSource.getCloseCount());
        Thread.sleep(21);
        {
            PooledConnection conn = dataSource.getPooledConnection();
            conn.close();
        }
        Assert.assertEquals(3, validCount.get());// createValidate

        Assert.assertEquals(1, dataSource.getPoolingCount());
        Assert.assertEquals(2, dataSource.getCreateCount());
        Assert.assertEquals(1, dataSource.getDiscardCount());
        Assert.assertEquals(3, dataSource.getConnectCount());
        Assert.assertEquals(2, dataSource.getCloseCount());
    }
}

