package cn.myperf4j.base.test;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/17
 */
public class NumFormatUtilsTest {
    @Test
    public void test() {
        Assert.assertEquals("10011.22", getFormatStr(10011.22222));
        Assert.assertEquals("10011.22", getFormatStr(10011.22));
        Assert.assertEquals("1.22", getFormatStr(1.2222));
        Assert.assertEquals("1.20", getFormatStr(1.2));
        Assert.assertEquals("1.00", getFormatStr(1.0));
        Assert.assertEquals("0.00", getFormatStr(0.0));
        Assert.assertEquals("-1.00", getFormatStr((-1.0)));
        Assert.assertEquals("-1.10", getFormatStr((-1.1)));
    }
}

