package org.apache.commons.lang3;


import org.junit.Assert;
import org.junit.Test;


public class AmplJavaVersionTest {
    @Test(timeout = 10000)
    public void testGetJavaVersion() throws Exception {
        JavaVersion o_testGetJavaVersion__1 = JavaVersion.get("0.9");
        Assert.assertEquals("0.9", ((JavaVersion) (o_testGetJavaVersion__1)).toString());
        JavaVersion o_testGetJavaVersion__2 = JavaVersion.get("1.1");
        Assert.assertEquals("1.1", ((JavaVersion) (o_testGetJavaVersion__2)).toString());
        JavaVersion o_testGetJavaVersion__3 = JavaVersion.get("1.2");
        Assert.assertEquals("1.2", ((JavaVersion) (o_testGetJavaVersion__3)).toString());
        JavaVersion o_testGetJavaVersion__4 = JavaVersion.get("1.3");
        Assert.assertEquals("1.3", ((JavaVersion) (o_testGetJavaVersion__4)).toString());
        JavaVersion o_testGetJavaVersion__5 = JavaVersion.get("1.4");
        Assert.assertEquals("1.4", ((JavaVersion) (o_testGetJavaVersion__5)).toString());
        JavaVersion o_testGetJavaVersion__6 = JavaVersion.get("1.5");
        Assert.assertEquals("1.5", ((JavaVersion) (o_testGetJavaVersion__6)).toString());
        JavaVersion o_testGetJavaVersion__7 = JavaVersion.get("1.6");
        Assert.assertEquals("1.6", ((JavaVersion) (o_testGetJavaVersion__7)).toString());
        JavaVersion o_testGetJavaVersion__8 = JavaVersion.get("1.7");
        Assert.assertEquals("1.7", ((JavaVersion) (o_testGetJavaVersion__8)).toString());
        JavaVersion o_testGetJavaVersion__9 = JavaVersion.get("1.8");
        Assert.assertEquals("1.8", ((JavaVersion) (o_testGetJavaVersion__9)).toString());
        JavaVersion o_testGetJavaVersion__10 = JavaVersion.get("9");
        Assert.assertEquals("9", ((JavaVersion) (o_testGetJavaVersion__10)).toString());
        JavaVersion o_testGetJavaVersion__11 = JavaVersion.get("10");
        Assert.assertEquals("10", ((JavaVersion) (o_testGetJavaVersion__11)).toString());
        JavaVersion o_testGetJavaVersion__12 = JavaVersion.get("1.10");
        Assert.assertEquals("1.8", ((JavaVersion) (o_testGetJavaVersion__12)).toString());
        JavaVersion o_testGetJavaVersion__13 = JavaVersion.get("1.5");
        Assert.assertEquals("1.5", ((JavaVersion) (o_testGetJavaVersion__13)).toString());
        JavaVersion o_testGetJavaVersion__14 = JavaVersion.getJavaVersion("1.5");
        Assert.assertEquals("1.5", ((JavaVersion) (o_testGetJavaVersion__14)).toString());
        JavaVersion o_testGetJavaVersion__15 = JavaVersion.get("11");
        Assert.assertEquals("1.8", ((JavaVersion) (o_testGetJavaVersion__15)).toString());
        Assert.assertEquals("0.9", ((JavaVersion) (o_testGetJavaVersion__1)).toString());
        Assert.assertEquals("1.1", ((JavaVersion) (o_testGetJavaVersion__2)).toString());
        Assert.assertEquals("1.2", ((JavaVersion) (o_testGetJavaVersion__3)).toString());
        Assert.assertEquals("1.3", ((JavaVersion) (o_testGetJavaVersion__4)).toString());
        Assert.assertEquals("1.4", ((JavaVersion) (o_testGetJavaVersion__5)).toString());
        Assert.assertEquals("1.5", ((JavaVersion) (o_testGetJavaVersion__6)).toString());
        Assert.assertEquals("1.6", ((JavaVersion) (o_testGetJavaVersion__7)).toString());
        Assert.assertEquals("1.7", ((JavaVersion) (o_testGetJavaVersion__8)).toString());
        Assert.assertEquals("1.8", ((JavaVersion) (o_testGetJavaVersion__9)).toString());
        Assert.assertEquals("9", ((JavaVersion) (o_testGetJavaVersion__10)).toString());
        Assert.assertEquals("10", ((JavaVersion) (o_testGetJavaVersion__11)).toString());
        Assert.assertEquals("1.8", ((JavaVersion) (o_testGetJavaVersion__12)).toString());
        Assert.assertEquals("1.5", ((JavaVersion) (o_testGetJavaVersion__13)).toString());
        Assert.assertEquals("1.5", ((JavaVersion) (o_testGetJavaVersion__14)).toString());
    }
}

