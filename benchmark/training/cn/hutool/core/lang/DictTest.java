package cn.hutool.core.lang;


import cn.hutool.core.date.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class DictTest {
    @Test
    public void dictTest() {
        Dict dict = // long
        // int
        Dict.create().set("key1", 1).set("key2", 1000L).set("key3", DateTime.now());// Date

        Long v2 = dict.getLong("key2");
        Assert.assertEquals(Long.valueOf(1000L), v2);
    }
}

