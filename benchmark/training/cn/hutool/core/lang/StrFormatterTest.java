package cn.hutool.core.lang;


import cn.hutool.core.text.StrFormatter;
import org.junit.Assert;
import org.junit.Test;


public class StrFormatterTest {
    @Test
    public void formatTest() {
        // ????
        String result1 = StrFormatter.format("this is {} for {}", "a", "b");
        Assert.assertEquals("this is a for b", result1);
        // ??{}
        String result2 = StrFormatter.format("this is \\{} for {}", "a", "b");
        Assert.assertEquals("this is {} for a", result2);
        // ??\
        String result3 = StrFormatter.format("this is \\\\{} for {}", "a", "b");
        Assert.assertEquals("this is \\a for b", result3);
    }
}

