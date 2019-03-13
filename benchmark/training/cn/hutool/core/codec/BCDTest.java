package cn.hutool.core.codec;


import org.junit.Assert;
import org.junit.Test;


public class BCDTest {
    @Test
    public void bcdTest() {
        String strForTest = "123456ABCDEF";
        // ?BCD
        byte[] bcd = BCD.strToBcd(strForTest);
        String str = BCD.bcdToStr(bcd);
        // ??BCD
        Assert.assertEquals(strForTest, str);
    }
}

