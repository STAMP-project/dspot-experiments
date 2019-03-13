package com.taobao.yugong.common;


import com.taobao.yugong.common.utils.LikeUtil;
import org.junit.Test;
import org.springframework.util.Assert;


public class LikeUtilTest {
    @Test
    public void testSimple() {
        String table = "yugong_example_test";
        String pattern = "yugong_%_test";
        boolean result = LikeUtil.isMatch(pattern, table);
        Assert.isTrue(result);
        pattern = "yugong_________test";
        result = LikeUtil.isMatch(pattern, table);
        Assert.isTrue(result);
        pattern = "yugong%tat";
        result = LikeUtil.isMatch(pattern, table);
        Assert.isTrue((!result));
    }
}

