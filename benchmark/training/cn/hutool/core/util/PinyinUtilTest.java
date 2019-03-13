package cn.hutool.core.util;


import org.junit.Assert;
import org.junit.Test;


public class PinyinUtilTest {
    @Test
    public void getFirstLetterTest() {
        char firstLetter = PinyinUtil.getFirstLetter('?');
        Assert.assertEquals('n', firstLetter);
        firstLetter = PinyinUtil.getFirstLetter('?');
        Assert.assertEquals('w', firstLetter);
        // firstLetter = PinyinUtil.getFirstLetter('?');
        // Console.log(firstLetter);
        // Assert.assertEquals('y', firstLetter);
    }

    @Test
    public void getAllFirstLetterTest() {
        String allFirstLetter = PinyinUtil.getAllFirstLetter("?????");
        Assert.assertEquals("hdljd", allFirstLetter);
        allFirstLetter = PinyinUtil.getAllFirstLetter("?????");
        Assert.assertEquals("ylzsx", allFirstLetter);
    }

    @Test
    public void getAllFirstLetterTest2() {
        String allFirstLetter = PinyinUtil.getAllFirstLetter("??123");
        Assert.assertEquals("zs123", allFirstLetter);
    }

    @Test
    public void getPinyinTest() {
        String pinYin = PinyinUtil.getPinYin("?????");
        Assert.assertEquals("huidanglingjueding", pinYin);
        pinYin = PinyinUtil.getPinYin("?????");
        Assert.assertEquals("yilanzhongshanxiao", pinYin);
        // pinYin = PinyinUtil.getPinYin("?");
        // Assert.assertEquals("yi", pinYin);
        String cnStr = "??(?????)-?OK?@gitee.com";
        pinYin = PinyinUtil.getPinYin(cnStr);
        Assert.assertEquals("zhongwen(jinyibushuoming)-OK@gitee.com", pinYin);
    }
}

