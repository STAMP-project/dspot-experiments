package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class LastActiveTest_0 extends TestCase {
    private DruidDataSource dataSource;

    private Field field;

    public void test_0() throws Exception {
        long t0;
        long t1;
        {
            DruidPooledConnection conn = ((DruidPooledConnection) (dataSource.getConnection()));
            t0 = getLastActiveTime(conn);
            PreparedStatement stmt = conn.prepareStatement("select 1");
            Thread.sleep(2);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Thread.sleep(1000);
        {
            DruidPooledConnection conn = ((DruidPooledConnection) (dataSource.getConnection()));
            t1 = getLastActiveTime(conn);
            PreparedStatement stmt = conn.prepareStatement("select 1");
            Thread.sleep(2);
            stmt.execute();
            stmt.close();
            conn.close();
        }
        Assert.assertNotEquals(t0, t1);
    }
}

