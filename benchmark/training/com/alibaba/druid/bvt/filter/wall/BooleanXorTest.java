package com.alibaba.druid.bvt.filter.wall;


import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanXorTest extends TestCase {
    public void test_false() throws Exception {
        Assert.assertFalse(// 
        WallUtils.isValidateMySql("SELECT * from t where id = 1 XOR id = 2"));// 

    }

    public void test_true() throws Exception {
        WallConfig config = new WallConfig();
        config.setConditionOpXorAllow(true);
        Assert.assertTrue(// 
        WallUtils.isValidateMySql("SELECT * from t where id = 1 XOR id = 2", config));// 

    }
}

