package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class MaterializedInterfaceTest extends TestCase {
    public void test_parse() throws Exception {
        String text = "{\"id\":123, \"name\":\"chris\"}";
        MaterializedInterfaceTest.Bean bean = JSON.parseObject(text, MaterializedInterfaceTest.Bean.class);
        Assert.assertEquals(123, bean.getId());
        Assert.assertEquals("chris", bean.getName());
        String text2 = JSON.toJSONString(bean);
        System.out.println(text2);
    }

    public static interface Bean {
        int getId();

        void setId(int value);

        String getName();

        void setName(String value);
    }
}

