package com.alibaba.json.bvt.writeClassName;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


/**
 * Created by wenshao on 14/08/2017.
 */
public class WriteClassNameTest5 extends TestCase {
    public void test_for_writeClassName() throws Exception {
        WriteClassNameTest5.Model model = new WriteClassNameTest5.Model();
        WriteClassNameTest5.B b = new WriteClassNameTest5.B();
        b.id = 1001;
        b.value = 2017;
        model.a = b;
        String str = JSON.toJSONString(model);
        System.out.println(str);
        TestCase.assertEquals("{\"a\":{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest5$B\",\"id\":1001,\"value\":2017}}", str);
        WriteClassNameTest5.Model model2 = JSON.parseObject(str, WriteClassNameTest5.Model.class);
        TestCase.assertTrue(((model2.a) instanceof WriteClassNameTest5.B));
    }

    public void test_for_writeClassName_no() throws Exception {
        WriteClassNameTest5.Model model = new WriteClassNameTest5.Model();
        WriteClassNameTest5.A a = new WriteClassNameTest5.A();
        a.id = 1001;
        model.a = a;
        String str = JSON.toJSONString(model);
        System.out.println(str);
        TestCase.assertEquals("{\"a\":{\"id\":1001}}", str);
        WriteClassNameTest5.Model model2 = JSON.parseObject(str, WriteClassNameTest5.Model.class);
        TestCase.assertSame(WriteClassNameTest5.A.class, model2.a.getClass());
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.WriteClassName)
        public WriteClassNameTest5.A a;
    }

    public static class A {
        public int id;
    }

    public static class B extends WriteClassNameTest5.A {
        public int value;
    }
}

