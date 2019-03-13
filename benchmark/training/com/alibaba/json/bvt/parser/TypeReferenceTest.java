package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest extends TestCase {
    public void test_list() throws Exception {
        List<Long> list = JSON.parseObject("[1,2,3]", new com.alibaba.fastjson.TypeReference<List<Long>>() {});
        Assert.assertEquals(1L, ((Long) (list.get(0))).longValue());
    }
}

