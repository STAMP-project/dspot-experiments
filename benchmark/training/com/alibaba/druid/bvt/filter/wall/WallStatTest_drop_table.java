package com.alibaba.druid.bvt.filter.wall;


import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.WallTableStat;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import junit.framework.TestCase;
import org.junit.Assert;


public class WallStatTest_drop_table extends TestCase {
    private String sql = "drop table t";

    public void testMySql() throws Exception {
        WallProvider provider = new MySqlWallProvider();
        provider.getConfig().setDropTableAllow(true);
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDropCount());
    }

    public void testOracle() throws Exception {
        WallProvider provider = new OracleWallProvider();
        provider.getConfig().setDropTableAllow(true);
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDropCount());
    }

    public void testPG() throws Exception {
        WallProvider provider = new PGWallProvider();
        provider.getConfig().setDropTableAllow(true);
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDropCount());
    }

    public void testSQLServer() throws Exception {
        WallProvider provider = new SQLServerWallProvider();
        provider.getConfig().setDropTableAllow(true);
        Assert.assertTrue(provider.checkValid(sql));
        WallTableStat tableStat = provider.getTableStat("t");
        Assert.assertEquals(1, tableStat.getDropCount());
    }
}

