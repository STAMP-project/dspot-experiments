package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_101_for_rongganlin_case3 extends TestCase {
    public void test_for_bug() throws Exception {
        Bug_101_for_rongganlin_case3.Entity entity = new Bug_101_for_rongganlin_case3.Entity();
        entity.setHolder(new Bug_101_for_rongganlin_case3.Holder<String>("AAA"));
        JSONObject json = ((JSONObject) (JSON.toJSON(entity)));
        Bug_101_for_rongganlin_case3.Entity entity2 = JSON.toJavaObject(json, Bug_101_for_rongganlin_case3.Entity.class);
        Assert.assertEquals(JSON.toJSONString(entity), JSON.toJSONString(entity2));
    }

    public static class Entity {
        private Bug_101_for_rongganlin_case3.Holder<?> holder;

        public Bug_101_for_rongganlin_case3.Holder<?> getHolder() {
            return holder;
        }

        public void setHolder(Bug_101_for_rongganlin_case3.Holder<?> holder) {
            this.holder = holder;
        }
    }

    public static class Holder<T> {
        private T value;

        public Holder() {
        }

        public Holder(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}

