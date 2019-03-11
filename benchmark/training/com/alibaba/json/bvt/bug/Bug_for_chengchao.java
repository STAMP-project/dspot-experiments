package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;


public class Bug_for_chengchao extends TestCase {
    public void test_0() throws Exception {
        SerializerFeature[] features = new SerializerFeature[]{ SerializerFeature.WriteMapNullValue, SerializerFeature.WriteEnumUsingToString, SerializerFeature.SortField };
        Bug_for_chengchao.Entity entity = new Bug_for_chengchao.Entity();
        JSON.toJSONString(entity, features);
    }

    private static class Entity {
        private TimeUnit unit;

        public TimeUnit getUnit() {
            return unit;
        }

        public void setUnit(TimeUnit unit) {
            this.unit = unit;
        }
    }
}

