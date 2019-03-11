package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayListFieldTest_1 extends TestCase {
    public void test_var() throws Exception {
        JSON.parseObject("{\"value\":[{}]}", ArrayListFieldTest_1.V0.class);
        ArrayListFieldTest_1.V0<ArrayListFieldTest_1.A> v1 = JSON.parseObject("{\"value\":[{}]}", new com.alibaba.fastjson.TypeReference<ArrayListFieldTest_1.V0<ArrayListFieldTest_1.A>>() {});
        Assert.assertTrue(((v1.getValue().get(0)) instanceof ArrayListFieldTest_1.A));
        ArrayListFieldTest_1.V0<ArrayListFieldTest_1.B> v2 = JSON.parseObject("{\"value\":[{}]}", new com.alibaba.fastjson.TypeReference<ArrayListFieldTest_1.V0<ArrayListFieldTest_1.B>>() {});
        Assert.assertTrue(((v2.getValue().get(0)) instanceof ArrayListFieldTest_1.B));
    }

    private static class V<T> {}

    private static class V0<T> extends ArrayListFieldTest_1.V<T> {
        private ArrayList<T> value;

        public ArrayList<T> getValue() {
            return value;
        }

        public void setValue(ArrayList<T> value) {
            this.value = value;
        }
    }

    public static class A {}

    public static class B {}
}

