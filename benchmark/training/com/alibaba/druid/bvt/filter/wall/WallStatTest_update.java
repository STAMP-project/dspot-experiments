package com.alibaba.druid.bvt.filter.wall;


import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import junit.framework.TestCase;
import org.junit.Assert;


public class WallStatTest_update extends TestCase {
    private String sql = "update t set fname = ? where fid = ?";

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getUpdateCount());
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getUpdateCount());
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getUpdateCount());
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getUpdateCount());
    }
}

