package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class MaterializedInterfaceTest2 extends TestCase {
    public void test_parse() throws Exception {
        String text = "{\"id\":123, \"name\":\"chris\"}";
        JSONObject object = JSON.parseObject(text);
        MaterializedInterfaceTest2.Bean bean = TypeUtils.cast(object, MaterializedInterfaceTest2.Bean.class, null);
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

