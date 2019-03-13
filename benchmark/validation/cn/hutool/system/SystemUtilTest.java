package cn.hutool.system;


import org.junit.Assert;
import org.junit.Test;


public class SystemUtilTest {
    @Test
    public void getCurrentPidTest() {
        long pid = SystemUtil.getCurrentPID();
        Assert.assertTrue((pid > 0));
    }
}

