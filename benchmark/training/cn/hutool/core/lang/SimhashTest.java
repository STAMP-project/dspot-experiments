package cn.hutool.core.lang;


import cn.hutool.core.text.Simhash;
import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.Test;


public class SimhashTest {
    @Test
    public void simTest() {
        String text1 = "?? ?? ?? ???";
        String text2 = "?? ?? ?? ???";
        Simhash simhash = new Simhash();
        long hash = simhash.hash(StrUtil.split(text1, ' '));
        Assert.assertTrue((hash != 0));
        simhash.store(hash);
        boolean duplicate = simhash.equals(StrUtil.split(text2, ' '));
        Assert.assertTrue(duplicate);
    }
}

