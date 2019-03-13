package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsAnalyzeTableTest extends TestCase {
    public void test_0() throws Exception {
        String sql = "analyze table t partition(pt='1') compute statistics";
        Assert.assertEquals("ANALYZE TABLE t PARTITION (pt = '1') COMPUTE STATISTICS", SQLUtils.formatOdps(sql));
    }

    public void test_no_partition() throws Exception {
        String sql = "analyze table t compute statistics";
        Assert.assertEquals("ANALYZE TABLE t COMPUTE STATISTICS", SQLUtils.formatOdps(sql));
    }
}

