package cn.hutool.core.convert;


import org.junit.Assert;
import org.junit.Test;


public class NumberChineseFormaterTest {
    @Test
    public void formatTest() {
        String f1 = NumberChineseFormater.format(10889.72356, false);
        Assert.assertEquals("???????????", f1);
        f1 = NumberChineseFormater.format(12653, false);
        Assert.assertEquals("?????????", f1);
        f1 = NumberChineseFormater.format(215.6387, false);
        Assert.assertEquals("????????", f1);
        f1 = NumberChineseFormater.format(1024, false);
        Assert.assertEquals("??????", f1);
        f1 = NumberChineseFormater.format(100350089, false);
        Assert.assertEquals("??????????", f1);
        f1 = NumberChineseFormater.format(1200, false);
        Assert.assertEquals("????", f1);
        f1 = NumberChineseFormater.format(12, false);
        Assert.assertEquals("???", f1);
        f1 = NumberChineseFormater.format(0.05, false);
        Assert.assertEquals("????", f1);
    }

    @Test
    public void formatTest2() {
        String f1 = NumberChineseFormater.format((-0.3), false, false);
        Assert.assertEquals("????", f1);
    }

    @Test
    public void formatTranditionalTest() {
        String f1 = NumberChineseFormater.format(10889.72356, true);
        Assert.assertEquals("???????????", f1);
        f1 = NumberChineseFormater.format(12653, true);
        Assert.assertEquals("?????????", f1);
        f1 = NumberChineseFormater.format(215.6387, true);
        Assert.assertEquals("????????", f1);
        f1 = NumberChineseFormater.format(1024, true);
        Assert.assertEquals("??????", f1);
        f1 = NumberChineseFormater.format(100350089, true);
        Assert.assertEquals("??????????", f1);
        f1 = NumberChineseFormater.format(1200, true);
        Assert.assertEquals("????", f1);
        f1 = NumberChineseFormater.format(12, true);
        Assert.assertEquals("???", f1);
        f1 = NumberChineseFormater.format(0.05, true);
        Assert.assertEquals("????", f1);
    }

    @Test
    public void digitToChineseTest() {
        String digitToChinese = Convert.digitToChinese(1.241241241242112E13);
        Assert.assertEquals("????????????????????????????????", digitToChinese);
        String digitToChinese2 = Convert.digitToChinese(1.2412412412421E13);
        Assert.assertEquals("?????????????????????????????", digitToChinese2);
        String digitToChinese3 = Convert.digitToChinese(2421.02);
        Assert.assertEquals("???????????", digitToChinese3);
    }
}

