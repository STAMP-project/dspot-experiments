package cn.hutool.setting.test;


import cn.hutool.setting.Setting;
import org.junit.Assert;
import org.junit.Test;


/**
 * Setting????
 *
 * @author Looly
 */
public class SettingTest {
    @Test
    public void settingTest() {
        Setting setting = new Setting("test.setting", true);
        String driver = setting.getByGroup("driver", "demo");
        Assert.assertEquals("com.mysql.jdbc.Driver", driver);
        // ???????
        String user = setting.getByGroup("user", "demo");
        Assert.assertEquals("rootcom.mysql.jdbc.Driver", user);
        // ???????
        String user2 = setting.getByGroup("user2", "demo");
        Assert.assertEquals("rootcom.mysql.jdbc.Driver", user2);
        // ?????
        String value = setting.getStr("keyNotExist", "defaultTest");
        Assert.assertEquals("defaultTest", value);
    }

    @Test
    public void settingTestForCustom() {
        Setting setting = new Setting();
        setting.put("group1", "user", "root");
        setting.put("group2", "user", "root2");
        setting.put("group3", "user", "root3");
        setting.set("user", "root4");
        Assert.assertEquals("root", setting.getByGroup("user", "group1"));
        Assert.assertEquals("root2", setting.getByGroup("user", "group2"));
        Assert.assertEquals("root3", setting.getByGroup("user", "group3"));
        Assert.assertEquals("root4", setting.get("user"));
    }
}

