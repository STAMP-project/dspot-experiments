package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 07/08/2017.
 */
public class ProxyTest2 extends TestCase {
    public void test_0() throws Exception {
        ProxyTest2.Model model = JSON.parseObject("{\"id\":1001}", ProxyTest2.Model.class);
        ProxyTest2.Model model2 = JSON.parseObject("{\"id\":1001}", ProxyTest2.Model.class);
        System.out.println(model.getId());
        // System.out.println(model.getClass());
        // System.out.println(model2.getClass());
        TestCase.assertEquals("{\"id\":1001}", JSON.toJSONString(model));
        TestCase.assertEquals("{\"id\":1001}", JSON.toJSONString(model));
    }

    public static interface Model {
        int getId();

        void setId(int val);
    }
}

