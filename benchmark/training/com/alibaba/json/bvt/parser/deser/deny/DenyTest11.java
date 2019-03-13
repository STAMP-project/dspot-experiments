package com.alibaba.json.bvt.parser.deser.deny;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


public class DenyTest11 extends TestCase {
    ParserConfig config = new ParserConfig();

    public void test_autoTypeDeny() throws Exception {
        DenyTest11.Model model = new DenyTest11.Model();
        model.a = new DenyTest11.B();
        String text = JSON.toJSONString(model, WriteClassName);
        System.out.println(text);
        Object obj = JSON.parseObject(text, Object.class, config);
        TestCase.assertEquals(DenyTest11.Model.class, obj.getClass());
    }

    public static class Model {
        public DenyTest11.A a;
    }

    public static class Model2 {
        public DenyTest11.A a;
    }

    public static class A {}

    public static class B extends DenyTest11.A {}
}

