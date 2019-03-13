package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONArray;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_8 extends TestCase {
    public void test_0() throws Exception {
        List<String> typeList = JSONArray.parseArray("['java.lang.Class','java.lang.Long']", String.class);
        Assert.assertEquals("java.lang.Class", typeList.get(0));
        Assert.assertEquals("java.lang.Long", typeList.get(1));
    }
}

