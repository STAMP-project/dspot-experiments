package cn.hutool.setting.test;


import cn.hutool.setting.SettingUtil;
import org.junit.Assert;
import org.junit.Test;


public class SettingUtilTest {
    @Test
    public void getTest() {
        String driver = SettingUtil.get("test").get("demo", "driver");
        Assert.assertEquals("com.mysql.jdbc.Driver", driver);
    }
}

