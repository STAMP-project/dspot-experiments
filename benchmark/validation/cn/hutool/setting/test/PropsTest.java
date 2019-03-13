package cn.hutool.setting.test;


import cn.hutool.setting.dialect.Props;
import org.junit.Assert;
import org.junit.Test;


/**
 * Setting????
 *
 * @author Looly
 */
public class PropsTest {
    @Test
    public void propTest() {
        Props props = new Props("test.properties");
        String user = props.getProperty("user");
        Assert.assertEquals(user, "root");
        String driver = props.getStr("driver");
        Assert.assertEquals(driver, "com.mysql.jdbc.Driver");
    }
}

