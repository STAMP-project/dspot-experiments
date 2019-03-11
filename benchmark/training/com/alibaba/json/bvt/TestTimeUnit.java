package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestTimeUnit extends TestCase {
    public void test_0() throws Exception {
        String text = JSON.toJSONString(TimeUnit.DAYS);
        Assert.assertEquals(TimeUnit.DAYS, JSON.parseObject(text, TimeUnit.class));
    }
}

