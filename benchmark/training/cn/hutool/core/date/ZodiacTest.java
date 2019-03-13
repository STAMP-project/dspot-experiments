package cn.hutool.core.date;


import Month.JANUARY;
import org.junit.Assert;
import org.junit.Test;


public class ZodiacTest {
    @Test
    public void getZodiacTest() {
        Assert.assertEquals("???", Zodiac.getZodiac(JANUARY, 19));
        Assert.assertEquals("???", Zodiac.getZodiac(JANUARY, 20));
        Assert.assertEquals("???", Zodiac.getZodiac(6, 17));
    }

    @Test
    public void getChineseZodiacTest() {
        Assert.assertEquals("?", Zodiac.getChineseZodiac(1994));
        Assert.assertEquals("?", Zodiac.getChineseZodiac(2018));
        Assert.assertEquals("?", Zodiac.getChineseZodiac(2019));
    }
}

